package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.layouting.pile.impl.StockPileLayouter;
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

    public GameSetupMsgSubProcessor(final GameInfo gameInfo, final PileLayouterManagerService pileLayouterManager, final PileManagerService pileManager) {
        super();

        mGameInfo = gameInfo;
        mPileLayouterManager = pileLayouterManager;
        mPileManager = pileManager;
    }

    @Override
    public void processMessage(final GameSetupProtocolMessage serverMessage) {

        //We need to initialize piles according to amount of players in game
        initializePilesServices(serverMessage.getMessageData().getTotalPlayersInGame());

        //extract trump card data
        extractTrumpCardData(serverMessage.getMessageData().getTrumpCard());

        //extract current player as a bottom player
        extractCurrentPlayer(serverMessage.getMessageData().getMyPlayerData());

        //extract others already joined players
        extractAlreadyJoinedPlayers(serverMessage.getMessageData().getAlreadyJoinedPlayers(),
                serverMessage.getMessageData().getTotalPlayersInGame());

        //we need to adapt UI to amount of players in game
        initUI(serverMessage.getMessageData());

    }

    private void initUI(final GameSetupProtocolMessage.ProtocolMessageData data) {
        //since all the piles are currently in the stock pile , we should lay it out
        final StockPileLayouter stockPileLayouter = mPileLayouterManager.getPileLayouterForPile(mPileManager.getStockPile());
        //we need to hide top right player if there are only 2 players
        if (data.getTotalPlayersInGame() == 2) {
            ServiceLocator.locateService(HudManagementService.class).hidePlayerUI(GameInfo.PlayerLocation.TOP_RIGHT_PLAYER);
            stockPileLayouter.placeAtRightTop();
            ServiceLocator.locateService(HudManagementService.class).placeTrumpIconAtRightTop();
        }

        ServiceLocator.locateService(HudManagementService.class).setIconForPlayer(
                GameInfo.PlayerLocation.BOTTOM_PLAYER, data.getMyPlayerData().getPlayerMetaData().getPlayerAvatarResource());

        //TODO : set bottom player name when designs will be available, currently player name is not displayed ?
        stockPileLayouter.layout();
    }

    private void initializePilesServices(final int totalPlayersInGame) {

        //first player pile comes after stock and discard piles
        //then we calculate first field pile index
        final int firsFieldPileIndex = (2 + totalPlayersInGame);
        ServiceLocator.locateService(PileManagerService.class).setFirstFiledPileindex(firsFieldPileIndex);

        //now when field piles correctly initialized , we can initialize
        //field piles layouters
        ServiceLocator.locateService(PileLayouterManagerService.class).initFieldPileLayouters();
    }

    private void extractCurrentPlayer(final PlayerData playerData) {
        //current player is a bottom player so we are extracting his pile index and assigning to pile manager
        mPileManager.setBottomPlayerPileIndex(playerData.getPlayerPileIndex());
        //set index in game for bottom player
        mGameInfo.setGameIndexForPlayer(GameInfo.PlayerLocation.BOTTOM_PLAYER, playerData.getPlayerIndexInGame());

        //init player info for current player
        ServiceLocator.locateService(GameInfo.class).setPlayerInfoForPlayer(
                GameInfo.PlayerLocation.BOTTOM_PLAYER, new GameInfo.PlayerInfo(
                        playerData.getPlayerMetaData().getPlayerAvatarResource(),
                        playerData.getPlayerMetaData().getPlayerNickname()));

    }

    private void extractAlreadyJoinedPlayers(final List<PlayerData> alreadyJoinedPlayers, final int totalPlayersInGame) {

        //maybe there are no joined players yet
        if (alreadyJoinedPlayers.isEmpty())
            return;

        for (int i = 0; i < alreadyJoinedPlayers.size(); i++) {
            placePlayer(mGameInfo.getPlayerIndex(GameInfo.PlayerLocation.BOTTOM_PLAYER),
                    alreadyJoinedPlayers.get(i), totalPlayersInGame);
        }
    }

    private void placePlayer(final int bottomPlayerIndex, final PlayerData joinedPlayer,
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

    private void placeAsTopRight(final PlayerData joinedPlayer) {
        ServiceLocator.locateService(PileManagerService.class).setTopRightPlayerPileIndex(joinedPlayer.getPlayerPileIndex());
        ServiceLocator.locateService(GameInfo.class).setGameIndexForPlayer(GameInfo.PlayerLocation.TOP_RIGHT_PLAYER, joinedPlayer.getPlayerIndexInGame());

        //set the name and avatar icon for player
        ServiceLocator.locateService(HudManagementService.class).setNameForPlayer(GameInfo.PlayerLocation.TOP_RIGHT_PLAYER, joinedPlayer.getPlayerMetaData().getPlayerNickname());
        ServiceLocator.locateService(HudManagementService.class).setIconForPlayer(GameInfo.PlayerLocation.TOP_RIGHT_PLAYER, joinedPlayer.getPlayerMetaData().getPlayerAvatarResource());
    }

    private void placeAsTopLeft(final PlayerData joinedPlayer) {
        ServiceLocator.locateService(PileManagerService.class).setTopLeftPlayerPileIndex(joinedPlayer.getPlayerPileIndex());
        ServiceLocator.locateService(GameInfo.class).setGameIndexForPlayer(GameInfo.PlayerLocation.TOP_LEFT_PLAYER, joinedPlayer.getPlayerIndexInGame());

        //set the name and avatar icon for player
        ServiceLocator.locateService(HudManagementService.class).setNameForPlayer(GameInfo.PlayerLocation.TOP_LEFT_PLAYER, joinedPlayer.getPlayerMetaData().getPlayerNickname());
        ServiceLocator.locateService(HudManagementService.class).setIconForPlayer(GameInfo.PlayerLocation.TOP_LEFT_PLAYER, joinedPlayer.getPlayerMetaData().getPlayerAvatarResource());
    }

    private void extractTrumpCardData(final CardData trumpCardData) {
        //extract trump card and save it in game session
        mGameInfo.setTrumpCard(new Card(trumpCardData.getRank(), trumpCardData.getSuit()));

        //we need set trump suit to be visible when stock pile gets empty
        ServiceLocator.locateService(HudManagementService.class).setTrumpSuit(trumpCardData.getSuit());
    }
}