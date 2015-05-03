package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.layouting.pile.FieldPilePositioner;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.service.services.PileLayouterManagerService;
import com.yan.durak.service.services.PileManagerService;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

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
    //    private YANReadOnlyVector2 mPilePositionOnField;
    private FieldPilePositioner mFieldPilePositioner;

    public FieldPileLayouter(final CardNodesManagerService mCardNodesManager, final TweenManager mTweenManager, final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);
        mFieldPilePositioner = new FieldPilePositioner();
    }

    @Override
    public void init(float sceneWidth, float sceneHeight) {
        this.mCardWidhtForPile = mCardNodesManager.getCardNodeOriginalWidth() * FIELD_PILE_SIZE_SCALE;
        this.mCardHeightForPile = mCardNodesManager.getCardNodeOriginalHeight() * FIELD_PILE_SIZE_SCALE;
        mFieldPilePositioner.init(sceneWidth, sceneHeight, mCardWidhtForPile, mCardHeightForPile);
    }

    @Override
    public void layout() {

        //before we layouting this pile , we will layout previous piles on field recursively
        if (mBoundpile.getPileIndex() > PileManagerService.FIRST_FIELD_PILE_INDEX) {
            PileModel previousFieldPile = ServiceLocator.locateService(PileManagerService.class).getPileWithIndex(mBoundpile.getPileIndex() - 1);
            ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(previousFieldPile).layout();
        }

        //get position for current pile
        YANReadOnlyVector2 pilePositionOnField = mFieldPilePositioner.getPositionForPile(mBoundpile);

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
            animateCardNode(cardNode, pilePositionOnField.getX(), pilePositionOnField.getY(),
                    rotationZ, mCardWidhtForPile, mCardHeightForPile, 1f, CARD_MOVEMENT_ANIMATION_DURATION);

            index++;
        }
    }
}
