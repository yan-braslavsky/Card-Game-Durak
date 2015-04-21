package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestRetaliatePilesMsgSubProcessor extends BaseMsgSubProcessor<RequestRetaliatePilesMessage> {

    public RequestRetaliatePilesMsgSubProcessor() {
        super();
    }

    @Override
    public void processMessage(RequestRetaliatePilesMessage serverMessage) {
//        //FIXME : Do not store this info on the screen
//        //rather transition to other processor state
//        mMsgProcessor.getPrototypeGameScreen().getGameSession().setActivePlayerState(ActivePlayerState.REQUEST_RETALIATION);
//
//        mMsgProcessor.getPrototypeGameScreen().getGameSession().getCardsPendingRetaliationMap().clear();
//
//        for (List<CardData> cardDataList : serverMessage.getMessageData().getPilesBeforeRetaliation()) {
//            for (CardData cardData : cardDataList) {
//                mMsgProcessor.getPrototypeGameScreen().getGameSession().getCardsPendingRetaliationMap().put(new Card(cardData.getRank(), cardData.getSuit()), null);
//            }
//        }
//
//        //in that case we want the hud to present us with option to take the card
//        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showTakeButton();
    }
}
