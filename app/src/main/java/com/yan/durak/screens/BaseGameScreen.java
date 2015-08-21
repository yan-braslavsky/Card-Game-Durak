package com.yan.durak.screens;

import glengine.yan.glengine.assets.YANAssetManager;
import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANTextNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.screens.YANNodeScreen;
import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.colors.YANColor;
import glengine.yan.glengine.util.loggers.YANFPSLogger;

/**
 * Created by Yan-Home on 11/21/2014.
 */
public abstract class BaseGameScreen extends YANNodeScreen {

    /**
     * Solid background that is used to clear frame buffer
     */
    private static final int BG_HEX_COLOR = 0x9F9E36;

    //names of used assets
    public static final String UI_ATLAS_NAME = "ui_atlas";
    public static final String CARDS_ATLAS_NAME = "cards_atlas";
    public static final String DIALOGS_ATLAS_NAME = "dialogs_atlas";
    public static final String STANDARD_FONT_NAME = "standard_font";
    public static final String SPEECH_BUBBLES_FONT_NAME = "chunkfive";
    public static final String PLAYERS_NAMES_FONT_NAME = "Izhitsa";

    //texture atlases
    protected final YANTextureAtlas mUiAtlas;
    protected final YANTextureAtlas mCardsAtlas;
    protected final YANTextureAtlas mDialogsAtlas;

    //TODO : remove on production
    //Used to log FPS data on screen
    YANTextNode mFpsTextNode;
    YANFPSLogger mFPSLogger;

    public BaseGameScreen(final YANGLRenderer renderer) {
        super(renderer);

        //setup fps logger
        mFPSLogger = new YANFPSLogger();
        mFPSLogger.setFPSLoggerListener(new YANFPSLogger.FPSLoggerListener() {
            @Override
            public void onValueChange(final int newFpsValue) {
                mFpsTextNode.setText("FPS " + newFpsValue);
            }
        });

        //load atlases
        mUiAtlas = ServiceLocator.locateService(YANAssetManager.class).getLoadedAtlas(UI_ATLAS_NAME);
        mCardsAtlas = ServiceLocator.locateService(YANAssetManager.class).getLoadedAtlas(CARDS_ATLAS_NAME);
        mDialogsAtlas = ServiceLocator.locateService(YANAssetManager.class).getLoadedAtlas(DIALOGS_ATLAS_NAME);
    }

    @Override
    protected void onAddNodesToScene() {
        addNode(mFpsTextNode);
    }

    @Override
    protected void onLayoutNodes() {
        //fps node
        mFpsTextNode.setPosition(150, 50);
    }

    @Override
    protected void onCreateNodes() {

        //create a text node
        mFpsTextNode = new YANTextNode(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(STANDARD_FONT_NAME), "FPS 1000".length());
        mFpsTextNode.setText("FPS " + 0);
        mFpsTextNode.setSortingLayer(5000);
    }

    @Override
    public void onSetActive() {
        super.onSetActive();
        getRenderer().setRendererBackgroundColor(YANColor.createFromHexColor(BG_HEX_COLOR));

        //for efficiency reasons we are not loading texture into openGL until we are need it
        ServiceLocator.locateService(YANAssetManager.class).loadTexture(mUiAtlas.getAtlasImageFilePath());
        ServiceLocator.locateService(YANAssetManager.class).loadTexture(mCardsAtlas.getAtlasImageFilePath());
        ServiceLocator.locateService(YANAssetManager.class).loadTexture(mDialogsAtlas.getAtlasImageFilePath());

        //load font atlas into a memory
        ServiceLocator.locateService(YANAssetManager.class).loadTexture(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(STANDARD_FONT_NAME).getGlyphImageFilePath());
        ServiceLocator.locateService(YANAssetManager.class).loadTexture(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(SPEECH_BUBBLES_FONT_NAME).getGlyphImageFilePath());
        ServiceLocator.locateService(YANAssetManager.class).loadTexture(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(PLAYERS_NAMES_FONT_NAME).getGlyphImageFilePath());
    }

    @Override
    public void onSetNotActive() {
        super.onSetNotActive();

        //for efficiency reasons we are deleting loaded texture into openGL
        ServiceLocator.locateService(YANAssetManager.class).unloadTexture(mUiAtlas.getAtlasImageFilePath());
        ServiceLocator.locateService(YANAssetManager.class).unloadTexture(mCardsAtlas.getAtlasImageFilePath());
        ServiceLocator.locateService(YANAssetManager.class).unloadTexture(mDialogsAtlas.getAtlasImageFilePath());

        //release atlas font from a memory
        ServiceLocator.locateService(YANAssetManager.class).unloadTexture(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(STANDARD_FONT_NAME).getGlyphImageFilePath());
        ServiceLocator.locateService(YANAssetManager.class).unloadTexture(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(SPEECH_BUBBLES_FONT_NAME).getGlyphImageFilePath());
        ServiceLocator.locateService(YANAssetManager.class).unloadTexture(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(PLAYERS_NAMES_FONT_NAME).getGlyphImageFilePath());
    }

    @Override
    public void onUpdate(final float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);

        //TODO : put to updatable list
        //update logger value
        mFPSLogger.update(deltaTimeSeconds);
    }
}