package com.yan.durak.screen_fragments.cards;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.entities.cards.CardsHelper;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.tweening.CardsTweenAnimator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.util.YANLogger;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.math.YANMathUtils;

/**
 * Created by Yan-Home on 1/29/2015.
 */
public class CardsScreenFragment implements ICardsScreenFragment {

    private static final int CARDS_COUNT = 36;
    private static final int MAX_CARDS_IN_LINE = 8;

    //pile indexes that will be loaded from the game server
    private int mStockPileIndex;
    private int mDiscardPileIndex;
    private int mBottomPlayerPileIndex;
    private int mTopPlayerToTheRightPileIndex;
    private int mTopLeftPlayerToTheLeftPileIndex;

    private int mTopCardOnFieldSortingLayer = 50;

    private CardsTweenAnimator mCardsTweenAnimator;

    /**
     * Mapping between pile index and array of cards in it
     */
    private Map<Integer, Collection<Card>> mPileIndexToCardListMap;

    /**
     * Mapping between pile index and position of the pile
     */
    private Map<Integer, YANVector2> mPileIndexToPositionMap;

    /**
     * Actual Texture nodes that hold necessary card data
     */
    private Map<Card, CardNode> mCardNodes;

    //cached player texture nodes of cards
    private ArrayList<CardNode> mBottomPlayerCardNodes;
    private ArrayList<CardNode> mTopRightPlayerCardNodes;
    private ArrayList<CardNode> mTopLeftPlayerCardNodes;

    private YANTexturedNode mBackOfCardNode;

    //cached card dimensions
    private float mCardWidth;
    private float mCardHeight;
    private Card mTrumpCard;
    private ICardMovementListener mCardMovementListener;


    public CardsScreenFragment(CardsTweenAnimator cardsTweenAnimator) {

        mCardsTweenAnimator = cardsTweenAnimator;
        mCardNodes = new HashMap<>(CARDS_COUNT);
        mPileIndexToCardListMap = new HashMap<>(CARDS_COUNT / 2);
        mPileIndexToPositionMap = new HashMap<>(CARDS_COUNT / 2);

        //allocate temp array of texture cards
        mBottomPlayerCardNodes = new ArrayList<>(CARDS_COUNT);
        mTopRightPlayerCardNodes = new ArrayList<>(CARDS_COUNT);
        mTopLeftPlayerCardNodes = new ArrayList<>(CARDS_COUNT);
    }

    @Override
    public void createNodes(YANTextureAtlas cardAtlas) {
        mBackOfCardNode = new YANTexturedNode(cardAtlas.getTextureRegion("cards_back.png"));
        ArrayList<Card> cardEntities = CardsHelper.create36Deck();

        for (Card card : cardEntities) {
            String name = "cards_" + card.getSuit() + "_" + card.getRank() + ".png";
            CardNode cardNode = new CardNode(cardAtlas.getTextureRegion(name), mBackOfCardNode.getTextureRegion(), card);
            mCardNodes.put(card, cardNode);

            //hide the card
            cardNode.useBackTextureRegion();
        }

    }


    @Override
    public void setNodesSizes(YANReadOnlyVector2 sceneSize) {
        //cards
        float aspectRatio = mBackOfCardNode.getTextureRegion().getWidth() / mBackOfCardNode.getTextureRegion().getHeight();
        mCardWidth = Math.min(sceneSize.getX(), sceneSize.getY()) / (float) ((MAX_CARDS_IN_LINE) / 2);
        mCardHeight = mCardWidth / aspectRatio;

        //set size for each card
        for (YANTexturedNode cardNode : mCardNodes.values()) {
            cardNode.setSize(mCardWidth, mCardHeight);
        }
    }

