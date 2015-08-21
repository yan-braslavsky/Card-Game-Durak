package com.yan.durak.animation;

import android.support.annotation.NonNull;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;

/**
 * Created by Yan-Home on 15/8/2015.
 * Aggregates various animation related functions
 */
public class AnimationHelper {
    public static void createButtonNodeClickAnimation(final YANButtonNode node, final TweenManager tweenManager) {
        final float originalWidth = node.getSize().getX();
        final float originalHeight = node.getSize().getY();
        final float duration = 0.05f;

        //default to pressed
        node.setStateChangeAnimator(node.createStateChangeAnimator(YANButtonNode.YANButtonState.DEFAULT, YANButtonNode.YANButtonState.PRESSED,
                new YANButtonNode.ButtonAnimation() {
                    @Override
                    public void startButtonAnimation(@NonNull YANButtonNode buttonNode) {
                        tweenManager.killTarget(node);
                        Timeline.createSequence()
                                .beginParallel()
                                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_X, duration).target(originalWidth * 0.95f).ease(Sine.OUT))
                                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_Y, duration).target(originalHeight * 0.85f).ease(Sine.OUT))
                                .start(tweenManager);
                    }
                }));

        //pressed to default
        node.setStateChangeAnimator(
                node.createStateChangeAnimator(YANButtonNode.YANButtonState.PRESSED, YANButtonNode.YANButtonState.DEFAULT,
                        new YANButtonNode.ButtonAnimation() {
                            @Override
                            public void startButtonAnimation(@NonNull YANButtonNode buttonNode) {
                                tweenManager.killTarget(node);
                                Timeline.createSequence()
                                        .beginParallel()
                                        .push(Tween.to(node, YANTweenNodeAccessor.SIZE_X, duration).target(originalWidth).ease(Sine.IN))
                                        .push(Tween.to(node, YANTweenNodeAccessor.SIZE_Y, duration).target(originalHeight).ease(Sine.IN))
                                        .start(tweenManager);
                            }
                        }));
    }

    public static void createInfiniteBreathingAnimation(final YANBaseNode node, final TweenManager tweenManager) {
        final float duration = 0.6f;
        final float originalWidth = node.getSize().getX();
        final float originalHeight = node.getSize().getY();
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_X, duration).target(originalWidth * 1.05f))
                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_Y, duration).target(originalHeight * 0.95f))
                .end()
                .beginParallel()
                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_X, duration).target(originalWidth))
                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_Y, duration).target(originalHeight))
                .end()
                .repeat(Tween.INFINITY, 0.0f)
                .start(tweenManager);
    }
}
