package com.yan.durak.service.services;


import android.support.annotation.IntDef;

import com.yan.durak.service.IService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANCircleNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.nodes.YANTexturedScissorNode;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;
import glengine.yan.glengine.util.colors.YANColor;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.loggers.YANLogger;

/**
 * Created by Yan-Home on 1/25/2015.
 */
public class HudManagementService implements IService {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            AVATAR_BG_BOTTOM_RIGHT_INDEX,
            AVATAR_BG_TOP_RIGHT_INDEX,
            AVATAR_BG_TOP_LEFT_INDEX,
            AVATAR_ICON_BOTTOM_RIGHT_INDEX,
            AVATAR_ICON_TOP_RIGHT_INDEX,
            AVATAR_ICON_TOP_LEFT_INDEX,
            DONE_BUTTON_INDEX,
            TAKE_BUTTON_INDEX,
            TRUMP_IMAGE_INDEX,
            YOU_WIN_IMAGE_INDEX,
            YOU_LOOSE_IMAGE_INDEX,
            V_BUTTON_INDEX,
            /**
             * We don't want to show all the cards in a stock pile.
             * Instead we are showing only one, which is this node.
             * Underneath this node there is a trump card.
             */
            MASK_CARD_INDEX,
            FENCE_INDEX,
            GLADE_INDEX,
            GLOW_INDEX,
            CIRCLE_TIMER_BOTTOM_RIGHT_INDEX,
            CIRCLE_TIMER_TOP_RIGHT_INDEX,
            CIRCLE_TIMER_TOP_LEFT_INDEX
    })
    public @interface HudNode {
    }

    //Avatar backgrounds
    public static final int AVATAR_BG_BOTTOM_RIGHT_INDEX = 0;
    public static final int AVATAR_BG_TOP_RIGHT_INDEX = 1;
    public static final int AVATAR_BG_TOP_LEFT_INDEX = 2;

    //Avatar icons
    public static final int AVATAR_ICON_BOTTOM_RIGHT_INDEX = 3;
    public static final int AVATAR_ICON_TOP_RIGHT_INDEX = 4;
    public static final int AVATAR_ICON_TOP_LEFT_INDEX = 5;
    public static final int TRUMP_IMAGE_INDEX = 6;

    //Action buttons
    public static final int DONE_BUTTON_INDEX = 7;
    public static final int TAKE_BUTTON_INDEX = 8;

    public static final int GLOW_INDEX = 9;

    //End game dialogs
    public static final int YOU_WIN_IMAGE_INDEX = 10;
    public static final int YOU_LOOSE_IMAGE_INDEX = 11;
    public static final int V_BUTTON_INDEX = 12;


    public static final int MASK_CARD_INDEX = 13;
    public static final int FENCE_INDEX = 14;
    public static final int GLADE_INDEX = 15;
    public static final int CIRCLE_TIMER_BOTTOM_RIGHT_INDEX = 16;
    public static final int CIRCLE_TIMER_TOP_RIGHT_INDEX = 17;
    public static final int CIRCLE_TIMER_TOP_LEFT_INDEX = 18;

    /**
     * By default hud will be placed on hud sorting layer and above
     */
    public static final int HUD_SORTING_LAYER = 500;

    /**
     * Duration of appearing of the popup window
     */
    private static final float POPUP_ANIMATION_DURATION = 0.7f;

    /**
     * Used to perform tween animations
     */
    private final TweenManager mTweenManager;

    /**
     * All nodes that exist in the hud manager will be placed in this map
     */
    private Map<Integer, YANBaseNode> mHudNodesMap;

    private static final YANColor TIMER_RETALIATION_COLOR = YANColor.createFromHexColor(0xFFF200);
    private static final YANColor TIMER_THROW_IN_COLOR = YANColor.createFromHexColor(0x00A5B2);

    private YANTextureAtlas mHudAtlas;
    private TweenCallback showVButtonTweenCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> baseTween) {
            if (TweenCallback.COMPLETE == type) {
                getNode(V_BUTTON_INDEX).setOpacity(1f);
            }
        }
    };

    //Cached click listeners for action buttons
    private YANButtonNode.YanButtonNodeClickListener mDoneBtnClickListener;
    private YANButtonNode.YanButtonNodeClickListener mTakeButtonClickListener;


    public HudManagementService(TweenManager tweenManager) {
        mTweenManager = tweenManager;
        mHudNodesMap = new HashMap<>();
    }

    public void createNodes(YANTextureAtlas hudAtlas) {

        //cache HUD atlas for later use
        mHudAtlas = hudAtlas;

        //add image of glade
        putToNodeMap(GLADE_INDEX, createGladeImage(hudAtlas));

        //add image of fence
        putToNodeMap(FENCE_INDEX, createFenceImage(hudAtlas));

        //add image of the trump
        putToNodeMap(TRUMP_IMAGE_INDEX, createTrumpImage(hudAtlas));

        //create avatars
        putToNodeMap(AVATAR_BG_BOTTOM_RIGHT_INDEX, createAvatar(hudAtlas));
        putToNodeMap(AVATAR_BG_TOP_RIGHT_INDEX, createAvatar(hudAtlas));
        putToNodeMap(AVATAR_BG_TOP_LEFT_INDEX, createAvatar(hudAtlas));

        //create avatar icons
        putToNodeMap(AVATAR_ICON_BOTTOM_RIGHT_INDEX, createAvatarIcon(hudAtlas));
        putToNodeMap(AVATAR_ICON_TOP_RIGHT_INDEX, createAvatarIcon(hudAtlas));
        putToNodeMap(AVATAR_ICON_TOP_LEFT_INDEX, createAvatarIcon(hudAtlas));

        //create timers
        putToNodeMap(CIRCLE_TIMER_BOTTOM_RIGHT_INDEX, createCircleTimer());
        putToNodeMap(CIRCLE_TIMER_TOP_RIGHT_INDEX, createCircleTimer());
        putToNodeMap(CIRCLE_TIMER_TOP_LEFT_INDEX, createCircleTimer());

        //create action buttons
        putToNodeMap(DONE_BUTTON_INDEX, createDoneButton(hudAtlas));
        putToNodeMap(TAKE_BUTTON_INDEX, createTakeButton(hudAtlas));

        //end game popups
        putToNodeMap(YOU_WIN_IMAGE_INDEX, createYouWonImage(hudAtlas));
        putToNodeMap(YOU_LOOSE_IMAGE_INDEX, createYouLooseImage(hudAtlas));

        //create v button for popup
        putToNodeMap(V_BUTTON_INDEX, createVButton(hudAtlas));

        //TODO : add back card image to the hud atlas
        //create v button for popup
        putToNodeMap(MASK_CARD_INDEX, createMaskCard(hudAtlas));

        putToNodeMap(GLOW_INDEX, createCardGlow(hudAtlas));

        //at the beginning some nodes might have a different state
        setupInitialState();

    }

    private YANCircleNode createCircleTimer() {
        YANCircleNode yanCircleNode = new YANCircleNode();
        yanCircleNode.setColor(TIMER_RETALIATION_COLOR.getR(), TIMER_RETALIATION_COLOR.getG(), TIMER_RETALIATION_COLOR.getB());
        yanCircleNode.setClockWiseDraw(true);
        yanCircleNode.setPieCirclePercentage(1f);
        return yanCircleNode;
    }

    private YANTexturedNode createAvatarIcon(YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("avatar.png"));
    }

    private YANTexturedNode createCardGlow(YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("card_glow.png"));
    }

    private void setupInitialState() {

        //popup images are invisible
        getNode(YOU_WIN_IMAGE_INDEX).setOpacity(0);
        getNode(YOU_LOOSE_IMAGE_INDEX).setOpacity(0);

        //popups anchor is at the middle
        getNode(YOU_WIN_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);
        getNode(YOU_LOOSE_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);

        //v button
        getNode(V_BUTTON_INDEX).setOpacity(0);

        //action buttons also have zero opacity
        getNode(TAKE_BUTTON_INDEX).setOpacity(0);
        getNode(DONE_BUTTON_INDEX).setOpacity(0);
        getNode(GLOW_INDEX).setOpacity(0);
    }

    private YANButtonNode createVButton(YANTextureAtlas hudAtlas) {
        YANButtonNode node = new YANButtonNode(hudAtlas.getTextureRegion("v_btn.png"), hudAtlas.getTextureRegion("v_btn_clicked.png"));
        node.setClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                YANLogger.log("v button clicked");
            }
        });
        node.setSortingLayer(HUD_SORTING_LAYER - 2);
        return node;
    }

    private YANTexturedNode createMaskCard(YANTextureAtlas hudAtlas) {
        YANTexturedNode maskCard = new YANTexturedNode(hudAtlas.getTextureRegion("cards_back.png"));
        maskCard.setSortingLayer(HUD_SORTING_LAYER);
        return maskCard;
    }

    private YANTexturedNode createYouWonImage(YANTextureAtlas hudAtlas) {
        YANTexturedNode popupImage = new YANTexturedNode(hudAtlas.getTextureRegion("you_won.png"));
        popupImage.setSortingLayer(HUD_SORTING_LAYER - 3);
        return popupImage;
    }

    private YANTexturedNode createYouLooseImage(YANTextureAtlas hudAtlas) {
        YANTexturedNode popupImage = new YANTexturedNode(hudAtlas.getTextureRegion("you_lose.png"));
        popupImage.setSortingLayer(HUD_SORTING_LAYER - 3);
        return popupImage;
    }

    private YANTexturedNode createGladeImage(YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("glade.png"));
    }

    private YANTexturedNode createFenceImage(YANTextureAtlas hudAtlas) {
        YANTexturedNode image = new YANTexturedNode(hudAtlas.getTextureRegion("fence.png"));
        image.setSortingLayer(HUD_SORTING_LAYER);
        return image;
    }

    private YANTexturedNode createTrumpImage(YANTextureAtlas hudAtlas) {
        YANTexturedNode trumpImage = new YANTexturedNode(hudAtlas.getTextureRegion("trump_marker_hearts.png"));
        trumpImage.setSortingLayer(-50);
        return trumpImage;
    }

    private YANButtonNode createTakeButton(YANTextureAtlas hudAtlas) {
        return new YANButtonNode(hudAtlas.getTextureRegion("btn_take.png"), hudAtlas.getTextureRegion("btn_take.png"));
    }

    private YANButtonNode createDoneButton(YANTextureAtlas hudAtlas) {
        return new YANButtonNode(hudAtlas.getTextureRegion("btn_done.png"), hudAtlas.getTextureRegion("btn_done.png"));
    }

    private <T extends YANBaseNode> void putToNodeMap(@HudNode int nodeIndex, T node) {
        mHudNodesMap.put(nodeIndex, node);
    }

    public <T extends YANBaseNode> T getNode(@HudNode int nodeIndex) {
        return (T) mHudNodesMap.get(nodeIndex);
    }

    private YANTexturedNode createScissorCock(YANTextureAtlas hudAtlas) {
        //scissor cock node that will be used to present the fill in for cocks
        YANTexturedScissorNode scissorCock = new YANTexturedScissorNode(hudAtlas.getTextureRegion("yellow_cock.png"));
        scissorCock.setSortingLayer(HUD_SORTING_LAYER + 100);
        return scissorCock;
    }

    private YANTexturedNode createCock(YANTextureAtlas hudAtlas) {
        YANTexturedNode cock = new YANTexturedNode(hudAtlas.getTextureRegion("grey_cock.png"));
        cock.setSortingLayer(HUD_SORTING_LAYER);
        return cock;
    }

    private YANTexturedNode createAvatar(YANTextureAtlas hudAtlas) {
        YANTexturedNode avatar = new YANTexturedNode(hudAtlas.getTextureRegion("stump_bg.png"));
        avatar.setSortingLayer(HUD_SORTING_LAYER);
        return avatar;
    }

    public void setNodesSizes(YANReadOnlyVector2 sceneSize) {

        //set avatars sizes
        float bottomAvatarScaleFactor = 0.3f;
        YANTexturedNode avatar = getNode(AVATAR_BG_BOTTOM_RIGHT_INDEX);
        float aspectRatio = avatar.getTextureRegion().getWidth() / avatar.getTextureRegion().getHeight();
        float newWidth = sceneSize.getX() * bottomAvatarScaleFactor;
        float newHeight = newWidth / aspectRatio;

        //avatars
        getNode(AVATAR_BG_BOTTOM_RIGHT_INDEX).setSize(newWidth, newHeight);

        //top avatars is smaller than bottom one
        float topAvatarsScaleFactor = 0.8f;
        YANTexturedNode avatarBGTopRight = getNode(AVATAR_BG_TOP_RIGHT_INDEX);
        avatarBGTopRight.setSize(newWidth * topAvatarsScaleFactor, newHeight * topAvatarsScaleFactor);
        getNode(AVATAR_BG_TOP_LEFT_INDEX).setSize(newWidth * topAvatarsScaleFactor, newHeight * topAvatarsScaleFactor);

        //set avatar icons
        //check how much the icon smaller than background
        YANTexturedNode bottomRightAvatarIcon = getNode(AVATAR_ICON_BOTTOM_RIGHT_INDEX);
        float avatarIconToAvatarBgScaleFactor = bottomRightAvatarIcon.getTextureRegion().getWidth() / avatarBGTopRight.getTextureRegion().getWidth();

        float bottomIconSize = getNode(AVATAR_BG_BOTTOM_RIGHT_INDEX).getSize().getX() * avatarIconToAvatarBgScaleFactor;
        //set bottom avatar icon
        bottomRightAvatarIcon.setSize(bottomIconSize, bottomIconSize);

        //setup bottom timer size
        //calculate timer scale factor
        float timerToIconScaleFactor = avatarIconToAvatarBgScaleFactor + 0.095f;
        float bottomTimerSize = getNode(AVATAR_BG_BOTTOM_RIGHT_INDEX).getSize().getX() * timerToIconScaleFactor;
        getNode(CIRCLE_TIMER_BOTTOM_RIGHT_INDEX).setSize(bottomTimerSize, bottomTimerSize);

        //set top avatar icons
        float topIconsSize = avatarBGTopRight.getSize().getX() * avatarIconToAvatarBgScaleFactor;
        getNode(AVATAR_ICON_TOP_RIGHT_INDEX).setSize(topIconsSize, topIconsSize);
        getNode(AVATAR_ICON_TOP_LEFT_INDEX).setSize(topIconsSize, topIconsSize);

        //set top timers size
        timerToIconScaleFactor += 0.32f;
        float topTimerSize = topIconsSize * timerToIconScaleFactor;
        getNode(CIRCLE_TIMER_TOP_RIGHT_INDEX).setSize(topTimerSize, topTimerSize);
        getNode(CIRCLE_TIMER_TOP_LEFT_INDEX).setSize(topTimerSize, topTimerSize);

        //set action buttons size
        getNode(DONE_BUTTON_INDEX).setSize(bottomIconSize, bottomIconSize);
        getNode(TAKE_BUTTON_INDEX).setSize(bottomIconSize, bottomIconSize);

        //set trump image size
        YANTexturedNode trumpImage = getNode(TRUMP_IMAGE_INDEX);
        aspectRatio = trumpImage.getTextureRegion().getWidth() / trumpImage.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.1f;
        newHeight = newWidth / aspectRatio;
        trumpImage.setSize(newWidth, newHeight);

        //popups
        YANTexturedNode youWinImage = getNode(YOU_WIN_IMAGE_INDEX);
        aspectRatio = youWinImage.getTextureRegion().getWidth() / youWinImage.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.9f;
        newHeight = newWidth / aspectRatio;
        youWinImage.setSize(newWidth, newHeight);
        getNode(YOU_LOOSE_IMAGE_INDEX).setSize(newWidth, newHeight);

        //v button
        YANTexturedNode vButton = getNode(V_BUTTON_INDEX);
        aspectRatio = vButton.getTextureRegion().getWidth() / vButton.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.2f;
        newHeight = newWidth / aspectRatio;
        vButton.setSize(newWidth, newHeight);

        //mask card
        //initial size is not matter as it will be changed once game setup message will be received
        getNode(MASK_CARD_INDEX).setSize(1, 1);

        //fence
        YANTexturedNode fenceImage = getNode(FENCE_INDEX);
        aspectRatio = fenceImage.getTextureRegion().getWidth() / fenceImage.getTextureRegion().getHeight();
        fenceImage.setSize(sceneSize.getX(), sceneSize.getX() / aspectRatio);

        //glade
        YANTexturedNode gladeImage = getNode(GLADE_INDEX);
        aspectRatio = gladeImage.getTextureRegion().getWidth() / gladeImage.getTextureRegion().getHeight();
        float gladeWidth = Math.min(sceneSize.getX(), sceneSize.getY()) * 0.9f;
        gladeImage.setSize(gladeWidth, gladeWidth / aspectRatio);

        //later glow size will be overridden
        getNode(GLOW_INDEX).setSize(0, 0);
    }

    public Collection<? extends YANBaseNode> getCardNodes() {
        return mHudNodesMap.values();
    }

    public void layoutNodes(YANReadOnlyVector2 sceneSize) {
        //layout avatars
        float offsetX = sceneSize.getX() * 0.01f;

        //setup avatarBg for bottom player
        YANTexturedNode avatarBg = getNode(AVATAR_BG_BOTTOM_RIGHT_INDEX);
        avatarBg.setAnchorPoint(1f, 1f);
        avatarBg.setSortingLayer(HUD_SORTING_LAYER + 1);
        avatarBg.setPosition(sceneSize.getX() - offsetX, sceneSize.getY() - offsetX);

        //setup bottom timer
        YANBaseNode bottomTimer = getNode(CIRCLE_TIMER_BOTTOM_RIGHT_INDEX);
        float offsetSize = (avatarBg.getSize().getX() - bottomTimer.getSize().getX()) / 2;
        bottomTimer.setSortingLayer(avatarBg.getSortingLayer() + 1);
        bottomTimer.setAnchorPoint(1f, 1f);
        bottomTimer.setPosition(avatarBg.getPosition().getX() - offsetSize, avatarBg.getPosition().getY() - offsetSize);

        //setup bottom avatar icon
        YANTexturedNode bottomAvatarIcon = getNode(AVATAR_ICON_BOTTOM_RIGHT_INDEX);
        bottomAvatarIcon.setAnchorPoint(1f, 1f);
        bottomAvatarIcon.setSortingLayer(bottomTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - bottomAvatarIcon.getSize().getX()) / 2;
        bottomAvatarIcon.setPosition(avatarBg.getPosition().getX() - offsetSize, avatarBg.getPosition().getY() - offsetSize);

        //take action is at the same place as bottom avatarBg
        YANTexturedNode takeButton = getNode(TAKE_BUTTON_INDEX);
        takeButton.setAnchorPoint(1f, 1f);
        takeButton.setSortingLayer(bottomAvatarIcon.getSortingLayer() + 1);
        takeButton.setPosition(bottomAvatarIcon.getPosition().getX(), bottomAvatarIcon.getPosition().getY());

        //finish action is at the same place as bottom avatarBg
        YANTexturedNode doneButton = getNode(DONE_BUTTON_INDEX);
        doneButton.setSortingLayer(bottomAvatarIcon.getSortingLayer() + 1);
        doneButton.setAnchorPoint(1f, 1f);
        doneButton.setPosition(takeButton.getPosition().getX(), takeButton.getPosition().getY());

        //setup avatarBg for top right player
        float topOffset = sceneSize.getY() * 0.07f;
        avatarBg = getNode(AVATAR_BG_TOP_RIGHT_INDEX);
        avatarBg.setAnchorPoint(1f, 0f);
        avatarBg.setSortingLayer(HUD_SORTING_LAYER + 1);
        avatarBg.setPosition(sceneSize.getX() - offsetX, topOffset);

        //setup top right timer
        YANBaseNode topRightTimer = getNode(CIRCLE_TIMER_TOP_RIGHT_INDEX);
        offsetSize = (avatarBg.getSize().getX() - topRightTimer.getSize().getX()) / 2;
        topRightTimer.setSortingLayer(avatarBg.getSortingLayer() + 1);
        topRightTimer.setAnchorPoint(1f, 0f);
        topRightTimer.setPosition(avatarBg.getPosition().getX() - offsetSize, avatarBg.getPosition().getY() + offsetSize);

        //setup icon for top right player
        YANTexturedNode topRightAvatarIcon = getNode(AVATAR_ICON_TOP_RIGHT_INDEX);
        topRightAvatarIcon.setAnchorPoint(1f, 0f);
        topRightAvatarIcon.setSortingLayer(topRightTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - topRightAvatarIcon.getSize().getX()) / 2;
        topRightAvatarIcon.setPosition(avatarBg.getPosition().getX() - offsetSize, avatarBg.getPosition().getY() + offsetSize);

        //setup avatarBg for top left player
        avatarBg = getNode(AVATAR_BG_TOP_LEFT_INDEX);
        avatarBg.setAnchorPoint(0f, 0f);
        avatarBg.setSortingLayer(HUD_SORTING_LAYER + 1);
        avatarBg.setPosition(offsetX, topOffset);

        //setup top left timer
        YANBaseNode topLeftTimer = getNode(CIRCLE_TIMER_TOP_LEFT_INDEX);
        offsetSize = (avatarBg.getSize().getX() - topLeftTimer.getSize().getX()) / 2;
        topLeftTimer.setSortingLayer(avatarBg.getSortingLayer() + 1);
        topLeftTimer.setAnchorPoint(0f, 0f);
        topLeftTimer.setPosition(avatarBg.getPosition().getX() + offsetSize, avatarBg.getPosition().getY() + offsetSize);

        //setup icon for top left player
        YANTexturedNode topLeftAvatarIcon = getNode(AVATAR_ICON_TOP_LEFT_INDEX);
        topLeftAvatarIcon.setAnchorPoint(0f, 0f);
        topLeftAvatarIcon.setSortingLayer(topLeftTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - topLeftAvatarIcon.getSize().getX()) / 2;
        topLeftAvatarIcon.setPosition(avatarBg.getPosition().getX() + offsetSize, avatarBg.getPosition().getY() + offsetSize);

        //trump image
        getNode(TRUMP_IMAGE_INDEX).setPosition((sceneSize.getX() - getNode(TRUMP_IMAGE_INDEX).getSize().getX()) / 2, sceneSize.getY() * 0.06f);

        //setup popups
        float popupAnchorXOffset = getNode(YOU_WIN_IMAGE_INDEX).getSize().getX() / 2;
        float popupAnchorYOffset = getNode(YOU_WIN_IMAGE_INDEX).getSize().getY() / 2;
        getNode(YOU_WIN_IMAGE_INDEX).setPosition(
                ((sceneSize.getX() - getNode(YOU_WIN_IMAGE_INDEX).getSize().getX()) / 2) + popupAnchorXOffset,
                ((sceneSize.getY() - getNode(YOU_WIN_IMAGE_INDEX).getSize().getY()) / 2) + popupAnchorYOffset);
        getNode(YOU_LOOSE_IMAGE_INDEX).setPosition(getNode(YOU_WIN_IMAGE_INDEX).getPosition().getX(), getNode(YOU_WIN_IMAGE_INDEX).getPosition().getY());

        //v button
        getNode(V_BUTTON_INDEX).setPosition(
                getNode(YOU_WIN_IMAGE_INDEX).getPosition().getX() - (getNode(V_BUTTON_INDEX).getSize().getX() / 2),
                getNode(YOU_WIN_IMAGE_INDEX).getPosition().getY() - ((getNode(V_BUTTON_INDEX).getSize().getY()) * 1.25f) + popupAnchorYOffset);

        //fence
        float centerX = (sceneSize.getX() - getNode(FENCE_INDEX).getSize().getX()) / 2;
        float centerY = (sceneSize.getY() - getNode(FENCE_INDEX).getSize().getY());
        getNode(FENCE_INDEX).setPosition(centerX, centerY);

        //glade
        centerX = (sceneSize.getX() - getNode(GLADE_INDEX).getSize().getX()) / 2;
        centerY = (sceneSize.getY() - getNode(GLADE_INDEX).getSize().getY()) / 2;
        getNode(GLADE_INDEX).setPosition(centerX, centerY);
    }

    public void update(float deltaTimeSeconds) {
    }

    public void setTakeButtonClickListener(YANButtonNode.YanButtonNodeClickListener listener) {
        mTakeButtonClickListener = listener;
    }

    public void setFinishButtonClickListener(YANButtonNode.YanButtonNodeClickListener listener) {
        mDoneBtnClickListener = listener;
    }

    public void showFinishButton() {

        //attach a click listener at the end of animation
        YANButtonNode doneBtn = getNode(DONE_BUTTON_INDEX);
        doneBtn.setSortingLayer(getNode(TAKE_BUTTON_INDEX).getSortingLayer() + 1);
        doneBtn.setOpacity(1f);
        doneBtn.setClickListener(mDoneBtnClickListener);
    }

    public void showTakeButton() {
        YANButtonNode takeBtn = getNode(TAKE_BUTTON_INDEX);
        takeBtn.setSortingLayer(getNode(DONE_BUTTON_INDEX).getSortingLayer() + 1);
        takeBtn.setOpacity(1f);
        takeBtn.setClickListener(mTakeButtonClickListener);
    }

    public void hideTakeButton() {
        YANButtonNode takeBtn = getNode(TAKE_BUTTON_INDEX);
        takeBtn.setSortingLayer(takeBtn.getSortingLayer() - 1);
        takeBtn.setClickListener(null);
        takeBtn.setOpacity(0);
    }

    public void hideFinishButton() {
        YANButtonNode doneBtn = getNode(DONE_BUTTON_INDEX);
        doneBtn.setSortingLayer(doneBtn.getSortingLayer() - 1);
        doneBtn.setClickListener(null);
        doneBtn.setOpacity(0);
    }

    public void setTrumpSuit(String suit) {
        //change texture region
        YANTexturedNode trumpImage = getNode(TRUMP_IMAGE_INDEX);
        trumpImage.setTextureRegion(mHudAtlas.getTextureRegion("trump_marker_" + suit.toLowerCase() + ".png"));

        //fix aspect ratio
        float aspectRatio = trumpImage.getTextureRegion().getWidth() / trumpImage.getTextureRegion().getHeight();
        trumpImage.setSize(trumpImage.getSize().getX(), trumpImage.getSize().getX() / aspectRatio);
    }

    public void showYouWonMessage() {
        makeNodeAppearWithAnimation(getNode(YOU_WIN_IMAGE_INDEX), showVButtonTweenCallback);
    }

    public void showYouLooseMessage() {
        makeNodeAppearWithAnimation(getNode(YOU_LOOSE_IMAGE_INDEX), showVButtonTweenCallback);
    }

    private void makeNodeAppearWithAnimation(YANBaseNode node, TweenCallback cbk) {
        Timeline sequence = Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_X, POPUP_ANIMATION_DURATION).target(node.getSize().getX()))
                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_Y, POPUP_ANIMATION_DURATION).target(node.getSize().getY()))
                .push(Tween.to(node, YANTweenNodeAccessor.OPACITY, POPUP_ANIMATION_DURATION).target(1f))
                .push(Tween.to(node, YANTweenNodeAccessor.ROTATION_Z_CW, POPUP_ANIMATION_DURATION).target(360)).setCallback(cbk).end();

        //make the popup barley visible
        node.setOpacity(0f);
        node.setSize(0.1f, 0.1f);

        //animate
        sequence.start(mTweenManager);
    }
}