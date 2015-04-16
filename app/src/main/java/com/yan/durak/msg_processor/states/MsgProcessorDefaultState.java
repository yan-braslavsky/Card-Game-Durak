package com.yan.durak.msg_processor.states;

import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;
import com.yan.durak.msg_processor.MsgProcessor;

/**
 * Created by ybra on 16/04/15.
 */
public class MsgProcessorDefaultState extends MsgProcessorBaseState {

    protected MsgProcessorDefaultState(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    @Override
    public void applyState() {

    }

    @Override
    public void handleCardMoveMessage(CardMovedProtocolMessage serverMessage) {

    }

    @Override
    public void handleRequestCardForAttackMessage(RequestCardForAttackMessage serverMessage) {

    }

    @Override
    public void handleRequestRetaliatePilesMessage(RequestRetaliatePilesMessage serverMessage) {

    }

    @Override
    public void handleGameSetupMessage(GameSetupProtocolMessage serverMessage) {

    }

    @Override
    public void handlePlayerTakesActionMessage(PlayerTakesActionMessage serverMessage) {

    }

    @Override
    public void handleInvalidRetaliationMessage(RetaliationInvalidProtocolMessage serverMessage) {

    }

    @Override
    public void handleRequestThrowInsMessageMessage(RequestThrowInsMessage serverMessage) {

    }

    @Override
    public void handleGameOverMessage(GameOverProtocolMessage serverMessage) {

    }
}
