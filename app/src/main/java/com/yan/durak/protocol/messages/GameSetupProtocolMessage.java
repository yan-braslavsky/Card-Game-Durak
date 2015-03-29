package com.yan.durak.protocol.messages;

import com.yan.durak.protocol.BaseProtocolMessage;
import com.yan.durak.protocol.data.CardData;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Yan-Home on 12/24/2014.
 */
public class GameSetupProtocolMessage extends BaseProtocolMessage<GameSetupProtocolMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "gameSetup";

    /**
     * @param playerPileIndex index of a pile that belongs to relieving player
     */
    public GameSetupProtocolMessage(int playerPileIndex, String trumpCardRank, String trumpCardSuit) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(playerPileIndex, new CardData(trumpCardRank, trumpCardSuit)));
    }

    public static class ProtocolMessageData {

        //TODO : in future perhaps we will need more information

        @SerializedName("myPileIndex")
        int mMyPileIndex;

        @SerializedName("trumpCard")
        CardData mTrumpCard;

        public ProtocolMessageData(int myPileIndex, CardData trumpCard) {
            mMyPileIndex = myPileIndex;
            mTrumpCard = trumpCard;
        }

        public int getMyPileIndex() {
            return mMyPileIndex;
        }

        public CardData getTrumpCard() {
            return mTrumpCard;
        }
    }
}
