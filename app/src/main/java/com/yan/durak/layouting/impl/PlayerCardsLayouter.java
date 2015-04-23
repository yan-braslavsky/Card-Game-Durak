package com.yan.durak.layouting.impl;

import com.yan.durak.layouting.CardsLayoutSlot;
import com.yan.durak.layouting.CardsLayoutStrategy;
import com.yan.durak.layouting.CardsLayouter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan-Home on 11/8/2014.
 */
public class PlayerCardsLayouter implements CardsLayouter {


    public enum YDistanceBetweenRows {
        DEFAULT, COMPACT, EXPANDED;
    }

    public static final int BASE_SORTING_LAYER = 5;
    private int mActiveSlotsAmount;
    private ArrayList<CardsLayouterSlotImpl> mSlots;

    //layouting strategies used for delegations of actual calculations
    private CardsLayoutStrategyFan mFanStrategy;
    private CardsLayoutStrategyLine mLineStrategy;

    //metrics for calculations
    private float mCardWidth;
    private float mCardHeight;
    private float mMaxAvailableWidth;
    private float mBaseXPosition;
    private float mBaseYPosition;
    private float mYDeltaBetweenRows;
    private float mYPosition;


    private List<List<CardsLayouterSlotImpl>> mLinesOfSlots;

    //external data that required
    public PlayerCardsLayouter(int maxSlotsAmount) {
        mFanStrategy = new CardsLayoutStrategyFan();
        mLineStrategy = new CardsLayoutStrategyLine();
        mSlots = new ArrayList<>(maxSlotsAmount);
        for (int i = 0; i < maxSlotsAmount; i++) {
            mSlots.add(new CardsLayouterSlotImpl());
        }

        //allocate list of slots
        mLinesOfSlots = new ArrayList<>();

    }

    @Override
    public void setActiveSlotsAmount(int amount) {
        mActiveSlotsAmount = amount;
        recalculateSlotsData();
    }

    public void adjustYDistanceBetweenRows(YDistanceBetweenRows distance) {
        switch (distance) {
            case DEFAULT:
                mYDeltaBetweenRows = mCardHeight / 4;
                mYPosition = mBaseYPosition;
                return;
            case COMPACT:
                mYDeltaBetweenRows = mCardHeight / 8;
                mYPosition = mBaseYPosition * 1.15f;
                return;
            case EXPANDED:
                mYDeltaBetweenRows = mCardHeight / 2;
                mYPosition = mBaseYPosition;
                return;
        }
    }

    @Override
    public void init(float cardWidth, float cardHeight, float maxAvailableWidth, float baseXPosition, float baseYPosition) {
        mCardWidth = cardWidth;
        mCardHeight = cardHeight;
        mMaxAvailableWidth = maxAvailableWidth;
        mBaseXPosition = baseXPosition;
        mBaseYPosition = baseYPosition;
        mYDeltaBetweenRows = mCardHeight / 4;
    }

    private void recalculateSlotsData() {

        mLinesOfSlots.clear();

        //the step will change according to logic
        int step = calculateStep();
        int i = 0;
        CardsLayoutStrategy strategy;
        while (i < mActiveSlotsAmount) {
            int start = i;
            int end = Math.min(i + step, mActiveSlotsAmount);

            //strategy will change depending on amount of cards in line
            int cardsInLine = end - start;
            boolean isLineStrategy = cardsInLine == 2;
            if (isLineStrategy)
                strategy = mLineStrategy;
            else
                strategy = mFanStrategy;

            strategy.init(mBaseXPosition, mYPosition, mMaxAvailableWidth, mCardWidth, mCardHeight);
            List<CardsLayouterSlotImpl> subList = mSlots.subList(start, end);
            strategy.layoutRowOfSlots(subList);

            //add subList to list of lines of slots
            mLinesOfSlots.add(subList);

            mYPosition -= mYDeltaBetweenRows;
            i += step;
        }

        calculateSortingLayer();
    }

    private void calculateSortingLayer() {
        // 1 is the bottom line
        //used to order slots by z order
        int sortingLayer = BASE_SORTING_LAYER;
        for (int i = getLinesOfSlots().size() - 1; i >= 0; i--) {
            List<CardsLayouterSlotImpl> currentLine = getLinesOfSlots().get(i);
            for (CardsLayouterSlotImpl slot : currentLine) {
                slot.setSortingLayer(sortingLayer);
                sortingLayer++;
            }
        }
    }

    private int calculateStep() {
        //the step is defined according to  logic
        return (mActiveSlotsAmount > 21) ? 11 : (mActiveSlotsAmount > 14) ? 9 : 7;
    }

    @Override
    public CardsLayoutSlot getSlotAtPosition(int position) {
        if (mActiveSlotsAmount < position) {
            throw new RuntimeException("There is only " + mActiveSlotsAmount + " active slots , cannot provide slot at position " + position);
        }

        return mSlots.get(position);
    }

    @Override
    public List<List<CardsLayouterSlotImpl>> getLinesOfSlots() {
        return mLinesOfSlots;
    }
}
