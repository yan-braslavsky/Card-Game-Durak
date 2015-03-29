package com.yan.durak.input.cards.states;

import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.cards.CardsTouchProcessorState;
import com.yan.durak.nodes.CardNode;

import glengine.yan.glengine.EngineWrapper;
import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.tasks.YANDelayedTask;
import glengine.yan.glengine.util.geometry.YANVector2;


/**
 * Created by Yan-Home on 11/21/2014.
 */
public class CardsTouchProcessorSelectedState extends CardsTouchProcessorState {

    public static final float SELECTED_CARD_SIZE_SCALE = 1.1f;
    public static final int RETURN_TO_DEFAULT_STATE_DELAY_SECONDS = 2;
    public static final float SELECTION_ANIMATION_DURATION = 0.1f;
    public static final int HUD_SORTING_LAYER = 50;

    private CardNode mSelectedCard;
    private int originalSortingLayer;
    private float mOriginalYPosition;
    private float mInitialYOffset;
    private YANDelayedTask mDelayedTask;
    private long mLastTouchDownForTap;

    public CardsTouchProcessorSelectedState(CardsTouchProcessor cardsTouchProcessor) {
        super(cardsTouchProcessor);
    }

    @Override
    public void applyState() {

        //fix in case card is faded out
        mCardsTouchProcessor.getCardsTweenAnimator().animateCardToAlpha(mSelectedCard, 1f, 0.1f);

        //cache original values
        mOriginalYPosition = mSelectedCard.getPosition().getY();
        originalSortingLayer = mSelectedCard.getSortingLayer();

        //change sorting layer
        mSelectedCard.setSortingLayer(HUD_SORTING_LAYER - 1);

        //make card bigger
//        float yOffset = mSelectedCard.getPosition().getY() * 0.1f;
//        mCardsTouchProcessor.getCardsTweenAnimator().animateSizeAndPositionXY(mSelectedCard,
//                mCardsTouchProcessor.getOriginalCardSize().getX() * SELECTED_CARD_SIZE_SCALE, mCardsTouchProcessor.getOriginalCardSize().getY() * SELECTED_CARD_SIZE_SCALE,
//                mSelectedCard.getPosition().getX(), mSelectedCard.getPosition().getY() - yOffset, SELECTION_ANIMATION_DURATION);

        mDelayedTask = new YANDelayedTask(RETURN_TO_DEFAULT_STATE_DELAY_SECONDS, new YANDelayedTask.YANDelayedTaskListener() {
            @Override
            public void onComplete() {
                returnToDefaultState();
            }
        });

        mDelayedTask.start();

    }

    private void returnToDefaultState() {
        mDelayedTask.stop();
        mSelectedCard.setSortingLayer(originalSortingLayer);

//        mCardsTouchProcessor.getCardsTweenAnimator().animateSizeAndPositionXY(mSelectedCard,
//                mCardsTouchProcessor.getOriginalCardSize().getX(), mCardsTouchProcessor.getOriginalCardSize().getY(),
//                mSelectedCard.getPosition().getX(), mOriginalYPosition + mInitialYOffset,
//                BACK_IN_PLACE_ANIMATION_DURATION);

        CardsTouchProcessorDefaultState defaultState = new CardsTouchProcessorDefaultState(mCardsTouchProcessor);
        mCardsTouchProcessor.setCardsTouchProcessorState(defaultState);
    }

    @Override
    public boolean onTouchUp(float normalizedX, float normalizedY) {

        //if touch down not on the selected card , go back to  default state , else go to drag state
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //find touched card under the touch point
        CardNode touchedCard = mCardsTouchProcessor.findTouchedCard(touchToWorldPoint);
        if (touchedCard == mSelectedCard) {
            if (mCardsTouchProcessor.getCardsTouchProcessorListener() != null) {
                mDelayedTask.stop();
                CardsTouchProcessorDefaultState defaultState = new CardsTouchProcessorDefaultState(mCardsTouchProcessor);
                mCardsTouchProcessor.setCardsTouchProcessorState(defaultState);
                mCardsTouchProcessor.getCardsTouchProcessorListener().onSelectedCardTap(mSelectedCard);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchDrag(float normalizedX, float normalizedY) {

        //we need a small delay between touch down and touch up to identify TAP
        if ((System.currentTimeMillis() - mLastTouchDownForTap) < 65) {
            return false;
        }

        //if touch down not on the selected card , go back to  default state , else go to drag state
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //find touched card under the touch point
        YANTexturedNode touchedCard = mCardsTouchProcessor.findTouchedCard(touchToWorldPoint);
        if (touchedCard == mSelectedCard) {
            // go to drag state
            mDelayedTask.stop();
            CardsTouchProcessorDragState dragState = new CardsTouchProcessorDragState(mCardsTouchProcessor);
            dragState.setDraggedCard(mSelectedCard);
            dragState.setTouchPositionOffset(touchToWorldPoint.getX() - mSelectedCard.getPosition().getX(), touchToWorldPoint.getY() - mSelectedCard.getPosition().getY());
            mCardsTouchProcessor.setCardsTouchProcessorState(dragState);
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchDown(float normalizedX, float normalizedY) {

        //if touch down not on the selected card , go back to  default state , else go to drag state
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //find touched card under the touch point
        YANTexturedNode touchedCard = mCardsTouchProcessor.findTouchedCard(touchToWorldPoint);
        if (touchedCard != mSelectedCard) {
            // we returning to default state
            returnToDefaultState();
            return true;
        } else {
            //start tap counting
            mLastTouchDownForTap = System.currentTimeMillis();
        }

        return false;
    }


    public void setSelectedCard(CardNode selectedCard) {
        mSelectedCard = selectedCard;
    }

    public void setInitialYOffset(float initialYOffset) {
        mInitialYOffset = initialYOffset;
    }

}
