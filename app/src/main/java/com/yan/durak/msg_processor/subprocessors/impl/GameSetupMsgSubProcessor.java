package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.models.PileModel;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.session.GameInfo;

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

        extractPilesData(serverMessage);
        extractGameInfoData(serverMessage);

        //since all the piles are currently in the stock pile , we should lay out it
        PileModel stockPile = mPileManager.getStockPile();
        IPileLayouter stockPileLayouter = mPileLayouterManager.getPileLayouterForPile(stockPile);
        stockPileLayouter.layout();
    }

    private void extractPilesData(GameSetupProtocolMessage serverMessage) {
        //depending on my player index we need to identify indexes of all players
        int bottomPlayerPileIndex = serverMessage.getMessageData().getMyPlayerData().getPlayerPileIndex();
        int topLeftPlayerPileIndex = (bottomPlayerPileIndex + 1);
        int topRightPlayerPileIndex = (bottomPlayerPileIndex + 2);

        //TODO :load all pile indexes from server ?
        //correct other players positions
        if ((topRightPlayerPileIndex / 5) > 0)
            topRightPlayerPileIndex = (topRightPlayerPileIndex % 5) + 2;

        if ((topLeftPlayerPileIndex / 5) > 0)
            topLeftPlayerPileIndex = (topLeftPlayerPileIndex % 5) + 2;

        //store pile indexes of all players
        mPileManager.setPlayersPilesIndexes(bottomPlayerPileIndex, topRightPlayerPileIndex, topLeftPlayerPileIndex);
    }

    private void extractGameInfoData(GameSetupProtocolMessage serverMessage) {

        int bottomPlayerIndex = serverMessage.getMessageData().getMyPlayerData().getPlayerIndexInGame();
        int topLeftPlayerIndex = bottomPlayerIndex + 1;
        int topRightPlayerIndex = bottomPlayerIndex + 2;

        //correct other players positions
        if ((topRightPlayerIndex / 3) > 0)
            topRightPlayerIndex = (topRightPlayerIndex % 3);

        if ((topLeftPlayerIndex / 3) > 0)
            topLeftPlayerIndex = (topLeftPlayerIndex % 3);

        mGameInfo.setPlayerIndexes(bottomPlayerIndex, topRightPlayerIndex, topLeftPlayerIndex);

        //extract trump card and save it in game session
        CardData trumpCardData = serverMessage.getMessageData().getTrumpCard();
        mGameInfo.setTrumpCard(new Card(trumpCardData.getRank(), trumpCardData.getSuit()));

        //we need set trump suit to be visible when stock pile gets empty
        ServiceLocator.locateService(HudManagementService.class).setTrumpSuit(trumpCardData.getSuit());
    }
}
