package com.yan.durak.gamelogic.communication.protocol.messages;


import com.google.gson.annotations.SerializedName;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerData;

import java.util.List;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class GameSetupProtocolMessage extends BaseProtocolMessage<GameSetupProtocolMessage.ProtocolMessageData> {

    public static final String MESSAGE_NAME = "gameSetup";

    public GameSetupProtocolMessage(final PlayerData myPlayerData, final CardData trumpCard,
                                    final List<PlayerData> alreadyJoinedPlayers, final int totalPlayersInGame) {
        super();
        setMessageName(MESSAGE_NAME);
        setMessageData(new ProtocolMessageData(myPlayerData, trumpCard, alreadyJoinedPlayers, totalPlayersInGame));
    }

    public static class ProtocolMessageData {

        @SerializedName("myPlayerData")
        PlayerData mMyPlayerData;

        @SerializedName("trumpCard")
        CardData mTrumpCard;

        @SerializedName("alreadyJoinedPlayers")
        List<PlayerData> mAlreadyJoinedPlayers;

        @SerializedName("totalPlayersInGame")
        int mTotalPlayersInGame;

        public ProtocolMessageData(final PlayerData myPlayerData, final CardData trumpCard,
                                   final List<PlayerData> alreadyJoinedPlayers, final int totalPlayersInGame) {
            mMyPlayerData = myPlayerData;
            mTrumpCard = trumpCard;
            mAlreadyJoinedPlayers = alreadyJoinedPlayers;
            mTotalPlayersInGame = totalPlayersInGame;
        }

        public PlayerData getMyPlayerData() {
            return mMyPlayerData;
        }

        public CardData getTrumpCard() {
            return mTrumpCard;
        }

        public List<PlayerData> getAlreadyJoinedPlayers() {
            return mAlreadyJoinedPlayers;
        }

        public int getTotalPlayersInGame() {
            return mTotalPlayersInGame;
        }
    }
}
