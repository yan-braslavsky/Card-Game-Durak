package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public class GameOverMsgSubProcessor extends BaseMsgSubProcessor<GameOverProtocolMessage> {

    public GameOverMsgSubProcessor() {
        super();
    }

    @Override
    public void processMessage(GameOverProtocolMessage serverMessage) {
//        boolean iLostTheGame = (mMsgProcessor.getPrototypeGameScreen().getGameSession().getBottomPlayerGameIndex() == serverMessage.getMessageData().getLoosingPlayer().getPlayerIndexInGame());
//        if (iLostTheGame) {
//            mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showYouLooseMessage();
//        } else {
//            mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().showYouWonMessage();
//        }
    }
}