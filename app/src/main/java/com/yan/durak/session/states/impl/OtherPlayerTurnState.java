package com.yan.durak.session.states.impl;

import com.yan.durak.session.states.IActivePlayerState;

/**
 * Created by Yan-Home on 4/26/2015.
 */
public class OtherPlayerTurnState implements IActivePlayerState {
    @Override
    public ActivePlayerStateDefinition getStateDefinition() {
        return ActivePlayerStateDefinition.OTHER_PLAYER_TURN;
    }

    @Override
    public void resetState() {

    }
}
