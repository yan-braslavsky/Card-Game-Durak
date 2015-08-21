package com.yan.durak.input.cards;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.CardsHelper;
import com.yan.durak.nodes.CardNode;

import java.util.ArrayList;
import java.util.List;

import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.util.geometry.YANVector2;

import static com.yan.durak.nodes.CardNode.TAG_TOUCH_DISABLED;
import static com.yan.durak.physics.YANCollisionDetector.findClosestNodeToWorldTouchPoint;
import static glengine.yan.glengine.input.YANInputManager.touchToWorld;
import static glengine.yan.glengine.util.object_pool.YANObjectPool.getInstance;
import static java.lang.System.currentTimeMillis;


/**
 * Created by Yan-Home on 11/21/2014.
 */
public class CardsTouchProcessorDefaultState extends CardsTouchProcessorState {

    private final List<YANBaseNode> mPlayerCardNodes;

    public CardsTouchProcessorDefaultState() {
        super();
        this.mPlayerCardNodes = new ArrayList<>(CardsHelper.MAX_CARDS_IN_DECK);
    }

    @Override
    public void resetState() {
        super.resetState();
        mPlayerCardNodes.clear();
    }


    @Override
    protected boolean onTouchUp(final float normalizedX, final float normalizedY) {
        return false;
    }

    @Override
    protected boolean onTouchDrag(final float normalizedX, final float normalizedY) {
        return false;
    }

    @Override
    protected boolean onTouchDown(final float normalizedX, final float normalizedY) {

        //load the player card nodes
        mPlayerCardNodes.clear();
        for (int i = 0; i < mCardsTouchProcessor.getPlayerPile().getCardsInPile().size(); i++) {
            final Card card = mCardsTouchProcessor.getPlayerPile().getCardsInPile().get(i);
            mPlayerCardNodes.add(mCardsTouchProcessor.getCardNodesManager().getCardNodeForCard(card));
        }

        //adapt to world touch point
        final YANVector2 touchToWorldPoint = touchToWorld(normalizedX, normalizedY,
                mScreenSize.getX(), mScreenSize.getY());

        //find touched card under the touch point
        final CardNode touchedCard = (CardNode) findClosestNodeToWorldTouchPoint(
                touchToWorldPoint.getX(), touchToWorldPoint.getY(), mPlayerCardNodes);
        if (touchedCard == null || touchedCard.containsTag(TAG_TOUCH_DISABLED))
            return false;

        //we need to identify touch time to process tap later
        final long touchTime = currentTimeMillis();

        //move to drag state
        final CardsTouchProcessorDragState dragState = getInstance().obtain(CardsTouchProcessorDragState.class);
        dragState.setCardsTouchProcessor(mCardsTouchProcessor);
        dragState.setDraggedCard(touchedCard);
        dragState.setTouchPositionOffset(touchToWorldPoint.getX() - touchedCard.getPosition().getX(), touchToWorldPoint.getY() - touchedCard.getPosition().getY());
        dragState.setTouchTime(touchTime);
        mCardsTouchProcessor.setCardsTouchProcessorState(dragState);
        return true;

    }

}