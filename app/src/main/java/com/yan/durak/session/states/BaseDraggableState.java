package com.yan.durak.session.states;

/**
 * Created by Yan-Home on 4/26/2015.
 */
public abstract class BaseDraggableState implements IActivePlayerState {
    private float mDraggedCardDistanceFromPileField;
    private boolean mDragging;

    @Override
    public void resetState() {
        mDraggedCardDistanceFromPileField = 0f;
        mDragging = false;
    }

    public void setDraggedCardDistanceFromPileField(float draggedCardDistanceFromPileField) {
        mDraggedCardDistanceFromPileField = draggedCardDistanceFromPileField;
    }

    public float getDraggingCardDistanceFromPileField() {
        return mDraggedCardDistanceFromPileField;
    }

    public void setDragging(boolean dragging) {
        mDragging = dragging;
    }

    public boolean isDragging() {
        return mDragging;
    }
}