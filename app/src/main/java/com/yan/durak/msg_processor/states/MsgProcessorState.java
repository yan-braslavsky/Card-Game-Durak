package com.yan.durak.msg_processor.states;

import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;

/**
 * Created by ybra on 16/04/15.
 * <p/>
 * PURPOSE :
 * Defines a state that msg processor can be in.
 */
public interface MsgProcessorState {

    /**
     * Called as soon as this state becomes active
     */
    public void applyState();

    void handleCardMoveMessage(CardMovedProtocolMessage serverMessage);

    void handleRequestCardForAttackMessage(RequestCardForAttackMessage serverMessage);

    void handleRequestRetaliatePilesMessage(RequestRetaliatePilesMessage serverMessage);

    void handleGameSetupMessage(GameSetupProtocolMessage serverMessage);

    void handlePlayerTakesActionMessage(PlayerTakesActionMessage serverMessage);

    void handleInvalidRetaliationMessage(RetaliationInvalidProtocolMessage serverMessage);

    void handleRequestThrowInsMessageMessage(RequestThrowInsMessage serverMessage);

    void handleGameOverMessage(GameOverProtocolMessage serverMessage);

}
