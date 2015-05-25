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

        //TODO : update timer animation
        @HudManagementService.HudNode int timerNodeIndex = retrieveTimerPosition(serverMessage.getMessageData().getPlayerIndex());
        ServiceLocator.locateService(HudManagementService.class).resetTimerAnimation(timerNodeIndex);

        @HudManagementService.SpeechBubbleText String speechBubbleText = null;
        PlayerTakesActionMessage.PlayerAction playerAction = PlayerTakesActionMessage.PlayerAction.valueOf(serverMessage.getMessageData().getAction());

        switch (playerAction) {
            case ATTACK_START:
                speechBubbleText = HudManagementService.SPEECH_BUBBLE_ATTACK_TEXT;
                break;
            case RETALIATION_START:
                //this will be called every time player retaliates , on attack and on throw ins
                break;
            case THROW_IN_PASS:
                speechBubbleText = HudManagementService.SPEECH_BUBBLE_PASS_TEXT;
                break;
            case THROW_IN_END:
                speechBubbleText = HudManagementService.SPEECH_BUBBLE_THROW_IN_END_TEXT;
                break;
            case THROW_IN_START:
                speechBubbleText = HudManagementService.SPEECH_BUBBLE_THINKING_TEXT;
                break;
            case PLAYER_TAKES_CARDS:
                speechBubbleText = HudManagementService.SPEECH_BUBBLE_TAKING_TEXT;
                break;
            default:
                //Nothing
                break;
        }

        if (speechBubbleText != null) {
            //show speech bubble
            ServiceLocator.locateService(HudManagementService.class).showSpeechBubbleWithText(speechBubbleText,
                    mGameInfo.getPlayerForIndex(serverMessage.getMessageData().getPlayerIndex()));
        }

    }

    private void updateActivePlayerState(PlayerTakesActionMessage.ProtocolMessageData messageData) {

        if (messageData.getPlayerIndex() == mGameInfo.getPlayerIndex(GameInfo.Player.BOTTOM_PLAYER)) {

            PlayerTakesActionMessage.PlayerAction action = PlayerTakesActionMessage.PlayerAction.valueOf(messageData.getAction());

            //TODO : make those updates in specific subprocessors
            if (action == PlayerTakesActionMessage.PlayerAction.ATTACK_START) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(AttackState.class));
            } else if (action == PlayerTakesActionMessage.PlayerAction.RETALIATION_START) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(RetaliationState.class));
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
