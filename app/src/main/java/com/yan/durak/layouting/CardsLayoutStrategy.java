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

    public void init(final float xPosition, final float yPosition, final float maxAvailableWidth, final float slotWidth, final float slotHeight) {
        mNormalizedBaseXPosition = xPosition;
        mNormalizedBaseYPosition = yPosition;
        mMaxWidth = maxAvailableWidth;
        mSlotWidth = slotWidth;
        mSlotHeight = slotHeight;
    }

    public abstract void layoutRowOfSlots(List<CardsLayouterSlotImpl> slots);
}
