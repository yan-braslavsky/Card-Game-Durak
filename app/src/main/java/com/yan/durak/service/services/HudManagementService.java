package com.yan.durak.service.services;


import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.yan.durak.screens.BaseGameScreen;
import com.yan.durak.service.IService;
import com.yan.durak.session.GameInfo;

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
import glengine.yan.glengine.assets.YANAssetManager;
import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANCircleNode;
import glengine.yan.glengine.nodes.YANTextNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.nodes.YANTexturedScissorNode;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;
import glengine.yan.glengine.util.colors.YANColor;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.loggers.YANLogger;

/**
 * Created by Yan-Home on 1/25/2015.
 */
public class HudManagementService implements IService {


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SPEECH_BUBBLE_TAKING_TEXT,
            SPEECH_BUBBLE_PASS_TEXT,
            SPEECH_BUBBLE_DONE_TEXT,
            SPEECH_BUBBLE_THROW_IN_END_TEXT,
            SPEECH_BUBBLE_ATTACK_TEXT,
            SPEECH_BUBBLE_THINKING_TEXT,
            SPEECH_BUBBLE_RETALIATION_TEXT
    })
    public @interface SpeechBubbleText {
    }

    public static final String SPEECH_BUBBLE_TAKING_TEXT = "I'll Take!";
    public static final String SPEECH_BUBBLE_PASS_TEXT = "Pass";
    public static final String SPEECH_BUBBLE_DONE_TEXT = "Done!";
    public static final String SPEECH_BUBBLE_THROW_IN_END_TEXT = "Adding";
    public static final String SPEECH_BUBBLE_THINKING_TEXT = "Thinking";
    public static final String SPEECH_BUBBLE_ATTACK_TEXT = "My Turn!";
    public static final String SPEECH_BUBBLE_RETALIATION_TEXT = "Defending";

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
            CIRCLE_TIMER_TOP_LEFT_INDEX,
            ROOF_INDEX,
            BOTTOM_SPEECH_BUBBLE_INDEX,
            TOP_RIGHT_SPEECH_BUBBLE_INDEX,
            TOP_LEFT_SPEECH_BUBBLE_INDEX,
            BOTTOM_SPEECH_BUBBLE_TEXT_INDEX,
            TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX,
            TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX
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
    public static final int ROOF_INDEX = 19;
    public static final int BOTTOM_SPEECH_BUBBLE_INDEX = 20;
    public static final int TOP_RIGHT_SPEECH_BUBBLE_INDEX = 21;
    public static final int TOP_LEFT_SPEECH_BUBBLE_INDEX = 22;
    public static final int BOTTOM_SPEECH_BUBBLE_TEXT_INDEX = 23;
    public static final int TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX = 24;
    public static final int TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX = 25;

    /**
     * By default hud will be placed on hud sorting layer and above
     */
    public static final int HUD_SORTING_LAYER = 500;

    /**
     * Duration of appearing of the popup window
     */
    private static final float POPUP_ANIMATION_DURATION = 0.7f;

    /**
     * Duration of scale up scale down animation
     */
    private static final float SCALE_UP_SCALE_DOWN_ANIMATION_DURATION = 0.4f;

    /**
     * Time when speech bubble is fully visible
     */
    public static final float SPEECH_BUBBLE_VISIBILITY_DURATION_SECONDS = 1.3f;

    /**
     * Time when speech bubble is fully visible
     */
    public static final float SPEECH_BUBBLE_APPEARANCE_DURATION_SECONDS = 0.25f;

    /**
     * Used to perform tween animations
     */
    private final TweenManager mTweenManager;

    /**
     * All nodes that exist in the hud manager will be placed in this map
     */
    private Map<Integer, YANBaseNode> mHudNodesMap;

    private static final YANColor SPEECH_BUBBLE_TEXT_COLOR = YANColor.createFromHexColor(0x4F3723);

    public static final YANColor TIMER_RETALIATION_COLOR = YANColor.createFromHexColor(0xFFF200);
    public static final YANColor TIMER_THROW_IN_COLOR = YANColor.createFromHexColor(0x00A5B2);

    private YANTextureAtlas mHudAtlas;
    private TweenCallback showVButtonTweenCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> baseTween) {
            if (TweenCallback.COMPLETE == type) {
                getNode(V_BUTTON_INDEX).setOpacity(1f);
            }
        }
    };