    @Override
    public void layoutNodes(YANReadOnlyVector2 sceneSize) {
        //stock pile layout parameters
        float stockPileXPosition = (sceneSize.getX() - mCardWidth) / 2;
        float stockPileYPosition = 0;
        float stockPileScaleSize = 0.7f;

        //init "field piles" ( can be no more than 2 cards)
        for (int i = (mTopLeftPlayerToTheLeftPileIndex + 1); i < CARDS_COUNT / 2; i++) {
            mPileIndexToCardListMap.put(i, new ArrayList<Card>(2));
        }

        //stock pile
        layoutPile(mStockPileIndex, stockPileXPosition, stockPileYPosition, 100, stockPileScaleSize);

        CardNode trumpCardNode = mCardNodes.get(mTrumpCard);

        //layout trump card separately
        layoutCard(stockPileXPosition, stockPileYPosition + mCardHeight / 4, 5, stockPileScaleSize, trumpCardNode);
        trumpCardNode.setSortingLayer(0);
        trumpCardNode.useFrontTextureRegion();

        //discard pile (off the screen)
        layoutPile(mDiscardPileIndex, -sceneSize.getX(), sceneSize.getY() / 2, 90, 1f);

        //player one pile (bottom middle)
        layoutPile(mBottomPlayerPileIndex, (sceneSize.getX() - mCardWidth) / 2, sceneSize.getY() - mCardHeight, 90, 1f);

        //player two pile (top right)
        layoutPile(mTopPlayerToTheRightPileIndex, (sceneSize.getX() - mCardWidth), 0, 90, 1f);

        //player three pile (top left)
        layoutPile(mTopLeftPlayerToTheLeftPileIndex, 0, 0, 90, 1f);

        float leftBorderX = mCardWidth *0.2f;
        float rightBorderX = sceneSize.getX() - (mCardWidth * 0.8f);
        float topBorderY = sceneSize.getY() * 0.3f;
        float bottomBorderY = sceneSize.getY() * 0.5f;

        float xAdvance = mCardWidth * 1.2f;
        float yAdvance = mCardHeight * 1.2f;

        float currentX = leftBorderX;
        float currentY = topBorderY;

        //init "field piles" positions
        for (int i = (mTopLeftPlayerToTheLeftPileIndex + 1); i < CARDS_COUNT / 2; i++) {
            float x = currentX;
            float y = currentY;
            mPileIndexToPositionMap.put(i, new YANVector2(x, y));
            currentX += xAdvance;
            if (currentX > rightBorderX) {
                currentX = leftBorderX;
                currentY += yAdvance;

                //just return to the initial position
                if (bottomBorderY > bottomBorderY) {
                    currentY = topBorderY;
                }
            }
        }
    }

    private void layoutPile(int pileIndex, float x, float y, int rotationZ, float sizeScale) {
        Collection<Card> cardsInStockPile = mPileIndexToCardListMap.get(pileIndex);
        for (Card card : cardsInStockPile) {
            CardNode cardTexturedNode = mCardNodes.get(card);
            layoutCard(x, y, rotationZ, sizeScale, cardTexturedNode);
        }

        //init pile position
        mPileIndexToPositionMap.put(pileIndex, new YANVector2(x, y));
    }

    private void layoutCard(float x, float y, int rotationZ, float sizeScale, CardNode cardTexturedNode) {
        cardTexturedNode.setPosition(x, y);
        cardTexturedNode.setRotationZ(rotationZ);
        cardTexturedNode.setSize(mCardWidth * sizeScale, mCardHeight * sizeScale);
    }

    @Override
    public int getBottomPlayerPileIndex() {
        return mBottomPlayerPileIndex;
    }

    @Override
    public int getTopRightPlayerPileIndex() {
        return mTopPlayerToTheRightPileIndex;
    }

    @Override
    public int getTopLeftPlayerPileIndex() {
        return mTopLeftPlayerToTheLeftPileIndex;
    }

    @Override
    public CardNode findUnderlyingCard(CardNode cardNode) {

        //iterate all field piles
        for (int i = (mTopLeftPlayerToTheLeftPileIndex + 1); i < CARDS_COUNT / 2; i++) {
            Collection<Card> cards = mPileIndexToCardListMap.get(i);

            //we don't want to choose piles that are already covered
            if (cards.size() > 1)
                continue;

            for (Card card : cards) {

                //we dont want to find temporary covered cards
                if (mCardNodes.get(card).getTag() instanceof CardNode.TemporaryCoveredTag)
                    continue;

                if (isCollides(cardNode, mCardNodes.get(card))) {
                    return mCardNodes.get(card);
                }
            }
        }

        YANLogger.warn("underlying Card not Found !");

        return null;
    }

    @Override
    public void removeTagsFromCards() {
        for (CardNode cardNode : mCardNodes.values()) {
            cardNode.setTag(null);
        }
    }

    private boolean isCollides(CardNode cardNode, CardNode card) {
        return cardNode.getBoundingRectangle().contains(card.getBoundingRectangle());
    }

    @Override
    public void setCardMovementListener(ICardMovementListener listener) {
        mCardMovementListener = listener;
    }

