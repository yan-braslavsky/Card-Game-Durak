package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.impl.PlayerCardsLayouter;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.models.PileModel;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class BottomPlayerPileLayouter extends BasePileLayouter {


    private final PlayerCardsLayouter mPlayerCardsLayouter;

    public BottomPlayerPileLayouter(final CardNodesManager mCardNodesManager, final TweenManager mTweenManager, final PileModel boundPile) {
        super(mCardNodesManager, mTweenManager, boundPile);

        //init player cards layouter , assuming the entire deck can be in his hands
        mPlayerCardsLayouter = new PlayerCardsLayouter(mCardNodesManager.getAllCardNodes().size());
    }


    /**
     * Initializes positions and all needed values for layouting
     */
    @Override
    public void init(float sceneWidth, float sceneHeight) {

        //init the player cards layouter
        mPlayerCardsLayouter.init(mCardNodesManager.getCardNodeWidth(), mCardNodesManager.getCardNodeHeight(),
                //maximum available width
                sceneWidth,
                //base x position ( center )
                sceneWidth / 2,
                //base y position
                sceneHeight * 0.9f);
    }


    @Override
    public void layout() {
        //TODO : Every pile layouter will make sure to change size of the card
    }
}
