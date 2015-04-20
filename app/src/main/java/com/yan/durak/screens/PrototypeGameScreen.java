package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.cards.states.CardsTouchProcessorMultipleChoiceState;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.managers.PileLayouterManager;
import com.yan.durak.managers.PileManager;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.screen_fragments.CardsScreenFragment;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.session.GameSession;
import com.yan.durak.session.states.ActivePlayerState;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;

/**
 * Created by Yan-Home on 10/3/2014.
 */
public class PrototypeGameScreen extends BaseGameScreen {

    //Player hand touch processor
    private CardsTouchProcessor mCardsTouchProcessor;

    //communication
    private IGameServerConnector mGameServerConnector;
    private GameServerMessageSender mMessageSender;

    //fragments
    private HudScreenFragment mHudNodesFragment;
    private CardsScreenFragment mCardsScreenFragment;

    //game state
    private GameSession mGameSession;

    //updatables
    private final TweenManager mSharedTweenManager;

    //managers
    PileLayouterManager mPileLayouterManager;

    //pile manager
    PileManager mPileManager;

    //card nodes manager
    CardNodesManager mCardNodesManager;


    //TODO: Replace this with some kind of cool layouter
    @Deprecated
    private CardsTouchProcessorMultipleChoiceState mThrowInInputProcessorState;


