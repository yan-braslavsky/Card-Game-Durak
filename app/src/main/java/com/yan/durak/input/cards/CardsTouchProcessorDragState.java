package com.yan.durak.input.cards;

import com.yan.durak.nodes.CardNode;

import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.object_pool.YANObjectPool;


/**
 * Created by Yan-Home on 11/21/2014.
 */
public class CardsTouchProcessorDragState extends CardsTouchProcessorState {

    private CardNode mDraggedCard;
    private YANVector2 mTouchPositionOffset;
    private long touchTime;

    public CardsTouchProcessorDragState() {
        super();
        mTouchPositionOffset = new YANVector2();
    }

    @Override
    public void resetState() {
        super.resetState();
        mDraggedCard = null;
        mTouchPositionOffset.setXY(0, 0);
        touchTime = 0;
    }


    @Override
    protected boolean onTouchUp(final float normalizedX, final float normalizedY) {

        //if there was a short time between touch down and touch up then it is a tap
        if ((System.currentTimeMillis() - touchTime) < 70) {
            //notify listener of tap
            if (mCardsTouchProcessor.getCardsTouchProcessorListener() != null && mDraggedCard != null) {
                mCardsTouchProcessor.getCardsTouchProcessorListener().onSelectedCardTap(mDraggedCard);
            }
        } else {
            //notify listener of drag release
            if (mCardsTouchProcessor.getCardsTouchProcessorListener() != null && mDraggedCard != null) {
                mCardsTouchProcessor.getCardsTouchProcessorListener().onDraggedCardReleased(mDraggedCard);
            }
        }

        final CardsTouchProcessorDefaultState defaultState = YANObjectPool.getInstance().obtain(CardsTouchProcessorDefaultState.class);
        defaultState.setCardsTouchProcessor(mCardsTouchProcessor);
        mCardsTouchProcessor.setCardsTouchProcessorState(defaultState);
        return true;
    }

    @Override
    protected boolean onTouchDrag(final float normalizedX, final float normalizedY) {

        //get the world position
        final YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                mScreenSize.getX(), mScreenSize.getY());

        //change position of dragged card
        if (mDraggedCard != null) {
            mDraggedCard.setPosition(touchToWorldPoint.getX() - mTouchPositionOffset.getX(), touchToWorldPoint.getY() - mTouchPositionOffset.getY());
        }

        //notify listener
        if (mCardsTouchProcessor.getCardsTouchProcessorListener() != null) {
            mCardsTouchProcessor.getCardsTouchProcessorListener().onCardDragProgress(mDraggedCard);
        }

        return true;
    }

    @Override
    protected boolean onTouchDown(final float normalizedX, final float normalizedY) {
        //Not supposed to be reachable
        return false;
    }


    protected void setDraggedCard(final CardNode draggedCard) {
        mDraggedCard = draggedCard;
    }

    protected void setTouchPositionOffset(final float xOffset, final float yOffset) {
        mTouchPositionOffset.setX(xOffset);
        mTouchPositionOffset.setY(yOffset);
    }


    public void setTouchTime(final long touchTime) {
        this.touchTime = touchTime;
    }
}
