package com.yan.durak.session.states.impl;

import com.yan.durak.session.states.BaseDraggableState;

/**
 * Created by Yan-Home on 4/26/2015.
 */
public class RetaliationState extends BaseDraggableState {

    @Override
    public ActivePlayerStateDefinition getStateDefinition() {
        return ActivePlayerStateDefinition.REQUEST_RETALIATION;
    }

    @Override
    public void resetState() {
        super.resetState();
    }
}