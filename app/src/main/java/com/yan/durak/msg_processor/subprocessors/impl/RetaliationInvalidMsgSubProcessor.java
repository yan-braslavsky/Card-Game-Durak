package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public class RetaliationInvalidMsgSubProcessor extends BaseMsgSubProcessor<RetaliationInvalidProtocolMessage> {

    public RetaliationInvalidMsgSubProcessor() {
        super();
    }

    @Override
    public void processMessage(RetaliationInvalidProtocolMessage serverMessage) {
//        //TODO : cache the value for efficiency
//        ArrayList<Card> cardsToRemoveTagFrom = new ArrayList<>();
//
//        //remove from map all invalid retaliations
//        for (RetaliationSetData retaliationSetData : serverMessage.getMessageData().getInvalidRetaliationsList()) {
//
//            Card coveredCard = new Card(retaliationSetData.getCoveredCardData().getRank(), retaliationSetData.getCoveredCardData().getSuit());
//            Card coveringCard = new Card(retaliationSetData.getCoveringCardData().getRank(), retaliationSetData.getCoveringCardData().getSuit());
//
//            //FIXME : Do not keep that info in the screen rather on the state
//            mMsgProcessor.getPrototypeGameScreen().getGameSession().getCardsPendingRetaliationMap().remove(coveredCard);
//
//            cardsToRemoveTagFrom.add(coveredCard);
//            cardsToRemoveTagFrom.add(coveringCard);
//        }
//
//        for (Card card : cardsToRemoveTagFrom) {
//            CardNode cardNode = mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getCardToNodesMap().get(card);
//            cardNode.removeTag(CardNode.TAG_TEMPORALLY_COVERED);
//        }
//
//        mMsgProcessor.getPrototypeGameScreen().layoutBottomPlayerCards();
    }
}
