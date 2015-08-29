package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.services.hud.creator.NodeCreatorHelper;

import java.util.ArrayList;

import glengine.yan.glengine.assets.atlas.YANAtlasTextureRegion;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.math.YANMathUtils;

/**
 * Created by yan.braslavsky on 8/21/2015.
 */
public class MatchingScreen extends BaseGameScreen {

    private static final int AVATARS_COUNT = 7;
    private static float MOVEMENT_SPEED = 700;

    private float mAnimationTimeBeforeNextScreen = 5;
    private final PrototypeGameScreen mGameScreen;
    private final ArrayList<YANTexturedNode> mAvatarList;
    private final YANVector2 mOriginalSize;
    private final YANAtlasTextureRegion[] mAvatarIcons;
    private float mDistanceBetweenAvatars;

    public MatchingScreen(YANGLRenderer renderer, final IGameServerConnector gameServerConnector) {
        super(renderer);
        mGameScreen = new PrototypeGameScreen(renderer, gameServerConnector);
        mAvatarList = new ArrayList<>(AVATARS_COUNT);
        mOriginalSize = new YANVector2();
        mAvatarIcons = new YANAtlasTextureRegion[3];
    }

    @Override
    protected void onCreateNodes() {
        super.onCreateNodes();

        //init all available icons
        for (int i = 0; i < mAvatarIcons.length; i++) {
            mAvatarIcons[i] = mUiAtlas.getTextureRegion("avatar_" + (i + 1) + ".png");
        }

        //create avatars
        for (int i = 0; i < AVATARS_COUNT; i++) {
            YANAtlasTextureRegion avatar = mAvatarIcons[(i % mAvatarIcons.length)];
            mAvatarList.add(NodeCreatorHelper.createAvatarBgWithTimerAndIcon(
                    mUiAtlas.getTextureRegion("stump_bg.png"), avatar, true));
        }
    }

    @Override
    protected void onChangeNodesSize() {
        calculateAvatarSize();
        for (YANTexturedNode avatar : mAvatarList) {
            avatar.setSize(mOriginalSize.getX(), mOriginalSize.getY());
        }
    }

    private void calculateAvatarSize() {
        //TODO : Beware code repetitions from node creator!
        final float bottomAvatarScaleFactor = 0.3f;
        YANAtlasTextureRegion textureRegion = mAvatarList.get(0).getTextureRegion();
        float aspectRatio = textureRegion.getWidth() / textureRegion.getHeight();
        float newWidth = getSceneSize().getX() * bottomAvatarScaleFactor;
        float newHeight = newWidth / aspectRatio;
        mOriginalSize.setXY(newWidth, newHeight);
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

        mDistanceBetweenAvatars = getSceneSize().getX() * 0.4f;
        float screenHalfHeight = getSceneSize().getY() / 2f;

        //set initial position
        for (int i = 0; i < mAvatarList.size(); i++) {
            final YANTexturedNode avatar = mAvatarList.get(i);
            avatar.setPosition((i * mDistanceBetweenAvatars) - mDistanceBetweenAvatars, screenHalfHeight);
            avatar.setOpacity(0);
        }
    }


    @Override
    public void onSetActive() {
        super.onSetActive();
    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);
        moveAvatars(MOVEMENT_SPEED * deltaTimeSeconds);

        mAnimationTimeBeforeNextScreen -= deltaTimeSeconds;
        if (mAnimationTimeBeforeNextScreen < 0)
            getRenderer().setActiveScreen(mGameScreen);
    }

    private void moveAvatars(final float xDistance) {
        YANTexturedNode offScreenAvatar = null;
        final float offscreen = getSceneSize().getX() + mOriginalSize.getX();
        float percentage = 0;
        final float screenHalfWidth = getSceneSize().getX() / 2;
        for (int i = 0; i < mAvatarList.size(); i++) {
            final YANTexturedNode avatar = mAvatarList.get(i);
            avatar.setPosition(avatar.getPosition().getX() + xDistance, avatar.getPosition().getY());
            percentage = 1 - (Math.abs(avatar.getPosition().getX() - screenHalfWidth) / screenHalfWidth);
            avatar.setOpacity(percentage);
            avatar.setSize(mOriginalSize.getX() * percentage, mOriginalSize.getY() * percentage);
            if (avatar.getPosition().getX() > offscreen) {
                offScreenAvatar = avatar;
            }
        }

        if (offScreenAvatar != null) {
            final YANTexturedNode mostLeftAvatar = mAvatarList.get(mAvatarList.size() - 1);
            mAvatarList.remove(offScreenAvatar);
            offScreenAvatar.setPosition(mostLeftAvatar.getPosition().getX() - mDistanceBetweenAvatars,
                    offScreenAvatar.getPosition().getY());
            mAvatarList.add(offScreenAvatar);
            YANTexturedNode icon = (YANTexturedNode) offScreenAvatar.getChildNodes().get(0).getChildNodes().get(0);
            icon.setTextureRegion(mAvatarIcons[((int) Math.floor(YANMathUtils.randomInRange(0, mAvatarIcons.length)))]);
        }

    }
}
