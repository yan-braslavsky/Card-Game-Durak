package com.yan.durak.managers;

import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.layouting.pile.impl.BottomPlayerPileLayouter;
import com.yan.durak.layouting.pile.impl.DiscardPileLayouter;
import com.yan.durak.layouting.pile.impl.FieldPileLayouter;
import com.yan.durak.layouting.pile.impl.StockPileLayouter;
import com.yan.durak.layouting.pile.impl.TopLeftPlayerPileLayouter;
import com.yan.durak.layouting.pile.impl.TopRightPlayerPileLayouter;
import com.yan.durak.models.IPile;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class PileLayouterManager {

    //managers
    final PileManager mPileManager;

    //layouters
    final BottomPlayerPileLayouter mBottomPlayerPileLayouter;
    final TopLeftPlayerPileLayouter mTopLeftPlayerPileLayouter;
    final TopRightPlayerPileLayouter mTopRightPlayerPileLayouter;
    final StockPileLayouter mStockPileLayouter;
    final DiscardPileLayouter mDiscardPileLayouter;
    final List<FieldPileLayouter> mFieldPileLayouterList;

    public PileLayouterManager(CardNodesManager cardNodesManager, TweenManager tweenManager, PileManager pileManager) {

        this.mPileManager = pileManager;

        //init bottom player layouter
        mBottomPlayerPileLayouter = new BottomPlayerPileLayouter(cardNodesManager, tweenManager, mPileManager.getBottomPlayerPile());

        //init top left player layouter
        mTopLeftPlayerPileLayouter = new TopLeftPlayerPileLayouter(cardNodesManager, tweenManager, mPileManager.getTopLeftPlayerPile());

        //init top right player layouter
        mTopRightPlayerPileLayouter = new TopRightPlayerPileLayouter(cardNodesManager, tweenManager, mPileManager.getTopRightPlayerPile());

        //init stock pile layouter
        mStockPileLayouter = new StockPileLayouter(cardNodesManager, tweenManager, mPileManager.getStockPile());

        //init discard pile layouter
        mDiscardPileLayouter = new DiscardPileLayouter(cardNodesManager, tweenManager, mPileManager.getDiscardPile());

        //init field piles list
        mFieldPileLayouterList = new ArrayList<>(PileManager.MAX_PILES_ON_FIELD);

        //init list of field layouters
        for (int pileIndex = PileManager.FIRST_FIELD_PILE_INDEX; pileIndex < PileManager.MAX_PILE_INDEX; pileIndex++) {
            mFieldPileLayouterList.add(new FieldPileLayouter(cardNodesManager, tweenManager, mPileManager.getPileWithIndex(pileIndex)));
        }
    }


    /**
     * Initializes positions and all needed values for layouting
     */
    public void init(float sceneWidth, float sceneHeight) {

        //init layouters for players
        mBottomPlayerPileLayouter.init(sceneWidth, sceneHeight);
        mTopLeftPlayerPileLayouter.init(sceneWidth, sceneHeight);
        mTopRightPlayerPileLayouter.init(sceneWidth, sceneHeight);

        //init stock and discard layouters
        mStockPileLayouter.init(sceneWidth, sceneHeight);
        mDiscardPileLayouter.init(sceneWidth, sceneHeight);

        //init field piles layouters
        for (FieldPileLayouter pileLayouter : mFieldPileLayouterList) {
            pileLayouter.init(sceneWidth, sceneHeight);
        }
    }

    public IPileLayouter getPileLayouterForPile(IPile pile) {
        //TODO Implement
        throw new UnsupportedOperationException();
    }
}