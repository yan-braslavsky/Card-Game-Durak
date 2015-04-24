package com.yan.durak.input.cards;

import com.yan.durak.nodes.CardNode;

import glengine.yan.glengine.EngineWrapper;
import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.util.geometry.YANVector2;


/**
 * Created by Yan-Home on 11/21/2014.
 */
class CardsTouchProcessorDragState extends CardsTouchProcessorState {

    private CardNode mDraggedCard;
    private YANVector2 mTouchPositionOffset;

    protected CardsTouchProcessorDragState(CardsTouchProcessor cardsTouchProcessor) {
        super(cardsTouchProcessor);
        mTouchPositionOffset = new YANVector2();
        mDraggedCard = null;
    }

    @Override
    protected void applyState() {
        //Do nothing
        //TODO : layout player pile ?
    }

    @Override
    protected boolean onTouchUp(float normalizedX, float normalizedY) {

        //notify listener
        if (mCardsTouchProcessor.getCardsTouchProcessorListener() != null && mDraggedCard != null) {
            mCardsTouchProcessor.getCardsTouchProcessorListener().onDraggedCardReleased(mDraggedCard);
        }

        //TODO : Pool the state
        CardsTouchProcessorDefaultState defaultState = new CardsTouchProcessorDefaultState(mCardsTouchProcessor);
        mCardsTouchProcessor.setCardsTouchProcessorState(defaultState);
        return true;
    }

    @Override
    protected boolean onTouchDrag(float normalizedX, float normalizedY) {

        //get the world position
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

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
    protected boolean onTouchDown(float normalizedX, float normalizedY) {
        //Not supposed to be reachable
        return false;
    }


    protected void setDraggedCard(CardNode draggedCard) {
        mDraggedCard = draggedCard;
    }

    protected void setTouchPositionOffset(float xOffset, float yOffset) {
        mTouchPositionOffset.setX(xOffset);
        mTouchPositionOffset.setY(yOffset);
    }
}
