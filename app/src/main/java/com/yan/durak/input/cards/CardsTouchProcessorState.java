package com.yan.durak.input.cards;

import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.object_pool.YANIPoolableObject;

/**
 * Created by Yan-Home on 11/21/2014.
 */
public abstract class CardsTouchProcessorState implements YANIPoolableObject {

    protected CardsTouchProcessor mCardsTouchProcessor;
    protected final YANVector2 mScreenSize;

    //Must have a public constructor
    public CardsTouchProcessorState() {
        mScreenSize = new YANVector2();
    }

    protected void setCardsTouchProcessor(CardsTouchProcessor cardsTouchProcessor) {
        mCardsTouchProcessor = cardsTouchProcessor;
    }

    protected void applyState(float screenWidth, float screenHeight) {
        mScreenSize.setXY(screenWidth, screenHeight);
    }

    protected abstract boolean onTouchUp(float normalizedX, float normalizedY);

    protected abstract boolean onTouchDrag(float normalizedX, float normalizedY);

    protected abstract boolean onTouchDown(float normalizedX, float normalizedY);

    @Override
    public void resetState() {
        mCardsTouchProcessor = null;
    }
}