package com.yan.durak.layouting.pile;

import com.yan.durak.service.services.CardNodesManagerService;
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

    protected void animateCardNode(CardNode cardNode, float endPositionX, float endPositionY, float endRotationZ, float endWidth, float endHeight, float endAlpha) {
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(cardNode, YANTweenNodeAccessor.OPACITY, CARD_MOVEMENT_ANIMATION_DURATION).target(endAlpha))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.ROTATION_Z_CW, CARD_MOVEMENT_ANIMATION_DURATION).target(endRotationZ))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.POSITION_X, CARD_MOVEMENT_ANIMATION_DURATION).target(endPositionX))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.POSITION_Y, CARD_MOVEMENT_ANIMATION_DURATION).target(endPositionY))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.SIZE_X, CARD_MOVEMENT_ANIMATION_DURATION).target(endWidth))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.SIZE_Y, CARD_MOVEMENT_ANIMATION_DURATION).target(endHeight))
                .start(mTweenManager);
    }
}
