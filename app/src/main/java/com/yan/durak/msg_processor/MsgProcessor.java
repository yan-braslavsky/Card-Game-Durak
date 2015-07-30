package com.yan.durak.msg_processor;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerJoinProtocolMessage;
import com.yan.durak.msg_processor.subprocessors.MsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.CardMovedMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.GameOverMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.GameSetupMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.PlayerJoinSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.PlayerTakesActionMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.RequestCardForAttackMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.RequestRetaliatePilesMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.RequestThrowInsMsgSubProcessor;
import com.yan.durak.msg_processor.subprocessors.impl.RetaliationInvalidMsgSubProcessor;
import com.yan.durak.screens.PrototypeGameScreen;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.session.GameInfo;

import java.util.HashMap;
import java.util.Map;

import glengine.yan.glengine.service.ServiceLocator;

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

        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        GameServerMessageSender messageSender = ServiceLocator.locateService(GameServerMessageSender.class);
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        PileLayouterManagerService pileLayouterManager = ServiceLocator.locateService(PileLayouterManagerService.class);

        //Card Moved
        mProcessorsMap.put(CardMovedProtocolMessage.class, new CardMovedMsgSubProcessor(pileManager, pileLayouterManager));

        //Game Setup
        mProcessorsMap.put(GameSetupProtocolMessage.class, new GameSetupMsgSubProcessor(gameInfo,
                pileLayouterManager, pileManager));

        //Request Attack
        mProcessorsMap.put(RequestCardForAttackMessage.class, new RequestCardForAttackMsgSubProcessor(pileManager,
                messageSender));

        //Request Retaliation
        mProcessorsMap.put(RequestRetaliatePilesMessage.class, new RequestRetaliatePilesMsgSubProcessor(gameInfo,messageSender));

        //Player Action
        mProcessorsMap.put(PlayerTakesActionMessage.class, new PlayerTakesActionMsgSubProcessor(pileManager,
                pileLayouterManager, gameInfo));

        //Retaliation Invalid
        mProcessorsMap.put(RetaliationInvalidProtocolMessage.class, new RetaliationInvalidMsgSubProcessor());

        //Request Throw In
        mProcessorsMap.put(RequestThrowInsMessage.class, new RequestThrowInsMsgSubProcessor(messageSender));

        //Game Over
        mProcessorsMap.put(GameOverProtocolMessage.class, new GameOverMsgSubProcessor(gameInfo));

        //Player Join
        mProcessorsMap.put(PlayerJoinProtocolMessage.class, new PlayerJoinSubProcessor());
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