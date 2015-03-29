package com.yan.durak.layouting;

import com.yan.durak.layouting.impl.CardsLayouterSlotImpl;

import java.util.List;

/**
 * Created by Yan-Home on 11/9/2014.
 */
public abstract class CardsLayoutStrategy {

    protected float mNormalizedBaseYPosition;
    protected float mNormalizedBaseXPosition;
    protected float mMaxWidth;
    protected float mSlotWidth;
    protected float mSlotHeight;

    public void init(float xPosition, float yPosition, float maxAvailableWidth, float slotWidth, float slotHeight) {
        mNormalizedBaseXPosition = xPosition;
        mNormalizedBaseYPosition = yPosition;
        mMaxWidth = maxAvailableWidth;
        mSlotWidth = slotWidth;
        mSlotHeight = slotHeight;
    }

    public abstract void layoutRowOfSlots(List<CardsLayouterSlotImpl> slots);
}