//    private TweenCallback scaleUpScaleDownTweenCallback = new TweenCallback() {
//        @Override
//        public void onEvent(int type, BaseTween<?> baseTween) {
//            if (TweenCallback.COMPLETE == type) {
//                getNode(V_BUTTON_INDEX).setOpacity(1f);
//            }
//        }
//    };

    //Cached click listeners for action buttons
    private YANButtonNode.YanButtonNodeClickListener mDoneBtnClickListener;
    private YANButtonNode.YanButtonNodeClickListener mTakeButtonClickListener;

    /**
     * Timer node that is currently animated
     */
    private YANCircleNode mActiveTimerNode;


    public HudManagementService(TweenManager tweenManager) {
        mTweenManager = tweenManager;
        mHudNodesMap = new HashMap<>();

//        //preallocate speech bubble delayed task
//        YANObjectPool.getInstance().preallocate(YANDelayedTask.class, 3);
//        YANObjectPool.getInstance().preallocate(HideSpeechBubbleDelayedTaskListener.class, 3);
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

        //speech bubbles
        putToNodeMap(BOTTOM_SPEECH_BUBBLE_INDEX, createSpeechBubble(hudAtlas));
        putToNodeMap(TOP_RIGHT_SPEECH_BUBBLE_INDEX, createSpeechBubble(hudAtlas));
        putToNodeMap(TOP_LEFT_SPEECH_BUBBLE_INDEX, createSpeechBubble(hudAtlas));

        //speechBubble text
        putToNodeMap(BOTTOM_SPEECH_BUBBLE_TEXT_INDEX, createSpeechBubbleText(hudAtlas));
        putToNodeMap(TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX, createSpeechBubbleText(hudAtlas));
        putToNodeMap(TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX, createSpeechBubbleText(hudAtlas));

        //create avatar_1 icons
        putToNodeMap(AVATAR_ICON_BOTTOM_RIGHT_INDEX, createAvatarIcon(hudAtlas, "avatar_1.png"));
        putToNodeMap(AVATAR_ICON_TOP_RIGHT_INDEX, createAvatarIcon(hudAtlas, "avatar_2.png"));
        putToNodeMap(AVATAR_ICON_TOP_LEFT_INDEX, createAvatarIcon(hudAtlas, "avatar_3.png"));

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
        putToNodeMap(ROOF_INDEX, createRoof(hudAtlas));

        //at the beginning some nodes might have a different state
        setupInitialState();

    }

    private YANBaseNode createSpeechBubbleText(YANTextureAtlas hudAtlas) {
        YANTextNode yanTextNode = new YANTextNode(YANAssetManager.getInstance().getLoadedFont(BaseGameScreen.SPEECH_BUBBLES_FONT_NAME), "I will Take This !".length());
        yanTextNode.setTextColor(SPEECH_BUBBLE_TEXT_COLOR.getR(), SPEECH_BUBBLE_TEXT_COLOR.getG(), SPEECH_BUBBLE_TEXT_COLOR.getB());

        //we are setting the longest text that will be used
        yanTextNode.setText(SPEECH_BUBBLE_TAKING_TEXT);
        return yanTextNode;
    }

    private YANBaseNode createSpeechBubble(YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("speech_bubble.png"));
    }

    private YANBaseNode createRoof(YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("roof.png"));
    }

    private YANCircleNode createCircleTimer() {
        YANCircleNode yanCircleNode = new YANCircleNode();
        yanCircleNode.setColor(TIMER_RETALIATION_COLOR.getR(), TIMER_RETALIATION_COLOR.getG(), TIMER_RETALIATION_COLOR.getB());
        yanCircleNode.setClockWiseDraw(false);
        yanCircleNode.setPieCirclePercentage(1f);
        return yanCircleNode;
    }

    private YANTexturedNode createAvatarIcon(YANTextureAtlas hudAtlas, String avatarTextureName) {
        return new YANTexturedNode(hudAtlas.getTextureRegion(avatarTextureName));
    }

    private YANTexturedNode createCardGlow(YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("card_glow.png"));
    }

    private void setupInitialState() {

        //popup images are invisible
        getNode(YOU_WIN_IMAGE_INDEX).setOpacity(0);
        getNode(YOU_LOOSE_IMAGE_INDEX).setOpacity(0);

        //timers are invisible
        getNode(CIRCLE_TIMER_BOTTOM_RIGHT_INDEX).setOpacity(0);
        getNode(CIRCLE_TIMER_TOP_RIGHT_INDEX).setOpacity(0);
        getNode(CIRCLE_TIMER_TOP_LEFT_INDEX).setOpacity(0);

        //popups anchor is at the middle
        getNode(YOU_WIN_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);
        getNode(YOU_LOOSE_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);

        //v button
        getNode(V_BUTTON_INDEX).setOpacity(0);

        //action buttons also have zero opacity
        getNode(TAKE_BUTTON_INDEX).setOpacity(0);
        getNode(DONE_BUTTON_INDEX).setOpacity(0);
        getNode(GLOW_INDEX).setOpacity(0);

        //all speech bubbles and their texts are invisible at the beginning
        getNode(BOTTOM_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(TOP_RIGHT_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(TOP_LEFT_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(BOTTOM_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
        getNode(TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
        getNode(TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
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

        //speech bubbles
        YANTexturedNode bottomSpeechBubble = getNode(BOTTOM_SPEECH_BUBBLE_INDEX);
        aspectRatio = bottomSpeechBubble.getTextureRegion().getWidth() / bottomSpeechBubble.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.4f;
        newHeight = newWidth / aspectRatio;

        getNode(BOTTOM_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);
        getNode(TOP_RIGHT_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);
        getNode(TOP_LEFT_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);

        //set avatar_1 icons
        //check how much the icon smaller than background
        YANTexturedNode bottomRightAvatarIcon = getNode(AVATAR_ICON_BOTTOM_RIGHT_INDEX);
        float avatarIconToAvatarBgScaleFactor = bottomRightAvatarIcon.getTextureRegion().getWidth() / avatarBGTopRight.getTextureRegion().getWidth();

        float bottomIconSize = getNode(AVATAR_BG_BOTTOM_RIGHT_INDEX).getSize().getX() * avatarIconToAvatarBgScaleFactor;
        //set bottom avatar_1 icon
        bottomRightAvatarIcon.setSize(bottomIconSize, bottomIconSize);

        //setup bottom timer size
        //calculate timer scale factor
        float timerToIconScaleFactor = avatarIconToAvatarBgScaleFactor + 0.105f;
        float bottomTimerSize = getNode(AVATAR_BG_BOTTOM_RIGHT_INDEX).getSize().getX() * timerToIconScaleFactor;
        getNode(CIRCLE_TIMER_BOTTOM_RIGHT_INDEX).setSize(bottomTimerSize, bottomTimerSize);

        //set top avatar_1 icons
        float topIconsSize = avatarBGTopRight.getSize().getX() * avatarIconToAvatarBgScaleFactor;
        getNode(AVATAR_ICON_TOP_RIGHT_INDEX).setSize(topIconsSize, topIconsSize);
        getNode(AVATAR_ICON_TOP_LEFT_INDEX).setSize(topIconsSize, topIconsSize);

        //set top timers size
        timerToIconScaleFactor += 0.335f;
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

        //rood
        YANTexturedNode roofImage = getNode(ROOF_INDEX);
        aspectRatio = roofImage.getTextureRegion().getWidth() / roofImage.getTextureRegion().getHeight();
        roofImage.setSize(sceneSize.getX(), sceneSize.getX() / aspectRatio);

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

        //setup bottom avatar_1 icon
        YANTexturedNode bottomAvatarIcon = getNode(AVATAR_ICON_BOTTOM_RIGHT_INDEX);
        float bottomAvatarIconHalfSize = bottomAvatarIcon.getSize().getX() / 2;
        bottomAvatarIcon.setAnchorPoint(0.5f, 0.5f);
        bottomAvatarIcon.setSortingLayer(bottomTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - bottomAvatarIcon.getSize().getX()) / 2;
        bottomAvatarIcon.setPosition(avatarBg.getPosition().getX() - offsetSize - bottomAvatarIconHalfSize, avatarBg.getPosition().getY() - offsetSize - bottomAvatarIconHalfSize);

        //take action is at the same place as bottom avatarBg
        YANTexturedNode takeButton = getNode(TAKE_BUTTON_INDEX);
        takeButton.setAnchorPoint(0.5f, 0.5f);
        takeButton.setSortingLayer(bottomAvatarIcon.getSortingLayer() + 1);
        takeButton.setPosition(bottomAvatarIcon.getPosition().getX(), bottomAvatarIcon.getPosition().getY());


        //finish action is at the same place as bottom avatarBg
        YANTexturedNode doneButton = getNode(DONE_BUTTON_INDEX);
        doneButton.setSortingLayer(bottomAvatarIcon.getSortingLayer() + 1);
        doneButton.setAnchorPoint(0.5f, 0.5f);
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
        float topRightAvatarHalfSize = topRightAvatarIcon.getSize().getX() / 2;
        topRightAvatarIcon.setAnchorPoint(0.5f, 0.5f);
        topRightAvatarIcon.setSortingLayer(topRightTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - topRightAvatarIcon.getSize().getX()) / 2;
        topRightAvatarIcon.setPosition(avatarBg.getPosition().getX() - offsetSize - topRightAvatarHalfSize, avatarBg.getPosition().getY() + offsetSize + topRightAvatarHalfSize);

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
        float topLeftAvatarHalfSize = topLeftAvatarIcon.getSize().getX() / 2;
        topLeftAvatarIcon.setAnchorPoint(0.5f, 0.5f);
        topLeftAvatarIcon.setSortingLayer(topLeftTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - topLeftAvatarIcon.getSize().getX()) / 2;
        topLeftAvatarIcon.setPosition(avatarBg.getPosition().getX() + offsetSize + topLeftAvatarHalfSize, avatarBg.getPosition().getY() + offsetSize + topLeftAvatarHalfSize);

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

        //roof
        getNode(ROOF_INDEX).setSortingLayer(1000);

        //glade
        centerX = (sceneSize.getX() - getNode(GLADE_INDEX).getSize().getX()) / 2;
        centerY = (sceneSize.getY() - getNode(GLADE_INDEX).getSize().getY()) / 2;
        getNode(GLADE_INDEX).setPosition(centerX, centerY);


        //speech bubbles
        //bottom speech bubble
        YANBaseNode bottomSpeechBubble = getNode(BOTTOM_SPEECH_BUBBLE_INDEX);
        bottomSpeechBubble.setAnchorPoint(1f, 1f);
        bottomSpeechBubble.setSortingLayer(HUD_SORTING_LAYER + 100);
        bottomSpeechBubble.setPosition(sceneSize.getX() - (sceneSize.getX() * 0.05f),
                bottomAvatarIcon.getPosition().getY() - (bottomAvatarIcon.getSize().getY() / 2));

        //top right speech bubble
        YANBaseNode topRightSpeechBubble = getNode(TOP_RIGHT_SPEECH_BUBBLE_INDEX);
        topRightSpeechBubble.setRotationZ(180);
        topRightSpeechBubble.setRotationY(180);
        topRightSpeechBubble.setAnchorPoint(1f, 0f);
        topRightSpeechBubble.setSortingLayer(HUD_SORTING_LAYER + 100);
        topRightSpeechBubble.setPosition(sceneSize.getX() - (sceneSize.getX() * 0.05f),
                topRightAvatarIcon.getPosition().getY() + (topRightAvatarIcon.getSize().getY() / 2));

        //top left speech bubble
        YANBaseNode topLeftSpeechBubble = getNode(TOP_LEFT_SPEECH_BUBBLE_INDEX);
        topLeftSpeechBubble.setRotationZ(180);
        topLeftSpeechBubble.setAnchorPoint(0f, 0f);
        topLeftSpeechBubble.setSortingLayer(HUD_SORTING_LAYER + 100);
        topLeftSpeechBubble.setPosition((sceneSize.getX() * 0.05f),
                topLeftAvatarIcon.getPosition().getY() + (topLeftAvatarIcon.getSize().getY() / 2));

        //speech bubble texts
        //bottom
        float rightSpeechTextXPosition = bottomSpeechBubble.getPosition().getX() - bottomSpeechBubble.getSize().getX();
        rightSpeechTextXPosition += sceneSize.getX() * 0.06f;
        float bottomSpeechTextY = bottomSpeechBubble.getPosition().getY() - (bottomSpeechBubble.getSize().getY() / 1.4f);

        YANTextNode bottomSpeechBubbleText = getNode(BOTTOM_SPEECH_BUBBLE_TEXT_INDEX);
        bottomSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        bottomSpeechBubbleText.setPosition(
                //middle of speech bubble
                bottomSpeechBubble.getPosition().getX() - (bottomSpeechBubble.getSize().getX() * 0.53f),
                bottomSpeechBubble.getPosition().getY() - (bottomSpeechBubble.getSize().getY() * 0.5f) - (bottomSpeechBubbleText.getSize().getY() * 0.25f));
        bottomSpeechBubbleText.setSortingLayer(bottomSpeechBubble.getSortingLayer() + 1);

        //top right
        YANBaseNode topRightSpeechBubbleText = getNode(TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX);
        topRightSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        topRightSpeechBubbleText.setPosition(
                topRightSpeechBubble.getPosition().getX() - (topRightSpeechBubble.getSize().getX() * 0.5f),
                topRightSpeechBubble.getPosition().getY() + (topRightSpeechBubble.getSize().getY() * 0.5f) + (topRightSpeechBubbleText.getSize().getY() * 0.2f));
        topRightSpeechBubbleText.setSortingLayer(topRightSpeechBubble.getSortingLayer() + 1);

        //top left
        YANBaseNode topLeftSpeechBubbleText = getNode(TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX);
        topLeftSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        topLeftSpeechBubbleText.setPosition(
                topLeftSpeechBubble.getPosition().getX() + (topLeftSpeechBubble.getSize().getX() / 2),
                topLeftSpeechBubble.getPosition().getY() + (topLeftSpeechBubble.getSize().getY() * 0.5f) + (topLeftSpeechBubbleText.getSize().getY() * 0.2f));
        topLeftSpeechBubbleText.setSortingLayer(topLeftSpeechBubble.getSortingLayer() + 1);
    }

    public void update(float deltaTimeSeconds) {
        if (mActiveTimerNode == null)
            return;

        //TODO : make timer actualy dependant on time
        float currentPercentage = mActiveTimerNode.getPieCirclePercentage();
        currentPercentage -= 0.0006f;
        mActiveTimerNode.setPieCirclePercentage(currentPercentage);
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


    public void animateScaleUpPlayerAvatar(@NonNull GameInfo.Player player) {

//        YANBaseNode backgroundAvatarNode = getAvatarBgForPlayer(player);
        final YANBaseNode avatarIconNode = getIconForPlayer(player);
        final int originalSortingLayer = avatarIconNode.getSortingLayer();
        avatarIconNode.setSortingLayer(HUD_SORTING_LAYER + 1000);
//        YANBaseNode timerNode = getTimerNodeForPlayer(player);

        float originSize = avatarIconNode.getSize().getX();
        float targetSize = originSize * 1.3f;

        Timeline sequence = Timeline.createSequence()
                .beginSequence().beginParallel()
                .push(Tween.to(avatarIconNode, YANTweenNodeAccessor.SIZE_X, SCALE_UP_SCALE_DOWN_ANIMATION_DURATION).target(targetSize))
                .push(Tween.to(avatarIconNode, YANTweenNodeAccessor.SIZE_Y, SCALE_UP_SCALE_DOWN_ANIMATION_DURATION).target(targetSize))
                .end()
                .beginSequence().beginParallel()
                .push(Tween.to(avatarIconNode, YANTweenNodeAccessor.SIZE_X, SCALE_UP_SCALE_DOWN_ANIMATION_DURATION).target(originSize))

                        //TODO : avoid allocations
                .push(Tween.to(avatarIconNode, YANTweenNodeAccessor.SIZE_Y, SCALE_UP_SCALE_DOWN_ANIMATION_DURATION).target(originSize)).setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> baseTween) {
                        if (TweenCallback.COMPLETE == type) {
                            avatarIconNode.setSortingLayer(originalSortingLayer);
                        }
                    }
                });

        //animate
        sequence.start(mTweenManager);
    }

    private YANBaseNode getIconForPlayer(GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(AVATAR_ICON_BOTTOM_RIGHT_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(AVATAR_ICON_TOP_RIGHT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(AVATAR_ICON_TOP_LEFT_INDEX);
            default:
                return null;
        }
    }

    public void showSpeechBubbleWithText(@NonNull @SpeechBubbleText String text, @NonNull GameInfo.Player player) {
        YANBaseNode speechBubbleNode = getSpeechBubbleForPlayer(player);
        YANTextNode textNode = getTextNodeForPlayer(player);

        //TODO : it is not an elegant way
        adjustSpeechTextScale(text, speechBubbleNode, textNode);

        //kill all previous animations
        mTweenManager.killTarget(speechBubbleNode);
        mTweenManager.killTarget(textNode);

        Timeline sequence = Timeline.createSequence()
                .beginSequence()
                .beginParallel()
                .push(Tween.to(speechBubbleNode, YANTweenNodeAccessor.OPACITY, SPEECH_BUBBLE_APPEARANCE_DURATION_SECONDS).target(1f))
                .push(Tween.to(textNode, YANTweenNodeAccessor.OPACITY, SPEECH_BUBBLE_APPEARANCE_DURATION_SECONDS).target(1f))
                .end()
                .pushPause(SPEECH_BUBBLE_VISIBILITY_DURATION_SECONDS)
                .beginParallel()
                .push(Tween.to(speechBubbleNode, YANTweenNodeAccessor.OPACITY, SPEECH_BUBBLE_APPEARANCE_DURATION_SECONDS).target(0f))
                .push(Tween.to(textNode, YANTweenNodeAccessor.OPACITY, SPEECH_BUBBLE_APPEARANCE_DURATION_SECONDS).target(0f))
                .end();

        //animate
        sequence.start(mTweenManager);
    }

    private YANVector2 _cachedVector = new YANVector2();

    /**
     * Text can appear differently on different screens.
     * In order to scale the node properly we need to do some calculations.
     */
    private void adjustSpeechTextScale(@NonNull @SpeechBubbleText String text, @NonNull YANBaseNode speechBubbleNode, @NonNull YANTextNode textNode) {
        float percentageOfSpeechBubbleWidth = (text == SPEECH_BUBBLE_PASS_TEXT) ? 0.37f : (text == SPEECH_BUBBLE_THROW_IN_END_TEXT) ? 0.55f : 0.65f;
        float maxAllowedTextWidth = speechBubbleNode.getSize().getX() * percentageOfSpeechBubbleWidth;
        float rangeDelta = 5f;
        float neededScale = 1.0f;
        textNode.calculateSizeForString(text, neededScale, _cachedVector);
        if (!isFloatInRange(_cachedVector.getX(), maxAllowedTextWidth - rangeDelta, maxAllowedTextWidth + rangeDelta)) {
            neededScale = maxAllowedTextWidth / _cachedVector.getX();
        }
        textNode.setTextScale(neededScale);
        textNode.setText(text);
    }

    private boolean isFloatInRange(float num, float min, float max) {
        return num < max && num > min;
    }

    private YANTextNode getTextNodeForPlayer(@NonNull GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(BOTTOM_SPEECH_BUBBLE_TEXT_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX);
        }
        return null;
    }

    private YANBaseNode getSpeechBubbleForPlayer(@NonNull GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(BOTTOM_SPEECH_BUBBLE_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(TOP_RIGHT_SPEECH_BUBBLE_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(TOP_LEFT_SPEECH_BUBBLE_INDEX);
        }
        return null;
    }


    public void startTimerForPlayer(@NonNull GameInfo.Player player, YANColor timerColor) {

        if (mActiveTimerNode != null) {
            //reset previous timer
            mActiveTimerNode.setPieCirclePercentage(1f);
            mActiveTimerNode.setOpacity(0f);
        }

        //set new timer as active
        mActiveTimerNode = getTimerNodeForPlayer(player);
        mActiveTimerNode.setColor(timerColor.getR(), timerColor.getG(), timerColor.getB());
        mActiveTimerNode.setOpacity(1f);
    }

    private YANCircleNode getTimerNodeForPlayer(GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(CIRCLE_TIMER_BOTTOM_RIGHT_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(CIRCLE_TIMER_TOP_RIGHT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(CIRCLE_TIMER_TOP_LEFT_INDEX);
            default:
                return null;
        }
    }


//    /**
//     * We retaining this class to not create many instances of it
//     */
//    protected static class HideSpeechBubbleDelayedTaskListener implements YANDelayedTask.YANDelayedTaskListener, YANIPoolableObject {
//        private YANBaseNode mSpeechBubbleNode;
//        private YANTextNode mSpeechBubbleTextNode;
//        private YANDelayedTask mDelayedTask;
//
//        public HideSpeechBubbleDelayedTaskListener() {
//            //Empty constructor required
//        }
//
//        @Override
//        public void onComplete() {
//
//            //hide the speech bubble and text
//            mSpeechBubbleNode.setOpacity(0f);
//            mSpeechBubbleTextNode.setOpacity(0f);
//
//            //recycle delayed task
//            YANObjectPool.getInstance().offer(mDelayedTask);
//
//            //recycle this listener
//            YANObjectPool.getInstance().offer(HideSpeechBubbleDelayedTaskListener.this);
//
//        }
//
//        @Override
//        public void resetState() {
//            mSpeechBubbleNode = null;
//            mSpeechBubbleTextNode = null;
//            mDelayedTask = null;
//        }
//
//        public void setSpeechBubbleNode(YANBaseNode speechBubbleNode) {
//            mSpeechBubbleNode = speechBubbleNode;
//        }
//
//        public void setSpeechBubbleTextNode(YANTextNode speechBubbleTextNode) {
//            mSpeechBubbleTextNode = speechBubbleTextNode;
//        }
//
//        public void setDelayedTask(YANDelayedTask delayedTask) {
//            mDelayedTask = delayedTask;
//        }
//    }
}