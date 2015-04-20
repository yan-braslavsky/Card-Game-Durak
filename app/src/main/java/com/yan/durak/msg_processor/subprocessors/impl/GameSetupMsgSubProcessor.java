package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.managers.PileLayouterManager;
import com.yan.durak.managers.PileManager;
import com.yan.durak.models.IPile;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.session.GameSession;

/**
 * Created by ybra on 17/04/15.
 */
public class GameSetupMsgSubProcessor extends BaseMsgSubProcessor<GameSetupProtocolMessage> {

    private final GameSession mGameSession;
    private final PileLayouterManager mPileLayouterManager;
    private final PileManager mPileManager;

    public GameSetupMsgSubProcessor(final MsgProcessor mMsgProcessor, final GameSession gameSession, final PileLayouterManager pileLayouterManager, PileManager pileManager) {
        super(mMsgProcessor);

        this.mGameSession = gameSession;
        this.mPileLayouterManager = pileLayouterManager;
        this.mPileManager = pileManager;
    }

    @Override
    public void processMessage(GameSetupProtocolMessage serverMessage) {

        //store current player index on the game session
        mGameSession.setBottomPlayerGameIndex(serverMessage.getMessageData().getMyPlayerData().getPlayerIndexInGame());

        //depending on my player index we need to identify indexes of all players
        int bottomPlayerPileIndex = serverMessage.getMessageData().getMyPlayerData().getPlayerPileIndex();
        int topRightPlayerPileIndex = (bottomPlayerPileIndex + 1);
        int topLeftPlayerPileIndex = (bottomPlayerPileIndex + 2);

        //TODO :load all pile indexes from server ?
        //correct other players positions
        if ((topRightPlayerPileIndex / 5) > 0)
            topRightPlayerPileIndex = (topRightPlayerPileIndex % 5) + 2;

        if ((topLeftPlayerPileIndex / 5) > 0)
            topLeftPlayerPileIndex = (topLeftPlayerPileIndex % 5) + 2;

        //store pile indexes of all players
        mGameSession.setBottomPlayerPileIndex(bottomPlayerPileIndex);
        mGameSession.setTopLeftPlayerPileIndex(topLeftPlayerPileIndex);
        mGameSession.setTopRightPlayerPileIndex(topRightPlayerPileIndex);

        //extract trump card and save it in game session
        CardData trumpCardData = serverMessage.getMessageData().getTrumpCard();
        mGameSession.setTrumpCard(new Card(trumpCardData.getRank(), trumpCardData.getSuit()));

        //since all the piles are currently in the stock pile , we should lay out it
        IPile stockPile = mPileManager.getPileWithIndex(mGameSession.getStockPileIndex());
        IPileLayouter stockPileLayouter = mPileLayouterManager.getPileLayouterForPile(stockPile);
        stockPileLayouter.layout();
    }


//    public void positionMaskCard(CardData trumpCardNode) {
//
//        Card trumpCard = new Card(trumpCardNode.getRank(), trumpCardNode.getSuit());
//
//        //position the mask at the same place with the stock pile cards
//        Collection<Card> cardsInStockPile = mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getCardsInPileWithIndex(0);
//        Iterator<Card> iterator = cardsInStockPile.iterator();
//        Card randomCardInStockPile = iterator.next();
//        if (trumpCard.equals(randomCardInStockPile)) {
//            randomCardInStockPile = iterator.next();
//        }
//        CardNode randomCardNodeInStockPile = mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getCardToNodesMap().get(randomCardInStockPile);
//
//        //position the stock pile mask
//        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().setMaskCardTransform(
//                randomCardNodeInStockPile.getPosition().getX(), randomCardNodeInStockPile.getPosition().getY(),
//                randomCardNodeInStockPile.getSize().getX(), randomCardNodeInStockPile.getSize().getY(),
//                randomCardNodeInStockPile.getRotationZ());
//    }
}
