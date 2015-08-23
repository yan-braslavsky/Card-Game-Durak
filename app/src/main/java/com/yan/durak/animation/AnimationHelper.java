package com.yan.durak.animation;

import android.support.annotation.NonNull;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;

/**
 * Created by Yan-Home on 15/8/2015.
 * Aggregates various animation related functions
 */
public class AnimationHelper {


    /**
     * Creates a click animation for button
     *
     * @param buttonNode   the button node that triggers the animation
     * @param animatedNode the node that actually gets animated
     * @param tweenManager tween manager that runs update every frame
     * @param onComplete   the action that will be called once button back to normal state animation will be finished
     */
    public static void createButtonNodeClickAnimation(final YANButtonNode buttonNode, final YANBaseNode animatedNode,
                                                      final TweenManager tweenManager, final Runnable onComplete) {
        final float originalWidth = animatedNode.getSize().getX();
        final float originalHeight = animatedNode.getSize().getY();
        final float duration = 0.55f;

        //default to pressed
        buttonNode.setStateChangeAnimator(buttonNode.createStateChangeAnimator(YANButtonNode.YANButtonState.DEFAULT, YANButtonNode.YANButtonState.PRESSED,
                new YANButtonNode.ButtonAnimation() {
                    @Override
                    public void startButtonAnimation(@NonNull YANButtonNode buttonNode) {
                        tweenManager.killTarget(animatedNode);
                        Timeline.createSequence()
                                .beginParallel()
                                .push(Tween.to(animatedNode, YANTweenNodeAccessor.SIZE_X, duration).target(originalWidth * 0.85f).ease(Elastic.OUT))
                                .push(Tween.to(animatedNode, YANTweenNodeAccessor.SIZE_Y, duration).target(originalHeight * 0.85f).ease(Elastic.OUT))
                                .end()
                                .start(tweenManager);
                    }
                }));

        //pressed to default
        buttonNode.setStateChangeAnimator(
                buttonNode.createStateChangeAnimator(YANButtonNode.YANButtonState.PRESSED, YANButtonNode.YANButtonState.DEFAULT,
                        new YANButtonNode.ButtonAnimation() {
                            @Override
                            public void startButtonAnimation(@NonNull YANButtonNode buttonNode) {
                                tweenManager.killTarget(animatedNode);
                                Timeline.createSequence()
                                        .beginParallel()
                                        .push(Tween.to(animatedNode, YANTweenNodeAccessor.SIZE_X, duration).target(originalWidth).ease(Elastic.OUT))
                                        .push(Tween.to(animatedNode, YANTweenNodeAccessor.SIZE_Y, duration).target(originalHeight).ease(Elastic.OUT))
                                        .setCallback(new TweenCallback() {
                                            @Override
                                            public void onEvent(final int type, final BaseTween<?> baseTween) {
                                                if (type == TweenCallback.COMPLETE) {
                                                    if (onComplete != null)
                                                        onComplete.run();
                                                }
                                            }
                                        })
                                        .start(tweenManager);
                            }
                        }));
    }

    public static void createInfiniteBreathingAnimation(final YANBaseNode node, final TweenManager tweenManager) {
        final float duration = 0.6f;
        final float originalWidth = node.getSize().getX();
        final float originalHeight = node.getSize().getY();
        tweenManager.killTarget(node);

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
