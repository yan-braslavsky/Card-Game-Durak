package com.yan.durak.layouting;

import com.yan.durak.layouting.impl.CardsLayouterSlotImpl;

import java.util.List;

/**
 * Created by Yan-Home on 11/8/2014.
 */
public interface CardsLayouter {

    void setActiveSlotsAmount(int amount);

    /**
     * Sets required parameters for layouter to do the calculations
     *
     * @param cardWidth          width of the card on the screen
     * @param cardHeight         height of the card on the screen
     * @param maxAvailableWidth  maximum width that is available for cards to position
     * @param baseXPosition      starting x point fron where cards will be layed out
     * @param baseYPosition      starting y point from the bottom of the screen , where bottom cards line will be.
     */
    void init(float cardWidth, float cardHeight, float maxAvailableWidth, float baseXPosition, float baseYPosition);

    CardsLayoutSlot getSlotAtPosition(int position);

    /**
     * Returns arrays of slots that represent a line.
     * Order is from botom to top. That means array at position 0 is the bottom line ,
     * array at position 1 is a second line , etc..
     */
    List<List<CardsLayouterSlotImpl>> getLinesOfSlots();

}
