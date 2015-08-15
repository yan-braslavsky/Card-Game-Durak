package com.yan.durak.gamelogic.communication.protocol.messages;


import com.google.gson.annotations.SerializedName;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class RequestThrowInsMessage extends BaseProtocolMessage<RequestThrowInsMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "requestThrowIns";

    public RequestThrowInsMessage(final List<Card> possibleThrowInCards, final int maxThrowInCardsAmount) {
        super();
        setMessageName(MESSAGE_NAME);
        final List<CardData> cardDataList = convertCardDataList(possibleThrowInCards);
        setMessageData(new ProtocolMessageData(cardDataList,maxThrowInCardsAmount));
    }

    private List<CardData> convertCardDataList(final List<Card> possibleThrowInCards) {
        final List<CardData> retList = new ArrayList<>();
        for (final Card card : possibleThrowInCards) {
            retList.add(new CardData(card.getRank(), card.getSuit()));
        }
        return retList;
    }


    public static class ProtocolMessageData {

        @SerializedName("possibleThrowInCards")
        List<CardData> mPossibleThrowInCards;

        @SerializedName("maxThrowInCardsAmount")
        int mMaxThrowInCardsAmount;


        public ProtocolMessageData(final List<CardData> possibleThrowInCards, final int maxThrowInCardsAmount) {
            mPossibleThrowInCards = possibleThrowInCards;
            mMaxThrowInCardsAmount = maxThrowInCardsAmount;
        }

        public List<CardData> getPossibleThrowInCards() {
            return mPossibleThrowInCards;
        }

        public int getMaxThrowInCardsAmount() {
            return mMaxThrowInCardsAmount;
        }
    }
}
