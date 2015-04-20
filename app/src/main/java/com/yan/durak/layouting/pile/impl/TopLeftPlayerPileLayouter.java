package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.layouting.threepoint.ThreePointFanLayouter;
import com.yan.durak.managers.ICardNodesManager;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class TopLeftPlayerPileLayouter extends BasePileLayouter {

    private ThreePointFanLayouter mThreePointFanLayouterTopLeft;

    public TopLeftPlayerPileLayouter(ICardNodesManager mCardNodesManager, TweenManager mTweenManager) {
        super(mCardNodesManager, mTweenManager);

        mThreePointFanLayouterTopLeft = new ThreePointFanLayouter(2);
    }

    @Override
    public void layout() {
        //TODO : Every pile layouter will make sure to change size of the card
    }
}
