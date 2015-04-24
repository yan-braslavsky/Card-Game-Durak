package com.yan.durak.input.cards;

import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;

import glengine.yan.glengine.input.YANInputManager;

/**
 * Created by Yan-Home on 11/21/2014.
 * <p/>
 * PURPOSE :
 * Responsible for user touch interaction with the cards.
 */
public class CardsTouchProcessor {

    private final CardNodesManager mCardNodesManager;
    private final PileModel mPlayerPile;

    public interface CardsTouchProcessorListener {

        /**
         * Called when card was tapped
         */
        void onSelectedCardTap(CardNode card);

        /**
         * Called when dragged card was released
         *
         * @param card
         */
        void onDraggedCardReleased(CardNode card);

        /**
         * Called multiple times every time dragged card is changing position
         *
         * @param card
         */
        void onCardDragProgress(CardNode card);
    }

    private final YANInputManager.TouchListener mTouchListener;
    private CardsTouchProcessorState mCardsTouchProcessorState;
    private CardsTouchProcessorListener mCardsTouchProcessorListener;

    public CardsTouchProcessor(CardsTouchProcessorListener cardsTouchProcessorListener,final CardNodesManager cardNodesManager, final PileModel playerPile) {

        mCardsTouchProcessorListener = cardsTouchProcessorListener;
        mCardNodesManager = cardNodesManager;
        mPlayerPile = playerPile;

        //TODO: Pool the states
        //starting from a default state
        setCardsTouchProcessorState(new CardsTouchProcessorDefaultState(this));

        //touch listener that is added to input processor
        mTouchListener = new YANInputManager.TouchListener() {
            @Override
            public boolean onTouchDown(float normalizedX, float normalizedY) {
                return mCardsTouchProcessorState.onTouchDown(normalizedX, normalizedY);
            }

            @Override
            public boolean onTouchUp(float normalizedX, float normalizedY) {
                return mCardsTouchProcessorState.onTouchUp(normalizedX, normalizedY);
            }

            @Override
            public boolean onTouchDrag(float normalizedX, float normalizedY) {
                return mCardsTouchProcessorState.onTouchDrag(normalizedX, normalizedY);
            }

            @Override
            public int getSortingLayer() {
                //It does not matter what we returning here , since we managing touch processing ourselves
                return 0;
            }
        };

    }

    /**
     * Making the touch processor active.It starts listen to touch events
     * and process it on cards.
     */
    public void register() {
        YANInputManager.getInstance().addEventListener(mTouchListener);
    }

    /**
     * Makes touch processor not active.It no longer processes touch events on cards.
     */
    public void unRegister() {
        YANInputManager.getInstance().removeEventListener(mTouchListener);
    }


    protected void setCardsTouchProcessorState(CardsTouchProcessorState cardsTouchProcessorState) {
        mCardsTouchProcessorState = cardsTouchProcessorState;
        mCardsTouchProcessorState.applyState();
    }

    protected CardsTouchProcessorListener getCardsTouchProcessorListener() {
        return mCardsTouchProcessorListener;
    }

    protected PileModel getPlayerPile() {
        return mPlayerPile;
    }

    protected CardNodesManager getCardNodesManager() {
        return mCardNodesManager;
    }
}
