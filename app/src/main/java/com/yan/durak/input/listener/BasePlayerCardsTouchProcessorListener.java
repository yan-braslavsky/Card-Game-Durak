package com.yan.durak.input.listener;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.layouting.pile.impl.FieldPileLayouter;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.physics.YANCollisionDetector;
import com.yan.durak.services.CardNodesManagerService;
import com.yan.durak.services.CardsTouchProcessorService;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.BaseDraggableState;

import java.util.ArrayList;
import java.util.List;

import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.service.ServiceLocator;
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
    protected static final float SCENE_HEIGHT_FOR_CARD_RETURN = 0.5f;

    //Used by children to understand whether they should proceed with drag handling or not
    protected boolean mDragReleaseHandled;

    @Override
    public void onSelectedCardTap(final CardNode cardNode) {
        //just return the card back to player
        returnCardToPlayerHand(cardNode);
    }

    @Override
    public void onDraggedCardReleased(final CardNode cardNode) {

        //when card is released we need to reset remembered dragged card
        ServiceLocator.locateService(CardsTouchProcessorService.class).setDraggedCardNode(null);

        //reset the flag
        mDragReleaseHandled = false;

        //we need to reset dragging state
        final BaseDraggableState draggableState = getActiveDraggableState();
        draggableState.setDragging(false);
        draggableState.setDraggedCardDistanceFromPileField(1f);

        //In case player wanted to reposition cards within his hand
        //We want to allow that kind of interaction
        if (handleRepositioning(cardNode)) {
            mDragReleaseHandled = true;
            return;
        }
    }

    @Override
    public void onCardDragProgress(final CardNode cardNode) {

        //the dragged card is removed from all piles so we need to remember the node during the dragging
        ServiceLocator.locateService(CardsTouchProcessorService.class).setDraggedCardNode(cardNode);

        //we want that dragged card will be above all
        cardNode.setSortingLayer(DRAGGED_CARD_SORTING_LAYER);

        final PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        final GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);
        final PileLayouterManagerService pileLayouterManager = ServiceLocator.locateService(PileLayouterManagerService.class);
        final CardNodesManagerService cardNodesManagerService = ServiceLocator.locateService(CardNodesManagerService.class);

        final BaseDraggableState draggableState;
        if (gameInfo.getActivePlayerState() instanceof BaseDraggableState) {
            draggableState = (BaseDraggableState) gameInfo.getActivePlayerState();
        } else {
            throw new IllegalStateException("The state is not allowing dragging " + gameInfo.getActivePlayerState().getStateDefinition());
        }

        //mark dragging
        draggableState.setDragging(true);
        final float sceneHeight = ServiceLocator.locateService(SceneSizeProviderService.class).getSceneHeight();

        //TODO : implement
        final float screenMiddleY = sceneHeight / 2f;
        final float lowestYPosition = sceneHeight * DRAG_CARDS_HIDING_THRESHOLD;
        final float delta = lowestYPosition - screenMiddleY;

        float distanceFromScreenMiddle = (((cardNode.getPosition().getY() - screenMiddleY) / delta));

        //distance is a percentage , it can be in range 0 to 1
        distanceFromScreenMiddle = YANMathUtils.clamp(distanceFromScreenMiddle, 0f, 1f);

        //adjust dragged card node size
        final float scaleFactor = Math.max(FieldPileLayouter.FIELD_PILE_SIZE_SCALE, distanceFromScreenMiddle);
        final float cardWidth = cardNodesManagerService.getCardNodeOriginalWidth() * scaleFactor;
        final float cardHeight = cardNodesManagerService.getCardNodeOriginalHeight() * scaleFactor;
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
    private boolean handleRepositioning(final CardNode cardNode) {

        //TODO : beautify this code !

        //cache services
        final CardNodesManagerService cardNodesManager = ServiceLocator.locateService(CardNodesManagerService.class);
        final PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);

        //TODO : cache not allocate
        final List<YANBaseNode> bottomPlayerCardNodes = new ArrayList<>();

        for (final Card card : pileManager.getBottomPlayerPile().getCardsInPile()) {
            bottomPlayerCardNodes.add(cardNodesManager.getCardNodeForCard(card));
        }

        //TODO : This detector also allocates array every time , this is not efficient
        final List<CardNode> allCollidedNodes = YANCollisionDetector.findAllNodesThatCollideWithGivenNode(cardNode, bottomPlayerCardNodes);

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
    protected void returnCardToPlayerHand(final CardNode cardNode) {
        final PileModel bottomPlayerPile = ServiceLocator.locateService(PileManagerService.class).getBottomPlayerPile();
        bottomPlayerPile.addCard(cardNode.getCard());
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(bottomPlayerPile).layout();
    }

    protected BaseDraggableState getActiveDraggableState() {
        return (BaseDraggableState) ServiceLocator.locateService(GameInfo.class).getActivePlayerState();
    }
}
