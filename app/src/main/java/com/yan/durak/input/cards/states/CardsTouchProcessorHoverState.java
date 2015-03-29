package com.yan.durak.input.cards.states;

import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.cards.CardsTouchProcessorState;
import com.yan.durak.nodes.CardNode;

import glengine.yan.glengine.EngineWrapper;
import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.util.geometry.YANVector2;


/**
 * Created by Yan-Home on 11/21/2014.
 */
public class CardsTouchProcessorHoverState extends CardsTouchProcessorState {

    private static final float NOT_HOVERED_CARDS_OPACITY = 0.2f;
    private static final float HOVERED_ALPHA_ANIMATION_DURATION = 0.05f;
    private static final float NOT_HOVERED_ALPHA_ANIMATION_DURATION = 0.07f;
    public static final float Y_OFFSET_MULTIPLIER = 0.6f;
    CardNode mHoveredCard;
    CardNode mPoppedUpCard;

    public CardsTouchProcessorHoverState(CardsTouchProcessor cardsTouchProcessor) {
        super(cardsTouchProcessor);
    }

    @Override
    public void applyState() {
        //nothing to change
    }

    @Override
    public boolean onTouchUp(float normalizedX, float normalizedY) {

        float yOffset = (mCardsTouchProcessor.getOriginalCardSize().getY() * Y_OFFSET_MULTIPLIER);

        //return previously hovered back card in place
        if (mPoppedUpCard != null) {
//            mPoppedUpCard.setPosition(mPoppedUpCard.getPosition().getX() ,mPoppedUpCard.getPosition().getY() + yOffset);
//            mCardsTouchProcessor.getCardsTweenAnimator().animateCardToY(mHoveredCard, mHoveredCard.getPosition().getY() + yOffset, HOVERED_ALPHA_ANIMATION_DURATION, null);
            mPoppedUpCard = null;
        }

        //move to selected state
        CardsTouchProcessorSelectedState selectedState = new CardsTouchProcessorSelectedState(mCardsTouchProcessor);
        selectedState.setSelectedCard(mHoveredCard);
        selectedState.setInitialYOffset(yOffset);
        mCardsTouchProcessor.setCardsTouchProcessorState(selectedState);
        return true;
    }

    @Override
    public boolean onTouchDrag(float normalizedX, float normalizedY) {
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //find touched card under the touch point
        CardNode touchedCard = mCardsTouchProcessor.findTouchedCard(touchToWorldPoint);

        //no card touched ?
        if (touchedCard == null)
            return true;

        //we don't want to waste resources on the same card
        if (mHoveredCard == touchedCard)
            return true;

        //now the hover card is changed
        setHoveredCard(touchedCard);
        return true;
    }

    @Override
    public boolean onTouchDown(float normalizedX, float normalizedY) {
        return false;
    }


    public void setHoveredCard(CardNode hoveredCard) {

//        float yOffset = (mCardsTouchProcessor.getOriginalCardSize().getY() * Y_OFFSET_MULTIPLIER);

        //return previously hovered back card in place
        if (mPoppedUpCard != null) {
//            mPoppedUpCard.setPosition(mPoppedUpCard.getPosition().getX() ,mPoppedUpCard.getPosition().getY() + yOffset);
            mPoppedUpCard = null;
        }

        //assign new hovered card
        mHoveredCard = hoveredCard;

        //popup card new hovered card
//        mHoveredCard.setPosition(hoveredCard.getPosition().getX() ,hoveredCard.getPosition().getY() - yOffset);
//        mCardsTouchProcessor.getCardsTweenAnimator().animateCardToY(hoveredCard, hoveredCard.getPosition().getY() - yOffset, HOVERED_ALPHA_ANIMATION_DURATION, null);
        mPoppedUpCard = mHoveredCard;

        //hide other cards that are not hovered
        for (final YANTexturedNode card : mCardsTouchProcessor.getCardNodesArray()) {
            if (card == mHoveredCard) {
                mCardsTouchProcessor.getCardsTweenAnimator().animateCardToAlpha(card, 1f, HOVERED_ALPHA_ANIMATION_DURATION);
            } else {
                mCardsTouchProcessor.getCardsTweenAnimator().animateCardToAlpha(card, NOT_HOVERED_CARDS_OPACITY, NOT_HOVERED_ALPHA_ANIMATION_DURATION);
            }
        }
    }
}
