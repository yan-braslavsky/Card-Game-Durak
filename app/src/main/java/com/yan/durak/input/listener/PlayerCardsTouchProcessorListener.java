package com.yan.durak.input.listener;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.layouting.pile.impl.FieldPileLayouter;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.physics.YANCollisionDetector;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.service.services.HudManagementService;
import com.yan.durak.service.services.PileLayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.service.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.BaseDraggableState;
import com.yan.durak.session.states.impl.AttackState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.RetaliationState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import glengine.yan.glengine.nodes.YANBaseNode;
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
    public static final int DRAGGED_CARD_SORTING_LAYER = 1000;

    @Override
    public void onSelectedCardTap(CardNode cardNode) {

        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        BaseDraggableState draggableState = (BaseDraggableState) gameInfo.getActivePlayerState();

        //we need to reset dragging state
        draggableState.setDragging(false);
        draggableState.setDraggedCardDistanceFromPileField(0f);

        //just return the card back to player
        returnCardToPlayerHand(cardNode);
    }

    @Override
    public void onDraggedCardReleased(CardNode cardNode) {

        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        BaseDraggableState draggableState = (BaseDraggableState) gameInfo.getActivePlayerState();

        //we need to reset dragging state
        draggableState.setDragging(false);
        draggableState.setDraggedCardDistanceFromPileField(1f);

        //cache screen height
        float sceneHeight = ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight();

        //In case player wanted to reposition cards within his hand
        //We want to allow that kind of interaction
        if (handleRepositioning(cardNode)) {
            return;
        }

        //if player didn't drag to the field , we will return the card back to his hand
        if (cardNode.getPosition().getY() > (sceneHeight * 0.5f)) {
            returnCardToPlayerHand(cardNode);
            return;
        }

        if (gameInfo.getActivePlayerState() instanceof RetaliationState) {
            handleRetaliationDragRelease(cardNode);
        } else if (gameInfo.getActivePlayerState() instanceof AttackState) {
            handleAttackDragRelease(cardNode);
        } else {
            throw new IllegalStateException("The state is not expected " + gameInfo.getActivePlayerState().getStateDefinition());
        }
    }

    /**
     * Detects and handles repositioning of cards within the player hand
     *
     * @param cardNode dragged card that was released
     * @return true if there was a repositioning and false otherwise
     */
    private boolean handleRepositioning(CardNode cardNode) {

        //TODO : beautify this code !

        //cache services
        CardNodesManagerService cardNodesManager = ServiceLocator.locateService(CardNodesManagerService.class);
        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);

        //TODO : cache not allocate
        List<YANBaseNode> bottomPlayerCardNodes = new ArrayList<>();

        for (Card card : pileManager.getBottomPlayerPile().getCardsInPile()) {
            bottomPlayerCardNodes.add(cardNodesManager.getCardNodeForCard(card));
        }

        //TODO : This detector also allocates array every time , this is not efficient
        List<CardNode> allCollidedNodes = YANCollisionDetector.findAllNodesThatCollideWithGivenNode(cardNode, bottomPlayerCardNodes);

        if (allCollidedNodes.isEmpty())
            return false;

        int repositionIndex = -1;

        //if three cards then put instead of the middle card
        if (allCollidedNodes.size() >= 3) {
            repositionIndex = pileManager.getBottomPlayerPile().getCardsInPile().indexOf(allCollidedNodes.get(2).getCard());
        }
        //if 2 put between them
        else if (allCollidedNodes.size() == 2) {
            repositionIndex = pileManager.getBottomPlayerPile().getCardsInPile().indexOf(allCollidedNodes.get(1).getCard());
        }
        //if only one card , put instead of it
        else if (allCollidedNodes.size() == 1) {
            repositionIndex = pileManager.getBottomPlayerPile().getCardsInPile().indexOf(allCollidedNodes.get(0).getCard());

            //in case player wanted to put into the end of the pile
            if (repositionIndex == (bottomPlayerCardNodes.size() - 1))
                repositionIndex = bottomPlayerCardNodes.size();
        }


        pileManager.getBottomPlayerPile().addCardAtIndex(cardNode.getCard(), repositionIndex);

        //layout player cards
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(pileManager.getBottomPlayerPile()).layout();
        return true;
    }

    private void handleAttackDragRelease(CardNode cardNode) {

        //cache services
        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        GameServerMessageSender messageSender = ServiceLocator.locateService(GameServerMessageSender.class);
        PileLayouterManagerService pileLayouterManager = ServiceLocator.locateService(PileLayouterManagerService.class);
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);

        //cache screen height
        float sceneHeight = ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight();

        //if player didn't drag to the field , we will return the card back to his hand
        if (cardNode.getPosition().getY() > (sceneHeight * 0.5f)) {
            returnCardToPlayerHand(cardNode);
            return;
        }

        //add card to the first field pile
        pileManager.getFieldPiles().get(0).addCard(cardNode.getCard());

        //layout the field pile
        pileLayouterManager.getPileLayouterForPile(pileManager.getFieldPiles().get(0)).layout();

        //reset the state
        AttackState draggableState = (AttackState) gameInfo.getActivePlayerState();
        draggableState.resetState();

        //disable the hand of the player by setting another state
        gameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

        //we can just send the response
        messageSender.sendCardForAttackResponse(cardNode.getCard());
    }

    private void handleRetaliationDragRelease(CardNode cardNode) {

        //we must see what is the card node that our card collides with
        CardNode collidedFieldCardNode = findUnderlyingCard(cardNode);

        if (collidedFieldCardNode == null) {
            //card will be returned to player pile
            returnCardToPlayerHand(cardNode);
            return;
        }

        //cache retaliation state
        RetaliationState retaliationState = (RetaliationState) ServiceLocator.locateService(GameInfo.class).getActivePlayerState();

        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);

        //get the pile which contains the collided card
        PileModel fieldPile = pileManager.getFieldPileWithCard(collidedFieldCardNode.getCard());

        //add the dragged card into that field pile
        fieldPile.addCard(cardNode.getCard());

        //layout the field pile
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(fieldPile).layout();


        //update the retaliation state
        Iterator<RetaliationState.RetaliationSet> iterator = retaliationState.getPendingRetaliationCardSets().iterator();
        while (iterator.hasNext()) {
            RetaliationState.RetaliationSet retaliationSet = iterator.next();

            //find the set that contains the covered card
            if (retaliationSet.getCoveredCard().equals(collidedFieldCardNode.getCard())) {

                //remove this set from the collection
                iterator.remove();

                //update set with a covering card
                retaliationSet.setCoveringCard(cardNode.getCard());

                //move the set to retaliated piles set
                retaliationState.getRetaliatedCardSets().add(retaliationSet);
                break;
            }
        }

        //now if all piles are retaliated , send a message
        if (retaliationState.getPendingRetaliationCardSets().isEmpty()) {

            //TODO : cache , not allocate
            ArrayList<List<Card>> listOfPiles = new ArrayList<>();

            for (RetaliationState.RetaliationSet retaliationSet : retaliationState.getRetaliatedCardSets()) {

                //TODO : cache , not allocate
                ArrayList<Card> innerRetSet = new ArrayList<>(2);
                innerRetSet.add(retaliationSet.getCoveredCard());
                innerRetSet.add(retaliationSet.getCoveringCard());
                listOfPiles.add(innerRetSet);
            }

            //hide the take button
            ServiceLocator.locateService(HudManagementService.class).hideTakeButton();

            //send the response
            ServiceLocator.locateService(GameServerMessageSender.class).sendResponseRetaliatePiles(listOfPiles);
        } else {

            //currently player cards are down , we need to raise them up by relayouting
            //layout player cards
            ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(pileManager.getBottomPlayerPile()).layout();
        }
    }

    private void returnCardToPlayerHand(CardNode cardNode) {

        //cache pile manager service
        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        //add card to the player
        pileManager.getBottomPlayerPile().addCard(cardNode.getCard());

        //layout player cards
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(pileManager.getBottomPlayerPile()).layout();
    }

    private CardNode findUnderlyingCard(CardNode cardNode) {

        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        CardNodesManagerService cardNodesManagerService = ServiceLocator.locateService(CardNodesManagerService.class);

        for (PileModel pileModel : pileManager.getFieldPiles()) {

            //we skipping already retaliated piles (they have more than 2 cards)
            if (pileModel.getCardsInPile().isEmpty() || pileModel.getCardsInPile().size() > 1)
                continue;

            //this can be possible collide card
            CardNode collisionTestCardNode = cardNodesManagerService.getCardNodeForCard(pileModel.getCardsInPile().iterator().next());

            //return the tested card if it collides
            if (YANCollisionDetector.areTwoNodesCollide(cardNode, collisionTestCardNode))
                return collisionTestCardNode;
        }

        return null;
    }

    @Override
    public void onCardDragProgress(CardNode cardNode) {

        //we want that dragged card will be above all
        cardNode.setSortingLayer(DRAGGED_CARD_SORTING_LAYER);

        PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        PileLayouterManagerService pileLayouterManager = ServiceLocator.locateService(PileLayouterManagerService.class);
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
