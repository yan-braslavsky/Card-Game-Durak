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
public class ResponseThrowInsMessage extends BaseProtocolMessage<ResponseThrowInsMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "responseThrowIns";

    public ResponseThrowInsMessage(final List<Card> selectedThrowInCards) {
        super();
        setMessageName(MESSAGE_NAME);
        final List<CardData> cardDataList = convertCardDataList(selectedThrowInCards);
        setMessageData(new ProtocolMessageData(cardDataList));
    }

    private List<CardData> convertCardDataList(final List<Card> possibleThrowInCards) {
        final List<CardData> retList = new ArrayList<>();
        for (final Card card : possibleThrowInCards) {
            retList.add(new CardData(card.getRank(), card.getSuit()));
        }
        return retList;
    }


    public static class ProtocolMessageData {

        @SerializedName("selectedThrowInCards")
        List<CardData> mSelectedThrowInCards;

        public ProtocolMessageData(final List<CardData> selectedThrowInCards) {
            mSelectedThrowInCards = selectedThrowInCards;
        }

        public List<CardData> getSelectedThrowInCards() {
            return mSelectedThrowInCards;
        }
    }
}
