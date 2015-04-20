package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.CardNodesManager;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class FieldPileLayouter extends BasePileLayouter {

    public FieldPileLayouter(CardNodesManager mCardNodesManager, TweenManager mTweenManager) {
        super(mCardNodesManager, mTweenManager);
    }

    @Override
    public void layout() {
        //TODO : Every pile layouter will make sure to change size of the card
    }
}
