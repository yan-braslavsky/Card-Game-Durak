package com.yan.durak.input.listener;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.LayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.service.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.BaseDraggableState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;

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

        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        GameServerMessageSender messageSender = ServiceLocator.locateService(GameServerMessageSender.class);
        LayouterManagerService pileLayouterManager = ServiceLocator.locateService(LayouterManagerService.class);
        float sceneHeight = ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight();

        //add card back
        pileManager.getBottomPlayerPile().addCard(cardNode.getCard());

        //if player intended to trhow on the field we will send a message
        if (cardNode.getPosition().getY() < (sceneHeight * 0.6f)) {

            BaseDraggableState draggableState;
            if (gameInfo.getActivePlayerState() instanceof BaseDraggableState) {
                draggableState = (BaseDraggableState) gameInfo.getActivePlayerState();
            } else {
                throw new IllegalStateException("The state is not allowing dragging " + gameInfo.getActivePlayerState().getStateDefinition());
            }

            draggableState.setDraggedCardDistanceFromPileField(1f);
            draggableState.setDragging(false);

            //disable the hand of the player
            gameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

            //we can just send the response
            messageSender.sendCardForAttackResponse(cardNode.getCard());
        } else {
            //layout
            pileLayouterManager.getPileLayouterForPile(pileManager.getBottomPlayerPile()).layout();
        }
    }

    @Override
    public void onCardDragProgress(CardNode cardNode) {
        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        LayouterManagerService pileLayouterManager = ServiceLocator.locateService(LayouterManagerService.class);

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

        draggableState.setDraggedCardDistanceFromPileField(distanceFromScreenMiddle);

        //remove card from player pile
        pileManager.getBottomPlayerPile().removeCard(cardNode.getCard());

        //layout
        pileLayouterManager.getPileLayouterForPile(pileManager.getBottomPlayerPile()).layout();
    }

    //TODO : move to utils
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

}
