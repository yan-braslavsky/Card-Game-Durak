package com.yan.durak.protocol.messages;

import com.yan.durak.entities.cards.Card;
import com.yan.durak.protocol.BaseProtocolMessage;
import com.yan.durak.protocol.data.CardData;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Yan-Home on 12/24/2014.
 */
public class ResponseCardForAttackMessage extends BaseProtocolMessage<ResponseCardForAttackMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "responseCardForAttack";

    public ResponseCardForAttackMessage(Card cardForAttack) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(new CardData(cardForAttack.getRank(), cardForAttack.getSuit())));
    }

    public static class ProtocolMessageData {
        @SerializedName("cardForAttack")
        CardData mCardForAttack;

        public ProtocolMessageData(CardData cardForAttack) {
            mCardForAttack = cardForAttack;
        }

        public CardData getCardForAttack() {
            return mCardForAttack;
        }
    }
}