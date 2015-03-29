package com.yan.durak.layouting.impl;

import com.yan.durak.layouting.CardsLayoutStrategy;
import com.yan.glengine.util.geometry.YANVector2;
import com.yan.glengine.util.math.YANMathUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yan-Home on 11/8/2014.
 */
public class CardsLayoutStrategyFan extends CardsLayoutStrategy {


    private Comparator<CardsLayouterSlotImpl> mSlotsByIndexComparator;

    public CardsLayoutStrategyFan() {
        super();
        mSlotsByIndexComparator = new Comparator<CardsLayouterSlotImpl>() {
            @Override
            public int compare(CardsLayouterSlotImpl lhs, CardsLayouterSlotImpl rhs) {
                return rhs.getSortingIndex() - lhs.getSortingIndex();
            }
        };
    }

    public void layoutRowOfSlots(List<CardsLayouterSlotImpl> slots) {

        boolean isEvenAmountOfSlots = slots.size() % 2 == 0;

        float xCenterPosition = mNormalizedBaseXPosition;
        float yStartPosition = mNormalizedBaseYPosition - mSlotHeight;

        float yRotationPoint = mNormalizedBaseYPosition * 3;
        float halfAvailableWidth = (mMaxWidth / 2);
        double tanHalfAngle = halfAvailableWidth / (yRotationPoint - yStartPosition);
        double halfAngleInRadians = Math.atan(tanHalfAngle);

        float halfAngleInDegrees = (float) (Math.toDegrees(halfAngleInRadians));
        float fullAngleInDegrees = halfAngleInDegrees * 2;
        float angleStep = fullAngleInDegrees / slots.size();

        //adjust the step , to leave it inside the bounds of the screen
        angleStep = angleStep * 0.8f;

        float currentRotation;
        int currentDistanceFromOrigin = 0;
        YANVector2 rotationOrigin = new YANVector2(xCenterPosition, yRotationPoint);

        int halfArraySize = slots.size() / 2;
        int currentSortingIndex = halfArraySize;

        for (int i = 0; i < slots.size(); i++) {
            //slot that will be repositioned
            CardsLayouterSlotImpl slot = slots.get(i);

            //side represents left or right from the origin
            int side = ((i % 2) == 0) ? -1 : 1;

            //set sorting index that will help reorder slots
            //after reposition
            slot.setSortingIndex(currentSortingIndex);

            //onUpdate sorting index
            currentSortingIndex += currentDistanceFromOrigin * side;

            //the amount of rotation in degrees
            currentRotation = currentDistanceFromOrigin * angleStep * side;

            //slot will be rotated around origin starting from the center point
            slot.setPosition(xCenterPosition, yStartPosition);

            //rotate the slot
            YANMathUtils.rotatePointAroundOrigin(slot.getPosition(), rotationOrigin, currentRotation);
            slot.setRotation(currentRotation);

            //fix the offset from the pivot point
            slot.setPosition(slot.getPosition().getX() - mSlotWidth / 2, slot.getPosition().getY());

            if (isEvenAmountOfSlots) {
                //rotate one more time
                YANMathUtils.rotatePointAroundOrigin(slot.getPosition(), rotationOrigin, -angleStep / 2);
            }

            //increase the distance only when both sides are placed
            if (side < 0) {
                currentDistanceFromOrigin++;
            }
        }

        //reorder the array to fix the sorting mess
        Collections.sort(slots, mSlotsByIndexComparator);
    }
}
