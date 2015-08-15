package com.yan.durak.layouting.impl;

import com.yan.durak.layouting.CardsLayoutSlot;
import com.yan.durak.layouting.CardsLayoutStrategy;
import com.yan.durak.layouting.CardsLayouter;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan-Home on 11/8/2014.
 */
public class PlayerCardsLayouter implements CardsLayouter {


    public enum ExpansionLevelPreset {
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
    private float mDefaultYDeltaBetweenRows;


    private List<List<CardsLayouterSlotImpl>> mLinesOfSlots;

    //external data that required
    public PlayerCardsLayouter(final int maxSlotsAmount) {
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
    public void setActiveSlotsAmount(final int amount) {
        mActiveSlotsAmount = amount;
        recalculateSlotsData();
    }

    /**
     * Sets the expansion level of the pile.
     * The higher the level , the more expanded the pile is.
     *
     * @param level must be in range between 0 to 1
     */
    public void adjustExpansionLevel(final float level) {
        if (level < 0 || level > 1f)
            throw new InvalidParameterException("Level must be between 0 and 1");


        //TODO : can be cached , no need to calculate every time
        final float maxYDeltaBetweenRows = mCardHeight / 2f;
        final float maxYPosition = mBaseYPosition;

        final float minYDeltaBetweenRows = mCardHeight / 8f;
        final float minYPosition = mBaseYPosition * 1.15f;

        final float deltaYDeltaBetweenRows = maxYDeltaBetweenRows - minYDeltaBetweenRows;
        final float deltaYPosition = maxYPosition - minYPosition;

        mYPosition = minYPosition + (deltaYPosition * level);
        mYDeltaBetweenRows = minYDeltaBetweenRows + (deltaYDeltaBetweenRows * level);
    }

    /**
     * Sets the expansion level of the pile according to predefined Preset.
     */
    public void adjustExpansionLevel(final ExpansionLevelPreset levelPreset) {
        switch (levelPreset) {
            case DEFAULT:
                adjustExpansionLevel(0.6f);
                return;
            case COMPACT:
                adjustExpansionLevel(0);
                return;
            case EXPANDED:
                adjustExpansionLevel(1f);
                return;
        }
    }

    @Override
    public void init(final float cardWidth, final float cardHeight, final float maxAvailableWidth, final float baseXPosition, final float baseYPosition) {
        mCardWidth = cardWidth;
        mCardHeight = cardHeight;
        mMaxAvailableWidth = maxAvailableWidth;
        mBaseXPosition = baseXPosition;
        mBaseYPosition = baseYPosition;
        mDefaultYDeltaBetweenRows = mCardHeight / 4;
        mYDeltaBetweenRows = mDefaultYDeltaBetweenRows;
    }

    private void recalculateSlotsData() {

        mLinesOfSlots.clear();

        //the step will change according to logic
        final int step = calculateStep();
        int i = 0;
        CardsLayoutStrategy strategy;
        while (i < mActiveSlotsAmount) {
            final int start = i;
            final int end = Math.min(i + step, mActiveSlotsAmount);

            //strategy will change depending on amount of cards in line
            final int cardsInLine = end - start;
            final boolean isLineStrategy = cardsInLine == 2;
            if (isLineStrategy)
                strategy = mLineStrategy;
            else
                strategy = mFanStrategy;

            strategy.init(mBaseXPosition, mYPosition, mMaxAvailableWidth, mCardWidth, mCardHeight);
            final List<CardsLayouterSlotImpl> subList = mSlots.subList(start, end);
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
            final List<CardsLayouterSlotImpl> currentLine = getLinesOfSlots().get(i);
            for (final CardsLayouterSlotImpl slot : currentLine) {
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
    public CardsLayoutSlot getSlotAtPosition(final int position) {
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
