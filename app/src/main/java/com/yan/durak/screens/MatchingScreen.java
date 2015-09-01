package com.yan.durak.screens;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.nodes.TaggableTextureNode;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.services.hud.HudNodesPositioner;
import com.yan.durak.services.hud.creator.NodeCreatorHelper;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Sine;
import glengine.yan.glengine.assets.YANAssetManager;
import glengine.yan.glengine.assets.atlas.YANAtlasTextureRegion;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANTextNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;
import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.math.YANMathUtils;

/**
 * Created by yan.braslavsky on 8/21/2015.
 */
public class MatchingScreen extends BaseGameScreen {

    private static final int AVATARS_COUNT = 9;
    private static float MOVEMENT_SPEED = 400;
    private static float TOTAL_SCREEN_TIME_SECONDS = 12;
    private static float MATCH_FOUND_INTERVAL_SECONDS = TOTAL_SCREEN_TIME_SECONDS / 3;

    private float mTotalScreenTimeElapsed;
    private float mTimeElapsedAfterMatchFound;
    private final PrototypeGameScreen mGameScreen;
    private final ArrayList<YANTexturedNode> mAvatarList;
    private final YANVector2 mOriginalSize;
    private final YANAtlasTextureRegion[] mAvatarIcons;
    private float mDistanceBetweenAvatars;
    private YANTextNode mConnectingLabel;
    private final HudNodesPositioner mPositioner;
    private TaggableTextureNode mBottomRightAvatar;
    private TaggableTextureNode mTopRightAvatar;
    private TaggableTextureNode mTopLeftAvatar;
    private YANTexturedNode middleRotatingAvatar;
    private int mPlayersToBeMatched = 2;

    public MatchingScreen(YANGLRenderer renderer, final IGameServerConnector gameServerConnector) {
        super(renderer);
        mGameScreen = new PrototypeGameScreen(renderer, gameServerConnector);
        mAvatarList = new ArrayList<>(AVATARS_COUNT);
        mOriginalSize = new YANVector2();
        mAvatarIcons = new YANAtlasTextureRegion[3];
        mPositioner = new HudNodesPositioner(null);
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
            mAvatarList.add(createAvatar(mAvatarIcons[(i % mAvatarIcons.length)]));
        }

        final String connectingText = "Looking for players ...";
        mConnectingLabel = new YANTextNode(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(STANDARD_FONT_NAME), connectingText.length());
        mConnectingLabel.setText(connectingText);

