package com.yan.durak.input.listener;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.layouting.pile.impl.FieldPileLayouter;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.physics.YANCollisionDetector;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.service.services.PileLayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.service.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.BaseDraggableState;

import java.util.ArrayList;
import java.util.List;

import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.util.math.YANMathUtils;

/**
 * Created by Yan-Home on 5/1/2015.
 */
public abstract class BasePlayerCardsTouchProcessorListener implements CardsTouchProcessor.CardsTouchProcessorListener {

    /**
     * This value is a percentage of the screen height that is used to indicate
     * a point from which cards begin to go down in order to let player
     * better see the field piles
     */
    protected static final float DRAG_CARDS_HIDING_THRESHOLD = 0.75f;
    protected static final int DRAGGED_CARD_SORTING_LAYER = 1000;

    /**
     * Represents percentage of scene height. When card is dragged below this percentage of heigh
     * it will be returned back to player pile.
     */
    private static final float SCENE_HEIGHT_FOR_CARD_RETURN = 0.5f;

    //Used by children to understand whether they should proceed with drag handling or not
    protected boolean mDragReleaseHandled;

    @Override
    public void onSelectedCardTap(CardNode cardNode) {
        //just return the card back to player
        returnCardToPlayerHand(cardNode);
    }

    @Override
    public void onDraggedCardReleased(CardNode cardNode) {

        //reset the flag
        mDragReleaseHandled = false;

        //we need to reset dragging state
        BaseDraggableState draggableState = getActiveDraggableState();
        draggableState.setDragging(false);
        draggableState.setDraggedCardDistanceFromPileField(1f);

        //In case player wanted to reposition cards within his hand
        //We want to allow that kind of interaction
        if (handleRepositioning(cardNode)) {
            mDragReleaseHandled = true;
            return;
        }

        //if player didn't drag to the field , we will return the card back to his hand
        if (cardNode.getPosition().getY() > (ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight() * SCENE_HEIGHT_FOR_CARD_RETURN)) {
            returnCardToPlayerHand(cardNode);
            mDragReleaseHandled = true;
            return;
        }
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
        distanceFromScreenMiddle = YANMathUtils.clamp(distanceFromScreenMiddle, 0f, 1f);

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

    /**
     * Puts a card represented by given cardNode back to bottom player pile.
     * And layouts the pile right after that .
     *
     * @param cardNode node representing the card that will be put into bottom player pile.
     */
    protected void returnCardToPlayerHand(CardNode cardNode) {
        PileModel bottomPlayerPile = ServiceLocator.locateService(PileManagerService.class).getBottomPlayerPile();
        bottomPlayerPile.addCard(cardNode.getCard());
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(bottomPlayerPile).layout();
    }

    protected BaseDraggableState getActiveDraggableState() {
        return (BaseDraggableState) ServiceLocator.locateService(GameInfo.class).getActivePlayerState();
    }
}
