package com.yan.durak.session.states.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.services.CardsTouchProcessorService;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.session.states.BaseDraggableState;

import java.util.ArrayList;
import java.util.List;

import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.object_pool.YANIPoolableObject;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by Yan-Home on 4/26/2015.
 */
public class RetaliationState extends BaseDraggableState {


    /**
     * Cards that still should be retaliated
     */
    private final List<RetaliationSet> mPendingRetaliationCardSets;

    /**
     * Cards that still should be retaliated
     */
    private final List<RetaliationSet> mRetaliatedCardSets;

    public RetaliationState() {
        this.mPendingRetaliationCardSets = new ArrayList<>();
        this.mRetaliatedCardSets = new ArrayList<>();
    }

    @Override
    public ActivePlayerStateDefinition getStateDefinition() {
        return ActivePlayerStateDefinition.REQUEST_RETALIATION;
    }

    @Override
    public void resetState() {
        super.resetState();

        //put back to pool
        for (RetaliationSet retaliationSet : mPendingRetaliationCardSets) {
            retaliationSet.resetState();
            YANObjectPool.getInstance().offer(retaliationSet);
        }

        //put back to pool
        for (RetaliationSet rset : mRetaliatedCardSets) {
            rset.resetState();
            YANObjectPool.getInstance().offer(rset);
        }

        mPendingRetaliationCardSets.clear();
        mRetaliatedCardSets.clear();
    }

    public List<RetaliationSet> getPendingRetaliationCardSets() {
        return mPendingRetaliationCardSets;
    }

    public List<RetaliationSet> getRetaliatedCardSets() {
        return mRetaliatedCardSets;
    }

    public static class RetaliationSet implements YANIPoolableObject {
        private Card mCoveredCard;
        private Card mCoveringCard;

        public Card getCoveredCard() {
            return mCoveredCard;
        }

        public void setCoveredCard(Card coveredCard) {
            mCoveredCard = coveredCard;
        }

        public Card getCoveringCard() {
            return mCoveringCard;
        }

        public void setCoveringCard(Card coveringCard) {
            mCoveringCard = coveringCard;
        }

        @Override
        public void resetState() {
            mCoveredCard = null;
            mCoveringCard = null;
        }
    }

    @Override
    public void applyState() {
        ServiceLocator.locateService(CardsTouchProcessorService.class).register();
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(ServiceLocator.locateService(PileManagerService.class).getBottomPlayerPile()).layout();
    }
}