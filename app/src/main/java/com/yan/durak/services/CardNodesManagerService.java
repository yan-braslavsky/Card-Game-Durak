package com.yan.durak.services;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.nodes.CardNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.service.IService;
import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.colors.YANColor;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by ybra on 20/04/15.
 */
public class CardNodesManagerService implements IService {

    /**
     * Used to calculate the dimensions of the cards.
     * Exact amount of cards should fit the width of the screen
     */
    private static final int MAX_CARDS_IN_LINE = 8;

    /**
     * Used to show disabled cards
     */
    private static final YANColor CARD_DISABLED_OVERLAY_COLOR = new YANColor(0f, 0f, 0f, 0.5f);

    private final Map<Card, CardNode> mCardToCardNodesMap;
    private final PileManagerService mPileManger;

    //used to return all the nodes
    private Collection<CardNode> mImmutableCardNodes;
    private float mOriginalCardWidth;
    private float mOriginalCardHeight;

    public CardNodesManagerService() {
        final PileManagerService pileManager = ServiceLocator.locateService(PileManagerService.class);
        this.mCardToCardNodesMap = new HashMap<>(pileManager.getAllCards().size());
        this.mPileManger = pileManager;
    }

    /**
     * Returns a card node corresponding to the card provided
     *
     * @param card card logical representation
     * @return card node or null if no such card found
     */
    public CardNode getCardNodeForCard(final Card card) {
        return mCardToCardNodesMap.get(card);
    }

    public float getCardNodeOriginalWidth() {
        return mOriginalCardWidth;
    }

    public float getCardNodeOriginalHeight() {
        return mOriginalCardHeight;
    }

    public void createNodes(final YANTextureAtlas cardAtlas) {

        //create card nodes and flip them face down
        for (final Card card : mPileManger.getAllCards()) {
            final String textureRegionName = "cards_" + card.getSuit() + "_" + card.getRank() + ".png";
            final CardNode cardNode = new CardNode(cardAtlas.getTextureRegion(textureRegionName), cardAtlas.getTextureRegion("cards_back.png"), card);
            mCardToCardNodesMap.put(card, cardNode);

            //hide the card
            cardNode.useBackTextureRegion();
        }

        //wrap created map values in unmodifiable collection
        this.mImmutableCardNodes = Collections.unmodifiableCollection(mCardToCardNodesMap.values());
    }

    public void setNodesSizes(final YANReadOnlyVector2 sceneSize) {

        final CardNode sampleCardNode = getAllCardNodes().iterator().next();
        final float aspectRatio = sampleCardNode.getTextureRegion().getWidth() / sampleCardNode.getTextureRegion().getHeight();

        //cache original cards size
        mOriginalCardWidth = Math.min(sceneSize.getX(), sceneSize.getY()) / (float) ((MAX_CARDS_IN_LINE) / 2);
        mOriginalCardHeight = mOriginalCardWidth / aspectRatio;

        //set size for each card
        for (final YANTexturedNode cardNode : getAllCardNodes()) {
            cardNode.setSize(mOriginalCardWidth, mOriginalCardHeight);
        }
    }

    /**
     * @return an unmodifiable collection of card nodes
     */
    public Collection<CardNode> getAllCardNodes() {
        return mImmutableCardNodes;
    }

    public void disableCardNode(final CardNode cardNode) {
        cardNode.addTag(CardNode.TAG_TOUCH_DISABLED);
        cardNode.setOverlayColor(CARD_DISABLED_OVERLAY_COLOR.getR(), CARD_DISABLED_OVERLAY_COLOR.getG(), CARD_DISABLED_OVERLAY_COLOR.getB(), CARD_DISABLED_OVERLAY_COLOR.getA());
    }

    public void enableCardNode(final CardNode cardNode) {
        cardNode.removeTag(CardNode.TAG_TOUCH_DISABLED);
        cardNode.setOverlayColor(0, 0, 0, 0);
    }

    @Override
    public void clearServiceData() {
        //Does Nothing
    }
}
