package com.yan.durak.gamelogic.communication.protocol.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan-Home on 1/24/2015.
 * <p/>
 * This class describes the situation when player needs to retaliate one cards with another.
 * Class holds information of one card covered by the other.
 */
public class RetaliationSetData {

    @SerializedName("coveredCard")
    CardData mCoveredCardData;
    @SerializedName("coveringCard")
    CardData mCoveringCardData;

    public RetaliationSetData(final CardData coveredCardData, final CardData coveringCardData) {
        mCoveredCardData = coveredCardData;
        mCoveringCardData = coveringCardData;
    }

    public CardData getCoveredCardData() {
        return mCoveredCardData;
    }

    public CardData getCoveringCardData() {
        return mCoveringCardData;
    }
}
