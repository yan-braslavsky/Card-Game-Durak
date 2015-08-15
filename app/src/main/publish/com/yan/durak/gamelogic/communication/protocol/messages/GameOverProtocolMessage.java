package com.yan.durak.gamelogic.communication.protocol.messages;


import com.google.gson.annotations.SerializedName;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class GameOverProtocolMessage extends BaseProtocolMessage<GameOverProtocolMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "gameOver";

    public GameOverProtocolMessage(final PlayerData loosingPlayerData) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(loosingPlayerData));
    }

    public static class ProtocolMessageData {

        @SerializedName("loosingPlayer")
        PlayerData mLoosingPlayer;

        public ProtocolMessageData(final PlayerData loosingPlayer) {
            mLoosingPlayer = loosingPlayer;
        }

        public PlayerData getLoosingPlayer() {
            return mLoosingPlayer;
        }
    }
}
