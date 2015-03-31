package com.yan.durak.screens;

import com.yan.durak.communication.game_server.IGameServerConnector;
import com.yan.durak.communication.game_server.LocalGameServerCommunicator;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.data.RetaliationSetData;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.cards.states.CardsTouchProcessorDefaultState;
import com.yan.durak.input.cards.states.CardsTouchProcessorMultipleChoiceState;
import com.yan.durak.layouting.CardsLayoutSlot;
import com.yan.durak.layouting.CardsLayouter;
import com.yan.durak.layouting.impl.CardsLayouterSlotImpl;
import com.yan.durak.layouting.impl.PlayerCardsLayouter;
import com.yan.durak.layouting.threepoint.ThreePointFanLayouter;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.screen_fragments.cards.CardsScreenFragment;
import com.yan.durak.screen_fragments.cards.ICardsScreenFragment;
import com.yan.durak.screen_fragments.hud.HudScreenFragment;
import com.yan.durak.screen_fragments.hud.IHudScreenFragment;
import com.yan.durak.tweening.CardsTweenAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.util.YANLogger;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by Yan-Home on 10/3/2014.
 */
public class PrototypeGameScreen extends BaseGameScreen {

    public static final float CARD_SCALE_AMOUNT_OPPONENT = 0.6f;

    //Players hand related
    private CardsLayouter mPlayerCardsLayouter;
    private CardsTouchProcessor mCardsTouchProcessor;

    //opponent hand related
    private ThreePointFanLayouter mThreePointFanLayouterTopRightPlayer;
    private ThreePointFanLayouter mThreePointFanLayouterTopLeft;
    private CardsTweenAnimator mCardsTweenAnimator;
    private IGameServerConnector mGameServerConnector;
    private IHudScreenFragment mHudNodesManager;
    private ICardsScreenFragment mCardsScreenFragment;
    private boolean mCardForAttackRequested;
    private boolean mRequestedRetaliation;
    private HashMap<Card, Card> mCardsPendingRetaliationMap;
    private boolean mRequestThrowIn;
    private ArrayList<CardData> mThrowInPossibleCards;
    private int mThrowInCardsAllowed;
    private ArrayList<Card> mSelectedThrowInCards;
    private CardsTouchProcessorMultipleChoiceState mThrowInInputProcessorState;

