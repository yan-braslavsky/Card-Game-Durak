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

    public RequestRetaliatePilesMessage(final List<List<Card>> pilesBeforeRetaliation) {
        super();
        setMessageName(MESSAGE_NAME);
        final List<List<CardData>> piles = convertCardDataList(pilesBeforeRetaliation);
        setMessageData(new ProtocolMessageData(piles));
    }

    private List<List<CardData>> convertCardDataList(final List<List<Card>> pilesBeforeRetaliation) {
        final List<List<CardData>> retList = new ArrayList<>();
        for (final List<Card> pile : pilesBeforeRetaliation) {
            final List<CardData> cardDataList = new ArrayList<>();
            for (int i = 0; i < pile.size(); i++) {
                final Card card = pile.get(i);
                cardDataList.add(new CardData(card.getRank(), card.getSuit()));
            }
            retList.add(cardDataList);
        }
        return retList;
    }


    public static class ProtocolMessageData {

        @SerializedName("pilesBeforeRetaliation")
        List<List<CardData>> mPilesBeforeRetaliation;

        public ProtocolMessageData(final List<List<CardData>> piles) {
            mPilesBeforeRetaliation = piles;
        }

        public List<List<CardData>> getPilesBeforeRetaliation() {
            return mPilesBeforeRetaliation;
        }
    }
}
