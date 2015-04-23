package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.managers.PileLayouterManager;
import com.yan.durak.managers.PileManager;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.ActivePlayerState;

/**
 * Created by ybra on 17/04/15.
 */
public class PlayerTakesActionMsgSubProcessor extends BaseMsgSubProcessor<PlayerTakesActionMessage> {

    private final GameInfo mGameInfo;
    private final HudScreenFragment mHudScreenFragment;
    private final PileLayouterManager mPileLayouterManager;
    private final PileManager mPileManager;

    public PlayerTakesActionMsgSubProcessor(final PileManager pileManager, final PileLayouterManager pileLayouterManager, final GameInfo gameInfo, final HudScreenFragment hudScreenFragment) {
        super();

        this.mGameInfo = gameInfo;
        this.mHudScreenFragment = hudScreenFragment;
        this.mPileLayouterManager = pileLayouterManager;
        this.mPileManager = pileManager;
    }

    @Override
    public void processMessage(PlayerTakesActionMessage serverMessage) {
        int actionPlayerIndex = serverMessage.getMessageData().getPlayerIndex();

        updateActivePlayerState(serverMessage.getMessageData());


        @HudScreenFragment.HudNode int cockNodeIndex = retrieveCockPosition(actionPlayerIndex);
        mHudScreenFragment.resetCockAnimation(cockNodeIndex);
    }

    private void updateActivePlayerState(PlayerTakesActionMessage.ProtocolMessageData messageData) {

        if (messageData.getPlayerIndex() == mGameInfo.getPlayerIndex(GameInfo.Player.BOTTOM_PLAYER)) {

            PlayerTakesActionMessage.PlayerAction action = PlayerTakesActionMessage.PlayerAction.valueOf(messageData.getAction());

            if (action == PlayerTakesActionMessage.PlayerAction.ATTACK) {
                mGameInfo.setmActivePlayerState(ActivePlayerState.REQUEST_CARD_FOR_ATTACK);
            } else if (action == PlayerTakesActionMessage.PlayerAction.RETALIATION) {
                mGameInfo.setmActivePlayerState(ActivePlayerState.REQUEST_RETALIATION);
            } else if (action == PlayerTakesActionMessage.PlayerAction.THROW_IN) {
                mGameInfo.setmActivePlayerState(ActivePlayerState.REQUEST_THROW_IN);
            } else {
                throw new RuntimeException("not recognized player action " + action);
            }

        } else {
            mGameInfo.setmActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);
        }

        //in any case we need to re lay out the player pile :
        mPileLayouterManager.getPileLayouterForPile(mPileManager.getBottomPlayerPile()).layout();
    }

    private
    @HudScreenFragment.HudNode
    int retrieveCockPosition(int actionPlayerIndex) {
        switch (mGameInfo.getPlayerForIndex(actionPlayerIndex)) {
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
