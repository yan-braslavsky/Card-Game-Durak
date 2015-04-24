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
import com.yan.durak.session.states.ActivePlayerState;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.util.loggers.YANLogger;

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
    private final HudScreenFragment mHudScreenFragment;

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

        //game session will store the game state and related info
        mGameInfo = new GameInfo();

        //tween manager is used for various tween animations
        mSharedTweenManager = new TweenManager();

        //fragment that manages all the HUD nodes
        mHudScreenFragment = new HudScreenFragment(mSharedTweenManager);

        //pile manager
        mPileManager = new PileManager();

        //card nodes manager
        mCardNodesManager = new CardNodesManager(mPileManager);

        //layouters manager
        mPileLayouterManager = new PileLayouterManager(mCardNodesManager, mSharedTweenManager, mPileManager, mGameInfo, mHudScreenFragment);


        //TODO : set the nodes each time there is a change rather then give it by reference
        //currently we are initializing with empty array , cards will be set every time player pile content changes
        mCardsTouchProcessor = new CardsTouchProcessor(new CardsTouchProcessor.CardsTouchProcessorListener() {
            @Override
            public void onSelectedCardTap(CardNode cardNode) {
                //TODO : implement
            }

            @Override
            public void onDraggedCardReleased(CardNode cardNode) {
                //TODO : implement
                mGameInfo.setDraggingCardExpansionLevel(1f);

                //add card back
                mPileManager.getBottomPlayerPile().addCard(cardNode.getCard());

//                //layout
//                mPileLayouterManager.getPileLayouterForPile(mPileManager.getBottomPlayerPile()).layout();
                //we can just send the response
                mMessageSender.sendCardForAttackResponse(cardNode.getCard());
            }

            @Override
            public void onCardDragProgress(CardNode cardNode) {
                //TODO : implement
                float screenMiddleY = getSceneSize().getY() / 2f;
                float lowestYPosition = getSceneSize().getY() * 0.9f;
                float delta = lowestYPosition - screenMiddleY;

                mGameInfo.setmActivePlayerState(ActivePlayerState.PLAYER_DRAGGING_CARD);

                float dragExpansionLevel = (((cardNode.getPosition().getY() - screenMiddleY) / delta));

                dragExpansionLevel = clamp(dragExpansionLevel, 0f, 1f);

                mGameInfo.setDraggingCardExpansionLevel(dragExpansionLevel);

                //remove card from player pile
                mPileManager.getBottomPlayerPile().removeCard(cardNode.getCard());

                //layout
                mPileLayouterManager.getPileLayouterForPile(mPileManager.getBottomPlayerPile()).layout();
            }
        }, mCardNodesManager, mPileManager.getBottomPlayerPile());


        //used to send concrete messages to server
        mMessageSender = new GameServerMessageSender(mGameServerConnector);

        //TODO : replace "this" by managers that are really required by the processor
        //message processor will receive messages and react on them
        //msg processor is the listener for game server connector
        mGameServerConnector.setListener(new MsgProcessor(this));
    }

    //TODO : move to utils
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
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

        for (YANTexturedNode hudNode : mHudScreenFragment.getFragmentNodes()) {
            addNode(hudNode);
        }

        mHudScreenFragment.hideBitoButton();
        mHudScreenFragment.hideTakeButton();

    }


    @Override
    protected void onLayoutNodes() {
        super.onLayoutNodes();

        mHudScreenFragment.layoutNodes(getSceneSize());

        //we also need to initialize the pile manager
        mPileLayouterManager.init(getSceneSize().getX(), getSceneSize().getY());

    }


    @Override
    protected void onChangeNodesSize() {

        mCardNodesManager.setNodesSizes(getSceneSize());
        //set size of a card for touch processor
        mHudScreenFragment.setNodesSizes(getSceneSize());
    }

    @Override
    protected void onCreateNodes() {
        super.onCreateNodes();

        mHudScreenFragment.createNodes(mUiAtlas);
        mCardNodesManager.createNodes(mCardsAtlas);

        mHudScreenFragment.setTakeButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                //TODO : set listener
            }
        });

        mHudScreenFragment.setBitoButtonClickListener(new YANButtonNode.YanButtonNodeClickListener() {
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
        mHudScreenFragment.update(deltaTimeSeconds);
    }

    public HudScreenFragment getHudNodesFragment() {
        return mHudScreenFragment;
    }

    public GameInfo getGameInfo() {
        return mGameInfo;
    }

    public GameServerMessageSender getMessageSender() {
        return mMessageSender;
    }

    public CardsTouchProcessor getCardsTouchProcessor() {
        return mCardsTouchProcessor;
    }
}