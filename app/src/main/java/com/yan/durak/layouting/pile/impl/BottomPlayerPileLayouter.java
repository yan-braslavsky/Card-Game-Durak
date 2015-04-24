package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.CardsLayoutSlot;
import com.yan.durak.layouting.impl.PlayerCardsLayouter;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.managers.PileManager;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.session.GameInfo;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by ybra on 20/04/15.
 */
public class BottomPlayerPileLayouter extends BasePileLayouter {


    private final PlayerCardsLayouter mPlayerCardsLayouter;
    private final GameInfo mGameInfo;

    public BottomPlayerPileLayouter(final GameInfo gameInfo, final PileManager pileManager, final CardNodesManager cardNodesManager, final TweenManager tweenManager, final PileModel boundPile) {
        super(cardNodesManager, tweenManager, boundPile);

        //init player cards layouter , assuming the entire deck can be in his hands
        mPlayerCardsLayouter = new PlayerCardsLayouter(pileManager.getAllCards().size());
        mGameInfo = gameInfo;
    }


    /**
     * Initializes positions and all needed values for layouting
     */
    @Override
    public void init(float sceneWidth, float sceneHeight) {

        //init the player cards layouter
        mPlayerCardsLayouter.init(mCardNodesManager.getCardNodeOriginalWidth(), mCardNodesManager.getCardNodeOriginalHeight(),
                //maximum available width
                sceneWidth,
                //base x position ( center )
                sceneWidth / 2,
                //base y position
                sceneHeight * 0.93f);
    }


    @Override
    public void layout() {

        //TODO:  this is in user testing
        switch (mGameInfo.getmActivePlayerState()) {
            case REQUEST_CARD_FOR_ATTACK:
//            case REQUEST_RETALIATION:
//            case REQUEST_THROW_IN:
                mPlayerCardsLayouter.adjustExpansionLevel(PlayerCardsLayouter.ExpansionLevelPreset.EXPANDED);
                break;
            case PLAYER_DRAGGING_CARD:
                //TODO : this is a bad coupling
                mPlayerCardsLayouter.adjustExpansionLevel(mGameInfo.getDraggingCardExpansionLevel());
                break;
            default:
                mPlayerCardsLayouter.adjustExpansionLevel(PlayerCardsLayouter.ExpansionLevelPreset.COMPACT);
        }

        //update layouter to recalculate positions
        int cardsInPileAmount = getBoundpile().getCardsInPile().size();
        mPlayerCardsLayouter.setActiveSlotsAmount(cardsInPileAmount);

        CardsLayoutSlot slot;
        CardNode cardNode;
        int slotPosition = 0;

        for (Card card : mBoundpile.getCardsInPile()) {
            cardNode = mCardNodesManager.getCardNodeForCard(card);
            slot = mPlayerCardsLayouter.getSlotAtPosition(slotPosition);

            //important to update sorting layer
            cardNode.setSortingLayer(slot.getSortingLayer());

            //as it is the pile of the active player we want all cards to be visible
            cardNode.useFrontTextureRegion();

            //animate card to its place with new transform values
            animateCardNode(cardNode, slot.getPosition().getX(), slot.getPosition().getY(),
                    slot.getRotation(), mCardNodesManager.getCardNodeOriginalWidth(), mCardNodesManager.getCardNodeOriginalHeight(), 1f);

            slotPosition++;
        }
    }
}
