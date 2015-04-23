package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.session.GameInfo;

/**
 * Created by ybra on 17/04/15.
 */
public class PlayerTakesActionMsgSubProcessor extends BaseMsgSubProcessor<PlayerTakesActionMessage> {

    private final GameInfo nGameInfo;
    private final HudScreenFragment mHudScreenFragment;

    public PlayerTakesActionMsgSubProcessor(final GameInfo gameInfo, final HudScreenFragment hudScreenFragment) {
        super();

        this.nGameInfo = gameInfo;
        this.mHudScreenFragment = hudScreenFragment;
    }

    @Override
    public void processMessage(PlayerTakesActionMessage serverMessage) {
        int actionPlayerIndex = serverMessage.getMessageData().getPlayerIndex();

        @HudScreenFragment.HudNode int cockNodeIndex = retrieveCockPosition(actionPlayerIndex);
        mHudScreenFragment.resetCockAnimation(cockNodeIndex);
    }

    private
    @HudScreenFragment.HudNode
    int retrieveCockPosition(int actionPlayerIndex) {
        switch (nGameInfo.getPlayerForIndex(actionPlayerIndex)) {
            case BOTTOM_PLAYER:
                return HudScreenFragment.COCK_BOTTOM_RIGHT_INDEX;
            case TOP_RIGHT_PLAYER:
                return HudScreenFragment.COCK_TOP_RIGHT_INDEX;
            case TOP_LEFT_PLAYER:
                return HudScreenFragment.COCK_TOP_LEFT_INDEX;
            default:
                throw new RuntimeException("player not found for index : " + actionPlayerIndex);
        }
    }
}
