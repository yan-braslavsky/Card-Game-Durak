package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.models.IPile;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class StockPileLayouter extends BasePileLayouter {


    public StockPileLayouter(final CardNodesManager mCardNodesManager, final TweenManager mTweenManager, final IPile boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);
    }

    @Override
    public void layout() {
        //TODO : Stock pile layouter will make sure to remove mask if there is 1 card left
    }
}
