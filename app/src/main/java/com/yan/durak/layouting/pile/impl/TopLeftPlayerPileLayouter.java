package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.layouting.threepoint.ThreePointFanLayouter;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.models.IPile;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by ybra on 20/04/15.
 */
public class TopLeftPlayerPileLayouter extends BasePileLayouter {

    private ThreePointFanLayouter mThreePointFanLayouterTopLeft;

    public TopLeftPlayerPileLayouter(final CardNodesManager mCardNodesManager, final TweenManager mTweenManager, final IPile boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);

        mThreePointFanLayouterTopLeft = new ThreePointFanLayouter(2);
    }

    @Override
    public void layout() {
        //TODO : Every pile layouter will make sure to change size of the card
    }

    @Override
    public void init(float sceneWidth, float sceneHeight) {

        //offset from the beginning of the screen left
        float offsetX = sceneWidth * 0.01f;
        float topOffset = sceneHeight * 0.07f;
        float fanDistance = sceneWidth * 0.05f;

        YANVector2 pos = new YANVector2(offsetX, topOffset);
        YANVector2 origin = new YANVector2(pos.getX() * 4, pos.getY());
        YANVector2 leftBasis = new YANVector2(origin.getX() + fanDistance, origin.getY());
        YANVector2 rightBasis = new YANVector2(origin.getX(), origin.getY() + fanDistance);

        //set three points
        mThreePointFanLayouterTopLeft.setThreePoints(origin, leftBasis, rightBasis);

        //swap direction
        mThreePointFanLayouterTopLeft.setDirection(ThreePointFanLayouter.LayoutDirection.RTL);
    }
}
