package com.yan.durak.input.cards.states;

import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.cards.CardsTouchProcessorState;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.screen_fragments.CardsScreenFragment;

import glengine.yan.glengine.EngineWrapper;
import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.util.geometry.YANVector2;


/**
 * Created by Yan-Home on 11/21/2014.
 */
public class CardsTouchProcessorDragState extends CardsTouchProcessorState {

    private CardNode mDraggedCard;
    private YANVector2 mTouchPositionOffset;

    public CardsTouchProcessorDragState(CardsTouchProcessor cardsTouchProcessor) {
        super(cardsTouchProcessor);
        mTouchPositionOffset = new YANVector2();
    }

    @Override
    public void applyState() {
        //make card smaller
        float destintationWidth = mDraggedCard.getSize().getX();
        float destintationHeigth = mDraggedCard.getSize().getY();
        destintationWidth *= CardsScreenFragment.CARDS_ON_FIELD_SIZE_MULTIPLIER;
        destintationHeigth *= CardsScreenFragment.CARDS_ON_FIELD_SIZE_MULTIPLIER;
        mCardsTouchProcessor.getCardsTweenAnimator().animateSize(mDraggedCard, destintationWidth, destintationHeigth, 0.5f);
    }

    @Override
    public boolean onTouchUp(float normalizedX, float normalizedY) {

        //find touched card under the touch point
        if (mCardsTouchProcessor.getCardsTouchProcessorListener() != null) {
            mCardsTouchProcessor.getCardsTouchProcessorListener().onDraggedCardReleased(mDraggedCard);
        }

        CardsTouchProcessorDefaultState defaultState = new CardsTouchProcessorDefaultState(mCardsTouchProcessor);
        mCardsTouchProcessor.setCardsTouchProcessorState(defaultState);
        return true;
    }

    @Override
    public boolean onTouchDrag(float normalizedX, float normalizedY) {
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //change position of dragged card
        mDraggedCard.setPosition(touchToWorldPoint.getX() - mTouchPositionOffset.getX(), touchToWorldPoint.getY() - mTouchPositionOffset.getY());
        return false;
    }

    @Override
    public boolean onTouchDown(float normalizedX, float normalizedY) {
        return false;
    }


    public void setDraggedCard(CardNode draggedCard) {
        mDraggedCard = draggedCard;
    }

    public void setTouchPositionOffset(float xOffset , float yOffset) {
        mTouchPositionOffset.setX(xOffset);
        mTouchPositionOffset.setY(yOffset);
    }
}
