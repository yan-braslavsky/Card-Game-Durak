package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.services.CardNodesManagerService;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by ybra on 20/04/15.
 */
public class DiscardPileLayouter extends BasePileLayouter {

    public static final int END_ROTATION_Z_DEGREES = 300;
    private YANReadOnlyVector2 mPilePositionOnField;

    public DiscardPileLayouter(final CardNodesManagerService mCardNodesManager, final TweenManager mTweenManager, final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);
    }

    @Override
    public void init(final float sceneWidth, final float sceneHeight) {
        mPilePositionOnField = new YANVector2(-(sceneWidth / 2f), sceneHeight / 2f);
    }

    @Override
    public void layout() {

        final Timeline tl = Timeline.createSequence().beginParallel();
        for (final Card card : mBoundpile.getCardsInPile()) {
            final CardNode cardNode = mCardNodesManager.getCardNodeForCard(card);

            //animate card to its place with new transform values
            addAnimationToTimelineForCardNode(tl, cardNode, mPilePositionOnField.getX(), mPilePositionOnField.getY(),
                    END_ROTATION_Z_DEGREES, cardNode.getSize().getX(), cardNode.getSize().getY(), 1f, CARD_MOVEMENT_ANIMATION_DURATION);
        }
        tl.start(mTweenManager);
    }
}
