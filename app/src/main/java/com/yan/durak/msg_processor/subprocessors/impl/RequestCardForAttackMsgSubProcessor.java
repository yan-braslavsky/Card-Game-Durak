package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.models.PileModel;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

import java.util.Iterator;

import glengine.yan.glengine.tasks.YANDelayedTask;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestCardForAttackMsgSubProcessor extends BaseMsgSubProcessor<RequestCardForAttackMessage> {

    private final PileManagerService mPileManager;
    private final GameServerMessageSender mMessageSender;

    private YANDelayedTask mTask;
    private final YANDelayedTask.YANDelayedTaskListener mDelayedTaskListener = new YANDelayedTask.YANDelayedTaskListener() {

        @Override
        public void onComplete() {
            PileModel bottomPlayerPile = mPileManager.getBottomPlayerPile();
            if (bottomPlayerPile.getCardsInPile().isEmpty())
                return;

            //TODO : this is just a mock move
            Iterator<Card> iterator = bottomPlayerPile.getCardsInPile().iterator();
            Card cardForAttack = iterator.next();

            //return task to the pool
            YANObjectPool.getInstance().offer(mTask);
            mTask = null;

            //we can just send the response
            mMessageSender.sendCardForAttackResponse(cardForAttack);
        }
    };


    public RequestCardForAttackMsgSubProcessor(final PileManagerService pileManager, final GameServerMessageSender messageSender) {
        super();
        this.mPileManager = pileManager;
        this.mMessageSender = messageSender;
    }

    @Override
    public void processMessage(RequestCardForAttackMessage serverMessage) {

//        mTask = YANObjectPool.getInstance().obtain(YANDelayedTask.class);
//        mTask.setDurationSeconds(2);
//        mTask.setDelayedTaskListener(mDelayedTaskListener);
//        mTask.start();

    }
}
