package com.yan.durak.managers;

import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.layouting.pile.impl.BottomPlayerPileLayouter;
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


    //TODO : that is debatable , there could be more piles on field
    public static final int MAX_PILES_ON_FIELD = 6;

    BottomPlayerPileLayouter mBottomPlayerPileLayouter;
    TopLeftPlayerPileLayouter mTopLeftPlayerPileLayouter;
    TopRightPlayerPileLayouter mTopRightPlayerPileLayouter;
    StockPileLayouter mStockPileLayouter;
    List<FieldPileLayouter> mFieldPileLayouterList;

    public PileLayouterManager(ICardNodesManager cardNodesManager, TweenManager tweenManager) {

        //init bottom player layouter
        mBottomPlayerPileLayouter = new BottomPlayerPileLayouter(cardNodesManager, tweenManager);

        //init top left player layouter
        mTopLeftPlayerPileLayouter = new TopLeftPlayerPileLayouter(cardNodesManager, tweenManager);

        //init top right player layouter
        mTopRightPlayerPileLayouter = new TopRightPlayerPileLayouter(cardNodesManager, tweenManager);

        //init stock pile layouter
        mStockPileLayouter = new StockPileLayouter(cardNodesManager, tweenManager);

        //init list of field layouters
        initFieldLayoutersList(cardNodesManager, tweenManager);

    }

    private void initFieldLayoutersList(ICardNodesManager cardNodesManager, TweenManager tweenManager) {
        mFieldPileLayouterList = new ArrayList<>();

        for (int i = 0; i < MAX_PILES_ON_FIELD; i++) {
            mFieldPileLayouterList.add(new FieldPileLayouter(cardNodesManager, tweenManager));
        }
    }

    /**
     * Initializes positions and all needed values for layouting
     */
    public void init(float sceneWidth, float sceneHeight) {

        //init the player cards layouter
        mPlayerCardsLayouter.init(mCardsScreenFragment.getCardNodeWidth(), mCardsScreenFragment.getCardNodeHeight(),
                //maximum available width
                getSceneSize().getX(),
                //base x position ( center )
                getSceneSize().getX() / 2,

                //TODO : Take the right values , not hard coded
                //base y position
                getSceneSize().getY() - /*mFence.getSize().getY() / 2*/100);

        //TODO : init positions of field layouters
    }

    IPileLayouter getPileLayouterForPile(IPile pile) {
        //TODO Implement
        throw new UnsupportedOperationException();
    }
}
