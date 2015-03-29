package com.yan.durak.layouting.impl;

import com.yan.durak.layouting.CardsLayoutStrategy;

import java.util.List;

/**
 * Created by Yan-Home on 11/8/2014.
 */
public class CardsLayoutStrategyLine extends CardsLayoutStrategy {


    public void layoutRowOfSlots(List<CardsLayouterSlotImpl> slots) {

        float yStartPosition = mNormalizedBaseYPosition - mSlotHeight;
        float distanceBetweenCards = mSlotWidth / 2;
        float xStartPosition = mNormalizedBaseXPosition - (distanceBetweenCards * (slots.size() / 2));

        CardsLayouterSlotImpl slot;
        float cursorPosition = xStartPosition;

        for (int i = 0; i < slots.size(); i++) {

            slot = slots.get(i);
            slot.setPosition(cursorPosition - mSlotWidth / 4, yStartPosition);
            slot.setRotation(0);

            cursorPosition += distanceBetweenCards;

        }
    }
}
