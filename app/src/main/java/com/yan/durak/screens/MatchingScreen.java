package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.services.hud.creator.NodeCreatorHelper;

import java.util.ArrayList;

import glengine.yan.glengine.assets.atlas.YANAtlasTextureRegion;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by yan.braslavsky on 8/21/2015.
 */
public class MatchingScreen extends BaseGameScreen {

    private static final int AVATARS_COUNT = 10;
    private static final int BASE_SORTING_LAYER = 10;
    private final IGameServerConnector gameServerConnector;
    private float angleSpeed = 40;
    private float currentOffset = 0;
    private ArrayList<YANTexturedNode> mAvatarList;
    private YANVector2 mOriginalSize;

    public MatchingScreen(YANGLRenderer renderer, final IGameServerConnector gameServerConnector) {
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
                    mUiAtlas.getTextureRegion("stump_bg.png"), mUiAtlas.getTextureRegion("avatar_" + avatarNumber + ".png"), true));
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
        mOriginalSize = new YANVector2(newWidth, newHeight);

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
        layoutInEllipse(0);
    }

    private void layoutInEllipse(final float angleOffset) {
        float centerX = getSceneSize().getX() / 2;
        float centerY = getSceneSize().getY() / 2;
        float x;
        float y;
        float angle = angleOffset;
        float angleStep = 360f / mAvatarList.size();
        float radiusX = getSceneSize().getX() * 0.4f;
        float radiusY = getSceneSize().getY() / 8;
        float rad;
        for (int i = 0; i < mAvatarList.size(); i++) {
            YANTexturedNode avatar = mAvatarList.get(i);
            rad = (float) Math.toRadians(angle);
            x = (float) ((double) centerX + (double) radiusX * Math.cos((double) rad));
            y = (float) ((double) centerY + (double) radiusY * Math.sin((double) rad));
            avatar.setPosition(x, y);

            //sorting layer
            avatar.setSortingLayer((angle > 180) ? BASE_SORTING_LAYER - Math.round(angle) : BASE_SORTING_LAYER + Math.round(angle));
            angle += angleStep;
            angle = angle % 360;

            float percentage = Math.abs((angle / 360f) - 0.5f);
            avatar.setSize(mOriginalSize.getX() * percentage, mOriginalSize.getY() * percentage);
            avatar.setOpacity(percentage);
        }
    }

    @Override
    public void onSetActive() {
        super.onSetActive();
    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);
        currentOffset += angleSpeed * deltaTimeSeconds;
        currentOffset %= 360;
        layoutInEllipse(currentOffset);
    }
}
