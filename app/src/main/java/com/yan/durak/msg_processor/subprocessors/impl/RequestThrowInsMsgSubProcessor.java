package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.input.cards.states.CardsTouchProcessorMultipleChoiceState;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.session.states.ActivePlayerState;

import java.util.ArrayList;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestThrowInsMsgSubProcessor extends BaseMsgSubProcessor<RequestThrowInsMessage> {

    private ArrayList<CardNode> mAvailableCards;

    public RequestThrowInsMsgSubProcessor(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);

        mAvailableCards = new ArrayList<>();
    }

    @Override
    public void processMessage(RequestThrowInsMessage serverMessage) {
        //FIXME : That entire method requires rewriting

        //we attaching finish button to screen
        //player can finish with his throw ins any time by pressing the button
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showBitoButton();

        mMsgProcessor.getPrototypeGameScreen().getGameSession().setActivePlayerState(ActivePlayerState.REQUEST_THROW_IN);
        mMsgProcessor.getPrototypeGameScreen().setThrowInCardsAllowed(serverMessage.getMessageData().getPossibleThrowInCards().size());

        mAvailableCards.clear();
        for (CardData cardData : serverMessage.getMessageData().getPossibleThrowInCards()) {
            for (CardNode playerCardNode : mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getBottomPlayerCardNodes()) {
                if (cardData.getRank().equals(playerCardNode.getCard().getRank()) && cardData.getSuit().equals(playerCardNode.getCard().getSuit())) {
                    mAvailableCards.add(playerCardNode);
                }
            }
        }

        //FIXME : Something ugly is going on here...
        mMsgProcessor.getPrototypeGameScreen().setThrowInInputProcessorState(new CardsTouchProcessorMultipleChoiceState(mMsgProcessor.getPrototypeGameScreen().getCardsTouchProcessor(), mAvailableCards));
        mMsgProcessor.getPrototypeGameScreen().getCardsTouchProcessor().setCardsTouchProcessorState(mMsgProcessor.getPrototypeGameScreen().getThrowInInputProcessorState());

        mMsgProcessor.getPrototypeGameScreen().getGameSession().getSelectedThrowInCards().clear();
        mMsgProcessor.getPrototypeGameScreen().getGameSession().getThrowInPossibleCards().clear();
        mMsgProcessor.getPrototypeGameScreen().getGameSession().getThrowInPossibleCards().addAll(serverMessage.getMessageData().getPossibleThrowInCards());
    }
}
