package com.yan.durak.screens;

import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.assets.YANAssetManager;
import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.screens.YANNodeScreen;
import glengine.yan.glengine.util.colors.YANColor;

/**
 * Created by Yan-Home on 11/21/2014.
 */
public abstract class BaseGameScreen extends YANNodeScreen {

    private static final int BG_HEX_COLOR = 0x9F9E36;
    protected static final int HIGHEST_SORTING_LAYER = 50;

    //TODO : Move to Hud Fragment
    protected YANTextureAtlas mUiAtlas;
    protected YANTexturedNode mFence;
    private YANTexturedNode mGlade;
    protected final YANTextureAtlas mCardsAtlas;
    protected final TweenManager mSharedTweenManager;

    public BaseGameScreen(YANGLRenderer renderer) {
        super(renderer);

        //tween manager is used for various tween animations
        mSharedTweenManager = new TweenManager();
        mUiAtlas = YANAssetManager.getInstance().getLoadedAtlas("ui_atlas");
        mCardsAtlas = YANAssetManager.getInstance().getLoadedAtlas("cards_atlas");
    }

    @Override
    protected void onAddNodesToScene() {
        //add all the other nodes
        addNode(mGlade);
        addNode(mFence);
    }


    @Override
    protected void onLayoutNodes() {
        //fence
        float centerX = (getSceneSize().getX() - mFence.getSize().getX()) / 2;
        float centerY = (getSceneSize().getY() - mFence.getSize().getY());
        mFence.setPosition(centerX, centerY);

        //glade
        centerX = (getSceneSize().getX() - mGlade.getSize().getX()) / 2;
        centerY = (getSceneSize().getY() - mGlade.getSize().getY()) / 2;
        mGlade.setPosition(centerX, centerY);
    }


    @Override
    protected void onChangeNodesSize() {
        float aspectRatio;

        //fence
        aspectRatio = mFence.getTextureRegion().getWidth() / mFence.getTextureRegion().getHeight();
        mFence.setSize(getSceneSize().getX(), getSceneSize().getX() / aspectRatio);

        //glade
        aspectRatio = mGlade.getTextureRegion().getWidth() / mGlade.getTextureRegion().getHeight();
        float gladeWidth = Math.min(getSceneSize().getX(), getSceneSize().getY()) * 0.9f;
        mGlade.setSize(gladeWidth, gladeWidth / aspectRatio);
    }

    @Override
    protected void onCreateNodes() {

        mFence = new YANTexturedNode(mUiAtlas.getTextureRegion("fence.png"));

        //fence is on top of cards
        mFence.setSortingLayer(HIGHEST_SORTING_LAYER);
        mGlade = new YANTexturedNode(mUiAtlas.getTextureRegion("glade.png"));
    }

    @Override
    public void onSetActive() {
        super.onSetActive();
        getRenderer().setRendererBackgroundColor(YANColor.createFromHexColor(BG_HEX_COLOR));

        //for efficiency reasons we are not loading texture into openGL until we are need it
        YANAssetManager.getInstance().loadTexture(mUiAtlas.getAtlasImageFilePath());
        YANAssetManager.getInstance().loadTexture(mCardsAtlas.getAtlasImageFilePath());
    }

    @Override
    public void onSetNotActive() {
        super.onSetNotActive();

        //for efficiency reasons we are deleting loaded texture into openGL
        YANAssetManager.getInstance().unloadTexture(mUiAtlas.getAtlasImageFilePath());
        YANAssetManager.getInstance().unloadTexture(mCardsAtlas.getAtlasImageFilePath());
    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);

        //update shared tween manager
        mSharedTweenManager.update(deltaTimeSeconds * 1);
    }
}