package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by ybra on 20/04/15.
 */
public class DiscardPileLayouter extends BasePileLayouter {

    private YANReadOnlyVector2 mPilePositionOnField;

    public DiscardPileLayouter(final CardNodesManager mCardNodesManager, final TweenManager mTweenManager, final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);
    }

    @Override
    public void init(float sceneWidth, float sceneHeight) {
        mPilePositionOnField = new YANVector2(-(sceneWidth / 2f), sceneHeight / 2f);
    }

    @Override
    public void layout() {

        for (Card card : mBoundpile.getCardsInPile()) {
            CardNode cardNode = mCardNodesManager.getCardNodeForCard(card);

            //animate card to its place with new transform values
            animateCardNode(cardNode, mPilePositionOnField.getX(), mPilePositionOnField.getY(),
                    300, cardNode.getSize().getX(), cardNode.getSize().getY(), 1f);
        }
    }
}
