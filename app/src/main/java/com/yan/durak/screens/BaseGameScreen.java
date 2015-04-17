package com.yan.durak.screens;

import glengine.yan.glengine.assets.YANAssetManager;
import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.screens.YANNodeScreen;
import glengine.yan.glengine.util.colors.YANColor;

/**
 * Created by Yan-Home on 11/21/2014.
 */
public abstract class BaseGameScreen extends YANNodeScreen {

    /**
     * Solid background that is used to clear frame buffer
     */
    private static final int BG_HEX_COLOR = 0x9F9E36;

    //texture atlases
    protected YANTextureAtlas mUiAtlas;
    protected final YANTextureAtlas mCardsAtlas;

    public BaseGameScreen(YANGLRenderer renderer) {
        super(renderer);

        //load atlases
        mUiAtlas = YANAssetManager.getInstance().getLoadedAtlas("ui_atlas");
        mCardsAtlas = YANAssetManager.getInstance().getLoadedAtlas("cards_atlas");
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
    }
}