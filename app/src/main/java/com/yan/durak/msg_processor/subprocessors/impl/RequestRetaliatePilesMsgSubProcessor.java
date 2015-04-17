package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

import java.util.List;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestRetaliatePilesMsgSubProcessor extends BaseMsgSubProcessor<RequestRetaliatePilesMessage> {

    public RequestRetaliatePilesMsgSubProcessor(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    @Override
    public void processMessage(RequestRetaliatePilesMessage serverMessage) {
        //FIXME : Do not store this info on the screen
        //rather transition to other processor state
        mMsgProcessor.getPrototypeGameScreen().setRequestedRetaliation(true);
        mMsgProcessor.getPrototypeGameScreen().getCardsPendingRetaliationMap().clear();

        for (List<CardData> cardDataList : serverMessage.getMessageData().getPilesBeforeRetaliation()) {
            for (CardData cardData : cardDataList) {
                mMsgProcessor.getPrototypeGameScreen().getCardsPendingRetaliationMap().put(new Card(cardData.getRank(), cardData.getSuit()), null);
            }
        }

        //in that case we want the hud to present us with option to take the card
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showTakeButton();
    }
}
