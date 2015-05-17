package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.HudManagementService;
import com.yan.durak.service.services.PileLayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.AttackState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.RetaliationState;

import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 */
public class PlayerTakesActionMsgSubProcessor extends BaseMsgSubProcessor<PlayerTakesActionMessage> {

    private final GameInfo mGameInfo;
    private final PileLayouterManagerService mPileLayouterManager;
    private final PileManagerService mPileManager;

    public PlayerTakesActionMsgSubProcessor(final PileManagerService pileManager,
                                            final PileLayouterManagerService pileLayouterManager,
                                            final GameInfo gameInfo) {
        super();
        this.mGameInfo = gameInfo;
        this.mPileLayouterManager = pileLayouterManager;
        this.mPileManager = pileManager;
    }

    @Override
    public void processMessage(PlayerTakesActionMessage serverMessage) {
        updateActivePlayerState(serverMessage.getMessageData());

//        //update cock only on attack
//        if (PlayerTakesActionMessage.PlayerAction.valueOf(serverMessage.getMessageData().getAction()) != PlayerTakesActionMessage.PlayerAction.ATTACK)
//            return;

        //TODO : update timer animation
        @HudManagementService.HudNode int timerNodeIndex = retrieveTimerPosition(serverMessage.getMessageData().getPlayerIndex());
        ServiceLocator.locateService(HudManagementService.class).resetTimerAnimation(timerNodeIndex);
    }

    private void updateActivePlayerState(PlayerTakesActionMessage.ProtocolMessageData messageData) {

        if (messageData.getPlayerIndex() == mGameInfo.getPlayerIndex(GameInfo.Player.BOTTOM_PLAYER)) {

            PlayerTakesActionMessage.PlayerAction action = PlayerTakesActionMessage.PlayerAction.valueOf(messageData.getAction());

            if (action == PlayerTakesActionMessage.PlayerAction.ATTACK) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(AttackState.class));
            } else if (action == PlayerTakesActionMessage.PlayerAction.RETALIATION) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(RetaliationState.class));
            } else if (action == PlayerTakesActionMessage.PlayerAction.THROW_IN) {
                //Maybe the throw in just sent to notify , but actual action cannot be taken
//                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(ThrowInState.class));
            } else {
                throw new RuntimeException("not recognized player action " + action);
            }


        } else {
            mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));
        }

        //in any case we need to re lay out the player pile :
        mPileLayouterManager.getPileLayouterForPile(mPileManager.getBottomPlayerPile()).layout();
    }

    private
    @HudManagementService.HudNode
    int retrieveTimerPosition(int actionPlayerIndex) {
        switch (mGameInfo.getPlayerForIndex(actionPlayerIndex)) {
            case BOTTOM_PLAYER:
                return HudManagementService.CIRCLE_TIMER_BOTTOM_RIGHT_INDEX;
            case TOP_RIGHT_PLAYER:
                return HudManagementService.CIRCLE_TIMER_TOP_RIGHT_INDEX;
            case TOP_LEFT_PLAYER:
                return HudManagementService.CIRCLE_TIMER_TOP_LEFT_INDEX;
            default:
                throw new RuntimeException("player not found for index : " + actionPlayerIndex);
        }
    }
}
