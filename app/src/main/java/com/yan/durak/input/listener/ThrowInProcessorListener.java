package com.yan.durak.input.listener;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.services.CardNodesManagerService;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.ThrowInState;

import java.util.ArrayList;

import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

import static glengine.yan.glengine.service.ServiceLocator.locateService;

/**
 * Created by Yan-Home on 5/1/2015.
 */
public class ThrowInProcessorListener implements CardsTouchProcessor.CardsTouchProcessorListener {
    private final PlayerCardsTouchProcessorListener mPlayerCardsTouchProcessorListener;

    public ThrowInProcessorListener(final PlayerCardsTouchProcessorListener playerCardsTouchProcessorListener) {
        mPlayerCardsTouchProcessorListener = playerCardsTouchProcessorListener;
    }

    @Override
    public void onSelectedCardTap(final CardNode cardNode) {

    }

    @Override
    public void onDraggedCardReleased(final CardNode cardNode) {

        //if player didn't drag to the field , we will return the card back to his hand
        if (cardNode.getPosition().getY() > (ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight() * BasePlayerCardsTouchProcessorListener.SCENE_HEIGHT_FOR_CARD_RETURN)) {
            mPlayerCardsTouchProcessorListener.returnCardToPlayerHand(cardNode);
            return;
        }

        //cache services
        final GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);

        //add card to the first field pile
        final PileModel emptyFieldPile = findFirstEmptyFieldPile();
        emptyFieldPile.addCard(cardNode.getCard());

        //layout the field pile
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(emptyFieldPile).layout();

        //getState
        final ThrowInState throwInState = (ThrowInState) gameInfo.getActivePlayerState();

        //add chosen card to throw in
        throwInState.getChosenCardsToThrowIn().add(cardNode.getCard());

        //check for finishing condition
        final boolean isReachedMaxCardsToThrowIn = throwInState.getChosenCardsToThrowIn().size() == throwInState.getMaxCardsToThrowInAmount();
        final boolean isSelectedAllPossibleCardsToThrowIn = throwInState.getChosenCardsToThrowIn().size() == throwInState.getAllowedCardsToThrowIn().size();
        if (isReachedMaxCardsToThrowIn || isSelectedAllPossibleCardsToThrowIn) {

            //disable the hand of the player by setting another state
            gameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

            //we want to enable all disabled cards again
            enableAllDisabledCards(throwInState);

            //send the response to server
            ServiceLocator.locateService(GameServerMessageSender.class).sendThrowInResponse(throwInState.getChosenCardsToThrowIn());
        }

        //layout bottom player pile
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(ServiceLocator.locateService(PileManagerService.class)
                .getBottomPlayerPile()).layout();
    }

    private void enableAllDisabledCards(final ThrowInState throwInState) {
        //hide the button
        ServiceLocator.locateService(HudManagementService.class).hideActionButton();

        //cache services
        final PileManagerService pileManagerService = ServiceLocator.locateService(PileManagerService.class);
        final CardNodesManagerService cardNodesManagerService = ServiceLocator.locateService(CardNodesManagerService.class);

        //enable all disabled cards
        final ArrayList<Card> allowedCardsToThrowIn = throwInState.getAllowedCardsToThrowIn();
        for (final Card cardInPile : pileManagerService.getBottomPlayerPile().getCardsInPile()) {
            //in case this cards is not allowed currently to be thrown in
            if (!allowedCardsToThrowIn.contains(cardInPile)) {
                //enable the node back
                final CardNode node = cardNodesManagerService.getCardNodeForCard(cardInPile);
                cardNodesManagerService.enableCardNode(node);
            }
        }
    }

    private PileModel findFirstEmptyFieldPile() {
        for (int i = 0; i < locateService(PileManagerService.class).getFieldPiles().size(); i++) {
            final PileModel pileModel = locateService(PileManagerService.class).getFieldPiles().get(i);
            if (pileModel.getCardsInPile().isEmpty())
                return pileModel;
        }

        return null;
    }

    @Override
    public void onCardDragProgress(final CardNode cardNode) {

    }


}
