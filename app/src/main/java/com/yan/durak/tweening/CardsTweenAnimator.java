package com.yan.durak.tweening;

import com.yan.durak.layouting.CardsLayoutSlot;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;

/**
 * Created by Yan-Home on 11/21/2014.
 */
public class CardsTweenAnimator {
    private TweenManager mTweenManager;

    public CardsTweenAnimator(TweenManager tweenManager) {
        mTweenManager = tweenManager;
    }

    /**
     * Animates values of the card to values of the slot
     */
    public void animateCardToSlot(YANTexturedNode card, CardsLayoutSlot slot) {
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_X, 0.5f).target(slot.getPosition().getX()))
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_Y, 0.5f).target(slot.getPosition().getY()))
                .push(Tween.to(card, YANTweenNodeAccessor.ROTATION_Z_CW, 0.5f).target(slot.getRotation()))
                .start(mTweenManager);
    }

    public void animateCardToValues(YANTexturedNode card, float targetXPosition, float targetYPosition, float targetRotation, TweenCallback animationEndCallback) {
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_X, 0.5f).target(targetXPosition))
                .setCallback(
                        animationEndCallback)
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_Y, 0.5f).target(targetYPosition))
                .push(Tween.to(card, YANTweenNodeAccessor.ROTATION_Z_CW, 0.5f).target(targetRotation))
                .start(mTweenManager);
    }

    public void animateCardToValues(YANTexturedNode card, float targetXPosition, float targetYPosition, float targetWidthPosition, float targetHeightPosition, float targetRotation, TweenCallback animationEndCallback) {
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_X, 0.5f).target(targetXPosition))
                .setCallback(
                        animationEndCallback)
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_Y, 0.5f).target(targetYPosition))
                .push(Tween.to(card, YANTweenNodeAccessor.ROTATION_Z_CW, 0.5f).target(targetRotation))
                .push(Tween.to(card, YANTweenNodeAccessor.SIZE_X, 0.5f).target(targetWidthPosition))
                .push(Tween.to(card, YANTweenNodeAccessor.SIZE_Y, 0.5f).target(targetHeightPosition))
                .start(mTweenManager);
    }

    public void animateCardToAlpha(YANTexturedNode card, float targetAlpha, float duration) {
        mTweenManager.killTarget(card, YANTweenNodeAccessor.OPACITY);
        Timeline.createSequence()
                .push(Tween.to(card, YANTweenNodeAccessor.OPACITY, duration).target(targetAlpha))
                .start(mTweenManager);
    }

    public void animateCardToXY(YANTexturedNode card, float targetX, float targetY, float duration) {
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_X, duration).target(targetX))
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_Y, duration).target(targetY))
                .start(mTweenManager);
    }

    public void animateCardToY(YANTexturedNode card, float targetY, float duration, TweenCallback animationEndCallback) {
        Timeline.createSequence()
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_Y, duration).target(targetY))
                .setCallback(animationEndCallback)
                .start(mTweenManager);
    }

    public void animateSize(YANTexturedNode card, float width, float height, float duration) {
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(card, YANTweenNodeAccessor.SIZE_X, duration).target(width))
                .push(Tween.to(card, YANTweenNodeAccessor.SIZE_Y, duration).target(height))
                .start(mTweenManager);
    }

    public void animateSizeAndPositionXY(YANTexturedNode card, float width, float height, float targetX, float targetY, float duration) {
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(card, YANTweenNodeAccessor.SIZE_X, duration).target(width))
                .push(Tween.to(card, YANTweenNodeAccessor.SIZE_Y, duration).target(height))
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_X, duration).target(targetX))
                .push(Tween.to(card, YANTweenNodeAccessor.POSITION_Y, duration).target(targetY))
                .start(mTweenManager);
    }

    public void kill(YANTexturedNode node) {
        mTweenManager.killTarget(node);
    }

    public TweenManager getTweenManager() {
        return mTweenManager;
    }
}
