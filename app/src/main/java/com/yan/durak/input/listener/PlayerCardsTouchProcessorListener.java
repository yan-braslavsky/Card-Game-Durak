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

    private float _previousExpansionLevel = 1f;

    @Override
    public void onSelectedCardTap(CardNode cardNode) {
        //TODO : implement
    }

    @Override
    public void onDraggedCardReleased(CardNode cardNode) {
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

        draggableState.setDragging(true);
        float sceneHeight = ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight();

        //TODO : implement
        float screenMiddleY = sceneHeight / 2f;
        float lowestYPosition = sceneHeight * 0.8f;
        float delta = lowestYPosition - screenMiddleY;

        float dragExpansionLevel = (((cardNode.getPosition().getY() - screenMiddleY) / delta));

        dragExpansionLevel = clamp(dragExpansionLevel, 0f, 1f);

        draggableState.setDraggedCardDistanceFromPileField(dragExpansionLevel);

        //remove card from player pile
        pileManager.getBottomPlayerPile().removeCard(cardNode.getCard());

        //To optimize layout only if major change occurred
//        if (Math.abs(_previousExpansionLevel - dragExpansionLevel) > 0.05f) {
        if (Math.abs(_previousExpansionLevel - dragExpansionLevel) > 0.005f) {
            _previousExpansionLevel = dragExpansionLevel;

            //layout
            pileLayouterManager.getPileLayouterForPile(pileManager.getBottomPlayerPile()).layout();
        }
    }

    //TODO : move to utils
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

}
