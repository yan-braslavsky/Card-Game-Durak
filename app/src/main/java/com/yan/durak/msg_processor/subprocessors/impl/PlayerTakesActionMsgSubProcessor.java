package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.services.hud.HudNodes;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.AttackState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.RetaliationState;

import glengine.yan.glengine.service.ServiceLocator;
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
    public void processMessage(final PlayerTakesActionMessage serverMessage) {

        updateActivePlayerState(serverMessage.getMessageData());

        @HudNodes.SpeechBubbleText String speechBubbleText = null;
        final PlayerTakesActionMessage.PlayerAction playerAction = PlayerTakesActionMessage.PlayerAction.valueOf(serverMessage.getMessageData().getAction());

        switch (playerAction) {
            case ATTACK_START:
                //update timer animation
                ServiceLocator.locateService(HudManagementService.class).startTimerForPlayer(mGameInfo.getPlayerForIndex(serverMessage.getMessageData().getPlayerIndex()),
                        HudManagementService.TIMER_RETALIATION_COLOR);

                speechBubbleText = HudNodes.SPEECH_BUBBLE_ATTACK_TEXT;
                break;
            case RETALIATION_START:

                //update timer animation
                ServiceLocator.locateService(HudManagementService.class).startTimerForPlayer(mGameInfo.getPlayerForIndex(serverMessage.getMessageData().getPlayerIndex()),
                        HudManagementService.TIMER_RETALIATION_COLOR);

                //this will be called every time player retaliates , on attack and on throw ins
                ServiceLocator.locateService(HudManagementService.class).animateScaleUpPlayerAvatar(mGameInfo.getPlayerForIndex(serverMessage.getMessageData().getPlayerIndex()));
                break;
            case THROW_IN_PASS:
                speechBubbleText = HudNodes.SPEECH_BUBBLE_PASS_TEXT;
                break;
            case THROW_IN_END:
                speechBubbleText = HudNodes.SPEECH_BUBBLE_THROW_IN_END_TEXT;
                break;
            case THROW_IN_START:

                //update timer animation
                ServiceLocator.locateService(HudManagementService.class).startTimerForPlayer(mGameInfo.getPlayerForIndex(serverMessage.getMessageData().getPlayerIndex()),
                        HudManagementService.TIMER_THROW_IN_COLOR);

                ServiceLocator.locateService(HudManagementService.class).animateScaleUpPlayerAvatar(mGameInfo.getPlayerForIndex(serverMessage.getMessageData().getPlayerIndex()));
                break;
            case PLAYER_TAKES_CARDS:
                speechBubbleText = HudNodes.SPEECH_BUBBLE_TAKING_TEXT;
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

    private void updateActivePlayerState(final PlayerTakesActionMessage.ProtocolMessageData messageData) {

        if (messageData.getPlayerIndex() == mGameInfo.getPlayerIndex(GameInfo.PlayerLocation.BOTTOM_PLAYER)) {

            final PlayerTakesActionMessage.PlayerAction action = PlayerTakesActionMessage.PlayerAction.valueOf(messageData.getAction());

            //TODO : make those updates in specific subprocessors
            if (action == PlayerTakesActionMessage.PlayerAction.ATTACK_START) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(AttackState.class));
            } else if (action == PlayerTakesActionMessage.PlayerAction.RETALIATION_START) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(RetaliationState.class));
            }

        } else {
            mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));
        }

    }
}