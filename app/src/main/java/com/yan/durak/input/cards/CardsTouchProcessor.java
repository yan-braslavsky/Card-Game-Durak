package com.yan.durak.input.cards;

import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.service.ServiceLocator;

import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by Yan-Home on 11/21/2014.
 * <p/>
 * PURPOSE :
 * Responsible for user touch interaction with the cards.
 */
public class CardsTouchProcessor {

    private final CardNodesManagerService mCardNodesManager;
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

    public CardsTouchProcessor(final CardsTouchProcessorListener cardsTouchProcessorListener, final PileModel playerPile) {

        mCardsTouchProcessorListener = cardsTouchProcessorListener;
        mCardNodesManager = ServiceLocator.locateService(CardNodesManagerService.class);
        mPlayerPile = playerPile;

        //starting from a default state
        CardsTouchProcessorDefaultState defaultState = YANObjectPool.getInstance().obtain(CardsTouchProcessorDefaultState.class);
        defaultState.setCardsTouchProcessor(this);
        setCardsTouchProcessorState(defaultState);

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
        //release previous state to pool
        YANObjectPool.getInstance().offer(cardsTouchProcessorState);
        mCardsTouchProcessorState = cardsTouchProcessorState;
        mCardsTouchProcessorState.applyState();
    }

    protected CardsTouchProcessorListener getCardsTouchProcessorListener() {
        return mCardsTouchProcessorListener;
    }

    protected PileModel getPlayerPile() {
        return mPlayerPile;
    }

    protected CardNodesManagerService getCardNodesManager() {
        return mCardNodesManager;
    }
}
