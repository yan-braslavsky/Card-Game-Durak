package com.yan.durak.layouting;

import com.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by Yan-Home on 11/8/2014.
 */
public interface CardsLayoutSlot {

    YANReadOnlyVector2 getPosition();
    float getRotation();
    public int getSortingLayer();
}
