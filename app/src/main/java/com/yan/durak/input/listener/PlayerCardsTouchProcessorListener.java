package com.yan.durak.input.listener;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.layouting.pile.impl.FieldPileLayouter;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.service.services.LayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.service.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.BaseDraggableState;
import com.yan.durak.session.states.impl.AttackState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.RetaliationState;

import java.util.ArrayList;
import java.util.List;

import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by Yan-Home on 4/25/2015.
 */
public class PlayerCardsTouchProcessorListener implements CardsTouchProcessor.CardsTouchProcessorListener {

    /**
     * This value is a precentage of the screen height that is used to indicate
     * a point from which cards begin to go down in order to let player
     * better see the field piles
     */
    public static final float DRAG_CARDS_HIDING_THRESHOLD = 0.75f;

    @Override
    public void onSelectedCardTap(CardNode cardNode) {
        //TODO : implement
    }

    @Override
    public void onDraggedCardReleased(CardNode cardNode) {

        //TODO : handle for each dragging state !
        //we don't know in what state player is .
        //When player wants to attack or retaliate , the handling should be different.

        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        if (gameInfo.getActivePlayerState() instanceof RetaliationState) {
            handleRetaliationDragRelease(cardNode);
        } else if (gameInfo.getActivePlayerState() instanceof AttackState) {
            handleAttackDragRelease(cardNode);
        } else {
            throw new IllegalStateException("The state is not expected " + gameInfo.getActivePlayerState().getStateDefinition());
        }


    }

    private void handleAttackDragRelease(CardNode cardNode) {

        //cache services
        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        GameServerMessageSender messageSender = ServiceLocator.locateService(GameServerMessageSender.class);
        LayouterManagerService pileLayouterManager = ServiceLocator.locateService(LayouterManagerService.class);
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);

        //cache screen height
        float sceneHeight = ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight();

        //if player didn't drag to the field , we will return the card back to his hand
        if (cardNode.getPosition().getY() > (sceneHeight * 0.5f)) {

            //add card to the player
            pileManager.getBottomPlayerPile().addCard(cardNode.getCard());

            //layout player cards
            pileLayouterManager.getPileLayouterForPile(pileManager.getBottomPlayerPile()).layout();

            return;
        }

        //add card to the first field pile
        pileManager.getFieldPiles().get(0).addCard(cardNode.getCard());

        //layout the field pile
        pileLayouterManager.getPileLayouterForPile(pileManager.getFieldPiles().get(0)).layout();

        //now remove the card and put it into player pile
        pileManager.getFieldPiles().get(0).removeCard(cardNode.getCard());

        //add card to the player
        pileManager.getBottomPlayerPile().addCard(cardNode.getCard());

        //reset the state
        AttackState draggableState = (AttackState) gameInfo.getActivePlayerState();
        draggableState.resetState();

        //disable the hand of the player by setting another state
        gameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

        //we can just send the response
        messageSender.sendCardForAttackResponse(cardNode.getCard());
    }

    private void handleRetaliationDragRelease(CardNode cardNode) {

        //TODO : Implement
        GameServerMessageSender messageSender = ServiceLocator.locateService(GameServerMessageSender.class);

        //send empty retaliation for now
        messageSender.sendResponseRetaliatePiles(new ArrayList<List<Card>>());
    }

    @Override
    public void onCardDragProgress(CardNode cardNode) {
        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        LayouterManagerService pileLayouterManager = ServiceLocator.locateService(LayouterManagerService.class);
        CardNodesManagerService cardNodesManagerService = ServiceLocator.locateService(CardNodesManagerService.class);

        BaseDraggableState draggableState;
        if (gameInfo.getActivePlayerState() instanceof BaseDraggableState) {
            draggableState = (BaseDraggableState) gameInfo.getActivePlayerState();
        } else {
            throw new IllegalStateException("The state is not allowing dragging " + gameInfo.getActivePlayerState().getStateDefinition());
        }

        //mark dragging
        draggableState.setDragging(true);
        float sceneHeight = ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight();

        //TODO : implement
        float screenMiddleY = sceneHeight / 2f;
        float lowestYPosition = sceneHeight * DRAG_CARDS_HIDING_THRESHOLD;
        float delta = lowestYPosition - screenMiddleY;

        float distanceFromScreenMiddle = (((cardNode.getPosition().getY() - screenMiddleY) / delta));

        //distance is a percentage , it can be in range 0 to 1
        distanceFromScreenMiddle = clamp(distanceFromScreenMiddle, 0f, 1f);

        //adjust dragged card node size
        float scaleFactor = Math.max(FieldPileLayouter.FIELD_PILE_SIZE_SCALE, distanceFromScreenMiddle);
        float cardWidth = cardNodesManagerService.getCardNodeOriginalWidth() * scaleFactor;
        float cardHeight = cardNodesManagerService.getCardNodeOriginalHeight() * scaleFactor;
        cardNode.setSize(cardWidth, cardHeight);

        draggableState.setDraggedCardDistanceFromPileField(distanceFromScreenMiddle);

        //remove card from player pile
        pileManager.getBottomPlayerPile().removeCard(cardNode.getCard());

        //layout
        pileLayouterManager.getPileLayouterForPile(pileManager.getBottomPlayerPile()).layout();

        //TODO : when card is closing cards that should be retaliated , make the glowing around them
    }

    //TODO : move to utils
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

}
