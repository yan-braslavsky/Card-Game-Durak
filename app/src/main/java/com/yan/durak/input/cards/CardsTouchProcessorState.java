package com.yan.durak.input.cards;

/**
 * Created by Yan-Home on 11/21/2014.
 */
abstract class CardsTouchProcessorState {

    protected final CardsTouchProcessor mCardsTouchProcessor;

    protected CardsTouchProcessorState(final CardsTouchProcessor cardsTouchProcessor) {
        mCardsTouchProcessor = cardsTouchProcessor;
    }

    protected abstract void applyState();

    protected abstract boolean onTouchUp(float normalizedX, float normalizedY);

    protected abstract boolean onTouchDrag(float normalizedX, float normalizedY);

    protected abstract boolean onTouchDown(float normalizedX, float normalizedY);
}
