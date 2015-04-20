package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.tmp.ICardNodesManager;
import com.yan.durak.tmp.IPile;
import com.yan.durak.tmp.IPileLayouter;
import com.yan.durak.tmp.IPileLayouterManager;
import com.yan.durak.tmp.IPileManager;

/**
 * Created by ybra on 17/04/15.
 */
public class CardMovedMsgSubProcessor extends BaseMsgSubProcessor<CardMovedProtocolMessage> {

    public CardMovedMsgSubProcessor(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    private IPileManager mPileManager;
    private ICardNodesManager mCardNodesManager;
    private IPileLayouterManager mPileLayouterManager;

    @Override
    public void processMessage(CardMovedProtocolMessage serverMessage) {

        //get the indexes from the message
        int fromPileIndex = serverMessage.getMessageData().getFromPileIndex();
        int toPileIndex = serverMessage.getMessageData().getToPileIndex();

        //Pile is a local representation of what is going on on the server
        IPile fromPile = mPileManager.getPileWithIndex(fromPileIndex);

        //get the card that is about to be moved
        Card movedCard = fromPile.getCardByRankAndSuit(serverMessage.getMessageData().getMovedCard().getRank(), serverMessage.getMessageData().getMovedCard().getSuit());

        //make sure that the card that we want to move is actually in the pile
        if (movedCard == null) {
            throw new RuntimeException("The card " + movedCard + "is not found in pile");
        }

        //Pile is a local representation of what is going on on the server
        IPile toPile = mPileManager.getPileWithIndex(toPileIndex);

        //remove the card from the pile and place into the other
        fromPile.removeCard(movedCard);
        toPile.addCard(movedCard);

        //we need to update both piles that had changes
        IPileLayouter fromPileLayouter = mPileLayouterManager.getPileLayouterForPile(fromPile);
        IPileLayouter toPileLayouter = mPileLayouterManager.getPileLayouterForPile(toPile);

        //make the actual layout
        fromPileLayouter.layout();
        toPileLayouter.layout();


        //TODO : Stock pile layouter will make sure to remove mask if there is 1 card left
        //TODO : Every pile layouter will make sure to change size of the card
        //TODO : Every pile will have it's own layouter

    }
}
