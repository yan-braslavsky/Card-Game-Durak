package com.yan.durak.input.listener;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.PileLayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.service.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;

import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by Yan-Home on 5/1/2015.
 */
public class AttackProcessorListener implements CardsTouchProcessor.CardsTouchProcessorListener {
    private final PlayerCardsTouchProcessorListener mPlayerCardsTouchProcessorListener;

    public AttackProcessorListener(PlayerCardsTouchProcessorListener playerCardsTouchProcessorListener) {
        mPlayerCardsTouchProcessorListener = playerCardsTouchProcessorListener;
    }

    @Override
    public void onSelectedCardTap(CardNode cardNode) {

    }

    @Override
    public void onDraggedCardReleased(CardNode cardNode) {

        //if player didn't drag to the field , we will return the card back to his hand
        if (cardNode.getPosition().getY() > (ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight() * BasePlayerCardsTouchProcessorListener.SCENE_HEIGHT_FOR_CARD_RETURN)) {
            mPlayerCardsTouchProcessorListener.returnCardToPlayerHand(cardNode);
            return;
        }

        //cache services
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);

        //add card to the first field pile
        PileModel firstFieldPile = ServiceLocator.locateService(PileManagerService.class).getFieldPiles().get(0);
        firstFieldPile.addCard(cardNode.getCard());

        //layout the field pile
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(firstFieldPile).layout();

        //reset the state
        gameInfo.getActivePlayerState().resetState();

        //disable the hand of the player by setting another state
        gameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

        //we can just send the response
        ServiceLocator.locateService(GameServerMessageSender.class).sendCardForAttackResponse(cardNode.getCard());
    }

    @Override
    public void onCardDragProgress(CardNode cardNode) {

    }


}
