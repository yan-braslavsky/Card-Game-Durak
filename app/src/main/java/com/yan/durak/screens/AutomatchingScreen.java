package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.services.hud.creator.NodeCreatorHelper;

import java.util.ArrayList;

import glengine.yan.glengine.assets.atlas.YANAtlasTextureRegion;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;

/**
 * Created by yan.braslavsky on 8/21/2015.
 */
public class AutoMatchingScreen extends BaseGameScreen {

    private static final int AVATARS_COUNT = 4;
    private final IGameServerConnector gameServerConnector;
    private float secondsToWait = 8;
    private float speedPerSecond = 600;
    private ArrayList<YANTexturedNode> mAvatarList;
    private float xDistanceBetweenAvatars;

    public AutoMatchingScreen(YANGLRenderer renderer, final IGameServerConnector gameServerConnector) {
        super(renderer);
        this.gameServerConnector = gameServerConnector;
        mAvatarList = new ArrayList<>(AVATARS_COUNT);
    }

    @Override
    protected void onCreateNodes() {
        super.onCreateNodes();
        for (int i = 0; i < AVATARS_COUNT; i++) {
            int avatarNumber = (i % 3) + 1;
            mAvatarList.add(NodeCreatorHelper.createAvatarBgWithTimerAndIcon(
                    mUiAtlas.getTextureRegion("stump_bg.png"), mUiAtlas.getTextureRegion("avatar_" + avatarNumber + ".png")));
        }
    }

    @Override
    protected void onChangeNodesSize() {
        //TODO : Beware code repetitions !
        final float bottomAvatarScaleFactor = 0.3f;
        YANAtlasTextureRegion textureRegion = mAvatarList.get(0).getTextureRegion();
        float aspectRatio = textureRegion.getWidth() / textureRegion.getHeight();
        float newWidth = getSceneSize().getX() * bottomAvatarScaleFactor;
        float newHeight = newWidth / aspectRatio;

        for (YANTexturedNode avatar : mAvatarList) {
            avatar.setSize(newWidth, newHeight);
        }
    }

    @Override
    protected void onAddNodesToScene() {
        super.onAddNodesToScene();
        for (YANTexturedNode avatar : mAvatarList) {
            addNode(avatar);
        }
    }

    @Override
    protected void onLayoutNodes() {
        super.onLayoutNodes();
        xDistanceBetweenAvatars = mAvatarList.get(0).getSize().getX() + 50;
        float x = getSceneSize().getX() + xDistanceBetweenAvatars;
        float y = getSceneSize().getY() / 2;
        for (YANTexturedNode avatar : mAvatarList) {
            avatar.setPosition(x, y);
            x -= xDistanceBetweenAvatars;
        }
    }

    @Override
    public void onSetActive() {
        super.onSetActive();
    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);

        secondsToWait -= deltaTimeSeconds;
        if (secondsToWait < 0)
            getRenderer().setActiveScreen(new PrototypeGameScreen(getRenderer(), gameServerConnector));

        for (YANTexturedNode avatar : mAvatarList) {
            float newXPos = avatar.getPosition().getX() - (deltaTimeSeconds * speedPerSecond);

            if (newXPos < -xDistanceBetweenAvatars)
                newXPos = getSceneSize().getX() + xDistanceBetweenAvatars;

            avatar.setPosition(newXPos,
                    avatar.getPosition().getY());
        }
    }
}
