package com.yan.durak.input.listener;

import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.services.PlayerMoveService;
import com.yan.durak.services.SceneSizeProviderService;

import glengine.yan.glengine.service.ServiceLocator;

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
        //Does nothing on tap
    }

    @Override
    public void onDraggedCardReleased(CardNode cardNode) {

        //if player didn't drag to the field , we will return the card back to his hand
        if (cardNode.getPosition().getY() > (ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight() * BasePlayerCardsTouchProcessorListener.SCENE_HEIGHT_FOR_CARD_RETURN)) {
            mPlayerCardsTouchProcessorListener.returnCardToPlayerHand(cardNode);
            return;
        }

        //we are using player move service to make the actual move
        ServiceLocator.locateService(PlayerMoveService.class).makeCardForAttackMove(cardNode.getCard());
    }

    @Override
    public void onCardDragProgress(CardNode cardNode) {

    }


}
