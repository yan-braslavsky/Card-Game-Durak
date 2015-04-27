package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.listener.PlayerCardsTouchProcessorListener;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.service.services.HudManagementService;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.CardNodesManagerService;
import com.yan.durak.service.services.PileLayouterManagerService;
import com.yan.durak.service.services.PileManagerService;
import com.yan.durak.service.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;

import aurelienribon.tweenengine.TweenManager;
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
//    private final HudManagementService mHudManagementService;

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

        //service that manages all the HUD nodes
        ServiceLocator.addService(new HudManagementService(mSharedTweenManager));

        //pile manager
//        mPileManager = new PileManager();

        ServiceLocator.addService(new PileManagerService());

        //card nodes manager
//        mCardNodesManager = new CardNodesManager();

        ServiceLocator.addService(new CardNodesManagerService());

        //layouters manager
//        mPileLayouterManager = new PileLayouterManager(mCardNodesManager, mSharedTweenManager, mPileManager, mGameInfo, mHudScreenFragment);

        ServiceLocator.addService(new PileLayouterManagerService(mSharedTweenManager));


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

        for (YANTexturedNode hudNode : ServiceLocator.locateService(HudManagementService.class).getCardNodes()) {
            addNode(hudNode);
        }

        //TODO : should be created as hidden by default
        ServiceLocator.locateService(HudManagementService.class).hideBitoButton();
        ServiceLocator.locateService(HudManagementService.class).hideTakeButton();
    }


    @Override
    protected void onLayoutNodes() {
        super.onLayoutNodes();
        ServiceLocator.locateService(HudManagementService.class).layoutNodes(getSceneSize());
        //we also need to initialize the pile manager
        ServiceLocator.locateService(PileLayouterManagerService.class).init(getSceneSize().getX(), getSceneSize().getY());
    }


    @Override
    protected void onChangeNodesSize() {
        ServiceLocator.locateService(SceneSizeProviderService.class).setSceneSize(getSceneSize().getX(), getSceneSize().getY());
        ServiceLocator.locateService(CardNodesManagerService.class).setNodesSizes(getSceneSize());
        //set size of a card for touch processor
        ServiceLocator.locateService(HudManagementService.class).setNodesSizes(getSceneSize());
    }

    @Override
    protected void onCreateNodes() {
        super.onCreateNodes();

        ServiceLocator.locateService(HudManagementService.class).createNodes(mUiAtlas);
        ServiceLocator.locateService(CardNodesManagerService.class).createNodes(mCardsAtlas);

    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);

        //TODO: Create some updatable interface where all those
        //can be put into
        mSharedTweenManager.update(deltaTimeSeconds * 1);
        mGameServerConnector.update(deltaTimeSeconds);
        ServiceLocator.locateService(HudManagementService.class).update(deltaTimeSeconds);
    }

    public CardsTouchProcessor getCardsTouchProcessor() {
        return mCardsTouchProcessor;
    }
}