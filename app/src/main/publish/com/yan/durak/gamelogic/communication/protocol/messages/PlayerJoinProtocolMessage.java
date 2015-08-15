package com.yan.durak.gamelogic.communication.protocol.messages;


import com.google.gson.annotations.SerializedName;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class PlayerJoinProtocolMessage extends BaseProtocolMessage<PlayerJoinProtocolMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "playerJoin";

    public PlayerJoinProtocolMessage(PlayerData playerData, int totalPlayersInGame) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(playerData, totalPlayersInGame));
    }

    public static class ProtocolMessageData {

        @SerializedName("joinedPlayerData")
        PlayerData mJoinedPlayerData;

        @SerializedName("totalPlayersInGame")
        int mTotalPlayersInGame;

        public ProtocolMessageData(PlayerData joinedPlayerData, int totalPlayersInGame) {
            mJoinedPlayerData = joinedPlayerData;
            mTotalPlayersInGame = totalPlayersInGame;
        }

        public PlayerData getJoinedPlayerData() {
            return mJoinedPlayerData;
        }

        public int getTotalPlayersInGame() {
            return mTotalPlayersInGame;
        }
    }
}
