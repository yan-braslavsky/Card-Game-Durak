package com.yan.durak.managers;

import com.yan.durak.models.IPile;

/**
 * Created by ybra on 20/04/15.
 */
public class PileManager {

    //TODO : that is debatable , there could be more piles on field
    public static final int MAX_PILES_ON_FIELD = 8;
    public static final int FIRST_FIELD_PILE_INDEX = 4;
    public static final int MAX_PILE_INDEX = FIRST_FIELD_PILE_INDEX + MAX_PILES_ON_FIELD;

    private IPile mBottomPlayerPile;
    private IPile mTopLeftPlayerPile;
    private IPile mTopRightPlayerPile;
    private IPile mStockPile;
    private IPile mDiscardPile;

    public PileManager() {

        //TODO : init piles
    }

    public IPile getPileWithIndex(int pileIndex) {
        throw new UnsupportedOperationException();
    }


    public IPile getBottomPlayerPile() {
        return mBottomPlayerPile;
    }

    public IPile getTopLeftPlayerPile() {
        return mTopLeftPlayerPile;
    }

    public IPile getTopRightPlayerPile() {
        return mTopRightPlayerPile;
    }


    public IPile getStockPile() {
        return mStockPile;
    }

    public IPile getDiscardPile() {
        return mDiscardPile;
    }
}
