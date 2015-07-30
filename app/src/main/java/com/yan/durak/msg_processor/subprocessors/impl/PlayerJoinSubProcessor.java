package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.PlayerJoinProtocolMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public class PlayerJoinSubProcessor extends BaseMsgSubProcessor<PlayerJoinProtocolMessage> {

    public PlayerJoinSubProcessor() {
        super();
    }

    @Override
    public void processMessage(PlayerJoinProtocolMessage serverMessage) {
        //TODO : Implement
    }
}