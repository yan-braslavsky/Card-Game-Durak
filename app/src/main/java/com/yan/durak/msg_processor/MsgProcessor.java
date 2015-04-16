package com.yan.durak.msg_processor;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;
import com.yan.durak.msg_processor.states.MsgProcessorState;
import com.yan.durak.screens.PrototypeGameScreen;

/**
 * Created by ybra on 16/04/15.
 * <p/>
 * PURPOSE:
 * Manipulates screen state and nodes according to server messages that it receives.
 * <p/>
 * GENERAL :
 * Each message requires from client to respond. The logic can be complicated that
 * is why it is managed by this class.
 */
public class MsgProcessor implements IGameServerConnector.IGameServerCommunicatorListener {

    private final PrototypeGameScreen mPrototypeGameScreen;
    private MsgProcessorState mMsgProcessorState;

    /**
     * Require a direct reference to prototype screen in order
     * to manipulate its nodes and fragments
     *
     * @param prototypeGameScreen
     */
    public MsgProcessor(PrototypeGameScreen prototypeGameScreen) {
        this.mPrototypeGameScreen = prototypeGameScreen;
    }

    @Override
    public void handleServerMessage(BaseProtocolMessage serverMessage) {

        switch (serverMessage.getMessageName()) {

            //Card Moved
            case CardMovedProtocolMessage.MESSAGE_NAME:
                mMsgProcessorState.handleCardMoveMessage((CardMovedProtocolMessage) serverMessage);
                break;

            //Request Attack
            case RequestCardForAttackMessage.MESSAGE_NAME:
                mMsgProcessorState.handleRequestCardForAttackMessage((RequestCardForAttackMessage) serverMessage);
                break;

            //Request Retaliation
            case RequestRetaliatePilesMessage.MESSAGE_NAME:
                mMsgProcessorState.handleRequestRetaliatePilesMessage((RequestRetaliatePilesMessage) serverMessage);
                break;

            //Game Setup
            case GameSetupProtocolMessage.MESSAGE_NAME:
                mMsgProcessorState.handleGameSetupMessage((GameSetupProtocolMessage) serverMessage);
                break;

            //Player Action
            case PlayerTakesActionMessage.MESSAGE_NAME:
                mMsgProcessorState.handlePlayerTakesActionMessage((PlayerTakesActionMessage) serverMessage);
                break;

            //Retaliation Invalid
            case RetaliationInvalidProtocolMessage.MESSAGE_NAME:
                mMsgProcessorState.handleInvalidRetaliationMessage((RetaliationInvalidProtocolMessage) serverMessage);
                break;

            //Request Throw In
            case RequestThrowInsMessage.MESSAGE_NAME:
                mMsgProcessorState.handleRequestThrowInsMessageMessage((RequestThrowInsMessage) serverMessage);
                break;

            //Game Over
            case GameOverProtocolMessage.MESSAGE_NAME:
                mMsgProcessorState.handleGameOverMessage((GameOverProtocolMessage) serverMessage);
                break;

            //None
            default:
                throw new RuntimeException("Can't process a message with name " + serverMessage.getMessageName());
        }
    }

    public PrototypeGameScreen getPrototypeGameScreen() {
        return mPrototypeGameScreen;
    }

    public MsgProcessorState getMsgProcessorState() {
        return mMsgProcessorState;
    }

    public void setMsgProcessorState(MsgProcessorState mMsgProcessorState) {
        this.mMsgProcessorState = mMsgProcessorState;
        this.mMsgProcessorState.applyState();
    }
}
