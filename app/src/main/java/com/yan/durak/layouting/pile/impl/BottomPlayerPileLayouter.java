package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.impl.PlayerCardsLayouter;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.CardNodesManager;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class BottomPlayerPileLayouter extends BasePileLayouter {


    private final PlayerCardsLayouter mPlayerCardsLayouter;

    public BottomPlayerPileLayouter(CardNodesManager mCardNodesManager, TweenManager mTweenManager) {
        super(mCardNodesManager, mTweenManager);

        //init player cards layouter
        mPlayerCardsLayouter = new PlayerCardsLayouter(mCardNodesManager.getTotalCardsAmount());
    }


    /**
     * Initializes positions and all needed values for layouting
     */
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