package com.yan.durak.screens;

import glengine.yan.glengine.assets.YANAssetManager;
import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANTextNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.screens.YANNodeScreen;
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
    public static final String STANDARD_FONT_NAME = "standard_font";

    //texture atlases
    protected YANTextureAtlas mUiAtlas;
    protected final YANTextureAtlas mCardsAtlas;

    //TODO : remove on production
    //Used to log FPS data on screen
    YANTextNode mFpsTextNode;
    YANFPSLogger mFPSLogger;

    public BaseGameScreen(YANGLRenderer renderer) {
        super(renderer);

        //setup fps logger
        mFPSLogger = new YANFPSLogger();
        mFPSLogger.setFPSLoggerListener(new YANFPSLogger.FPSLoggerListener() {
            @Override
            public void onValueChange(int newFpsValue) {
                mFpsTextNode.setText("FPS " + newFpsValue);
            }
        });

        //load atlases
        mUiAtlas = YANAssetManager.getInstance().getLoadedAtlas(UI_ATLAS_NAME);
        mCardsAtlas = YANAssetManager.getInstance().getLoadedAtlas(CARDS_ATLAS_NAME);
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
        mFpsTextNode = new YANTextNode(YANAssetManager.getInstance().getLoadedFont(STANDARD_FONT_NAME));
        mFpsTextNode.setText("FPS " + 0);
        mFpsTextNode.setSortingLayer(5000);
    }

    @Override
    public void onSetActive() {
        super.onSetActive();
        getRenderer().setRendererBackgroundColor(YANColor.createFromHexColor(BG_HEX_COLOR));

        //for efficiency reasons we are not loading texture into openGL until we are need it
        YANAssetManager.getInstance().loadTexture(mUiAtlas.getAtlasImageFilePath());
        YANAssetManager.getInstance().loadTexture(mCardsAtlas.getAtlasImageFilePath());

        //load font atlas into a memory
        YANAssetManager.getInstance().loadTexture(YANAssetManager.getInstance().getLoadedFont("standard_font").getGlyphImageFilePath());
    }

    @Override
    public void onSetNotActive() {
        super.onSetNotActive();

        //for efficiency reasons we are deleting loaded texture into openGL
        YANAssetManager.getInstance().unloadTexture(mUiAtlas.getAtlasImageFilePath());
        YANAssetManager.getInstance().unloadTexture(mCardsAtlas.getAtlasImageFilePath());

        //release atlas font from a memory
        YANAssetManager.getInstance().unloadTexture(YANAssetManager.getInstance().getLoadedFont("standard_font").getGlyphImageFilePath());
    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);

        //TODO : put to updatable list
        //update logger value
        mFPSLogger.update(deltaTimeSeconds);
    }
}