package com.yan.durak.gamelogic.communication.protocol.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan-Home on 4/5/2015.
 */
public class PlayerData {

    @SerializedName("playerIndexInGame")
    private int mPlayerIndexInGame;

    @SerializedName("playerPileIndex")
    private int mPlayerPileIndex;

    @SerializedName("playerMetaData")
    private PlayerMetaData mPlayerMetaData;

    public PlayerData(final int playerIndexInGame, final int playerPileIndex, final PlayerMetaData playerMetaData) {
        mPlayerIndexInGame = playerIndexInGame;
        mPlayerPileIndex = playerPileIndex;
        mPlayerMetaData = playerMetaData;
    }

    public int getPlayerIndexInGame() {
        return mPlayerIndexInGame;
    }

    public int getPlayerPileIndex() {
        return mPlayerPileIndex;
    }

    public PlayerMetaData getPlayerMetaData() {
        return mPlayerMetaData;
    }
}