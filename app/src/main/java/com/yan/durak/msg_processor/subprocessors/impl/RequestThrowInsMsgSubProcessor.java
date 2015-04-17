package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.input.cards.states.CardsTouchProcessorMultipleChoiceState;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.nodes.CardNode;

import java.util.ArrayList;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestThrowInsMsgSubProcessor extends BaseMsgSubProcessor<RequestThrowInsMessage> {

    public RequestThrowInsMsgSubProcessor(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    @Override
    public void processMessage(RequestThrowInsMessage serverMessage) {
        //FIXME : That entire method requires rewriting

        //we attaching finish button to screen
        //player can finish with his throw ins any time by pressing the button
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showBitoButton();

        //FIXME : Do not keep that info in the screen rather on the state
        mMsgProcessor.getPrototypeGameScreen().setRequestThrowIn(true);
        mMsgProcessor.getPrototypeGameScreen().setThrowInCardsAllowed(serverMessage.getMessageData().getPossibleThrowInCards().size());

        //TODO : Make more efficient !
        ArrayList<CardNode> availableCards = new ArrayList<>();
        for (CardData cardData : serverMessage.getMessageData().getPossibleThrowInCards()) {
            for (CardNode playerCardNode : mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getBottomPlayerCardNodes()) {
                if (cardData.getRank().equals(playerCardNode.getCard().getRank()) && cardData.getSuit().equals(playerCardNode.getCard().getSuit())) {
                    availableCards.add(playerCardNode);
                }
            }
        }

        //FIXME : Something ugly is going on here...
        mMsgProcessor.getPrototypeGameScreen().setThrowInInputProcessorState(new CardsTouchProcessorMultipleChoiceState(mMsgProcessor.getPrototypeGameScreen().getCardsTouchProcessor(), availableCards));
        mMsgProcessor.getPrototypeGameScreen().getCardsTouchProcessor().setCardsTouchProcessorState(mMsgProcessor.getPrototypeGameScreen().getThrowInInputProcessorState());

        //FIXME : Do not keep that info in the screen rather on the state
        mMsgProcessor.getPrototypeGameScreen().getSelectedThrowInCards().clear();
        mMsgProcessor.getPrototypeGameScreen().getThrowInPossibleCards().clear();
        mMsgProcessor.getPrototypeGameScreen().getThrowInPossibleCards().addAll(serverMessage.getMessageData().getPossibleThrowInCards());
    }
}
