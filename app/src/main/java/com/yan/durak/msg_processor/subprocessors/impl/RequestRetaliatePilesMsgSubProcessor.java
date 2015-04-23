package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestRetaliatePilesMsgSubProcessor extends BaseMsgSubProcessor<RequestRetaliatePilesMessage> {

    private final GameServerMessageSender mMessageSender;
    private final List<List<Card>> mRetaliationList;

    public RequestRetaliatePilesMsgSubProcessor(final GameServerMessageSender messageSender) {
        super();

        this.mMessageSender = messageSender;
        this.mRetaliationList = new ArrayList<>();
    }

    @Override
    public void processMessage(RequestRetaliatePilesMessage serverMessage) {

        //TODO : for now just take all (send empty array)
        mMessageSender.sendResponseRetaliatePiles(mRetaliationList);


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
