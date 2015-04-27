package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.service.services.HudManagementService;
import com.yan.durak.session.GameInfo;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.nodes.YANTexturedNode;

/**
 * Created by ybra on 20/04/15.
 */
public class StockPileLayouter extends BasePileLayouter {

    /**
     * The scale difference from card original size
     */
    private static final float STOCK_PILE_SIZE_SCALE = 0.7f;
    private static final float STOCK_PILE_CARDS_ROTATION = 95;
    public static final float TRUMP_CARD_ROTATION = 190f;
    public static final float TRUMP_CARD_TOP_OFFSET = 0.055f;
    private final HudManagementService mHudManagementService;
    private final GameInfo mGameInfo;

    //cached positions
    private float mTrumpCardPositionY;


    public StockPileLayouter(final GameInfo gameInfo, final HudManagementService hudManagementService, final CardNodesManagerService cardNodesManager, final TweenManager tweenManager, final PileModel boundPile) {
        super(cardNodesManager, tweenManager, boundPile);

        this.mHudManagementService = hudManagementService;
        this.mGameInfo = gameInfo;
    }

    @Override
    public void init(float sceneWidth, float sceneHeight) {

        //place directly at the middle of the screen on top
        float stockPilePositionX = (sceneWidth - mCardNodesManager.getCardNodeOriginalWidth()) / 2;
        float stockPilePositionY = 0;

        //cache trump card offset
        mTrumpCardPositionY = sceneHeight * TRUMP_CARD_TOP_OFFSET;

        for (Card card : mBoundpile.getCardsInPile()) {
            //get card node representing this pile and layout it
            layoutCardInPile(stockPilePositionX, stockPilePositionY, mCardNodesManager.getCardNodeForCard(card));
        }

        //now position the mask
        YANTexturedNode maskCardNode = mHudManagementService.getNode(HudManagementService.MASK_CARD_INDEX);
        layoutCardInPile(stockPilePositionX, stockPilePositionY, maskCardNode);

        //only difference is that mask is visible and above other card nodes
        maskCardNode.setPosition(stockPilePositionX, stockPilePositionY);
        maskCardNode.setSortingLayer(2);
        maskCardNode.setOpacity(1f);
    }

    private void layoutCardInPile(float stockPilePositionX, float stockPilePositionY, YANTexturedNode cardNode) {
        cardNode.setPosition(stockPilePositionX, stockPilePositionY);
        cardNode.setRotationZ(STOCK_PILE_CARDS_ROTATION);
        cardNode.setSize(mCardNodesManager.getCardNodeOriginalWidth() * STOCK_PILE_SIZE_SCALE, mCardNodesManager.getCardNodeOriginalHeight() * STOCK_PILE_SIZE_SCALE);
        cardNode.setSortingLayer(1);

        //cards are not visible by default , only the mask is visible
        cardNode.setOpacity(0f);
    }

    @Override
    public void layout() {

        //When only one card in stock pile left (which is a trump) mask should be hidden
        if (mBoundpile.getCardsInPile().size() == 1) {
            mHudManagementService.getNode(HudManagementService.MASK_CARD_INDEX).setOpacity(0f);
        }

        //when trump card is not in the pile , we doing nothing
        if (!mBoundpile.isCardInPile(mGameInfo.getTrumpCard()))
            return;

        //layout the trump card
        CardNode trumpCardNode = mCardNodesManager.getCardNodeForCard(mGameInfo.getTrumpCard());
        trumpCardNode.setPosition(trumpCardNode.getPosition().getX(), mTrumpCardPositionY);
        trumpCardNode.setOpacity(1f);
        trumpCardNode.setSortingLayer(0);
        trumpCardNode.useFrontTextureRegion();
        trumpCardNode.setRotationZ(TRUMP_CARD_ROTATION);
    }
}
