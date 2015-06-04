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

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.math.YANMathUtils;

/**
 * Created by ybra on 20/04/15.
 */
public class FieldPileLayouter extends BasePileLayouter {


    public static final float FIELD_PILE_SIZE_SCALE = 0.7f;

    /**
     * When card put on field they have a sligh rotation to the
     * left or to the right
     */
    private static final float FIELD_CARDS_ROTATION_ANGLE = 7f;
    private static final float TOP_CARD_OFFSET_AFTER_ROTATION_X = 0.03f;
    private static final float TOP_CARD_OFFSET_AFTER_ROTATION_Y = 0.05f;

    private float mCardWidhtForPile;
    private float mCardHeightForPile;
    private FieldPilePositioner mFieldPilePositioner;
    private YANVector2 mCardOriginVector;
    private YANVector2 mCardPositionVector;

    public FieldPileLayouter(final CardNodesManagerService mCardNodesManager, final TweenManager mTweenManager, final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);
        mFieldPilePositioner = new FieldPilePositioner();
        mCardOriginVector = new YANVector2();
        mCardPositionVector = new YANVector2();
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

        //update position of the first card
        mCardPositionVector.setXY(pilePositionOnField.getX(), pilePositionOnField.getY());

        //set rotation origin that card will be rotated around
        mCardOriginVector.setXY(pilePositionOnField.getX() - mCardWidhtForPile, pilePositionOnField.getY() + mCardHeightForPile);

        int index = 0;
        final Timeline tl = Timeline.createSequence().beginParallel();
        for (Card card : mBoundpile.getCardsInPile()) {
            CardNode cardNode = mCardNodesManager.getCardNodeForCard(card);

            //field pile cards visible to all
            cardNode.useFrontTextureRegion();

            //rotation and sorting layer will change depending on position of the card in field pile
            float rotationZ;
            int sortingLayer;

            //when the card is first , means it at the bottom
            if (index == 0) {
                rotationZ = -(FIELD_CARDS_ROTATION_ANGLE);

                //we are rotating card around bottom left corner
                YANMathUtils.rotatePointAroundOrigin(mCardPositionVector, mCardOriginVector, rotationZ);
                sortingLayer = 1;
            } else {
                rotationZ = FIELD_CARDS_ROTATION_ANGLE;

                //we are rotating card around bottom left corner
                YANMathUtils.rotatePointAroundOrigin(mCardPositionVector, mCardOriginVector, rotationZ);

                //the top card should be slightly offset
                mCardPositionVector.setXY(mCardPositionVector.getX() + (mCardWidhtForPile * TOP_CARD_OFFSET_AFTER_ROTATION_X),
                        mCardPositionVector.getY() - (mCardHeightForPile * TOP_CARD_OFFSET_AFTER_ROTATION_Y));
                sortingLayer = 2;
            }

            //adjust sorting layer
            cardNode.setSortingLayer(sortingLayer);

            //animate card to its place with new transform values
            addAnimationToTimelineForCardNode(tl,cardNode, mCardPositionVector.getX(), mCardPositionVector.getY(),
                    rotationZ, mCardWidhtForPile, mCardHeightForPile, 1f, CARD_MOVEMENT_ANIMATION_DURATION);

            index++;
        }

        tl.start(mTweenManager);
    }
}
