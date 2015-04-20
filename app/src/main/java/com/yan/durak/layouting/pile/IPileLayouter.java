package com.yan.durak.layouting.pile;

/**
 * Created by ybra on 20/04/15.
 *
 * PURPOSE :
 * Pile layouter is responsible for repositioning the cards according to attached pile changes.
 */
public interface IPileLayouter {

    /**
     * Layouts the card in the corresponding pile with animation
     */
    void layout();
}
