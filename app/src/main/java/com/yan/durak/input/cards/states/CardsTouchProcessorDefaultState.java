package com.yan.durak.input.cards.states;

import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.cards.CardsTouchProcessorState;
import com.yan.durak.nodes.CardNode;

import glengine.yan.glengine.EngineWrapper;
import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.util.geometry.YANVector2;


/**
 * Created by Yan-Home on 11/21/2014.
 */
public class CardsTouchProcessorDefaultState extends CardsTouchProcessorState {


    public CardsTouchProcessorDefaultState(CardsTouchProcessor cardsTouchProcessor) {
        super(cardsTouchProcessor);
    }

    @Override
    public void applyState() {

        //TODO : fix
//        //Make all the cards at their regular alpha , position and size
//        for (CardNode card : mCardsTouchProcessor.getCardNodesArray()) {
//            mCardsTouchProcessor.getCardsTweenAnimator().animateCardToAlpha(card, 1f, BACK_IN_PLACE_ANIMATION_DURATION);
//
//            if (!card.containsTag(CardNode.TAG_SHOULD_NOT_RESIZE)) {
//                mCardsTouchProcessor.getCardsTweenAnimator().animateSize(card, mCardsTouchProcessor.getOriginalCardSize().getX(), mCardsTouchProcessor.getOriginalCardSize().getY(), 0.5f);
//            }
//        }
    }

    @Override
    public boolean onTouchUp(float normalizedX, float normalizedY) {
        return false;
    }

    @Override
    public boolean onTouchDrag(float normalizedX, float normalizedY) {
        return false;
    }

    @Override
    public boolean onTouchDown(float normalizedX, float normalizedY) {
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //find touched card under the touch point
        CardNode touchedCard = mCardsTouchProcessor.findTouchedCard(touchToWorldPoint);
        if (touchedCard == null)
            return false;

        //move to hover state
        CardsTouchProcessorHoverState hoverState = new CardsTouchProcessorHoverState(mCardsTouchProcessor);
        hoverState.setHoveredCard(touchedCard);
        mCardsTouchProcessor.setCardsTouchProcessorState(hoverState);
        return true;

    }


}
