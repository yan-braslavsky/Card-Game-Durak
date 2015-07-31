package com.yan.durak.services;

import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.layouting.pile.impl.BottomPlayerPileLayouter;
import com.yan.durak.layouting.pile.impl.DiscardPileLayouter;
import com.yan.durak.layouting.pile.impl.FieldPileLayouter;
import com.yan.durak.layouting.pile.impl.StockPileLayouter;
import com.yan.durak.layouting.pile.impl.TopLeftPlayerPileLayouter;
import com.yan.durak.layouting.pile.impl.TopRightPlayerPileLayouter;
import com.yan.durak.models.PileModel;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.session.GameInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.service.IService;
import glengine.yan.glengine.service.ServiceLocator;

/**
 * Created by ybra on 20/04/15.
 */
public class PileLayouterManagerService implements IService {

    //layouters
    final BottomPlayerPileLayouter mBottomPlayerPileLayouter;
    final TopLeftPlayerPileLayouter mTopLeftPlayerPileLayouter;
    final TopRightPlayerPileLayouter mTopRightPlayerPileLayouter;
    final StockPileLayouter mStockPileLayouter;
    final DiscardPileLayouter mDiscardPileLayouter;
    final List<FieldPileLayouter> mFieldPileLayouterList;

    //map
    final Map<PileModel, IPileLayouter> mPileToLayouterMap;
    private final TweenManager mTweenManager;

    public PileLayouterManagerService(final TweenManager tweenManager) {

        mTweenManager = tweenManager;
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        CardNodesManagerService cardNodesManager = ServiceLocator.locateService(CardNodesManagerService.class);
        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        HudManagementService hudManagementService = ServiceLocator.locateService(HudManagementService.class);

        this.mPileToLayouterMap = new HashMap<>();

        //init bottom player layouter
        this.mBottomPlayerPileLayouter = new BottomPlayerPileLayouter(pileManager, cardNodesManager, tweenManager, pileManager.getBottomPlayerPile());

        //init top left player layouter
        this.mTopLeftPlayerPileLayouter = new TopLeftPlayerPileLayouter(cardNodesManager, tweenManager, pileManager.getTopLeftPlayerPile());

        //init top right player layouter
        this.mTopRightPlayerPileLayouter = new TopRightPlayerPileLayouter(cardNodesManager, tweenManager, pileManager.getTopRightPlayerPile());

        //init stock pile layouter
        this.mStockPileLayouter = new StockPileLayouter(gameInfo, hudManagementService, cardNodesManager, tweenManager, pileManager.getStockPile());

        //init discard pile layouter
        this.mDiscardPileLayouter = new DiscardPileLayouter(cardNodesManager, tweenManager, pileManager.getDiscardPile());

        //init field piles list
        this.mFieldPileLayouterList = new ArrayList<>(PileManagerService.FIELD_PILES_AMOUNT);

        initMap();
    }

    public void initFieldPileLayouters() {
        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        CardNodesManagerService cardNodesManager = ServiceLocator.locateService(CardNodesManagerService.class);
        SceneSizeProviderService screenSize = ServiceLocator.locateService(SceneSizeProviderService.class);

        //init list of field layouters
        for (PileModel pileModel : pileManager.getFieldPiles()) {
            FieldPileLayouter fieldPileLayouter = new FieldPileLayouter(cardNodesManager, mTweenManager, pileModel);
            fieldPileLayouter.init(screenSize.getSceneWidth(), screenSize.getSceneHeight());
            mPileToLayouterMap.put(fieldPileLayouter.getBoundpile(), fieldPileLayouter);
            mFieldPileLayouterList.add(fieldPileLayouter);
        }
    }

    private void initMap() {

        //map stock and discard piles
        mPileToLayouterMap.put(mDiscardPileLayouter.getBoundpile(), mDiscardPileLayouter);
        mPileToLayouterMap.put(mStockPileLayouter.getBoundpile(), mStockPileLayouter);

        //map players piles
        mPileToLayouterMap.put(mBottomPlayerPileLayouter.getBoundpile(), mBottomPlayerPileLayouter);
        mPileToLayouterMap.put(mTopRightPlayerPileLayouter.getBoundpile(), mTopRightPlayerPileLayouter);
        mPileToLayouterMap.put(mTopLeftPlayerPileLayouter.getBoundpile(), mTopLeftPlayerPileLayouter);
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
    public <T extends IPileLayouter> T getPileLayouterForPile(PileModel pile) {
        return (T) mPileToLayouterMap.get(pile);
    }

    @Override
    public void clearServiceData() {
        //Does Nothing
    }
}