    public PrototypeGameScreen(YANGLRenderer renderer, IGameServerConnector gameServerConnector) {
        super(renderer);

        //we received the connector that should be used
        mGameServerConnector = gameServerConnector;

        //TODO : replace "this" by managers that are really required by the processor
        //message processor will receive messages and react on them
        //msg processor is the listener for game server connector
        mGameServerConnector.setListener(new MsgProcessor(this));

        //used to send concrete messages to server
        mMessageSender = new GameServerMessageSender(mGameServerConnector);

        //game session will store the game state and related info
        mGameSession = new GameSession();

        //tween manager is used for various tween animations
        mSharedTweenManager = new TweenManager();

        //fragment that manages all the HUD nodes
        mHudNodesFragment = new HudScreenFragment(mSharedTweenManager);

        //fragment that manages all card nodes
        mCardsScreenFragment = new CardsScreenFragment();

        //pile manager
        mPileManager = new PileManager();

        //card nodes manager
        mCardNodesManager = new CardNodesManager();

        //layouters manager
        mPileLayouterManager = new PileLayouterManager(mCardNodesManager, mSharedTweenManager);


        //TODO : set the nodes each time there is a change rather then give it by reference
        //currently we are initializing with empty array , cards will be set every time player pile content changes
        mCardsTouchProcessor = new CardsTouchProcessor(/*mCardsScreenFragment.getBottomPlayerCardNodes(), mCardsTweenAnimator*/);

        //TODO : this is very messy . This Listener should be extracted somewhere.
        //set listener to handle touches
        mCardsTouchProcessor.setCardsTouchProcessorListener(new CardsTouchProcessor.CardsTouchProcessorListener() {
            @Override
            public void onSelectedCardTap(CardNode cardNode) {

//                if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_CARD_FOR_ATTACK) {
//                    getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);
//

//                    mMessageSender.sendCardForAttackResponse(cardNode.getCard());
//
//                } else if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_THROW_IN) {
//
//                    for (CardData throwInPossibleCard : getGameSession().getThrowInPossibleCards()) {
//                        if (cardNode.getCard().getRank().equals(throwInPossibleCard.getRank()) && cardNode.getCard().getSuit().equals(throwInPossibleCard.getSuit())) {
//                            //add selected card
//                            getGameSession().getSelectedThrowInCards().add(cardNode.getCard());
//                            mThrowInCardsAllowed--;
//
//                            mThrowInInputProcessorState.markCardAsChoosen(cardNode);
//
//                            if (mThrowInCardsAllowed == 0) {
//                                sendThrowInResponse();
//                            }
//                        } else if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_RETALIATION) {
//                            // do nothing
//                        } else {
////                            layoutBottomPlayerCards();
//                        }
//                    }
//                }
            }

            @Override
            public void onDraggedCardReleased(CardNode cardNode) {
//                if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_CARD_FOR_ATTACK) {
//                    getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);
//
//                    //prevent card from being resized
//                    cardNode.addTag(CardNode.TAG_SHOULD_NOT_RESIZE);
//

//                    mMessageSender.sendCardForAttackResponse(cardNode.getCard());
//
//                } else if (getGameSession().getActivePlayerState() == ActivePlayerState.REQUEST_RETALIATION) {
//
//                    //collision detection with card user tries to retaliate
//                    CardNode underlyingCard = mCardsScreenFragment.findUnderlyingCard(cardNode);
//
//                    //user dragged the card to a wrong place
//                    if (underlyingCard == null) {
////                        layoutBottomPlayerCards();
//                        return;
//                    }
//
//                    //prevent card from being resized
//                    cardNode.addTag(CardNode.TAG_SHOULD_NOT_RESIZE);
//
//                    //update underlying card with retaliation card
//                    getGameSession().getCardsPendingRetaliationMap().put(underlyingCard.getCard(), cardNode.getCard());
//
//                    //move the cardNode on top of the underlying card
////                    mCardsTweenAnimator.animateCardToValues(cardNode, underlyingCard.getPosition().getX(), underlyingCard.getPosition().getY(), CardsScreenFragment.FIELD_CARDS_ROTATION_ANGLE, null);
//
//                    //we are tagging both cards as covered in order do not test collision with them later
//                    underlyingCard.addTag(CardNode.TAG_TEMPORALLY_COVERED);
//                    cardNode.addTag(CardNode.TAG_TEMPORALLY_COVERED);
//
//                    //FIXME : nothing gets animated after retaliation
//                    //now simulate move card from pile to pile
////                    mCardsScreenFragment.moveCardFromPileToPile(cardNode.getCard(), mCardsScreenFragment.getBottomPlayerPileIndex(), mCardsScreenFragment.getPileIndexForCard(underlyingCard.getCard()));
//
//                    //check if more retaliation cards left
//                    for (Card card : getGameSession().getCardsPendingRetaliationMap().values()) {
//                        if (card == null) {
//                            return;
//                        }
//                    }
//
//                    getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);
//                    mHudNodesFragment.hideTakeButton();
//
//                    //send all retaliated piles to server
//                    List<List<Card>> list = new ArrayList<>();
//                    for (Map.Entry<Card, Card> cardCardEntry : getGameSession().getCardsPendingRetaliationMap().entrySet()) {
//                        List<Card> innerList = new ArrayList<>();
//                        innerList.add(cardCardEntry.getValue());
//                        innerList.add(cardCardEntry.getKey());
//                        list.add(innerList);
//                    }
//
//                    mMessageSender.sendResponseRetaliatePiles(list);
//
//                    //now we should clear all the tags
//                    mCardsScreenFragment.removeTagsFromCards();
//                } else {
//
//                    //Card will be returned to place
////                    layoutBottomPlayerCards();
//                }
            }
        });
    }

//    private void sendThrowInResponse() {
//        mCardsTouchProcessor.setCardsTouchProcessorState(new CardsTouchProcessorDefaultState(mCardsTouchProcessor));
//        getGameSession().setActivePlayerState(ActivePlayerState.OTHER_PLAYER_TURN);
//        mThrowInInputProcessorState = null;
//
//        mHudNodesFragment.hideBitoButton();
//
//        mMessageSender.sendThrowInResponse(getGameSession().getSelectedThrowInCards());
//    }


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
        mCardsScreenFragment.layoutNodes(getSceneSize());

