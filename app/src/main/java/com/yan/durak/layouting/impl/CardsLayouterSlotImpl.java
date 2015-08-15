package com.yan.durak.layouting.impl;

import com.yan.durak.layouting.CardsLayoutSlot;

import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.object_pool.YANIPoolableObject;

/**
 * Created by Yan-Home on 11/8/2014.
 */
public class CardsLayouterSlotImpl implements CardsLayoutSlot, YANIPoolableObject {

    private YANVector2 mPosition;
    private float mRotation;
    private int mSortingLayer;

    //used internally for reordering after positioning
    private int mSortingIndex;

    public int getSortingIndex() {
        return mSortingIndex;
    }

    public void setSortingIndex(final int sortingIndex) {
        mSortingIndex = sortingIndex;
    }

    public CardsLayouterSlotImpl() {
        mPosition = new YANVector2();
    }

    @Override
    public YANVector2 getPosition() {
        return mPosition;
    }

    public void setPosition(final float x, final float y) {
        mPosition.setX(x);
        mPosition.setY(y);
    }

    public void setRotation(final float rotation) {
        mRotation = rotation;
    }

    @Override
    public float getRotation() {
        return mRotation;
    }

    @Override
    public int getSortingLayer() {
        return mSortingLayer;
    }

    public void setSortingLayer(final int sortingLayer) {
        mSortingLayer = sortingLayer;
    }

    @Override
    public void resetState() {
        mPosition.setXY(0, 0);
        mRotation = 0;
        mSortingLayer = 0;
        mSortingIndex = 0;
    }
}
