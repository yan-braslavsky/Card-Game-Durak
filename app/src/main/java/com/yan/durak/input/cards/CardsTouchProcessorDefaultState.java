package com.yan.durak.input.cards;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.physics.YANCollisionDetector;

import java.util.ArrayList;
import java.util.Collection;

import glengine.yan.glengine.input.YANInputManager;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.object_pool.YANObjectPool;


/**
 * Created by Yan-Home on 11/21/2014.
 */
public class CardsTouchProcessorDefaultState extends CardsTouchProcessorState {

    private final Collection<YANBaseNode> mPlayerCardNodes;

    public CardsTouchProcessorDefaultState() {
        super();
        this.mPlayerCardNodes = new ArrayList<>();
    }

    @Override
    public void resetState() {
        super.resetState();
        mPlayerCardNodes.clear();
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
                mScreenSize.getX(), mScreenSize.getY());

        //find touched card under the touch point
        CardNode touchedCard = (CardNode) YANCollisionDetector.findClosestNodeToWorldTouchPoint(touchToWorldPoint.getX(), touchToWorldPoint.getY(), mPlayerCardNodes);
        if (touchedCard == null || touchedCard.containsTag(CardNode.TAG_TOUCH_DISABLED))
            return false;

        //we need to identify touch time to process tap later
        long touchTime = System.currentTimeMillis();

        //move to drag state
        CardsTouchProcessorDragState dragState = YANObjectPool.getInstance().obtain(CardsTouchProcessorDragState.class);
        dragState.setCardsTouchProcessor(mCardsTouchProcessor);
        dragState.setDraggedCard(touchedCard);
        dragState.setTouchPositionOffset(touchToWorldPoint.getX() - touchedCard.getPosition().getX(), touchToWorldPoint.getY() - touchedCard.getPosition().getY());
        dragState.setTouchTime(touchTime);
        mCardsTouchProcessor.setCardsTouchProcessorState(dragState);
        return true;

    }

}