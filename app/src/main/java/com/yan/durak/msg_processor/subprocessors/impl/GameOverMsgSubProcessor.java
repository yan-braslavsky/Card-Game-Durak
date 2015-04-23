package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.session.GameInfo;

/**
 * Created by ybra on 17/04/15.
 */
public class GameOverMsgSubProcessor extends BaseMsgSubProcessor<GameOverProtocolMessage> {

    private final HudScreenFragment mHudScreenFragment;
    private final GameInfo mGameInfo;

    public GameOverMsgSubProcessor(final HudScreenFragment hudScreenFragment, final GameInfo gameInfo) {
        super();
        this.mHudScreenFragment = hudScreenFragment;
        this.mGameInfo = gameInfo;
    }

    @Override
    public void processMessage(GameOverProtocolMessage serverMessage) {
        boolean iLostTheGame = (mGameInfo.getPlayerIndex(GameInfo.Player.BOTTOM_PLAYER) == serverMessage.getMessageData().getLoosingPlayer().getPlayerIndexInGame());
        if (iLostTheGame) {
            mHudScreenFragment.showYouLooseMessage();
        } else {
            mHudScreenFragment.showYouWonMessage();
        }
    }
}