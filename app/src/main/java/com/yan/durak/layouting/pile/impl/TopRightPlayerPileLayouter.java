package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.CardsLayoutSlot;
import com.yan.durak.layouting.impl.CardsLayouterSlotImpl;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.layouting.threepoint.ThreePointFanLayouter;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by ybra on 20/04/15.
 */
public class TopRightPlayerPileLayouter extends BasePileLayouter {

    private ThreePointFanLayouter mThreePointFanLayouterTopRightPlayer;
    private float mCardWidhtForPile;
    private float mCardHeightForPile;
    private final List<CardsLayouterSlotImpl> mSlotsList;

    public TopRightPlayerPileLayouter(final CardNodesManager mCardNodesManager, final TweenManager mTweenManager, final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);

        //init 3 points layouter to create a fan of opponents hands
        this.mThreePointFanLayouterTopRightPlayer = new ThreePointFanLayouter(2);
        this.mSlotsList = new ArrayList<>();
    }

    @Override
    public void init(float sceneWidth, float sceneHeight) {

        //layout avatars
        float offsetX = sceneWidth * 0.01f;
        float topOffset = sceneHeight * 0.07f;

        //TODO : calculate the position relatively without the avatar
        float aspectRatio = 171f / 141f;
        float avatarWidth = sceneWidth * 0.2f;
        float avatarHeight = avatarWidth / aspectRatio;
        YANVector2 avatarSize = new YANVector2(avatarWidth, avatarHeight);

        //setup 3 points for player at right top
        float fanDistance = sceneWidth * 0.05f;

        YANVector2 pos = new YANVector2(sceneWidth - offsetX, topOffset);
        YANVector2 origin = new YANVector2(pos.getX() - avatarSize.getX(), pos.getY());
        YANVector2 leftBasis = new YANVector2(origin.getX(), origin.getY() + fanDistance);
        YANVector2 rightBasis = new YANVector2(origin.getX() - fanDistance, origin.getY());
        mThreePointFanLayouterTopRightPlayer.setThreePoints(origin, leftBasis, rightBasis);

        this.mCardWidhtForPile = mCardNodesManager.getCardNodeOriginalWidth() * OPPONENT_PILE_SIZE_SCALE;
        this.mCardHeightForPile = mCardNodesManager.getCardNodeOriginalHeight() * OPPONENT_PILE_SIZE_SCALE;

    }

    @Override
    public void layout() {

        mSlotsList.clear();
        for (Card card : mBoundpile.getCardsInPile()) {
            //TODO : must have optimisation , no allocation should be that often
            mSlotsList.add(new CardsLayouterSlotImpl());
        }

        //layout the slots
        mThreePointFanLayouterTopRightPlayer.layoutRowOfSlots(mSlotsList);

        int slotPosition = 0;
        CardsLayouterSlotImpl slot;
        CardNode cardNode;

        for (Card card : mBoundpile.getCardsInPile()) {
            cardNode = mCardNodesManager.getCardNodeForCard(card);
            slot = mSlotsList.get(slotPosition);

                        //important to update sorting layer
            cardNode.setSortingLayer(slot.getSortingLayer());

            //as it is the pile of opponent , we don't want cards to be visible
            cardNode.useBackTextureRegion();

            //animate card to its place with new transform values
            animateCardNode(cardNode, slot.getPosition().getX(), slot.getPosition().getY(),
                    slot.getRotation(), mCardWidhtForPile, mCardHeightForPile, 1f);

            slotPosition++;
        }
    }
}
