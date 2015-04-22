package com.yan.durak.managers;

import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.layouting.pile.impl.BottomPlayerPileLayouter;
import com.yan.durak.layouting.pile.impl.DiscardPileLayouter;
import com.yan.durak.layouting.pile.impl.FieldPileLayouter;
import com.yan.durak.layouting.pile.impl.StockPileLayouter;
import com.yan.durak.layouting.pile.impl.TopLeftPlayerPileLayouter;
import com.yan.durak.layouting.pile.impl.TopRightPlayerPileLayouter;
import com.yan.durak.models.PileModel;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.session.GameInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //map
    final Map<PileModel, IPileLayouter> mPileToLayouterMap;

    public PileLayouterManager(CardNodesManager cardNodesManager, TweenManager tweenManager, PileManager pileManager, GameInfo gameInfo, HudScreenFragment hudScreenFragment) {

        this.mPileManager = pileManager;

        this.mPileToLayouterMap = new HashMap<>();

        //init bottom player layouter
        this.mBottomPlayerPileLayouter = new BottomPlayerPileLayouter(mPileManager, cardNodesManager, tweenManager, mPileManager.getBottomPlayerPile());

        //init top left player layouter
        this.mTopLeftPlayerPileLayouter = new TopLeftPlayerPileLayouter(cardNodesManager, tweenManager, mPileManager.getTopLeftPlayerPile());

        //init top right player layouter
        this.mTopRightPlayerPileLayouter = new TopRightPlayerPileLayouter(cardNodesManager, tweenManager, mPileManager.getTopRightPlayerPile());

        //init stock pile layouter
        this.mStockPileLayouter = new StockPileLayouter(gameInfo, hudScreenFragment, cardNodesManager, tweenManager, mPileManager.getStockPile());

        //init discard pile layouter
        this.mDiscardPileLayouter = new DiscardPileLayouter(cardNodesManager, tweenManager, mPileManager.getDiscardPile());

        //init field piles list
        this.mFieldPileLayouterList = new ArrayList<>(mPileManager.getFieldPiles().size());

        //init list of field layouters
        for (PileModel pileModel : mPileManager.getFieldPiles()) {
            this.mFieldPileLayouterList.add(new FieldPileLayouter(cardNodesManager, tweenManager, pileModel));
        }

        initMap();
    }

    private void initMap() {

        //map stock and discard piles
        mPileToLayouterMap.put(mDiscardPileLayouter.getBoundpile(), mDiscardPileLayouter);
        mPileToLayouterMap.put(mStockPileLayouter.getBoundpile(), mStockPileLayouter);

        //map players piles
        mPileToLayouterMap.put(mBottomPlayerPileLayouter.getBoundpile(), mBottomPlayerPileLayouter);
        mPileToLayouterMap.put(mTopRightPlayerPileLayouter.getBoundpile(), mTopRightPlayerPileLayouter);
        mPileToLayouterMap.put(mTopLeftPlayerPileLayouter.getBoundpile(), mTopLeftPlayerPileLayouter);

        //mpa field piles
        for (FieldPileLayouter fieldPileLayouter : mFieldPileLayouterList) {
            mPileToLayouterMap.put(fieldPileLayouter.getBoundpile(), fieldPileLayouter);
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

    /**
     * Returns a layouter corresponding to provided pile
     *
     * @return layouter or null if layouter is not found
     */
    public IPileLayouter getPileLayouterForPile(PileModel pile) {
        return mPileToLayouterMap.get(pile);
    }
}