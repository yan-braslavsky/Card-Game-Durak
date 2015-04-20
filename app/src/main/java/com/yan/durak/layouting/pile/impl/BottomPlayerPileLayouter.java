package com.yan.durak.layouting.pile.impl;

import com.yan.durak.layouting.impl.PlayerCardsLayouter;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.ICardNodesManager;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class BottomPlayerPileLayouter extends BasePileLayouter {


    private final PlayerCardsLayouter mPlayerCardsLayouter;

    public BottomPlayerPileLayouter(ICardNodesManager mCardNodesManager, TweenManager mTweenManager) {
        super(mCardNodesManager, mTweenManager);

        //init player cards layouter
        mPlayerCardsLayouter = new PlayerCardsLayouter(mCardNodesManager.getTotalCardsAmount());
    }

    @Override
    public void layout() {
        //TODO : Every pile layouter will make sure to change size of the card
    }
}
