package com.yan.durak.gamelogic.communication.protocol.messages;


import com.google.gson.annotations.SerializedName;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class ResponseCardForAttackMessage extends BaseProtocolMessage<ResponseCardForAttackMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "responseCardForAttack";

    public ResponseCardForAttackMessage(final Card cardForAttack) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(new CardData(cardForAttack.getRank(), cardForAttack.getSuit())));
    }

    public static class ProtocolMessageData {
        @SerializedName("cardForAttack")
        CardData mCardForAttack;
        public ProtocolMessageData(final CardData cardForAttack) {
            mCardForAttack = cardForAttack;
        }

        public CardData getCardForAttack() {
            return mCardForAttack;
        }
    }
}