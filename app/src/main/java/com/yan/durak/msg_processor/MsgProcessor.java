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
import com.yan.durak.msg_processor.subprocessors.MsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.CardMovedMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.GameOverMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.GameSetupMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.PlayerTakesActionMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.RequestCardForAttackMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.RequestRetaliatePilesMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.RequestThrowInsMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.RetaliationInvalidMsgSubProcessor;
import com.yan.durak.screens.PrototypeGameScreen;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ybra on 16/04/15.
 * <p/>
 * PURPOSE:
 * Distribute received server messages across concrete handlers (subprocessors)
 */
public class MsgProcessor implements IGameServerConnector.IGameServerCommunicatorListener {

    private final PrototypeGameScreen mPrototypeGameScreen;
    private Map<Class<? extends BaseProtocolMessage>, MsgSubProcessor> mProcessorsMap;

    /**
     * Require a direct reference to prototype screen in order
     * to manipulate its nodes and fragments
     */
    public MsgProcessor(PrototypeGameScreen prototypeGameScreen) {
        this.mPrototypeGameScreen = prototypeGameScreen;
        this.mProcessorsMap = new HashMap<>();

        //map between messages and their processors
        fillProcessorsMap();
    }

    private void fillProcessorsMap() {

        //Card Moved
        mProcessorsMap.put(CardMovedProtocolMessage.class, new CardMovedMsgSubProcessor(getPrototypeGameScreen().getPileManager(), getPrototypeGameScreen().getPileLayouterManager()));

        //Game Setup
        mProcessorsMap.put(GameSetupProtocolMessage.class, new GameSetupMsgSubProcessor(getPrototypeGameScreen().getHudNodesFragment(), getPrototypeGameScreen().getGameInfo(),
                getPrototypeGameScreen().getPileLayouterManager(), getPrototypeGameScreen().getPileManager()));

        //Request Attack
        mProcessorsMap.put(RequestCardForAttackMessage.class, new RequestCardForAttackMsgSubProcessor(getPrototypeGameScreen().getPileManager(),
                getPrototypeGameScreen().getMessageSender()));

        //Request Retaliation
        mProcessorsMap.put(RequestRetaliatePilesMessage.class, new RequestRetaliatePilesMsgSubProcessor(getPrototypeGameScreen().getMessageSender()));

        //Player Action
        mProcessorsMap.put(PlayerTakesActionMessage.class, new PlayerTakesActionMsgSubProcessor(getPrototypeGameScreen().getPileManager(),
                getPrototypeGameScreen().getPileLayouterManager(), getPrototypeGameScreen().getGameInfo(), getPrototypeGameScreen().getHudNodesFragment(),getPrototypeGameScreen().getCardsTouchProcessor()));

        //Retaliation Invalid
        mProcessorsMap.put(RetaliationInvalidProtocolMessage.class, new RetaliationInvalidMsgSubProcessor());

        //Request Throw In
        mProcessorsMap.put(RequestThrowInsMessage.class, new RequestThrowInsMsgSubProcessor(getPrototypeGameScreen().getMessageSender()));

        //Game Over
        mProcessorsMap.put(GameOverProtocolMessage.class, new GameOverMsgSubProcessor(getPrototypeGameScreen().getHudNodesFragment(), getPrototypeGameScreen().getGameInfo()));
    }

    @Override
    public void handleServerMessage(BaseProtocolMessage serverMessage) {
        //delegate the processing to concrete processor
        mProcessorsMap.get(serverMessage.getClass()).processMessage(serverMessage);
    }

    public PrototypeGameScreen getPrototypeGameScreen() {
        return mPrototypeGameScreen;
    }


}