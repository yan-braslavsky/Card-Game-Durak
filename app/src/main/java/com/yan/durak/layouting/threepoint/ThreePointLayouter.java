package com.yan.durak.layouting.threepoint;

import com.yan.durak.layouting.impl.CardsLayouterSlotImpl;
import com.yan.glengine.util.geometry.YANVector2;

import java.util.List;

/**
 * Created by Yan-Home on 12/28/2014.
 * <p/>
 * This layouter take 3 points as a reference to create a layout
 */
public interface ThreePointLayouter {

    public enum LayoutDirection {
        LTR, RTL;
    }

    /**
     * Set the 3 points to define the layout
     *
     * @param originPoint origin of the triangle that defines the layout
     * @param leftBasis   left basis vertex of the triangle that defines the layout
     * @param rightBasis  right basis vertex of the triangle that defines the layout
     */
    void setThreePoints(YANVector2 originPoint, YANVector2 leftBasis, YANVector2 rightBasis);

    /**
     * Takes the slots list and layouts them in a row
     */
    void layoutRowOfSlots(List<CardsLayouterSlotImpl> slots);

    /**
     * Direction defines the order of laying out.
     * from right to left or from left to right
     */
    void setDirection(LayoutDirection direction);
}
