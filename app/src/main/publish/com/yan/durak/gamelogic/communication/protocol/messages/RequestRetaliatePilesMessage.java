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
public class RequestRetaliatePilesMessage extends BaseProtocolMessage<RequestRetaliatePilesMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "requestRetaliatePiles";

    public RequestRetaliatePilesMessage(List<List<Card>> pilesBeforeRetaliation) {
        super();
        setMessageName(MESSAGE_NAME);
        List<List<CardData>> piles = convertCardDataList(pilesBeforeRetaliation);
        setMessageData(new ProtocolMessageData(piles));
    }

    private List<List<CardData>> convertCardDataList(List<List<Card>> pilesBeforeRetaliation) {
        List<List<CardData>> retList = new ArrayList<>();
        for (List<Card> pile : pilesBeforeRetaliation) {
            List<CardData> cardDataList = new ArrayList<>();
            for (Card card : pile) {
                cardDataList.add(new CardData(card.getRank(), card.getSuit()));
            }
            retList.add(cardDataList);
        }
        return retList;
    }


    public static class ProtocolMessageData {

        @SerializedName("pilesBeforeRetaliation")
        List<List<CardData>> mPilesBeforeRetaliation;

        public ProtocolMessageData(List<List<CardData>> piles) {
            mPilesBeforeRetaliation = piles;
        }

        public List<List<CardData>> getPilesBeforeRetaliation() {
            return mPilesBeforeRetaliation;
        }
    }
}
