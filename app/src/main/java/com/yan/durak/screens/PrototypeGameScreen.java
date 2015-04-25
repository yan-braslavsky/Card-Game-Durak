package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.listener.PlayerCardsTouchProcessorListener;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.screen_fragments.HudScreenFragment;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.service.services.LayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.service.services.SceneSizeProviderService;
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
//    private final GameServerMessageSender mMessageSender;

    //fragments
    private final HudScreenFragment mHudScreenFragment;

//    //game state
//    private final GameInfo mGameInfo;

    //updatables
    private final TweenManager mSharedTweenManager;

//    //managers
//    private final PileLayouterManager mPileLayouterManager;
//
//    //pile manager
//    private final PileManager mPileManager;
//
//    //card nodes manager
//    private final CardNodesManager mCardNodesManager;


    public PrototypeGameScreen(YANGLRenderer renderer, IGameServerConnector gameServerConnector) {
        super(renderer);

        //we received the connector that should be used
        mGameServerConnector = gameServerConnector;

        //game session will store the game state and related info
//        mGameInfo = new GameInfo();

        ServiceLocator.addService(new GameInfo());

        //tween manager is used for various tween animations
        mSharedTweenManager = new TweenManager();

        //fragment that manages all the HUD nodes
        mHudScreenFragment = new HudScreenFragment(mSharedTweenManager);

        //pile manager
//        mPileManager = new PileManager();

        ServiceLocator.addService(new PileManagerService());

        //card nodes manager
//        mCardNodesManager = new CardNodesManager();

        ServiceLocator.addService(new CardNodesManagerService());

        //layouters manager
//        mPileLayouterManager = new PileLayouterManager(mCardNodesManager, mSharedTweenManager, mPileManager, mGameInfo, mHudScreenFragment);

        ServiceLocator.addService(new LayouterManagerService(mSharedTweenManager, mHudScreenFragment));


        //TODO : set the nodes each time there is a change rather then give it by reference
        //currently we are initializing with empty array , cards will be set every time player pile content changes
        mCardsTouchProcessor = new CardsTouchProcessor(new PlayerCardsTouchProcessorListener(), ServiceLocator.locateService(PileManagerService.class).getBottomPlayerPile());


        //used to send concrete messages to server
//        mMessageSender = new GameServerMessageSender(mGameServerConnector);
        ServiceLocator.addService(new GameServerMessageSender(mGameServerConnector));

        ServiceLocator.addService(new SceneSizeProviderService());

        //TODO : replace "this" by managers that are really required by the processor
        //message processor will receive messages and react on them
        //msg processor is the listener for game server connector
        mGameServerConnector.setListener(new MsgProcessor(this));
    }


    public TweenManager getSharedTweenManager() {
        return mSharedTweenManager;
    }

//    public PileLayouterManager getPileLayouterManager() {
//
//        return mPileLayouterManager;
//    }

//    public PileManager getPileManager() {
//        return mPileManager;
//    }

//    public CardNodesManager getCardNodesManager() {
//        return mCardNodesManager;
//    }

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
        for (YANTexturedNode cardNode : ServiceLocator.locateService(CardNodesManagerService.class).getAllCardNodes()) {
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
        ServiceLocator.locateService(LayouterManagerService.class).init(getSceneSize().getX(), getSceneSize().getY());
    }


    @Override
    protected void onChangeNodesSize() {
        ServiceLocator.locateService(SceneSizeProviderService.class).setSceneSize(getSceneSize().getX(), getSceneSize().getY());
        ServiceLocator.locateService(CardNodesManagerService.class).setNodesSizes(getSceneSize());
        //set size of a card for touch processor
        mHudScreenFragment.setNodesSizes(getSceneSize());
    }

    @Override
    protected void onCreateNodes() {
        super.onCreateNodes();

        mHudScreenFragment.createNodes(mUiAtlas);
        ServiceLocator.locateService(CardNodesManagerService.class).createNodes(mCardsAtlas);

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

//    public GameInfo getGameInfo() {
//
//        return mGameInfo;
//    }

//    public GameServerMessageSender getMessageSender() {
//        return mMessageSender;
//    }

    public CardsTouchProcessor getCardsTouchProcessor() {
        return mCardsTouchProcessor;
    }
}