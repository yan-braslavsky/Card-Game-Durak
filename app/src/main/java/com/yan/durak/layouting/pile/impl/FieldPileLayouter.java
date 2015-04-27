package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by ybra on 20/04/15.
 */
public class FieldPileLayouter extends BasePileLayouter {


    public static final float FIELD_PILE_SIZE_SCALE = 0.7f;

    /**
     * When card put on field they have a sligh rotation to the
     * left or to the right
     */
    private static final float FIELD_CARDS_ROTATION_ANGLE = 13f;

    private static final int MAX_PILES_IN_LINE = 4;

    private float mCardWidhtForPile;
    private float mCardHeightForPile;
    private YANReadOnlyVector2 mPilePositionOnField;

    public FieldPileLayouter(final CardNodesManagerService mCardNodesManager, final TweenManager mTweenManager, final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);

    }

    @Override
    public void init(float sceneWidth, float sceneHeight) {
        this.mCardWidhtForPile = mCardNodesManager.getCardNodeOriginalWidth() * FIELD_PILE_SIZE_SCALE;
        this.mCardHeightForPile = mCardNodesManager.getCardNodeOriginalHeight() * FIELD_PILE_SIZE_SCALE;

        float leftBorderX = mCardWidhtForPile * 0.2f;
        float topBorderY = sceneHeight * 0.3f;

        float xAdvance = mCardWidhtForPile * 1.2f;
        float yAdvance = mCardHeightForPile * 1.2f;

        //offset pile index to 0 - max_piles range
        int pileOrderOnField = mBoundpile.getPileIndex() - PileManagerService.FIRST_FIELD_PILE_INDEX;

        int pileIndexX = pileOrderOnField % (MAX_PILES_IN_LINE - 1);
        int pileIndexY = pileOrderOnField / (MAX_PILES_IN_LINE - 1);

        float currentX = leftBorderX + (xAdvance * pileIndexX);
        float currentY = topBorderY + (yAdvance * pileIndexY);

        mPilePositionOnField = new YANVector2(currentX, currentY);


    }

    @Override
    public void layout() {
        int index = 0;
        for (Card card : mBoundpile.getCardsInPile()) {
            CardNode cardNode = mCardNodesManager.getCardNodeForCard(card);

            //field pile cards visible to all
            cardNode.useFrontTextureRegion();

            //adjust rotation
            float rotationZ = (index == 0) ? (-FIELD_CARDS_ROTATION_ANGLE) : FIELD_CARDS_ROTATION_ANGLE;
            int sortingLayer = (index == 0) ? 1 : 2;
            cardNode.setSortingLayer(sortingLayer);

            //animate card to its place with new transform values
            animateCardNode(cardNode, mPilePositionOnField.getX(), mPilePositionOnField.getY(),
                    rotationZ, mCardWidhtForPile, mCardHeightForPile, 1f,CARD_MOVEMENT_ANIMATION_DURATION);

            index++;
        }
    }
}
