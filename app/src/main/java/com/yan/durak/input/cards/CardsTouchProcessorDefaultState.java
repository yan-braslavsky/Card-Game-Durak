package com.yan.durak.input.cards;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.physics.YANCollisionDetector;

import java.util.ArrayList;
import java.util.Collection;

import glengine.yan.glengine.EngineWrapper;
import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.util.geometry.YANVector2;


/**
 * Created by Yan-Home on 11/21/2014.
 */
class CardsTouchProcessorDefaultState extends CardsTouchProcessorState {

    private final Collection<YANBaseNode> mPlayerCardNodes;

    protected CardsTouchProcessorDefaultState(final CardsTouchProcessor cardsTouchProcessor) {
        super(cardsTouchProcessor);

        this.mPlayerCardNodes = new ArrayList<>();
    }

    @Override
    protected void applyState() {
        //Do nothing
    }

    @Override
    protected boolean onTouchUp(float normalizedX, float normalizedY) {
        return false;
    }

    @Override
    protected boolean onTouchDrag(float normalizedX, float normalizedY) {
        return false;
    }

    @Override
    protected boolean onTouchDown(float normalizedX, float normalizedY) {

        //load the player card nodes
        mPlayerCardNodes.clear();
        for (Card card : mCardsTouchProcessor.getPlayerPile().getCardsInPile()) {
            mPlayerCardNodes.add(mCardsTouchProcessor.getCardNodesManager().getCardNodeForCard(card));
        }

        //adapt to world touch point
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //find touched card under the touch point
        CardNode touchedCard = (CardNode) YANCollisionDetector.findClosestNodeToWorldTouchPoint(touchToWorldPoint.getX(), touchToWorldPoint.getY(), mPlayerCardNodes);
        if (touchedCard == null)
            return false;

        //TODO : pool the state
        //move to drag state
        CardsTouchProcessorDragState dragState = new CardsTouchProcessorDragState(mCardsTouchProcessor);
        dragState.setDraggedCard(touchedCard);
        dragState.setTouchPositionOffset(touchToWorldPoint.getX() - touchedCard.getPosition().getX(), touchToWorldPoint.getY() - touchedCard.getPosition().getY());
        mCardsTouchProcessor.setCardsTouchProcessorState(dragState);
        return true;

    }

}
