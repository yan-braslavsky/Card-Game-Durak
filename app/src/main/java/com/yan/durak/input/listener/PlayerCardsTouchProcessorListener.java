package com.yan.durak.input.listener;

import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.BaseDraggableState;
import com.yan.durak.session.states.impl.AttackState;
import com.yan.durak.session.states.impl.RetaliationState;
import com.yan.durak.session.states.impl.ThrowInState;

import java.util.HashMap;

/**
 * Created by Yan-Home on 4/25/2015.
 */
public class PlayerCardsTouchProcessorListener extends BasePlayerCardsTouchProcessorListener {

    private final HashMap<Class<? extends BaseDraggableState>, CardsTouchProcessor.CardsTouchProcessorListener> mTouchProcessors;

    public PlayerCardsTouchProcessorListener() {
        mTouchProcessors = new HashMap();

        //init handlers
        mTouchProcessors.put(AttackState.class, new AttackProcessorListener(this));
        mTouchProcessors.put(RetaliationState.class, new RetaliationProcessorListener(this));
        mTouchProcessors.put(ThrowInState.class, new ThrowInProcessorListener(this));
    }

    @Override
    public void onDraggedCardReleased(CardNode cardNode) {
        super.onDraggedCardReleased(cardNode);

        //we don't need to handle this state if parent already handled it
        if (mDragReleaseHandled)
            return;

        //delegate handling to relevant processor
        mTouchProcessors.get(ServiceLocator.locateService(GameInfo.class).getActivePlayerState().getClass()).onDraggedCardReleased(cardNode);
    }
}