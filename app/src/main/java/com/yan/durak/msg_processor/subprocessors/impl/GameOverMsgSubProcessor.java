package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.services.HudManagementService;
import com.yan.durak.session.GameInfo;

import glengine.yan.glengine.service.ServiceLocator;

/**
 * Created by ybra on 17/04/15.
 */
public class GameOverMsgSubProcessor extends BaseMsgSubProcessor<GameOverProtocolMessage> {

    private final GameInfo mGameInfo;

    public GameOverMsgSubProcessor(final GameInfo gameInfo) {
        super();
        this.mGameInfo = gameInfo;
    }

    @Override
    public void processMessage(GameOverProtocolMessage serverMessage) {
        boolean iLostTheGame = (mGameInfo.getPlayerIndex(GameInfo.Player.BOTTOM_PLAYER) == serverMessage.getMessageData().getLoosingPlayer().getPlayerIndexInGame());
        if (iLostTheGame) {
            ServiceLocator.locateService(HudManagementService.class).showYouLooseMessage();
        } else {
            ServiceLocator.locateService(HudManagementService.class).showYouWonMessage();
        }
    }
}