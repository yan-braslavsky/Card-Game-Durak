package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

import java.util.ArrayList;
import java.util.List;

import glengine.yan.glengine.tasks.YANDelayedTask;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestRetaliatePilesMsgSubProcessor extends BaseMsgSubProcessor<RequestRetaliatePilesMessage> {

    private final GameServerMessageSender mMessageSender;
    private final List<List<Card>> mRetaliationList;

    private YANDelayedTask mTask;
    private final YANDelayedTask.YANDelayedTaskListener mDelayedTaskListener = new YANDelayedTask.YANDelayedTaskListener() {

        @Override
        public void onComplete() {

            //return task to the pool
            YANObjectPool.getInstance().offer(mTask);
            mTask = null;

            //TODO : for now just take all (send empty array)
            mMessageSender.sendResponseRetaliatePiles(mRetaliationList);
        }
    };

    public RequestRetaliatePilesMsgSubProcessor(final GameServerMessageSender messageSender) {
        super();

        this.mMessageSender = messageSender;
        this.mRetaliationList = new ArrayList<>();
    }

    @Override
    public void processMessage(RequestRetaliatePilesMessage serverMessage) {

        mTask = YANObjectPool.getInstance().obtain(YANDelayedTask.class);
        mTask.setDurationSeconds(0.1f);
        mTask.setDelayedTaskListener(mDelayedTaskListener);
        mTask.start();

    }
}
