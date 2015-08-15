package com.yan.durak.gamelogic.communication.protocol.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan-Home on 15/8/2015.
 * Contains user information data , like name avatar statistics etc...
 */
public class PlayerMetaData {

    @SerializedName("playerAvatarResource")
    private String mPlayerAvatarResource;

    @SerializedName("playerNickname")
    private String mPlayerNickname;

    public PlayerMetaData(final String playerAvatarResource, final String playerNickname) {
        mPlayerAvatarResource = playerAvatarResource;
        mPlayerNickname = playerNickname;
    }

    public String getPlayerAvatarResource() {
        return mPlayerAvatarResource;
    }

    public String getPlayerNickname() {
        return mPlayerNickname;
    }
}