    public PrototypeGameScreen(YANGLRenderer renderer) {
        super(renderer);

        mCardsPendingRetaliationMap = new HashMap<>();
        mSelectedThrowInCards = new ArrayList<>();
        mThrowInPossibleCards = new ArrayList<>();
        mHudNodesManager = new HudScreenFragment();
        mHudNodesManager.setNodeNodeAttachmentChangeListener(new IHudScreenFragment.INodeAttachmentChangeListener() {
            @Override
            public void onNodeVisibilityChanged(YANTexturedNode node, boolean isVisible) {
                if (isVisible) {
                    addNode(node);
                } else {
                    removeNode(node);
                }
            }
        });


        //TODO : inject game server connector
//        mGameServerConnector = new RemoteGameServerCommunicator();
        mGameServerConnector = new LocalGameServerCommunicator();
        mGameServerConnector.setListener(new IGameServerConnector.IGameServerCommunicatorListener() {
            @Override
            public void handleServerMessage(BaseProtocolMessage serverMessage) {

                //TODO : this is not an efficient way to handle messages
                if (serverMessage.getMessageName().equals(CardMovedProtocolMessage.MESSAGE_NAME)) {
                    handleCardMoveMessage((CardMovedProtocolMessage) serverMessage);
                } else if (serverMessage.getMessageName().equals(RequestCardForAttackMessage.MESSAGE_NAME)) {
                    handleRequestCardForAttackMessage((RequestCardForAttackMessage) serverMessage);
                } else if (serverMessage.getMessageName().equals(RequestRetaliatePilesMessage.MESSAGE_NAME)) {
                    handleRequestRetaliatePilesMessage((RequestRetaliatePilesMessage) serverMessage);
                } else if (serverMessage.getMessageName().equals(GameSetupProtocolMessage.MESSAGE_NAME)) {
                    handleGameSetupMessage((GameSetupProtocolMessage) serverMessage);
                } else if (serverMessage.getMessageName().equals(PlayerTakesActionMessage.MESSAGE_NAME)) {
                    handlePlayerTakesActionMessage((PlayerTakesActionMessage) serverMessage);
                } else if (serverMessage.getMessageName().equals(RetaliationInvalidProtocolMessage.MESSAGE_NAME)) {
                    handleInvalidRetaliationMessage((RetaliationInvalidProtocolMessage) serverMessage);
                } else if (serverMessage.getMessageName().equals(RequestThrowInsMessage.MESSAGE_NAME)) {
                    handleRequestThrowInsMessageMessage((RequestThrowInsMessage) serverMessage);
                }
            }
        });

        mCardsTweenAnimator = new CardsTweenAnimator();
        mCardsScreenFragment = new CardsScreenFragment(mCardsTweenAnimator);
        mCardsScreenFragment.setCardMovementListener(new ICardsScreenFragment.ICardMovementListener() {
            @Override
            public void onCardMovesToBottomPlayerPile() {
                layoutBottomPlayerCards();
            }

            @Override
            public void onCardMovesToTopRightPlayerPile() {
                layoutTopRightPlayerCards();
            }

            @Override
            public void onCardMovesToTopLeftPlayerPile() {
                layoutTopLeftPlayerCards();
            }

            @Override
            public void onCardMovesToFieldPile() {
            }
        });

        //init 3 points layouter to create a fan of opponents hands
        mThreePointFanLayouterTopRightPlayer = new ThreePointFanLayouter(2);
        mThreePointFanLayouterTopLeft = new ThreePointFanLayouter(2);

        //init player cards layouter
        mPlayerCardsLayouter = new PlayerCardsLayouter(mCardsScreenFragment.getTotalCardsAmount());

        //currently we are initializing with empty array , cards will be set every time player pile content changes
        mCardsTouchProcessor = new CardsTouchProcessor(mCardsScreenFragment.getBottomPlayerCardNodes()/*mPlayerOneCardNodes*/, mCardsTweenAnimator);

        //set listener to handle touches
        mCardsTouchProcessor.setCardsTouchProcessorListener(new CardsTouchProcessor.CardsTouchProcessorListener() {
            @Override
            public void onSelectedCardTap(CardNode cardNode) {

                if (mCardForAttackRequested) {
                    mCardForAttackRequested = false;

                    ResponseCardForAttackMessage responseCardForAttackMessage = new ResponseCardForAttackMessage(cardNode.getCard());
                    mGameServerConnector.sentMessageToServer(responseCardForAttackMessage);

                } else if (mRequestThrowIn) {
                    for (CardData throwInPossibleCard : mThrowInPossibleCards) {
                        if (cardNode.getCard().getRank().equals(throwInPossibleCard.getRank()) && cardNode.getCard().getSuit().equals(throwInPossibleCard.getSuit())) {
                            //add selected card
                            mSelectedThrowInCards.add(cardNode.getCard());
                            mThrowInCardsAllowed--;

                            mThrowInInputProcessorState.markCardAsChoosen(cardNode);

                            if (mThrowInCardsAllowed == 0) {
                                sendThrowInResponse();
                            }
                        } else if (mRequestedRetaliation) {
                            //TODO : do nothing
                        } else {
                            layoutBottomPlayerCards();
                        }
                    }
                }
            }

            @Override
            public void onDraggedCardReleased(CardNode cardNode) {
                if (mCardForAttackRequested) {
                    mCardForAttackRequested = false;

                    ResponseCardForAttackMessage responseCardForAttackMessage = new ResponseCardForAttackMessage(cardNode.getCard());
                    mGameServerConnector.sentMessageToServer(responseCardForAttackMessage);

                } else if (mRequestedRetaliation) {

                    //collision detection with card user tries to retaliate
                    CardNode underlyingCard = mCardsScreenFragment.findUnderlyingCard(cardNode);

                    //user dragget the card to a wrong place
                    if (underlyingCard == null) {
                        layoutBottomPlayerCards();
                        return;
                    }

                    //update underlying card with retaliation card
                    mCardsPendingRetaliationMap.put(underlyingCard.getCard(), cardNode.getCard());

                    //move the cardNode on top of the underlying card
                    mCardsTweenAnimator.animateCardToXY(cardNode, underlyingCard.getPosition().getX(), underlyingCard.getPosition().getY(), 0.5f);

                    //we are tagging both cards as covered in order do not test collision with them later
                    underlyingCard.setTag(new CardNode.TemporaryCoveredTag());
                    cardNode.setTag(new CardNode.TemporaryCoveredTag());

                    //check if more retaliation cards left
                    for (Card card : mCardsPendingRetaliationMap.values()) {
                        if (card == null) {
                            return;
                        }
                    }

                    mRequestedRetaliation = false;
                    mHudNodesManager.setTakeButtonAttachedToScreen(false);

                    //send all retaliated piles to server
                    List<List<Card>> list = new ArrayList<>();
                    for (Map.Entry<Card, Card> cardCardEntry : mCardsPendingRetaliationMap.entrySet()) {
                        List<Card> innerList = new ArrayList<>();
                        innerList.add(cardCardEntry.getValue());
                        innerList.add(cardCardEntry.getKey());
                        list.add(innerList);
                    }

                    ResponseRetaliatePilesMessage responseRetaliatePilesMessage = new ResponseRetaliatePilesMessage(list);
                    mGameServerConnector.sentMessageToServer(responseRetaliatePilesMessage);

                    //now we should clear all the tags
                    mCardsScreenFragment.removeTagsFromCards();

                }
//                else if (mRequestThrowIn) {
//                    for (CardData throwInPossibleCard : mThrowInPossibleCards) {
//                        if (cardNode.getCard().getRank().equals(throwInPossibleCard.getRank()) && cardNode.getCard().getSuit().equals(throwInPossibleCard.getSuit())) {
//                            //add selected card
//                            mSelectedThrowInCards.add(cardNode.getCard());
//                            mThrowInCardsAllowed--;
//
//                            if (mThrowInCardsAllowed == 0) {
//                                sendThrowInResponse();
//                            }
//                        } else {
//                            layoutBottomPlayerCards();
//                        }
//                    }
//                }

                else {
                    layoutBottomPlayerCards();
                }
            }
        });
    }

