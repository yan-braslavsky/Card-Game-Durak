package com.yan.durak.activities;

import android.os.Bundle;

import com.yan.durak.BuildConfig;
import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.screens.PrototypeGameScreen;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import glengine.yan.glengine.EngineActivity;
import glengine.yan.glengine.assets.YANAssetDescriptor;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.screens.YANIScreen;


public class GameActivity extends EngineActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ArrayList<YANAssetDescriptor> onCreateAssets() {
        final ArrayList<YANAssetDescriptor> assets = new ArrayList<>();
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.TEXTURE_ATLAS, "texture_atlases" + File.separator, "ui_atlas", "json"));
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.TEXTURE_ATLAS, "texture_atlases" + File.separator, "cards_atlas", "json"));
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.TEXTURE_ATLAS, "texture_atlases" + File.separator, "dialogs_atlas", "json"));
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.FONT, "fonts" + File.separator, "standard_font", "fnt"));
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.FONT, "fonts" + File.separator, "chunkfive", "fnt"));
        assets.add(new YANAssetDescriptor(YANAssetDescriptor.YANAssetType.FONT, "fonts" + File.separator, "Izhitsa", "fnt"));
        return assets;
    }

    @Override
    protected YANIScreen onCreateStartScreen(final YANGLRenderer renderer) {

        final Class<? extends IGameServerConnector> clazz = (Class<? extends IGameServerConnector>) getIntent().getExtras().getSerializable(MainMenuActivity.EXTRA_CONNECTOR_CLASS_KEY);
        IGameServerConnector connector = null;
        try {
            connector = clazz.newInstance();
        } catch (final InstantiationException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }

        return new PrototypeGameScreen(renderer, connector);
    }

    @Override
    protected boolean isUsingAntiAliasing() {
        return (BuildConfig.FLAVOR.equals("device"));
    }

    public static class GameInitConfig implements Serializable{
        public final String nickname;
        public final String avatarResource;
        public final int playersAmount;

        public GameInitConfig(final String nickname, final String avatarResource, final int playersAmount) {
            this.nickname = nickname;
            this.avatarResource = avatarResource;
            this.playersAmount = playersAmount;
        }
    }
}
