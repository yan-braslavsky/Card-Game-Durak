package com.yan.durak.activities;

import android.os.Bundle;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.screens.PrototypeGameScreen;

import java.io.File;
import java.util.ArrayList;

import glengine.yan.glengine.EngineActivity;
import glengine.yan.glengine.assets.YANAssetDescriptor;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.screens.YANIScreen;


public class GameActivity extends EngineActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ArrayList<YANAssetDescriptor> onCreateAssets() {
        ArrayList<YANAssetDescriptor> assets = new ArrayList<>();
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.TEXTURE_ATLAS, "texture_atlases" + File.separator, "ui_atlas", "json"));
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.TEXTURE_ATLAS, "texture_atlases" + File.separator, "cards_atlas", "json"));
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.FONT, "fonts" + File.separator, "standard_font", "fnt"));
        return assets;
    }

    @Override
    protected YANIScreen onCreateStartScreen(YANGLRenderer renderer) {

        Class<? extends IGameServerConnector> clazz = (Class<? extends IGameServerConnector>) getIntent().getExtras().getSerializable(MainMenuActivity.EXTRA_CONNECTOR_CLASS_KEY);

        IGameServerConnector connector = null;
        try {
            connector = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return new PrototypeGameScreen(renderer, connector);
    }

}