    private void sendThrowInResponse() {
        mCardsTouchProcessor.setCardsTouchProcessorState(new CardsTouchProcessorDefaultState(mCardsTouchProcessor));
        mRequestThrowIn = false;
        mThrowInInputProcessorState = null;
        mHudNodesManager.setFinishButtonAttachedToScreen(false);
        ResponseThrowInsMessage responseRetaliatePilesMessage = new ResponseThrowInsMessage(mSelectedThrowInCards);
        mGameServerConnector.sentMessageToServer(responseRetaliatePilesMessage);
    }

    private void handleRequestThrowInsMessageMessage(RequestThrowInsMessage requestThrowInsMessage) {
        //we attaching finish button to screen
        //player can finish with his throw ins any time by pressing the button
        mHudNodesManager.setFinishButtonAttachedToScreen(true);
        mRequestThrowIn = true;
        mThrowInCardsAllowed = requestThrowInsMessage.getMessageData().getPossibleThrowInCards().size();

        //TODO : Make more efficient !
        ArrayList<CardNode> availableCards = new ArrayList<>();
        for (CardData cardData : requestThrowInsMessage.getMessageData().getPossibleThrowInCards()) {
            for (CardNode playerCardNode : mCardsScreenFragment.getBottomPlayerCardNodes()) {
                if (cardData.getRank().equals(playerCardNode.getCard().getRank()) && cardData.getSuit().equals(playerCardNode.getCard().getSuit())) {
                    availableCards.add(playerCardNode);
                }
            }
        }

        mThrowInInputProcessorState = new CardsTouchProcessorMultipleChoiceState(mCardsTouchProcessor, availableCards);
        mCardsTouchProcessor.setCardsTouchProcessorState(mThrowInInputProcessorState);

        mSelectedThrowInCards.clear();
        mThrowInPossibleCards.clear();
        mThrowInPossibleCards.addAll(requestThrowInsMessage.getMessageData().getPossibleThrowInCards());
    }


    @Override
    public void onSetActive() {
        super.onSetActive();
        mCardsTouchProcessor.register();
        mGameServerConnector.connect();
    }

