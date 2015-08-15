package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.impl.CardsLayouterSlotImpl;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.layouting.threepoint.ThreePointFanLayouter;
import com.yan.durak.services.CardNodesManagerService;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 20/04/15.
 */
public class TopLeftPlayerPileLayouter extends BasePileLayouter {

    private ThreePointFanLayouter mThreePointFanLayouterTopLeft;
    private float mCardWidhtForPile;
    private float mCardHeightForPile;
    private final List<CardsLayouterSlotImpl> mSlotsList;

    public TopLeftPlayerPileLayouter(final CardNodesManagerService mCardNodesManager, final TweenManager mTweenManager, final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);

        //init 3 points layouter to create a fan of opponents hands
        mThreePointFanLayouterTopLeft = new ThreePointFanLayouter(2);
        this.mSlotsList = new ArrayList<>();
    }

    @Override
    public void init(final float sceneWidth, final float sceneHeight) {

        //offset from the beginning of the screen left
        final float offsetX = sceneWidth * 0.02f;
        final float topOffset = sceneHeight * 0.09f;
        final float fanDistance = sceneWidth * 0.1f;

        final YANVector2 pos = new YANVector2(offsetX, topOffset);
        final YANVector2 origin = new YANVector2(pos.getX() * 4, pos.getY());
        final YANVector2 leftBasis = new YANVector2(origin.getX() + fanDistance, origin.getY());
        final YANVector2 rightBasis = new YANVector2(origin.getX(), origin.getY() + fanDistance);

        //set three points
        mThreePointFanLayouterTopLeft.setThreePoints(origin, leftBasis, rightBasis);

        //swap direction
        mThreePointFanLayouterTopLeft.setDirection(ThreePointFanLayouter.LayoutDirection.RTL);

        this.mCardWidhtForPile = mCardNodesManager.getCardNodeOriginalWidth() * OPPONENT_PILE_SIZE_SCALE;
        this.mCardHeightForPile = mCardNodesManager.getCardNodeOriginalHeight() * OPPONENT_PILE_SIZE_SCALE;

        //preallocate slots
        YANObjectPool.getInstance().preallocate(CardsLayouterSlotImpl.class, 10);
    }

    @Override
    public void layout() {

        //offer to pool everything that was in list
        for (final CardsLayouterSlotImpl cardsLayouterSlot : mSlotsList) {
            YANObjectPool.getInstance().offer(cardsLayouterSlot);
        }

        mSlotsList.clear();
        for (final Card card : mBoundpile.getCardsInPile()) {
            mSlotsList.add(YANObjectPool.getInstance().obtain(CardsLayouterSlotImpl.class));
        }

        //layout the slots
        mThreePointFanLayouterTopLeft.layoutRowOfSlots(mSlotsList);

        int slotPosition = 0;
        CardsLayouterSlotImpl slot;
        CardNode cardNode;
        final Timeline tl = Timeline.createSequence().beginParallel();
        for (final Card card : mBoundpile.getCardsInPile()) {
            cardNode = mCardNodesManager.getCardNodeForCard(card);
            slot = mSlotsList.get(slotPosition);

            //important to update sorting layer
            cardNode.setSortingLayer(slot.getSortingLayer());

            //as it is the pile of opponent , we don't want cards to be visible
            cardNode.useBackTextureRegion();

            //animate card to its place with new transform values
            addAnimationToTimelineForCardNode(tl,cardNode, slot.getPosition().getX(), slot.getPosition().getY(),
                    slot.getRotation(), mCardWidhtForPile, mCardHeightForPile, 1f,CARD_MOVEMENT_ANIMATION_DURATION);

            slotPosition++;
        }
        tl.start(mTweenManager);
    }
}
