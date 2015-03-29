package com.yan.durak.input.cards;

/**
 * Created by Yan-Home on 11/21/2014.
 */
public abstract class CardsTouchProcessorState {

    protected static final float BACK_IN_PLACE_ANIMATION_DURATION = 0.2f;

    protected CardsTouchProcessor mCardsTouchProcessor;

    public CardsTouchProcessorState(CardsTouchProcessor cardsTouchProcessor) {
        mCardsTouchProcessor = cardsTouchProcessor;
    }

    public abstract void applyState();

    public abstract boolean onTouchUp(float normalizedX, float normalizedY);

    public abstract boolean onTouchDrag(float normalizedX, float normalizedY);

    public abstract boolean onTouchDown(float normalizedX, float normalizedY);
}
