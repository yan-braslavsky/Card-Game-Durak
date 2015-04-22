package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.CardsLayoutSlot;
import com.yan.durak.layouting.impl.PlayerCardsLayouter;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.managers.PileManager;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;

/**
 * Created by ybra on 20/04/15.
 */
public class BottomPlayerPileLayouter extends BasePileLayouter {


    private final PlayerCardsLayouter mPlayerCardsLayouter;

    public BottomPlayerPileLayouter(final PileManager pileManager, final CardNodesManager cardNodesManager, final TweenManager tweenManager, final PileModel boundPile) {
        super(cardNodesManager, tweenManager, boundPile);

        //init player cards layouter , assuming the entire deck can be in his hands
        mPlayerCardsLayouter = new PlayerCardsLayouter(pileManager.getAllCards().size());
    }


    /**
     * Initializes positions and all needed values for layouting
     */
    @Override
    public void init(float sceneWidth, float sceneHeight) {

        //init the player cards layouter
        mPlayerCardsLayouter.init(mCardNodesManager.getCardNodeOriginalWidth(), mCardNodesManager.getCardNodeOriginalHeight(),
                //maximum available width
                sceneWidth,
                //base x position ( center )
                sceneWidth / 2,
                //base y position
                sceneHeight * 0.9f);
    }


    @Override
    public void layout() {

        //update layouter to recalculate positions
        int cardsInPileAmount = getBoundpile().getCardsInPile().size();
        mPlayerCardsLayouter.setActiveSlotsAmount(cardsInPileAmount);

        CardsLayoutSlot slot;
        int slotPosition = 0;

        for (Card card : mBoundpile.getCardsInPile()) {
            CardNode cardNode = mCardNodesManager.getCardNodeForCard(card);
            slot = mPlayerCardsLayouter.getSlotAtPosition(slotPosition);

            //important to update sorting layer
            cardNode.setSortingLayer(slot.getSortingLayer());

            //as it is the pile of the active player we want all cards to be visible
            cardNode.useFrontTextureRegion();

            //animate card to its place with new transform values
            animateCardNode(cardNode, slot.getPosition().getX(), slot.getPosition().getY(),
                    slot.getRotation(), mCardNodesManager.getCardNodeOriginalWidth(), mCardNodesManager.getCardNodeOriginalHeight(), 1f);

            slotPosition++;
        }


    }

    private void animateCardNode(CardNode cardNode, float endPositionX, float endPositionY, float endRotationZ, float endWidth, float endHeight, float endAlpha) {
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(cardNode, YANTweenNodeAccessor.OPACITY, CARD_MOVEMENT_ANIMATION_DURATION).target(endAlpha))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.ROTATION_Z_CW, CARD_MOVEMENT_ANIMATION_DURATION).target(endRotationZ))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.POSITION_X, CARD_MOVEMENT_ANIMATION_DURATION).target(endPositionX))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.POSITION_Y, CARD_MOVEMENT_ANIMATION_DURATION).target(endPositionY))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.SIZE_X, CARD_MOVEMENT_ANIMATION_DURATION).target(endWidth))
                .push(Tween.to(cardNode, YANTweenNodeAccessor.SIZE_Y, CARD_MOVEMENT_ANIMATION_DURATION).target(endHeight))
                .start(mTweenManager);
    }
}
