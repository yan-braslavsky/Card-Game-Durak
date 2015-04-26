package com.yan.durak.session.states.impl;

import com.yan.durak.session.states.IActivePlayerState;

/**
 * Created by Yan-Home on 4/26/2015.
 */
public class ThrowInState implements IActivePlayerState {


    @Override
    public ActivePlayerStateDefinition getStateDefinition() {
        return ActivePlayerStateDefinition.REQUEST_THROW_IN;
    }

    @Override
    public void resetState() {

    }
}