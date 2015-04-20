package com.yan.durak.layouting.pile;

import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.models.IPile;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public abstract class BasePileLayouter implements IPileLayouter {

    final protected CardNodesManager mCardNodesManager;
    final protected TweenManager mTweenManager;
    final protected IPile mBoundpile;

    protected BasePileLayouter(final CardNodesManager mCardNodesManager, final TweenManager mTweenManager, final IPile boundPile) {
        this.mCardNodesManager = mCardNodesManager;
        this.mTweenManager = mTweenManager;
        this.mBoundpile = boundPile;
    }


    public abstract void init(float sceneWidth, float sceneHeight);
}
