package com.yan.durak.input.cards;

import glengine.yan.glengine.util.object_pool.YANIPoolableObject;

/**
 * Created by Yan-Home on 11/21/2014.
 */
public abstract class CardsTouchProcessorState implements YANIPoolableObject {

    protected CardsTouchProcessor mCardsTouchProcessor;

    public CardsTouchProcessorState() {
        //Must have a public constructor
    }

    protected void setCardsTouchProcessor(CardsTouchProcessor cardsTouchProcessor) {
        mCardsTouchProcessor = cardsTouchProcessor;
    }

    protected abstract void applyState();

    protected abstract boolean onTouchUp(float normalizedX, float normalizedY);

    protected abstract boolean onTouchDrag(float normalizedX, float normalizedY);

    protected abstract boolean onTouchDown(float normalizedX, float normalizedY);

    @Override
    public void resetState() {
        mCardsTouchProcessor = null;
    }
}
