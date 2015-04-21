package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.models.PileModel;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class FieldPileLayouter extends BasePileLayouter {

    public FieldPileLayouter(final CardNodesManager mCardNodesManager,final  TweenManager mTweenManager,final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager,boundPile);
    }

    @Override
    public void init(float sceneWidth, float sceneHeight) {
        //TODO : implement
    }

    @Override
    public void layout() {
        //TODO : Every pile layouter will make sure to change size of the card
    }
}
