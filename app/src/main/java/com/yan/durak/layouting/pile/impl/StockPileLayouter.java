package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.ICardNodesManager;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class StockPileLayouter extends BasePileLayouter {


    public StockPileLayouter(ICardNodesManager mCardNodesManager, TweenManager mTweenManager) {
        super(mCardNodesManager, mTweenManager);
    }

    @Override
    public void layout() {
        //TODO : Stock pile layouter will make sure to remove mask if there is 1 card left
    }
}
