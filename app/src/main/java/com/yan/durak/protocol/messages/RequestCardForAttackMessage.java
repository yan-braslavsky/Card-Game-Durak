package com.yan.durak.protocol.messages;

import com.yan.durak.entities.cards.Card;
import com.yan.durak.protocol.BaseProtocolMessage;
import com.yan.durak.protocol.data.CardData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yan-Home on 12/24/2014.
 */
public class RequestCardForAttackMessage extends BaseProtocolMessage<RequestCardForAttackMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "requestCardForAttack";

    public RequestCardForAttackMessage(List<Card> cardsInHand) {
        super();
        setMessageName(MESSAGE_NAME);
        List<CardData> cardsInHandDataList = convertCardDataList(cardsInHand);
        setMessageData(new ProtocolMessageData(cardsInHandDataList));
    }

    private List<CardData> convertCardDataList(List<Card> cardsInHand) {
        List<CardData> ret = new ArrayList<>();

        for (Card card : cardsInHand) {
            ret.add(new CardData(card.getRank(), card.getSuit()));
        }
        return ret;
    }

    public static class ProtocolMessageData {

        @SerializedName("cardsInHand")
        List<CardData> mCardsInHand;

        public ProtocolMessageData(List<CardData> cardsInHand) {
            mCardsInHand = cardsInHand;
        }
    }
}