        //we also need to initialize the pile manager
        mPileLayouterManager.init(getSceneSize().getX(), getSceneSize().getY());


    }


    @Override
    protected void onChangeNodesSize() {

        mCardsScreenFragment.setNodesSizes(getSceneSize());

        //set size of a card for touch processor
        mCardsTouchProcessor.setOriginalCardSize(mCardsScreenFragment.getCardNodeWidth(), mCardsScreenFragment.getCardNodeHeight());

//        //init the player cards layouter
//        mPlayerCardsLayouter.init(mCardsScreenFragment.getCardNodeWidth(), mCardsScreenFragment.getCardNodeHeight(),
//                //maximum available width
//                getSceneSize().getX(),
//                //base x position ( center )
//                getSceneSize().getX() / 2,
//
//                //TODO : Take the right values , not hard coded
//                //base y position
//                getSceneSize().getY() - /*mFence.getSize().getY() / 2*/100);

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

                    //TODO : optimize
                    mMessageSender.sendResponseRetaliatePiles(new ArrayList<List<Card>>());

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

//    private void layoutTopLeftPlayerCards() {
//
//        List<CardsLayouterSlotImpl> slots = new ArrayList<>(mCardsScreenFragment.getTopLeftPlayerCardNodes().size());
//        for (int i = 0; i < mCardsScreenFragment.getTopLeftPlayerCardNodes().size(); i++) {
//            slots.add(new CardsLayouterSlotImpl());
//        }
//
//        //layout the slots
//        mThreePointFanLayouterTopLeft.layoutRowOfSlots(slots);
//
//        //make the layouting
//        for (int i = 0; i < slots.size(); i++) {
//            CardsLayouterSlotImpl slot = slots.get(i);
//            YANTexturedNode node = mCardsScreenFragment.getTopLeftPlayerCardNodes().get(i);
//            node.setSortingLayer(slot.getSortingLayer());
//            //make the animation
//            mCardsTweenAnimator.animateCardToValues(node, slot.getPosition().getX(), slot.getPosition().getY(), slot.getRotation(), null);
//            mCardsTweenAnimator.animateSize(node, mCardsScreenFragment.getCardNodeWidth() * CARD_SCALE_AMOUNT_OPPONENT, mCardsScreenFragment.getCardNodeHeight() * CARD_SCALE_AMOUNT_OPPONENT, 0.5f);
//        }
//    }

//    private void layoutTopRightPlayerCards() {
//        List<CardsLayouterSlotImpl> slots = new ArrayList<>(mCardsScreenFragment.getTopRightPlayerCardNodes().size());
//        for (int i = 0; i < mCardsScreenFragment.getTopRightPlayerCardNodes().size(); i++) {
//            slots.add(new CardsLayouterSlotImpl());
//        }
//
//        //layout the slots
//        mThreePointFanLayouterTopRightPlayer.layoutRowOfSlots(slots);
//
//        //make the layouting
//        for (int i = 0; i < slots.size(); i++) {
//            CardsLayouterSlotImpl slot = slots.get(i);
//            YANTexturedNode node = mCardsScreenFragment.getTopRightPlayerCardNodes().get(i);
//            node.setSortingLayer(slot.getSortingLayer());
//            //make the animation
//            mCardsTweenAnimator.animateCardToValues(node, slot.getPosition().getX(), slot.getPosition().getY(), slot.getRotation(), null);
//            mCardsTweenAnimator.animateSize(node, mCardsScreenFragment.getCardNodeWidth() * CARD_SCALE_AMOUNT_OPPONENT, mCardsScreenFragment.getCardNodeHeight() * CARD_SCALE_AMOUNT_OPPONENT, 0.5f);
//        }
//    }

//    public void layoutBottomPlayerCards() {
//        //update layouter to recalculate positions
//        mPlayerCardsLayouter.setActiveSlotsAmount(mCardsScreenFragment.getBottomPlayerCardNodes().size());
//
//        //each index in nodes array corresponds to slot index
//        for (int i = 0; i < mCardsScreenFragment.getBottomPlayerCardNodes().size(); i++) {
//            CardNode card = mCardsScreenFragment.getBottomPlayerCardNodes().get(i);
//            CardsLayoutSlot slot = mPlayerCardsLayouter.getSlotAtPosition(i);
//            card.setSortingLayer(slot.getSortingLayer());
//            mCardsTweenAnimator.animateCardToSlot(card, slot);
//
//            //animate card size back to normal
//            card.removeTag(CardNode.TAG_SHOULD_NOT_RESIZE);
//            mCardsTweenAnimator.animateSize(card, mCardsScreenFragment.getCardNodeWidth(), mCardsScreenFragment.getCardNodeHeight(), 0.5f);
//        }
//    }

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