    @Override
    public void onSetNotActive() {
        super.onSetNotActive();
        mCardsTouchProcessor.unRegister();
        mGameServerConnector.disconnect();
    }

    @Override
    protected void onAddNodesToScene() {
        super.onAddNodesToScene();

        //add card nodes
        for (YANTexturedNode cardNode : mCardsScreenFragment.getFragmentNodes()) {
            addNode(cardNode);
        }

        for (YANTexturedNode hudNode : mHudNodesManager.getFragmentNodes()) {
            addNode(hudNode);
        }

        mHudNodesManager.setFinishButtonAttachedToScreen(false);
        mHudNodesManager.setTakeButtonAttachedToScreen(false);
    }


    @Override
    protected void onLayoutNodes() {
        super.onLayoutNodes();

        mHudNodesManager.layoutNodes(getSceneSize());

        //layout avatars
        float offsetX = getSceneSize().getX() * 0.01f;
        float topOffset = getSceneSize().getY() * 0.07f;

        float aspectRatio = mUiAtlas.getTextureRegion("stump.png").getWidth() / mUiAtlas.getTextureRegion("stump.png").getHeight();
        float avatarWidth = getSceneSize().getX() * 0.2f;
        float avatarHeight = avatarWidth / aspectRatio;
        YANVector2 avatarSize = new YANVector2(avatarWidth, avatarHeight);

        //setup 3 points for player at right top
        float fanDistance = getSceneSize().getX() * 0.05f;

        YANVector2 pos = new YANVector2(getSceneSize().getX() - offsetX, topOffset);
        YANVector2 origin = new YANVector2(pos.getX() - avatarSize.getX(), pos.getY());
        YANVector2 leftBasis = new YANVector2(origin.getX(), origin.getY() + fanDistance);
        YANVector2 rightBasis = new YANVector2(origin.getX() - fanDistance, origin.getY());
        mThreePointFanLayouterTopRightPlayer.setThreePoints(origin, leftBasis, rightBasis);

        pos.setXY(offsetX, topOffset);

        //setup 3 points for player at left top
        origin = new YANVector2(pos.getX() * 4, pos.getY());
        leftBasis = new YANVector2(origin.getX() + fanDistance, origin.getY());
        rightBasis = new YANVector2(origin.getX(), origin.getY() + fanDistance);
        mThreePointFanLayouterTopLeft.setThreePoints(origin, leftBasis, rightBasis);

        //swap direction
        mThreePointFanLayouterTopLeft.setDirection(ThreePointFanLayouter.LayoutDirection.RTL);
    }


    @Override
    protected void onChangeNodesSize() {
        super.onChangeNodesSize();

        mCardsScreenFragment.setNodesSizes(getSceneSize());

        //set size of a card for touch processor
        mCardsTouchProcessor.setOriginalCardSize(/*mCardWidth*/mCardsScreenFragment.getCardNodeWidth(), mCardsScreenFragment.getCardNodeHeight());

        //init the player cards layouter
        mPlayerCardsLayouter.init(mCardsScreenFragment.getCardNodeWidth()/*mCardWidth*/, mCardsScreenFragment.getCardNodeHeight(),
                //maximum available width
                getSceneSize().getX(),
                //base x position ( center )
                getSceneSize().getX() / 2,
                //base y position
                getSceneSize().getY() - mFence.getSize().getY() / 2);

        mHudNodesManager.setNodesSizes(getSceneSize());
    }

