package com.yan.durak.screens;

import com.yan.durak.communication.game_server.LocalGameServer;
import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.game_server.connector.SocketConnectionManager;
import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.listener.PlayerCardsTouchProcessorListener;
import com.yan.durak.models.PileModel;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.services.CardNodesManagerService;
import com.yan.durak.services.CardsTouchProcessorService;
import com.yan.durak.services.DialogManagerService;
import com.yan.durak.services.PileLayouterManagerService;
import com.yan.durak.services.PileManagerService;
import com.yan.durak.services.PlayerMoveService;
import com.yan.durak.services.SceneSizeProviderService;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.session.GameInfo;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANCircleNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.service.ServiceLocator;

import static glengine.yan.glengine.nodes.YANButtonNode.YanButtonNodeClickListener;
import static glengine.yan.glengine.service.ServiceLocator.locateService;

/**
 * Created by Yan-Home on 10/3/2014.
 */
public class PrototypeGameScreen extends BaseGameScreen {

    //communication
    private final IGameServerConnector mGameServerConnector;

    //updatables
    private final TweenManager mSharedTweenManager;


    public PrototypeGameScreen(final YANGLRenderer renderer, final IGameServerConnector gameServerConnector) {
        super(renderer);

        //we received the connector that should be used
        mGameServerConnector = gameServerConnector;

        //tween manager is used for various tween animations
        mSharedTweenManager = new TweenManager();

        //TODO : redefine socket manager
        ServiceLocator.addService(new SocketConnectionManager());

        //service that manages dialogs
        ServiceLocator.addService(new DialogManagerService());

        //service that manages all the HUD nodes
        ServiceLocator.addService(new HudManagementService(mSharedTweenManager));

        //pile manager
        ServiceLocator.addService(new PileManagerService());

        //card nodes manager
        ServiceLocator.addService(new CardNodesManagerService());

        //game session will store the game state and related info
        ServiceLocator.addService(new CardsTouchProcessorService(new CardsTouchProcessor(new PlayerCardsTouchProcessorListener(), ServiceLocator.locateService(PileManagerService.class).getBottomPlayerPile())));

        //game session will store the game state and related info
        ServiceLocator.addService(new GameInfo());

        //layouters manager
        ServiceLocator.addService(new PileLayouterManagerService(mSharedTweenManager));

        //used to send concrete messages to server
        ServiceLocator.addService(new GameServerMessageSender(mGameServerConnector));

        //screen size provider grants an access to screen dimensions
        ServiceLocator.addService(new SceneSizeProviderService());

        //Auto move service helps to make an auto move for player
        ServiceLocator.addService(new PlayerMoveService());

        //TODO : replace "this" by managers that are really required by the processor
        //message processor will receive messages and react on them
        //msg processor is the listener for game server connector
        mGameServerConnector.setListener(new MsgProcessor(this));

        //set timer listener
        ServiceLocator.locateService(HudManagementService.class).setTimerListener(new HudManagementService.TimerListener() {
            @Override
            public void onTimerExpired(final YANCircleNode activeTimerNode) {
                ServiceLocator.locateService(PlayerMoveService.class)
                        .makeAutoMoveForState(ServiceLocator.locateService(GameInfo.class).getActivePlayerState().getStateDefinition());
            }
        });
    }


    @Override
    public void onSetActive() {
        super.onSetActive();
        mGameServerConnector.connect();
    }

    @Override
    public void onSetNotActive() {
        super.onSetNotActive();
        mGameServerConnector.disconnect();
        LocalGameServer.shutDown();
    }

    @Override
    protected void onAddNodesToScene() {
        super.onAddNodesToScene();

        //add card nodes
        for (final YANTexturedNode cardNode : ServiceLocator.locateService(CardNodesManagerService.class).getAllCardNodes()) {
            addNode(cardNode);
        }

        for (final YANBaseNode hudNode : ServiceLocator.locateService(HudManagementService.class).getNodes()) {
            addNode(hudNode);
        }

        for (final YANBaseNode hudNode : ServiceLocator.locateService(DialogManagerService.class).getNodes()) {
            addNode(hudNode);
        }

        //TODO : should be created as hidden by default
        locateService(HudManagementService.class).hideFinishButton();
        locateService(HudManagementService.class).hideTakeButton();
        locateService(DialogManagerService.class).hideExitDialog();
        locateService(DialogManagerService.class).setExitDialogClickListeners(new YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                //when confirm button clicked we are closing the game
                getRenderer().shutDown();
            }
        }, new YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                //When decline button hit , we are simply closing the dialog
                locateService(DialogManagerService.class).hideExitDialog();
            }
        });
    }


    @Override
    protected void onLayoutNodes() {
        super.onLayoutNodes();
        ServiceLocator.locateService(HudManagementService.class).layoutNodes(getSceneSize());
        ServiceLocator.locateService(DialogManagerService.class).layoutNodes(getSceneSize());

        //if we are coming from background we must relayout piles
        relayoutPiles();
    }

    private void relayoutPiles() {
        //we also need to initialize the pile layouter manager
        locateService(PileLayouterManagerService.class).init(getSceneSize().getX(), getSceneSize().getY());

        //if we are coming from background we must relayout piles
        final PileModel topRightPlayerPile = locateService(PileManagerService.class).getTopRightPlayerPile();
        final PileModel topLeftPlayerPile = locateService(PileManagerService.class).getTopLeftPlayerPile();
        final PileModel stockPile = locateService(PileManagerService.class).getStockPile();
        locateService(PileLayouterManagerService.class).getPileLayouterForPile(topRightPlayerPile).layout();
        locateService(PileLayouterManagerService.class).getPileLayouterForPile(topLeftPlayerPile).layout();

        //releayout also field piles
        for (int i = 0; i < locateService(PileManagerService.class).getFieldPiles().size(); i++) {
            final PileModel pileModel = locateService(PileManagerService.class).getFieldPiles().get(i);
            if (!pileModel.getCardsInPile().isEmpty())
                locateService(PileLayouterManagerService.class).getPileLayouterForPile(pileModel).layout();
        }

        //layout stock pile
        locateService(PileLayouterManagerService.class).getPileLayouterForPile(stockPile).layout();
    }


    @Override
    protected void onChangeNodesSize() {
        ServiceLocator.locateService(CardsTouchProcessorService.class).setSceneSize(getSceneSize().getX(), getSceneSize().getY());
        ServiceLocator.locateService(SceneSizeProviderService.class).setSceneSize(getSceneSize().getX(), getSceneSize().getY());
        ServiceLocator.locateService(CardNodesManagerService.class).setNodesSizes(getSceneSize());
        //set size of a card for touch processor
        ServiceLocator.locateService(HudManagementService.class).setNodesSizes(getSceneSize());
        ServiceLocator.locateService(DialogManagerService.class).setNodesSizes(getSceneSize());
    }

    @Override
    protected void onCreateNodes() {
        super.onCreateNodes();
        ServiceLocator.locateService(HudManagementService.class).createNodes(mUiAtlas);
        ServiceLocator.locateService(DialogManagerService.class).createNodes(mDialogsAtlas);
        ServiceLocator.locateService(CardNodesManagerService.class).createNodes(mCardsAtlas);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        ServiceLocator.locateService(DialogManagerService.class).showExitDialog();
    }

    @Override
    public void onUpdate(final float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);

        //TODO: Create some updatable interface where all those
        //can be put into
        mSharedTweenManager.update(deltaTimeSeconds * 1);
        mGameServerConnector.update(deltaTimeSeconds);
        ServiceLocator.locateService(HudManagementService.class).update(deltaTimeSeconds);
    }
}