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

    public PlayerData(int playerIndexInGame, int playerPileIndex) {
        mPlayerIndexInGame = playerIndexInGame;
        mPlayerPileIndex = playerPileIndex;
    }

    public int getPlayerIndexInGame() {
        return mPlayerIndexInGame;
    }

    public int getPlayerPileIndex() {
        return mPlayerPileIndex;
    }
}