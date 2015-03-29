package com.yan.durak.protocol.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan-Home on 12/24/2014.
 */
public class CardData {

    @SerializedName("rank")
    private String mRank;
    @SerializedName("suit")
    private String mSuit;

    public CardData(String rank, String suit) {
        mRank = rank;
        mSuit = suit;
    }

    public String getRank() {
        return mRank;
    }

    public String getSuit() {
        return mSuit;
    }
}
