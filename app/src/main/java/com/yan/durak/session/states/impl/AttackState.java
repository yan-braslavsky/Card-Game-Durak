package com.yan.durak.session.states.impl;


import com.yan.durak.services.CardsTouchProcessorService;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.session.states.BaseDraggableState;

import glengine.yan.glengine.service.ServiceLocator;

/**
 * Created by Yan-Home on 4/26/2015.
 */
public class AttackState extends BaseDraggableState {


    @Override
    public ActivePlayerStateDefinition getStateDefinition() {
        return ActivePlayerStateDefinition.REQUEST_CARD_FOR_ATTACK;
    }

    @Override
    public void applyState() {
        ServiceLocator.locateService(CardsTouchProcessorService.class).register();
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(ServiceLocator.locateService(PileManagerService.class).getBottomPlayerPile()).layout();
    }

    @Override
    public void resetState() {
        super.resetState();
    }
}
