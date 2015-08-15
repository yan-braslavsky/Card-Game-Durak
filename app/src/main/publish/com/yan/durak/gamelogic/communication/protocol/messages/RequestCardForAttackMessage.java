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
public class RequestCardForAttackMessage extends BaseProtocolMessage<RequestCardForAttackMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "requestCardForAttack";

    public RequestCardForAttackMessage(final List<Card> cardsInHand) {
        super();
        setMessageName(MESSAGE_NAME);
        final List<CardData> cardsInHandDataList = convertCardDataList(cardsInHand);
        setMessageData(new ProtocolMessageData(cardsInHandDataList));
    }

    private List<CardData> convertCardDataList(final List<Card> cardsInHand) {
        final List<CardData> ret = new ArrayList<>();

        for (final Card card : cardsInHand) {
            ret.add(new CardData(card.getRank(), card.getSuit()));
        }
        return ret;
    }

    public static class ProtocolMessageData {

        @SerializedName("cardsInHand")
        List<CardData> mCardsInHand;

        public ProtocolMessageData(final List<CardData> cardsInHand) {
            mCardsInHand = cardsInHand;
        }

        public List<CardData> getCardsInHand() {
            return mCardsInHand;
        }
    }
}
