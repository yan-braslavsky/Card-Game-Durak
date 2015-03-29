package com.yan.durak.protocol.messages;


import com.yan.durak.protocol.BaseProtocolMessage;
import com.yan.durak.protocol.data.CardData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class RequestRetaliatePilesMessage extends BaseProtocolMessage<RequestRetaliatePilesMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "requestRetaliatePiles";

//    public RequestRetaliatePilesMessage(List<Pile> pilesBeforeRetaliation) {
//        super();
//        setMessageName(MESSAGE_NAME);
//        List<List<CardData>> piles = convertCardDataList(pilesBeforeRetaliation);
//        setMessageData(new ProtocolMessageData(piles));
//    }

//    private List<List<CardData>> convertCardDataList(List<Pile> pilesBeforeRetaliation) {
//        List<List<CardData>> retList = new ArrayList<>();
//        for (Pile pile : pilesBeforeRetaliation) {
//            List<CardData> cardDataList = new ArrayList<>();
//            for (Card card : pile.getCardsInPile()) {
//                cardDataList.add(new CardData(card.getRank(), card.getSuit()));
//            }
//            retList.add(cardDataList);
//        }
//        return retList;
//    }


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