    @Override
    protected void onCreateNodes() {
        super.onCreateNodes();
        mHudNodesManager.createNodes(mUiAtlas);
        mCardsScreenFragment.createNodes(mCardsAtlas);

        mHudNodesManager.setTakeButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {

                YANLogger.log("Take Button Clicked");
                //TODO : here we are taking a cardNode
                //remove the flags
                if (mRequestedRetaliation) {
                    mRequestedRetaliation = false;

                    ResponseRetaliatePilesMessage responseRetaliatePilesMessage = new ResponseRetaliatePilesMessage(new ArrayList<List<Card>>());
                    mGameServerConnector.sentMessageToServer(responseRetaliatePilesMessage);

                    mHudNodesManager.setTakeButtonAttachedToScreen(false);
                }
            }
        });

        mHudNodesManager.setBitoButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                if (mRequestThrowIn) {
                    sendThrowInResponse();
                }
            }
        });
    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);
        mGameServerConnector.update();
        mCardsTweenAnimator.update(deltaTimeSeconds * 1);
        mHudNodesManager.update(deltaTimeSeconds);
        mCardsScreenFragment.update(deltaTimeSeconds);
    }


    private void handleInvalidRetaliationMessage(RetaliationInvalidProtocolMessage retaliationInvalidProtocolMessage) {

        //remove from map all invalid retaliations
        for (RetaliationSetData retaliationSetData : retaliationInvalidProtocolMessage.getMessageData().getInvalidRetaliationsList()) {
            mCardsPendingRetaliationMap.remove(new Card(retaliationSetData.getCoveredCardData().getRank(), retaliationSetData.getCoveredCardData().getSuit()));
        }

        ArrayList<CardNode> nodesToRemoveFromPlayerHand = new ArrayList<>();
        for (Card card : mCardsPendingRetaliationMap.values()) {
            //all valid retaliation does not belong to player any more
            for (CardNode cardNode : mCardsScreenFragment.getBottomPlayerCardNodes()) {
                if (cardNode.getCard().equals(card)) {
                    nodesToRemoveFromPlayerHand.add(cardNode);
                }
            }
        }

        //FIXME : that causes visual bug : cards are staying on the field
//        for (CardNode cardNode : nodesToRemoveFromPlayerHand) {
//            mCardsScreenFragment.getBottomPlayerCardNodes().remove(cardNode);
//        }

        layoutBottomPlayerCards();
    }

    private void handlePlayerTakesActionMessage(PlayerTakesActionMessage playerTakesActionMessage) {
        int actionPlayerIndex = playerTakesActionMessage.getMessageData().getPlayerIndex();

        //since we don't have reference to players indexes in the game
        //we translating the player index to pile index
        int actionPlayerPileIndex = (actionPlayerIndex + 2) % 5;
        @IHudScreenFragment.HudNode int cockPosition = IHudScreenFragment.COCK_BOTTOM_RIGHT_INDEX;
        if (actionPlayerPileIndex == mCardsScreenFragment.getBottomPlayerPileIndex()) {
            cockPosition = IHudScreenFragment.COCK_BOTTOM_RIGHT_INDEX;
        } else if (actionPlayerPileIndex == mCardsScreenFragment.getTopRightPlayerPileIndex()) {
            cockPosition = IHudScreenFragment.COCK_TOP_RIGHT_INDEX;
        } else if (actionPlayerPileIndex == mCardsScreenFragment.getTopLeftPlayerPileIndex()) {
            cockPosition = IHudScreenFragment.COCK_TOP_LEFT_INDEX;
        }
        mHudNodesManager.resetCockAnimation(cockPosition);
    }

    private void handleGameSetupMessage(GameSetupProtocolMessage gameSetupProtocolMessage) {

        //TODO : get rid of the statics and make it more generic
        int bottomPlayerPileIndex = gameSetupProtocolMessage.getMessageData().getMyPileIndex();
        int topPlayerToTheRightPileIndex = (bottomPlayerPileIndex + 1) % 5;
        int topLeftPlayerToTheLeftPileIndex = (bottomPlayerPileIndex + 2) % 5;

        //TODO :load all pile indexes from server
        mCardsScreenFragment.setPilesIndexes(0, 1, bottomPlayerPileIndex, topPlayerToTheRightPileIndex, topLeftPlayerToTheLeftPileIndex);

        //extract trump card
        CardData cardData = gameSetupProtocolMessage.getMessageData().getTrumpCard();
        mCardsScreenFragment.setTrumpCard(new Card(cardData.getRank(), cardData.getSuit()));
        mCardsScreenFragment.layoutNodes(getSceneSize());

    }

    private void handleRequestRetaliatePilesMessage(RequestRetaliatePilesMessage requestRetaliatePilesMessage) {
        mRequestedRetaliation = true;

        mCardsPendingRetaliationMap.clear();

        for (List<CardData> cardDataList : requestRetaliatePilesMessage.getMessageData().getPilesBeforeRetaliation()) {
            for (CardData cardData : cardDataList) {
                mCardsPendingRetaliationMap.put(new Card(cardData.getRank(), cardData.getSuit()), null);
            }
        }

        //in that case we want the hud to present us with option to take the card
        mHudNodesManager.setTakeButtonAttachedToScreen(true);
    }

    private void handleRequestCardForAttackMessage(RequestCardForAttackMessage requestCardForAttackMessage) {
        mCardForAttackRequested = true;
    }

    private void handleCardMoveMessage(CardMovedProtocolMessage cardMovedMessage) {
        //extract data
        Card movedCard = new Card(cardMovedMessage.getMessageData().getMovedCard().getRank(), cardMovedMessage.getMessageData().getMovedCard().getSuit());
        int fromPile = cardMovedMessage.getMessageData().getFromPileIndex();
        int toPile = cardMovedMessage.getMessageData().getToPileIndex();

        //execute the move
        mCardsScreenFragment.moveCardFromPileToPile(movedCard, fromPile, toPile);
    }

    private void layoutTopLeftPlayerCards() {

        List<CardsLayouterSlotImpl> slots = new ArrayList<>(mCardsScreenFragment.getTopLeftPlayerCardNodes().size());
        for (int i = 0; i < mCardsScreenFragment.getTopLeftPlayerCardNodes().size(); i++) {
            slots.add(new CardsLayouterSlotImpl());
        }

        //layout the slots
        mThreePointFanLayouterTopLeft.layoutRowOfSlots(slots);

        //make the layouting
        for (int i = 0; i < slots.size(); i++) {
            CardsLayouterSlotImpl slot = slots.get(i);
            YANTexturedNode node = mCardsScreenFragment.getTopLeftPlayerCardNodes().get(i);
            node.setSortingLayer(slot.getSortingLayer());
            //make the animation
            mCardsTweenAnimator.animateCardToValues(node, slot.getPosition().getX(), slot.getPosition().getY(), slot.getRotation(), null);
            mCardsTweenAnimator.animateSize(node, mCardsScreenFragment.getCardNodeWidth() * CARD_SCALE_AMOUNT_OPPONENT, mCardsScreenFragment.getCardNodeHeight() * CARD_SCALE_AMOUNT_OPPONENT, 0.5f);
        }
    }

    private void layoutTopRightPlayerCards() {
        List<CardsLayouterSlotImpl> slots = new ArrayList<>(mCardsScreenFragment.getTopRightPlayerCardNodes().size());
        for (int i = 0; i < mCardsScreenFragment.getTopRightPlayerCardNodes().size(); i++) {
            slots.add(new CardsLayouterSlotImpl());
        }

        //layout the slots
        mThreePointFanLayouterTopRightPlayer.layoutRowOfSlots(slots);

        //make the layouting
        for (int i = 0; i < slots.size(); i++) {
            CardsLayouterSlotImpl slot = slots.get(i);
            YANTexturedNode node = mCardsScreenFragment.getTopRightPlayerCardNodes().get(i);
            node.setSortingLayer(slot.getSortingLayer());
            //make the animation
            mCardsTweenAnimator.animateCardToValues(node, slot.getPosition().getX(), slot.getPosition().getY(), slot.getRotation(), null);
            mCardsTweenAnimator.animateSize(node, mCardsScreenFragment.getCardNodeWidth() * CARD_SCALE_AMOUNT_OPPONENT, mCardsScreenFragment.getCardNodeHeight() * CARD_SCALE_AMOUNT_OPPONENT, 0.5f);
        }
    }

    private void layoutBottomPlayerCards() {
        //update layouter to recalculate positions
        mPlayerCardsLayouter.setActiveSlotsAmount(mCardsScreenFragment.getBottomPlayerCardNodes().size());

        //each index in nodes array corresponds to slot index
        for (int i = 0; i < mCardsScreenFragment.getBottomPlayerCardNodes().size(); i++) {
            CardNode card = mCardsScreenFragment.getBottomPlayerCardNodes().get(i);
            CardsLayoutSlot slot = mPlayerCardsLayouter.getSlotAtPosition(i);
            card.setSortingLayer(slot.getSortingLayer());
            mCardsTweenAnimator.animateCardToSlot(card, slot);
        }
    }
}