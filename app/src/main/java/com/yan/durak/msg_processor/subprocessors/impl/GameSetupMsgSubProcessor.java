package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.session.GameInfo;

import java.util.List;

import glengine.yan.glengine.service.ServiceLocator;

/**
 * Created by ybra on 17/04/15.
 */
public class GameSetupMsgSubProcessor extends BaseMsgSubProcessor<GameSetupProtocolMessage> {

    private final GameInfo mGameInfo;
    private final PileLayouterManagerService mPileLayouterManager;
    private final PileManagerService mPileManager;

    public GameSetupMsgSubProcessor(final GameInfo gameInfo, final PileLayouterManagerService pileLayouterManager, PileManagerService pileManager) {
        super();

        mGameInfo = gameInfo;
        mPileLayouterManager = pileLayouterManager;
        mPileManager = pileManager;
    }

    @Override
    public void processMessage(GameSetupProtocolMessage serverMessage) {

//        int firsPileIndex = (2 + serverMessage.getMessageData().getTotalPlayersInGame());
//        ServiceLocator.locateService(PileManagerService.class).setFirstFiledPileindex(firsPileIndex);
//        ServiceLocator.locateService(PileLayouterManagerService.class).initFieldPileLayouters();

        //extract trump card data
        extractTrumpCardData(serverMessage.getMessageData().getTrumpCard());

        //extract current player as a bottom player
        extractCurrentPlayer(serverMessage.getMessageData().getMyPlayerData());

        //extract others already joined players
        extractAlreadyJoinedPlayers(serverMessage.getMessageData().getAlreadyJoinedPlayers(),
                serverMessage.getMessageData().getTotalPlayersInGame());

        //since all the piles are currently in the stock pile , we should lay out it
        IPileLayouter stockPileLayouter = mPileLayouterManager.getPileLayouterForPile(mPileManager.getStockPile());
        //TODO : set layouter position according to amount of players in game
        stockPileLayouter.layout();
    }

    private void extractCurrentPlayer(PlayerData playerData) {
        //current player is a bottom player so we are extracting his pile index and assigning to pile manager
        mPileManager.setBottomPlayerPileIndex(playerData.getPlayerPileIndex());
        //set index in game for bottom player
        mGameInfo.setGameIndexForPlayer(GameInfo.Player.BOTTOM_PLAYER, playerData.getPlayerIndexInGame());
    }

    private void extractAlreadyJoinedPlayers(List<PlayerData> alreadyJoinedPlayers, final int totalPlayersInGame) {

        //maybe there are no joined players yet
        if (alreadyJoinedPlayers.isEmpty())
            return;

        for (int i = 0; i < alreadyJoinedPlayers.size(); i++) {
            placePlayer(mGameInfo.getPlayerIndex(GameInfo.Player.BOTTOM_PLAYER),
                    alreadyJoinedPlayers.get(i), totalPlayersInGame);
        }
    }

    private void placePlayer(int bottomPlayerIndex, PlayerData joinedPlayer,
                             final int totalPlayersInGame) {
        int topLeftPlayerIndex = bottomPlayerIndex + 1;
        int topRightPlayerIndex = bottomPlayerIndex + 2;

        //correct other players positions
        if ((topRightPlayerIndex / totalPlayersInGame) > 0)
            topRightPlayerIndex = (topRightPlayerIndex % totalPlayersInGame);

        if ((topLeftPlayerIndex / totalPlayersInGame) > 0)
            topLeftPlayerIndex = (topLeftPlayerIndex % totalPlayersInGame);

        if (joinedPlayer.getPlayerIndexInGame() == topLeftPlayerIndex) {
            placeAsTopLeft(joinedPlayer);
        } else if (joinedPlayer.getPlayerIndexInGame() == topRightPlayerIndex) {
            placeAsTopRight(joinedPlayer);
        } else
            throw new IllegalStateException("Couldn't identify player position");
    }

    private void placeAsTopRight(PlayerData joinedPlayer) {
        ServiceLocator.locateService(PileManagerService.class).setTopRightPlayerPileIndex(joinedPlayer.getPlayerPileIndex());
        ServiceLocator.locateService(GameInfo.class).setGameIndexForPlayer(GameInfo.Player.TOP_RIGHT_PLAYER, joinedPlayer.getPlayerIndexInGame());
    }

    private void placeAsTopLeft(PlayerData joinedPlayer) {
        ServiceLocator.locateService(PileManagerService.class).setTopLeftPlayerPileIndex(joinedPlayer.getPlayerPileIndex());
        ServiceLocator.locateService(GameInfo.class).setGameIndexForPlayer(GameInfo.Player.TOP_LEFT_PLAYER, joinedPlayer.getPlayerIndexInGame());
    }

    private void extractTrumpCardData(CardData trumpCardData) {
        //extract trump card and save it in game session
        mGameInfo.setTrumpCard(new Card(trumpCardData.getRank(), trumpCardData.getSuit()));

        //we need set trump suit to be visible when stock pile gets empty
        ServiceLocator.locateService(HudManagementService.class).setTrumpSuit(trumpCardData.getSuit());
    }
}