    @Override
    public void moveCardFromPileToPile(Card movedCard, int fromPile, int toPile) {

        //remove card entity from its current pile
        mPileIndexToCardListMap.get(fromPile).remove(movedCard);

        //add card entity to its new pile
        mPileIndexToCardListMap.get(toPile).add(movedCard);

        //find node
        CardNode cardNode = mCardNodes.get(movedCard);

        //find destination position
        float destX = mPileIndexToPositionMap.get(toPile).getX();
        float destY = mPileIndexToPositionMap.get(toPile).getY();
        int rotationRange = 15;
        float destRotation = YANMathUtils.randomInRange(-rotationRange, rotationRange);

        //make the animation
        mCardsTweenAnimator.animateCardToValues(cardNode, destX, destY, destRotation, null);
        mCardsTweenAnimator.animateSize(cardNode, mCardWidth, mCardHeight, 0.5f);

        if (fromPile == mBottomPlayerPileIndex || toPile == mBottomPlayerPileIndex || toPile > mTopLeftPlayerToTheLeftPileIndex || toPile == mDiscardPileIndex) {

            //show the card
            cardNode.useFrontTextureRegion();
        } else {
            //hide the card
            cardNode.useBackTextureRegion();
        }

        if (toPile > mTopLeftPlayerToTheLeftPileIndex) {
            //moving to field pile
            //we need to adjust sorting layer
            int sortingLayer = (mPileIndexToCardListMap.get(toPile).size() == 1) ? 1 : 2;
            cardNode.setSortingLayer(sortingLayer);
        }

        if (fromPile > mTopLeftPlayerToTheLeftPileIndex) {
            //we need to adjust sorting layer
            int sortingLayer = (mPileIndexToCardListMap.get(fromPile).size() > 0) ? 2 : 1;
            cardNode.setSortingLayer(sortingLayer);
        }


        //check if card goes to or from player 1 pile
        if (toPile == mBottomPlayerPileIndex || fromPile == mBottomPlayerPileIndex) {

            if (toPile == mBottomPlayerPileIndex) {
                mBottomPlayerCardNodes.add(mCardNodes.get(movedCard));
            } else {
                mBottomPlayerCardNodes.remove(mCardNodes.get(movedCard));
            }

            mCardMovementListener.onCardMovesToBottomPlayerPile();
        }

        //player 2
        else if (toPile == mTopPlayerToTheRightPileIndex || fromPile == mTopPlayerToTheRightPileIndex) {
            if (toPile == mTopPlayerToTheRightPileIndex) {
                mTopRightPlayerCardNodes.add(mCardNodes.get(movedCard));
            } else {
                mTopRightPlayerCardNodes.remove(mCardNodes.get(movedCard));
            }

            mCardMovementListener.onCardMovesToTopRightPlayerPile();

        }

        //player 3
        else if (toPile == mTopLeftPlayerToTheLeftPileIndex || fromPile == mTopLeftPlayerToTheLeftPileIndex) {
            if (toPile == mTopLeftPlayerToTheLeftPileIndex) {
                mTopLeftPlayerCardNodes.add(mCardNodes.get(movedCard));
            } else {
                mTopLeftPlayerCardNodes.remove(mCardNodes.get(movedCard));
            }

            mCardMovementListener.onCardMovesToTopLeftPlayerPile();

        } else {

            mCardMovementListener.onCardMovesToFieldPile();

            //set the sorting layer higher
            mTopCardOnFieldSortingLayer++;
            mCardNodes.get(movedCard).setSortingLayer(mTopCardOnFieldSortingLayer);
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {

    }

    @Override
    public Collection<? extends YANTexturedNode> getFragmentNodes() {
        return mCardNodes.values();
    }

    @Override
    public float getCardNodeWidth() {
        return mCardWidth;
    }

    @Override
    public float getCardNodeHeight() {
        return mCardHeight;
    }

    @Override
    public int getTotalCardsAmount() {
        return CARDS_COUNT;
    }

    @Override
    public ArrayList<CardNode> getBottomPlayerCardNodes() {
        return mBottomPlayerCardNodes;
    }

    @Override
    public ArrayList<CardNode> getTopRightPlayerCardNodes() {
        return mTopRightPlayerCardNodes;
    }

    @Override
    public ArrayList<CardNode> getTopLeftPlayerCardNodes() {
        return mTopLeftPlayerCardNodes;
    }

    @Override
    public void setPilesIndexes(int stockPileIndex, int discardPileIndex, int bottomPlayerPileIndex, int topPlayerToTheRightPileIndex, int topLeftPlayerToTheLeftPileIndex) {
        this.mStockPileIndex = stockPileIndex;
        this.mDiscardPileIndex = discardPileIndex;
        this.mBottomPlayerPileIndex = bottomPlayerPileIndex;
        this.mTopPlayerToTheRightPileIndex = topPlayerToTheRightPileIndex;
        this.mTopLeftPlayerToTheLeftPileIndex = topLeftPlayerToTheLeftPileIndex;

        //clear the map
        mPileIndexToCardListMap.clear();


        ArrayList<Card> cardsCopy = new ArrayList<>(mCardNodes.keySet());

        //allocate card arrays for each pile index
        //put everything in the stock pile
        mPileIndexToCardListMap.put(stockPileIndex, cardsCopy);
        mPileIndexToCardListMap.put(discardPileIndex, new ArrayList<Card>(CARDS_COUNT));
        mPileIndexToCardListMap.put(bottomPlayerPileIndex, new ArrayList<Card>(CARDS_COUNT));
        mPileIndexToCardListMap.put(topPlayerToTheRightPileIndex, new ArrayList<Card>(CARDS_COUNT));
        mPileIndexToCardListMap.put(topLeftPlayerToTheLeftPileIndex, new ArrayList<Card>(CARDS_COUNT));
    }

    @Override
    public void setTrumpCard(Card card) {
        mTrumpCard = card;
    }

    @Override
    public Collection<Card> getCardsInPileWithIndex(int pileIndex) {
        return mPileIndexToCardListMap.get(pileIndex);
    }
}