        //create bottom right user mBottomRightAvatar
        mBottomRightAvatar = createAvatar(mAvatarIcons[0]);
        mTopLeftAvatar = createAvatar(mAvatarIcons[0]);
        mTopRightAvatar = createAvatar(mAvatarIcons[1]);

    }

    private TaggableTextureNode createAvatar(final YANAtlasTextureRegion avatarIcon) {
        return NodeCreatorHelper.createAvatarBgWithTimerAndIcon(
                mUiAtlas.getTextureRegion("stump_bg.png"), avatarIcon, true);
    }

    @Override
    protected void onChangeNodesSize() {
        calculateAvatarSize();
        for (YANTexturedNode avatar : mAvatarList) {
            avatar.setSize(mOriginalSize.getX(), mOriginalSize.getY());
        }

        mPositioner.adjustBottomRightAvatarSize(mBottomRightAvatar, getSceneSize());
        mPositioner.adjustTopAvatarSize(mTopLeftAvatar, getSceneSize());
        mPositioner.adjustTopAvatarSize(mTopRightAvatar, getSceneSize());
    }

    private void calculateAvatarSize() {
        //TODO : Beware code repetitions from node creator!
        final float bottomAvatarScaleFactor = 0.3f;
        YANAtlasTextureRegion textureRegion = mAvatarList.get(0).getTextureRegion();
        float aspectRatio = textureRegion.getWidth() / textureRegion.getHeight();
        float newWidth = getSceneSize().getX() * bottomAvatarScaleFactor;
        float newHeight = newWidth / aspectRatio;
        mOriginalSize.setXY(newWidth, newHeight);

        //adjust text size
        HudManagementService.adjustTextScaleToFitBounds(mConnectingLabel.getText(), getSceneSize().getX() * 0.6f, mConnectingLabel);
    }

    @Override
    protected void onAddNodesToScene() {
        super.onAddNodesToScene();
        for (YANTexturedNode avatar : mAvatarList) {
            addNode(avatar);
        }
        addNode(mConnectingLabel);
        addNode(mBottomRightAvatar);
        addNode(mTopLeftAvatar);
        addNode(mTopRightAvatar);
    }

    @Override
    protected void onLayoutNodes() {
        super.onLayoutNodes();

        mConnectingLabel.setPosition(getSceneSize().getX() * 0.1f, getSceneSize().getY() * 0.2f);
        mDistanceBetweenAvatars = mOriginalSize.getX() * 0.9f;
        float screenHalfHeight = getSceneSize().getY() / 2f;

        //set initial position
        for (int i = 0; i < mAvatarList.size(); i++) {
            final YANTexturedNode avatar = mAvatarList.get(i);
            avatar.setPosition((i * mDistanceBetweenAvatars) - mDistanceBetweenAvatars, screenHalfHeight);
//            avatar.setOpacity(0);
        }

        mPositioner.positionBottomRightAvatar(getSceneSize(), mBottomRightAvatar);
        mPositioner.positionTopLeftAvatar(mTopLeftAvatar, getSceneSize());
        mPositioner.positionTopRightAvatar(mTopRightAvatar, getSceneSize());
        mTopLeftAvatar.setOpacity(0);
        mTopRightAvatar.setOpacity(0);
        mTopLeftAvatar.setSortingLayer(9999);
        mTopRightAvatar.setSortingLayer(9999);

        moveAvatars(3000);
//        moveAvatars(-getSceneSize().getX());
    }


    @Override
    public void onSetActive() {
        super.onSetActive();
    }

    @Override
    public void onUpdate(float deltaTimeSeconds) {
        super.onUpdate(deltaTimeSeconds);

        if (mPlayersToBeMatched == 0)
            return;

        moveAvatars(MOVEMENT_SPEED * deltaTimeSeconds);

        mTotalScreenTimeElapsed += deltaTimeSeconds;
        mTimeElapsedAfterMatchFound += deltaTimeSeconds;

        //play match animation
        if (mTimeElapsedAfterMatchFound > MATCH_FOUND_INTERVAL_SECONDS) {
            //TODO : create opponent mBottomRightAvatar and animate it to place
            if (mPlayersToBeMatched > 1)
                animateMatchedTopAvatar(mTopRightAvatar);
            else
                animateMatchedTopAvatar(mTopLeftAvatar);

            mTimeElapsedAfterMatchFound = 0;
            mPlayersToBeMatched--;
        }

        if (mPlayersToBeMatched == 0)
            collapseAllMatches();

    }

    private void collapseAllMatches() {
        final float duration = 4;
        final Timeline anim = Timeline.createParallel()
                .beginParallel();

        final float screenMiddleX = getSceneSize().getX() / 2;

        for (int i = 0; i < mAvatarList.size(); i++) {
            final YANTexturedNode avatar = mAvatarList.get(i);
            anim
                    .push(Tween.to(avatar, YANTweenNodeAccessor.SIZE_XY, duration).target(0f))
                    .push(Tween.to(avatar, YANTweenNodeAccessor.POSITION_X, duration).target(screenMiddleX))
                    .push(Tween.to(avatar, YANTweenNodeAccessor.POSITION_Y, duration).target(avatar.getPosition().getY()))
                    .push(Tween.to(avatar, YANTweenNodeAccessor.OPACITY, duration).target(1f));
        }


        anim.end().setCallback(new TweenCallback() {
            @Override
            public void onEvent(final int type, final BaseTween<?> baseTween) {
                if (type == TweenCallback.COMPLETE) {
                    getRenderer().setActiveScreen(mGameScreen);
                }
            }
        }).start(getSharedTweenManager());
    }

    private void animateMatchedTopAvatar(final YANBaseNode topAvatar) {
        final float duration = 3;
        Timeline.createParallel()
                .beginParallel()
                .push(Tween.from(topAvatar, YANTweenNodeAccessor.SIZE_X, duration).target(middleRotatingAvatar.getSize().getX()).ease(Quad.OUT))
                .push(Tween.from(topAvatar, YANTweenNodeAccessor.SIZE_Y, duration).target(middleRotatingAvatar.getSize().getY()).ease(Sine.IN))
                .push(Tween.from(topAvatar, YANTweenNodeAccessor.POSITION_X, duration).target(middleRotatingAvatar.getPosition().getX()).ease(Elastic.INOUT))
                .push(Tween.from(topAvatar, YANTweenNodeAccessor.POSITION_Y, duration).target(middleRotatingAvatar.getPosition().getY()).ease(Elastic.INOUT))
                .push(Tween.to(topAvatar, YANTweenNodeAccessor.OPACITY, duration).target(1f).ease(Expo.OUT))
                .end()
                .start(getSharedTweenManager());
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
//            avatar.setOpacity(percentage);
            final float width = mOriginalSize.getX() * percentage;
            final float height = mOriginalSize.getY() * percentage;
            final float minWidth = mOriginalSize.getX() * 0.2f;
            final float minHeight = mOriginalSize.getY() * 0.2f;
            avatar.setSize(YANMathUtils.clamp(width, minWidth, width), YANMathUtils.clamp(height, minHeight, height));
            if (avatar.getPosition().getX() > offscreen) {
                offScreenAvatar = avatar;
            }

            //cache middle avatar for matching animation
            if (percentage > 0.9 && percentage < 1f)
                middleRotatingAvatar = avatar;
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
