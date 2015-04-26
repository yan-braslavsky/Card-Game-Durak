package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.service.services.LayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.AttackState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.RetaliationState;
import com.yan.durak.session.states.impl.ThrowInState;

import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by ybra on 17/04/15.
 */
public class PlayerTakesActionMsgSubProcessor extends BaseMsgSubProcessor<PlayerTakesActionMessage> {

    private final GameInfo mGameInfo;
    private final HudScreenFragment mHudScreenFragment;
    private final LayouterManagerService mPileLayouterManager;
    private final PileManagerService mPileManager;
    private final CardsTouchProcessor mCardsTouchProcessor;

    public PlayerTakesActionMsgSubProcessor(final PileManagerService pileManager, final LayouterManagerService pileLayouterManager,
                                            final GameInfo gameInfo, final HudScreenFragment hudScreenFragment, final CardsTouchProcessor cardsTouchProcessor) {
        super();

        this.mGameInfo = gameInfo;
        this.mHudScreenFragment = hudScreenFragment;
        this.mPileLayouterManager = pileLayouterManager;
        this.mPileManager = pileManager;
        this.mCardsTouchProcessor = cardsTouchProcessor;
    }

    @Override
    public void processMessage(PlayerTakesActionMessage serverMessage) {
        int actionPlayerIndex = serverMessage.getMessageData().getPlayerIndex();

        updateActivePlayerState(serverMessage.getMessageData());

        //update cock only on attack
        if (PlayerTakesActionMessage.PlayerAction.valueOf(serverMessage.getMessageData().getAction()) != PlayerTakesActionMessage.PlayerAction.ATTACK)
            return;

        @HudScreenFragment.HudNode int cockNodeIndex = retrieveCockPosition(actionPlayerIndex);
        mHudScreenFragment.resetCockAnimation(cockNodeIndex);
    }

    private void updateActivePlayerState(PlayerTakesActionMessage.ProtocolMessageData messageData) {

        if (messageData.getPlayerIndex() == mGameInfo.getPlayerIndex(GameInfo.Player.BOTTOM_PLAYER)) {

            PlayerTakesActionMessage.PlayerAction action = PlayerTakesActionMessage.PlayerAction.valueOf(messageData.getAction());

            if (action == PlayerTakesActionMessage.PlayerAction.ATTACK) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(AttackState.class));
            } else if (action == PlayerTakesActionMessage.PlayerAction.RETALIATION) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(RetaliationState.class));
            } else if (action == PlayerTakesActionMessage.PlayerAction.THROW_IN) {
                mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(ThrowInState.class));
            } else {
                throw new RuntimeException("not recognized player action " + action);
            }

            //enable user input possibility
            mCardsTouchProcessor.register();

        } else {
            mGameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

            //disable user input possibility
            mCardsTouchProcessor.unRegister();
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
