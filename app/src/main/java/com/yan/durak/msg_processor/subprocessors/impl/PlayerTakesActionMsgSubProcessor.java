package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.screen_fragments.HudScreenFragment;

/**
 * Created by ybra on 17/04/15.
 */
public class PlayerTakesActionMsgSubProcessor extends BaseMsgSubProcessor<PlayerTakesActionMessage> {

    public PlayerTakesActionMsgSubProcessor(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    @Override
    public void processMessage(PlayerTakesActionMessage serverMessage) {
        int actionPlayerIndex = serverMessage.getMessageData().getPlayerIndex();

        //since we don't have reference to players indexes in the game
        //we translating the player index to pile index
        int actionPlayerPileIndex = (actionPlayerIndex + 2) % 5;
        @HudScreenFragment.HudNode int cockPosition = HudScreenFragment.COCK_BOTTOM_RIGHT_INDEX;
        if (actionPlayerPileIndex == mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getBottomPlayerPileIndex()) {
            cockPosition = HudScreenFragment.COCK_BOTTOM_RIGHT_INDEX;
        } else if (actionPlayerPileIndex == mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getTopRightPlayerPileIndex()) {
            cockPosition = HudScreenFragment.COCK_TOP_RIGHT_INDEX;
        } else if (actionPlayerPileIndex == mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().getTopLeftPlayerPileIndex()) {
            cockPosition = HudScreenFragment.COCK_TOP_LEFT_INDEX;
        }
        mMsgProcessor.getPrototypeGameScreen().getHudNodesFragment().resetCockAnimation(cockPosition);
    }
}
