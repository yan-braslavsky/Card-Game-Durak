package com.yan.durak.layouting.impl;

import com.yan.durak.layouting.CardsLayoutSlot;

import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by Yan-Home on 11/8/2014.
 */
public class CardsLayouterSlotImpl implements CardsLayoutSlot {

    private YANVector2 mPosition;
    private float mRotation;
    private int mSortingLayer;

    public int getSortingIndex() {
        return mSortingIndex;
    }

    public void setSortingIndex(int sortingIndex) {
        mSortingIndex = sortingIndex;
    }

    //used internaly for reordering after positioning
    private int mSortingIndex;

    public CardsLayouterSlotImpl() {
        mPosition = new YANVector2();
    }

    @Override
    public YANVector2 getPosition() {
        return mPosition;
    }

    public void setPosition(float x, float y) {
        mPosition.setX(x);
        mPosition.setY(y);
    }

    public void setRotation(float rotation) {
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

    public void setSortingLayer(int sortingLayer) {
        mSortingLayer = sortingLayer;
    }
}
