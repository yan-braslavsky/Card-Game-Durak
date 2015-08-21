package com.yan.durak.layouting.pile;

import com.yan.durak.services.CardNodesManagerService;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;

/**
 * Created by ybra on 20/04/15.
 */
public abstract class BasePileLayouter implements IPileLayouter {

    protected static final float CARD_MOVEMENT_ANIMATION_DURATION = 0.5f;
    protected static final float OPPONENT_PILE_SIZE_SCALE = 0.6f;

    final protected CardNodesManagerService mCardNodesManager;
    final protected TweenManager mTweenManager;
    final protected PileModel mBoundpile;

    protected BasePileLayouter(final CardNodesManagerService cardNodesManager, final TweenManager tweenManager, final PileModel boundPile) {
        this.mCardNodesManager = cardNodesManager;
        this.mTweenManager = tweenManager;
        this.mBoundpile = boundPile;
    }


    public abstract void init(float sceneWidth, float sceneHeight);

    public PileModel getBoundpile() {
        return mBoundpile;
    }

    /**
     * Using an existing timeline object adds tween animation for card node.
     */
    protected void addAnimationToTimelineForCardNode(final Timeline tl, final CardNode cardNode, final float endPositionX, final float endPositionY, final float endRotationZ, final float endWidth, final float endHeight, final float endAlpha, final float duration) {
        tl.push(Tween.to(cardNode, YANTweenNodeAccessor.OPACITY, duration).target(endAlpha))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.ROTATION_Z_CW, duration).target(endRotationZ))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.POSITION_X, duration).target(endPositionX))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.POSITION_Y, duration).target(endPositionY))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.SIZE_X, duration).target(endWidth))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.SIZE_Y, duration).target(endHeight));
    }
}
