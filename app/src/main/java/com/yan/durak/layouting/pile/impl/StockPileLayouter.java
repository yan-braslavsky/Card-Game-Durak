package com.yan.durak.layouting.pile.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.layouting.pile.BasePileLayouter;
import com.yan.durak.services.CardNodesManagerService;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.services.SceneSizeProviderService;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.services.hud.HudNodes;
import com.yan.durak.session.GameInfo;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.service.ServiceLocator;

/**
 * Created by ybra on 20/04/15.
 */
public class StockPileLayouter extends BasePileLayouter {

    /**
     * The scale difference from card original size
     */
    private static final float STOCK_PILE_SIZE_SCALE = 0.7f;
    private static final float STOCK_PILE_CARDS_ROTATION = 95;
    public static final float TRUMP_CARD_ROTATION = -165f;
    public static final float STOCK_PILE_TOP_OFFSET = 0.06f;
    public static final float TRUMP_CARD_TOP_OFFSET = STOCK_PILE_TOP_OFFSET + 0.03f;
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
    public void init(final float sceneWidth, final float sceneHeight) {

        //place directly at the middle of the screen on top and then offset to the right
        final float offset = sceneWidth * 0.04f;
        final float stockPilePositionX = ((sceneWidth - mCardNodesManager.getCardNodeOriginalWidth()) / 2) + offset;
        final float stockPilePositionY = sceneHeight * STOCK_PILE_TOP_OFFSET;

        //cache trump card offset
        mTrumpCardPositionY = sceneHeight * TRUMP_CARD_TOP_OFFSET;

        initRelativeToPosition(stockPilePositionX, stockPilePositionY);
    }

    private void initRelativeToPosition(final float stockPilePositionX, final float stockPilePositionY) {
        for (final Card card : mBoundpile.getCardsInPile()) {
            //get card node representing this pile and layout it
            layoutCardInPile(stockPilePositionX, stockPilePositionY, mCardNodesManager.getCardNodeForCard(card));
        }

        //now position the mask
        final YANTexturedNode maskCardNode = mHudManagementService.getNode(HudNodes.MASK_CARD_INDEX);
        layoutCardInPile(stockPilePositionX, stockPilePositionY, maskCardNode);

        //only difference is that mask is visible and above other card nodes
        maskCardNode.setPosition(stockPilePositionX, stockPilePositionY);
        maskCardNode.setSortingLayer(2);
        maskCardNode.setOpacity(1f);
    }

    private void layoutCardInPile(final float stockPilePositionX, final float stockPilePositionY, final YANTexturedNode cardNode) {
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
            mHudManagementService.getNode(HudNodes.MASK_CARD_INDEX).setOpacity(0f);
        }

        //when trump card is not in the pile , we doing nothing
        if (!mBoundpile.isCardInPile(mGameInfo.getTrumpCard()))
            return;

        //layout the trump card
        final CardNode trumpCardNode = mCardNodesManager.getCardNodeForCard(mGameInfo.getTrumpCard());
        trumpCardNode.setPosition(trumpCardNode.getPosition().getX(), mTrumpCardPositionY);
        trumpCardNode.setOpacity(1f);
        trumpCardNode.setSortingLayer(0);
        trumpCardNode.useFrontTextureRegion();
        trumpCardNode.setRotationZ(TRUMP_CARD_ROTATION);
    }

    public void placeAtRightTop() {
        final SceneSizeProviderService screenSize = ServiceLocator.locateService(SceneSizeProviderService.class);
        final float offsetX = screenSize.getSceneWidth() * 0.06f;
        final float stockPilePositionX = ((screenSize.getSceneWidth() - mCardNodesManager.getCardNodeOriginalWidth())) - offsetX;
        final float stockPilePositionY = screenSize.getSceneHeight() * STOCK_PILE_TOP_OFFSET;
        initRelativeToPosition(stockPilePositionX, stockPilePositionY);
    }
}
