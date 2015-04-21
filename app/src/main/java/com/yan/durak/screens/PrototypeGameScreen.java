package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.managers.CardNodesManager;
import com.yan.durak.managers.PileLayouterManager;
import com.yan.durak.managers.PileManager;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.session.GameInfo;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;

/**
 * Created by Yan-Home on 10/3/2014.
 */
public class PrototypeGameScreen extends BaseGameScreen {

    //Player hand touch processor
    private final CardsTouchProcessor mCardsTouchProcessor;

    //communication
    private final IGameServerConnector mGameServerConnector;
    private final GameServerMessageSender mMessageSender;

    //fragments
    private final HudScreenFragment mHudNodesFragment;

    //game state
    private final GameInfo mGameInfo;

    //updatables
    private final TweenManager mSharedTweenManager;

    //managers
    private final PileLayouterManager mPileLayouterManager;

    //pile manager
    private final PileManager mPileManager;

    //card nodes manager
    private final CardNodesManager mCardNodesManager;


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
        mGameInfo = new GameInfo();

        //tween manager is used for various tween animations
        mSharedTweenManager = new TweenManager();

        //fragment that manages all the HUD nodes
        mHudNodesFragment = new HudScreenFragment(mSharedTweenManager);

        //pile manager
        mPileManager = new PileManager();

        //card nodes manager
        mCardNodesManager = new CardNodesManager(mPileManager);

        //layouters manager
        mPileLayouterManager = new PileLayouterManager(mCardNodesManager, mSharedTweenManager, mPileManager);


        //TODO : set the nodes each time there is a change rather then give it by reference
        //currently we are initializing with empty array , cards will be set every time player pile content changes
        mCardsTouchProcessor = new CardsTouchProcessor(/*mCardsScreenFragment.getBottomPlayerCardNodes(), mCardsTweenAnimator*/);

        //TODO : this is very messy . This Listener should be extracted somewhere.
        //set listener to handle touches
        mCardsTouchProcessor.setCardsTouchProcessorListener(new CardsTouchProcessor.CardsTouchProcessorListener() {
            @Override
            public void onSelectedCardTap(CardNode cardNode) {
                //TODO : implement
            }

            @Override
            public void onDraggedCardReleased(CardNode cardNode) {
                //TODO : implement
            }
        });
    }

    public TweenManager getSharedTweenManager() {
        return mSharedTweenManager;
    }

    public PileLayouterManager getPileLayouterManager() {
        return mPileLayouterManager;
    }

    public PileManager getPileManager() {
        return mPileManager;
    }

    public CardNodesManager getCardNodesManager() {
        return mCardNodesManager;
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
        for (YANTexturedNode cardNode : mCardNodesManager.getAllCardNodes()) {
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

        //we also need to initialize the pile manager
        mPileLayouterManager.init(getSceneSize().getX(), getSceneSize().getY());

    }


    @Override
    protected void onChangeNodesSize() {

        mCardNodesManager.setNodesSizes(getSceneSize());

        //set size of a card for touch processor
        mCardsTouchProcessor.setOriginalCardSize(mCardNodesManager.getCardNodeOriginalWidth(), mCardNodesManager.getCardNodeOriginalHeight());

//        //init the player cards layouter
//        mPlayerCardsLayouter.init(mCardsScreenFragment.getCardNodeOriginalWidth(), mCardsScreenFragment.getCardNodeOriginalHeight(),
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
        mCardNodesManager.createNodes(mCardsAtlas);

        mHudNodesFragment.setTakeButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                //TODO : set listener
            }
        });

        mHudNodesFragment.setBitoButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                //TODO : set listener
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
//        mCardsScreenFragment.update(deltaTimeSeconds);
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
//            mCardsTweenAnimator.animateSize(node, mCardsScreenFragment.getCardNodeOriginalWidth() * CARD_SCALE_AMOUNT_OPPONENT, mCardsScreenFragment.getCardNodeOriginalHeight() * CARD_SCALE_AMOUNT_OPPONENT, 0.5f);
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
//            mCardsTweenAnimator.animateSize(node, mCardsScreenFragment.getCardNodeOriginalWidth() * CARD_SCALE_AMOUNT_OPPONENT, mCardsScreenFragment.getCardNodeOriginalHeight() * CARD_SCALE_AMOUNT_OPPONENT, 0.5f);
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
//            mCardsTweenAnimator.animateSize(card, mCardsScreenFragment.getCardNodeOriginalWidth(), mCardsScreenFragment.getCardNodeOriginalHeight(), 0.5f);
//        }
//    }


    public HudScreenFragment getHudNodesFragment() {
        return mHudNodesFragment;
    }

    public CardsTouchProcessor getCardsTouchProcessor() {
        return mCardsTouchProcessor;
    }

    public GameInfo getGameInfo() {
        return mGameInfo;
    }


}