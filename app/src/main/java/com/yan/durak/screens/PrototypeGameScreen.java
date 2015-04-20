package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.data.CardData;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.ResponseThrowInsMessage;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.cards.states.CardsTouchProcessorDefaultState;
import com.yan.durak.input.cards.states.CardsTouchProcessorMultipleChoiceState;
import com.yan.durak.layouting.CardsLayoutSlot;
import com.yan.durak.layouting.CardsLayouter;
import com.yan.durak.layouting.impl.CardsLayouterSlotImpl;
import com.yan.durak.layouting.impl.PlayerCardsLayouter;
import com.yan.durak.layouting.threepoint.ThreePointFanLayouter;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.screen_fragments.CardsScreenFragment;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.session.GameSession;
import com.yan.durak.session.states.ActivePlayerState;
import com.yan.durak.tweening.CardsTweenAnimator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
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
    private HudScreenFragment mHudNodesFragment;
    private CardsScreenFragment mCardsScreenFragment;
    private int mThrowInCardsAllowed;
    private CardsTouchProcessorMultipleChoiceState mThrowInInputProcessorState;
    private MsgProcessor msgProcessor;
    private GameSession mGameSession;
    private final TweenManager mSharedTweenManager;


    public PrototypeGameScreen(YANGLRenderer renderer, IGameServerConnector gameServerConnector) {
        super(renderer);

        //message processor will receive messages and react on them
        msgProcessor = new MsgProcessor(this);

        //game session will store the game state and related info
        mGameSession = new GameSession();

        //tween manager is used for various tween animations
        mSharedTweenManager = new TweenManager();

        mHudNodesFragment = new HudScreenFragment(mSharedTweenManager);

        //we received the connector that should be used
        mGameServerConnector = gameServerConnector;

        //msg processor is the listener for game server connector
        mGameServerConnector.setListener(msgProcessor);

        mCardsTweenAnimator = new CardsTweenAnimator(mSharedTweenManager);
        mCardsScreenFragment = new CardsScreenFragment(mCardsTweenAnimator);

        //TODO : listener should be removed . Instead on card move message should trigger the layouts .
        mCardsScreenFragment.setCardMovementListener(new CardsScreenFragment.ICardMovementListener() {
            @Override
            public void onCardMovesToOrFromBottomPlayerPile() {
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

            @Override
            public void onCardMovesFromStockPile() {
                if (mCardsScreenFragment.getCardsInPileWithIndex(0).size() == 1) {

                    //when only one card left in the stock pile (which is a trump card)
                    //we are removing the mask
                    mHudNodesFragment.removeMaskCard();
                }
            }
        });

        //init 3 points layouter to create a fan of opponents hands
        mThreePointFanLayouterTopRightPlayer = new ThreePointFanLayouter(2);
        mThreePointFanLayouterTopLeft = new ThreePointFanLayouter(2);

        //init player cards layouter
        mPlayerCardsLayouter = new PlayerCardsLayouter(mCardsScreenFragment.getTotalCardsAmount());

        //currently we are initializing with empty array , cards will be set every time player pile content changes
        mCardsTouchProcessor = new CardsTouchProcessor(mCardsScreenFragment.getBottomPlayerCardNodes(), mCardsTweenAnimator);

        //TODO : this is very messy . This Listener should be extracted somewhere.
        //set listener to handle touches
        mCardsTouchProcessor.setCardsTouchProcessorListener(new CardsTouchProcessor.CardsTouchProcessorListener() {
            @Override
            public void onSelectedCardTap(CardNode cardNode) {

                if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_CARD_FOR_ATTACK) {
                    getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);

                    //TODO : Extract to some kind of message sender
                    ResponseCardForAttackMessage responseCardForAttackMessage = new ResponseCardForAttackMessage(cardNode.getCard());
                    mGameServerConnector.sentMessageToServer(responseCardForAttackMessage);

                } else if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_THROW_IN) {

                    for (CardData throwInPossibleCard : getGameSession().getThrowInPossibleCards()) {
                        if (cardNode.getCard().getRank().equals(throwInPossibleCard.getRank()) && cardNode.getCard().getSuit().equals(throwInPossibleCard.getSuit())) {
                            //add selected card
                            getGameSession().getSelectedThrowInCards().add(cardNode.getCard());
                            mThrowInCardsAllowed--;

                            mThrowInInputProcessorState.markCardAsChoosen(cardNode);

                            if (mThrowInCardsAllowed == 0) {
                                sendThrowInResponse();
                            }
                        } else if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_RETALIATION) {
                            // do nothing
                        } else {
                            layoutBottomPlayerCards();
                        }
                    }
                }
            }

            @Override
            public void onDraggedCardReleased(CardNode cardNode) {
                if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_CARD_FOR_ATTACK) {
                    getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);

                    //prevent card from being resized
                    cardNode.addTag(CardNode.TAG_SHOULD_NOT_RESIZE);

                    //TODO : Extract to some kind of message sender
                    ResponseCardForAttackMessage responseCardForAttackMessage = new ResponseCardForAttackMessage(cardNode.getCard());
                    mGameServerConnector.sentMessageToServer(responseCardForAttackMessage);

                } else if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_RETALIATION) {

                    //collision detection with card user tries to retaliate
                    CardNode underlyingCard = mCardsScreenFragment.findUnderlyingCard(cardNode);

                    //user dragged the card to a wrong place
                    if (underlyingCard == null) {
                        layoutBottomPlayerCards();
                        return;
                    }

                    //prevent card from being resized
                    cardNode.addTag(CardNode.TAG_SHOULD_NOT_RESIZE);

                    //update underlying card with retaliation card
                    getGameSession().getCardsPendingRetaliationMap().put(underlyingCard.getCard(), cardNode.getCard());

                    //move the cardNode on top of the underlying card
                    mCardsTweenAnimator.animateCardToValues(cardNode, underlyingCard.getPosition().getX(), underlyingCard.getPosition().getY(), CardsScreenFragment.FIELD_CARDS_ROTATION_ANGLE, null);

                    //we are tagging both cards as covered in order do not test collision with them later
                    underlyingCard.addTag(CardNode.TAG_TEMPORALLY_COVERED);
                    cardNode.addTag(CardNode.TAG_TEMPORALLY_COVERED);

                    //FIXME : nothing gets animated after retaliation
                    //now simulate move card from pile to pile
                    mCardsScreenFragment.moveCardFromPileToPile(cardNode.getCard(), mCardsScreenFragment.getBottomPlayerPileIndex(), mCardsScreenFragment.getPileIndexForCard(underlyingCard.getCard()));

                    //check if more retaliation cards left
                    for (Card card : getGameSession().getCardsPendingRetaliationMap().values()) {
                        if (card == null) {
                            return;
                        }
                    }

                    getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);
                    mHudNodesFragment.hideTakeButton();

                    //send all retaliated piles to server
                    List<List<Card>> list = new ArrayList<>();
                    for (Map.Entry<Card, Card> cardCardEntry : getGameSession().getCardsPendingRetaliationMap().entrySet()) {
                        List<Card> innerList = new ArrayList<>();
                        innerList.add(cardCardEntry.getValue());
                        innerList.add(cardCardEntry.getKey());
                        list.add(innerList);
                    }

                    //TODO : Extract to some kind of message sender
                    ResponseRetaliatePilesMessage responseRetaliatePilesMessage = new ResponseRetaliatePilesMessage(list);
                    mGameServerConnector.sentMessageToServer(responseRetaliatePilesMessage);

                    //now we should clear all the tags
                    mCardsScreenFragment.removeTagsFromCards();
                } else {

                    //Card will be returned to place
                    layoutBottomPlayerCards();
                }
            }
        });
    }

    private void sendThrowInResponse() {
        mCardsTouchProcessor.setCardsTouchProcessorState(new CardsTouchProcessorDefaultState(mCardsTouchProcessor));
        getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);
        mThrowInInputProcessorState = null;

        mHudNodesFragment.hideBitoButton();

        //TODO : Extract to some kind of message sender
        ResponseThrowInsMessage responseRetaliatePilesMessage = new ResponseThrowInsMessage(getGameSession().getSelectedThrowInCards());
        mGameServerConnector.sentMessageToServer(responseRetaliatePilesMessage);
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

        for (YANTexturedNode hudNode : mHudNodesFragment.getFragmentNodes()) {
            addNode(hudNode);
        }

        mHudNodesFragment.hideBitoButton();
        mHudNodesFragment.hideTakeButton();

    }


    @Override
    protected void onLayoutNodes() {
        super.onLayoutNodes();

        mHudNodesFragment.layoutNodes(getSceneSize());

        //TODO : avatars should also be removed to the hud fragment

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

        mCardsScreenFragment.setNodesSizes(getSceneSize());

        //set size of a card for touch processor
        mCardsTouchProcessor.setOriginalCardSize(mCardsScreenFragment.getCardNodeWidth(), mCardsScreenFragment.getCardNodeHeight());

        //init the player cards layouter
        mPlayerCardsLayouter.init(mCardsScreenFragment.getCardNodeWidth(), mCardsScreenFragment.getCardNodeHeight(),
                //maximum available width
                getSceneSize().getX(),
                //base x position ( center )
                getSceneSize().getX() / 2,

                //TODO : Take the right values , not hard coded
                //base y position
                getSceneSize().getY() - /*mFence.getSize().getY() / 2*/100);

        mHudNodesFragment.setNodesSizes(getSceneSize());
    }

    @Override
    protected void onCreateNodes() {
        super.onCreateNodes();

        mHudNodesFragment.createNodes(mUiAtlas);
        mCardsScreenFragment.createNodes(mCardsAtlas);

        mHudNodesFragment.setTakeButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {

                //remove the flags
                if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_RETALIATION) {
                    getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);

                    //TODO : Extract to some kind of message sender
                    ResponseRetaliatePilesMessage responseRetaliatePilesMessage = new ResponseRetaliatePilesMessage(new ArrayList<List<Card>>());
                    mGameServerConnector.sentMessageToServer(responseRetaliatePilesMessage);

                    //at the end we want the button to disappear
                    mHudNodesFragment.hideTakeButton();
                }
            }
        });

        mHudNodesFragment.setBitoButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_THROW_IN) {
                    sendThrowInResponse();
                }
            }
        });
    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);

        //TODO: Create some updatable interface where all those
        //can be put into
        mSharedTweenManager.update(deltaTimeSeconds * 1);
        mGameServerConnector.update(deltaTimeSeconds);
        mHudNodesFragment.update(deltaTimeSeconds);
        mCardsScreenFragment.update(deltaTimeSeconds);
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

    public void layoutBottomPlayerCards() {
        //update layouter to recalculate positions
        mPlayerCardsLayouter.setActiveSlotsAmount(mCardsScreenFragment.getBottomPlayerCardNodes().size());

        //each index in nodes array corresponds to slot index
        for (int i = 0; i < mCardsScreenFragment.getBottomPlayerCardNodes().size(); i++) {
            CardNode card = mCardsScreenFragment.getBottomPlayerCardNodes().get(i);
            CardsLayoutSlot slot = mPlayerCardsLayouter.getSlotAtPosition(i);
            card.setSortingLayer(slot.getSortingLayer());
            mCardsTweenAnimator.animateCardToSlot(card, slot);

            //animate card size back to normal
            card.removeTag(CardNode.TAG_SHOULD_NOT_RESIZE);
            mCardsTweenAnimator.animateSize(card, mCardsScreenFragment.getCardNodeWidth(), mCardsScreenFragment.getCardNodeHeight(), 0.5f);
        }
    }

    public CardsScreenFragment getCardsScreenFragment() {
        return mCardsScreenFragment;
    }

    public HudScreenFragment getHudNodesFragment() {
        return mHudNodesFragment;
    }

    public void setThrowInCardsAllowed(int mThrowInCardsAllowed) {
        this.mThrowInCardsAllowed = mThrowInCardsAllowed;
    }

    public CardsTouchProcessorMultipleChoiceState getThrowInInputProcessorState() {
        return mThrowInInputProcessorState;
    }

    public void setThrowInInputProcessorState(CardsTouchProcessorMultipleChoiceState mThrowInInputProcessorState) {
        this.mThrowInInputProcessorState = mThrowInInputProcessorState;
    }

    public CardsTouchProcessor getCardsTouchProcessor() {
        return mCardsTouchProcessor;
    }

    public GameSession getGameSession() {
        return mGameSession;
    }
}