package com.yan.durak.gamelogic.communication.protocol.messages;


import com.google.gson.annotations.SerializedName;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class CardMovedProtocolMessage extends BaseProtocolMessage<CardMovedProtocolMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "cardMoved";

    public CardMovedProtocolMessage(final String cardRank, final String cardSuit, final int movedFromPile, final int movedToPile) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(new CardData(cardRank, cardSuit), movedFromPile, movedToPile));
    }

    public static class ProtocolMessageData {

        @SerializedName("movedCard")
        CardData mMovedCard;
        @SerializedName("fromPileIndex")
        int mFromPileIndex;
        @SerializedName("toPileIndex")
        int mToPileIndex;

        public ProtocolMessageData(final CardData movedCard, final int fromPileIndex, final int toPileIndex) {
            mMovedCard = movedCard;
            mFromPileIndex = fromPileIndex;
            mToPileIndex = toPileIndex;
        }

        public CardData getMovedCard() {
            return mMovedCard;
        }

        public int getFromPileIndex() {
            return mFromPileIndex;
        }

        public int getToPileIndex() {
            return mToPileIndex;
        }
    }
}
