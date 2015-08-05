package com.yan.durak.services.hud;


import android.support.annotation.NonNull;

import com.yan.durak.services.SceneSizeProviderService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.IActivePlayerState;

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
import glengine.yan.glengine.nodes.YANTextNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.service.IService;
import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;
import glengine.yan.glengine.util.colors.YANColor;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by Yan-Home on 1/25/2015.
 */
public class HudManagementService implements IService {


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


    private static final float TOTAL_THROW_IN_TIMER_DURATION_SECONDS = 8f;

    private static final float TOTAL_RETALIATION_TIMER_DURATION_SECONDS = 12f;


    /**
     * Used to perform tween animations
     */
    private final TweenManager mTweenManager;

    /**
     * All nodes that exist in the hud manager will be placed in this map
     */
    private Map<Integer, YANBaseNode> mHudNodesMap;

    public static final YANColor TIMER_RETALIATION_COLOR = YANColor.createFromHexColor(0xFFF200);
    public static final YANColor TIMER_THROW_IN_COLOR = YANColor.createFromHexColor(0x00A5B2);

    //used for utility method calculation
    private static YANVector2 _cachedVector = new YANVector2();

    /**
     * Text can appear differently on different screens.
     * In order to scale the node properly we need to do some calculations.
     *
     * @param text                text that will be presented by the text node
     * @param maxAllowedTextWidth maximum allowed width of the text node
     * @param textNode            text node that will be used
     */
    public static void adjustTextScaleToFitBounds(@NonNull final String text, final float maxAllowedTextWidth, @NonNull final YANTextNode textNode) {
        float rangeDelta = 5f;
        float neededScale = 1.0f;
        textNode.calculateSizeForString(text, neededScale, _cachedVector);
        if (!isFloatInRange(_cachedVector.getX(), maxAllowedTextWidth - rangeDelta, maxAllowedTextWidth + rangeDelta)) {
            neededScale = maxAllowedTextWidth / _cachedVector.getX();
        }
        textNode.setTextScale(neededScale);
        textNode.setText(text);
    }

    private static boolean isFloatInRange(float num, float min, float max) {
        return num < max && num > min;
    }

    private YANTextureAtlas mHudAtlas;
    private TweenCallback showVButtonTweenCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> baseTween) {
            if (TweenCallback.COMPLETE == type) {
                getNode(HudNodes.V_BUTTON_INDEX).setOpacity(1f);
            }
        }
    };

    //Cached click listeners for action buttons
    private YANButtonNode.YanButtonNodeClickListener mDoneBtnClickListener;
    private YANButtonNode.YanButtonNodeClickListener mTakeButtonClickListener;

    /**
     * Timer node that is currently animated
     */
    private YANCircleNode mActiveTimerNode;
    private HudNodesCreator mHudNodesCreator;
    private HudNodesPositioner mHudNodesPositioner;
    private TimerListener mTimerListener;

    /**
     * Hides all related UI for given player
     */
    public void hidePlayerUI(GameInfo.Player player) {
        getSpeechBubbleTextNodeForPlayer(player).setOpacity(0);
        getBGAvatarForPlayer(player).setOpacity(0);
        getIconForPlayer(player).setOpacity(0);
        getTimerNodeForPlayer(player).setOpacity(0);
        getNameBgForPlayer(player).setOpacity(0);
        getNameTextNodeForPlayer(player).setOpacity(0);
    }

    private YANTexturedNode getNameBgForPlayer(GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                throw new IllegalStateException("There is no name for bottom player");
            case TOP_RIGHT_PLAYER:
                return getNode(HudNodes.NAME_BG_TOP_RIGHT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(HudNodes.NAME_BG_TOP_LEFT_INDEX);
            default:
                return null;
        }
    }

    private YANTextNode getNameTextNodeForPlayer(GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                throw new IllegalStateException("There is no name for bottom player");
            case TOP_RIGHT_PLAYER:
                return getNode(HudNodes.NAME_BG_TOP_RIGHT_TEXT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(HudNodes.NAME_BG_TOP_LEFT_TEXT_INDEX);
            default:
                return null;
        }
    }

    /**
     * Places the trump icon at the right top position
     */
    public void placeTrumpIconAtRightTop() {
        SceneSizeProviderService screenSize = ServiceLocator.locateService(SceneSizeProviderService.class);
        //trump image
        float originalYPos = getNode(HudNodes.TRUMP_IMAGE_INDEX).getPosition().getY();
        float xPos = getNode(HudNodes.MASK_CARD_INDEX).getPosition().getX() + screenSize.getSceneWidth() * 0.05f;
        getNode(HudNodes.TRUMP_IMAGE_INDEX).setPosition(xPos, originalYPos);

    }

    public void setNameForPlayer(@NonNull final GameInfo.Player player, @NonNull final String name) {
        YANTexturedNode nameBgForPlayer = getNameBgForPlayer(player);
        YANTextNode nameTextNodeForPlayer = getNameTextNodeForPlayer(player);
        adjustTextScaleToFitBounds(name,
                nameBgForPlayer.getSize().getX() * 0.6f, nameTextNodeForPlayer);
        //now we need to center vertically
        nameTextNodeForPlayer.setPosition(nameTextNodeForPlayer.getPosition().getX()
                , nameBgForPlayer.getPosition().getY() + (nameBgForPlayer.getSize().getY() - nameTextNodeForPlayer.getSize().getY()) / 2);
    }


    public interface TimerListener {
        void onTimerExpired(YANCircleNode activeTimerNode);
    }

    public HudManagementService(TweenManager tweenManager) {
        mTweenManager = tweenManager;
        mHudNodesMap = new HashMap<>();
        mHudNodesCreator = new HudNodesCreator(this);
        mHudNodesPositioner = new HudNodesPositioner(this);
    }

    public void createNodes(YANTextureAtlas hudAtlas) {

        //cache HUD atlas for later use
        mHudAtlas = hudAtlas;
        mHudNodesCreator.createNodes(hudAtlas);

        //at the beginning some nodes might have a different state
        setupInitialState();
    }

    private void setupInitialState() {

        //popup images are invisible
        getNode(HudNodes.YOU_WIN_IMAGE_INDEX).setOpacity(0);
        getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX).setOpacity(0);

        //timers are invisible
        getNode(HudNodes.CIRCLE_TIMER_BOTTOM_RIGHT_INDEX).setOpacity(0);
        getNode(HudNodes.CIRCLE_TIMER_TOP_RIGHT_INDEX).setOpacity(0);
        getNode(HudNodes.CIRCLE_TIMER_TOP_LEFT_INDEX).setOpacity(0);

        //popups anchor is at the middle
        getNode(HudNodes.YOU_WIN_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);
        getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);

        //v button
        getNode(HudNodes.V_BUTTON_INDEX).setOpacity(0);

        //action buttons also have zero opacity
        getNode(HudNodes.TAKE_BUTTON_INDEX).setOpacity(0);
        getNode(HudNodes.DONE_BUTTON_INDEX).setOpacity(0);
        getNode(HudNodes.GLOW_INDEX).setOpacity(0);

        //all speech bubbles and their texts are invisible at the beginning
        getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
        getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
        getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
    }

    public void setNodesSizes(YANReadOnlyVector2 sceneSize) {
        mHudNodesPositioner.setNodesSizes(sceneSize);
    }

    public void layoutNodes(YANReadOnlyVector2 sceneSize) {
        mHudNodesPositioner.layoutNodes(sceneSize);
    }

    protected <T extends YANBaseNode> void putToNodeMap(@HudNodes.HudNode int nodeIndex, T node) {
        mHudNodesMap.put(nodeIndex, node);
    }

    public <T extends YANBaseNode> T getNode(@HudNodes.HudNode int nodeIndex) {
        return (T) mHudNodesMap.get(nodeIndex);
    }

    public Collection<? extends YANBaseNode> getNodes() {
        return mHudNodesMap.values();
    }

    public void update(float deltaTimeSeconds) {

        if (mActiveTimerNode == null)
            return;

        //we assume that default state is retaliation or request for attack
        float speed = 1.0f / TOTAL_RETALIATION_TIMER_DURATION_SECONDS;

        //if it is a throw in request than speed should be different
        IActivePlayerState.ActivePlayerStateDefinition stateDefinition = ServiceLocator.locateService(GameInfo.class).getActivePlayerState().getStateDefinition();
        if (stateDefinition == IActivePlayerState.ActivePlayerStateDefinition.REQUEST_THROW_IN) {
            speed = 1.0f / TOTAL_THROW_IN_TIMER_DURATION_SECONDS;
        }

        float currentPercentage = mActiveTimerNode.getPieCirclePercentage();
        currentPercentage -= deltaTimeSeconds * speed;
        mActiveTimerNode.setPieCirclePercentage(currentPercentage);

        //handle timer expiration
        if (currentPercentage <= 0) {
            //notify timer listener that current timer is expired
            if (mTimerListener != null) {
                mTimerListener.onTimerExpired(mActiveTimerNode);
            }
            mActiveTimerNode = null;
        }
    }

    public void setTakeButtonClickListener(YANButtonNode.YanButtonNodeClickListener listener) {
        mTakeButtonClickListener = listener;
    }

    public void setFinishButtonClickListener(YANButtonNode.YanButtonNodeClickListener listener) {
        mDoneBtnClickListener = listener;
    }

    public void showFinishButton() {

        //attach a click listener at the end of animation
        YANButtonNode doneBtn = getNode(HudNodes.DONE_BUTTON_INDEX);
        doneBtn.setSortingLayer(getNode(HudNodes.TAKE_BUTTON_INDEX).getSortingLayer() + 1);
        doneBtn.setOpacity(1f);
        doneBtn.setClickListener(mDoneBtnClickListener);
    }

    public void showTakeButton() {
        YANButtonNode takeBtn = getNode(HudNodes.TAKE_BUTTON_INDEX);
        takeBtn.setSortingLayer(getNode(HudNodes.DONE_BUTTON_INDEX).getSortingLayer() + 1);
        takeBtn.setOpacity(1f);
        takeBtn.setClickListener(mTakeButtonClickListener);
    }

    public void hideTakeButton() {
        YANButtonNode takeBtn = getNode(HudNodes.TAKE_BUTTON_INDEX);
        takeBtn.setSortingLayer(takeBtn.getSortingLayer() - 1);
        takeBtn.setClickListener(null);
        takeBtn.setOpacity(0);
    }

    public void hideFinishButton() {
        YANButtonNode doneBtn = getNode(HudNodes.DONE_BUTTON_INDEX);
        doneBtn.setSortingLayer(doneBtn.getSortingLayer() - 1);
        doneBtn.setClickListener(null);
        doneBtn.setOpacity(0);
    }

    public void setTrumpSuit(String suit) {
        //change texture region
        YANTexturedNode trumpImage = getNode(HudNodes.TRUMP_IMAGE_INDEX);
        trumpImage.setTextureRegion(mHudAtlas.getTextureRegion("trump_marker_" + suit.toLowerCase() + ".png"));

        //fix aspect ratio
        float aspectRatio = trumpImage.getTextureRegion().getWidth() / trumpImage.getTextureRegion().getHeight();
        trumpImage.setSize(trumpImage.getSize().getX(), trumpImage.getSize().getX() / aspectRatio);
    }

    public void showYouWonMessage() {
        makeNodeAppearWithAnimation(getNode(HudNodes.YOU_WIN_IMAGE_INDEX), showVButtonTweenCallback);
    }

    public void showYouLooseMessage() {
        makeNodeAppearWithAnimation(getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX), showVButtonTweenCallback);
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

        final YANBaseNode avatarIconNode = getIconForPlayer(player);
        final int originalSortingLayer = avatarIconNode.getSortingLayer();
        avatarIconNode.setSortingLayer(HUD_SORTING_LAYER + 1000);

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
                return getNode(HudNodes.AVATAR_ICON_BOTTOM_RIGHT_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(HudNodes.AVATAR_ICON_TOP_RIGHT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(HudNodes.AVATAR_ICON_TOP_LEFT_INDEX);
            default:
                return null;
        }
    }

    public void showSpeechBubbleWithText(@NonNull @HudNodes.SpeechBubbleText String text, @NonNull GameInfo.Player player) {
        YANBaseNode speechBubbleNode = getSpeechBubbleForPlayer(player);
        YANTextNode textNode = getSpeechBubbleTextNodeForPlayer(player);

        //TODO : it is not an elegant way
        float percentageOfSpeechBubbleWidth = (text == HudNodes.SPEECH_BUBBLE_PASS_TEXT) ? 0.37f
                : (text == HudNodes.SPEECH_BUBBLE_THROW_IN_END_TEXT) ? 0.55f : 0.65f;
        float maxAllowedTextWidth = speechBubbleNode.getSize().getX() * percentageOfSpeechBubbleWidth;
        adjustTextScaleToFitBounds(text, maxAllowedTextWidth, textNode);

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


    private YANTextNode getSpeechBubbleTextNodeForPlayer(@NonNull GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_TEXT_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX);
        }
        throw new IllegalStateException("cannot find a node for give player");
    }

    private YANTexturedNode getBGAvatarForPlayer(GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX);
        }
        throw new IllegalStateException("cannot find a node for give player");
    }

    private YANBaseNode getSpeechBubbleForPlayer(@NonNull GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_INDEX);
        }
        return null;
    }

    /**
     * Starts the animation of timer attached to player.
     */
    public void startTimerForPlayer(@NonNull GameInfo.Player player, YANColor timerColor) {

        //stop active timer
        stopActiveTimer();

        //set new timer as active
        mActiveTimerNode = getTimerNodeForPlayer(player);
        mActiveTimerNode.setColor(timerColor.getR(), timerColor.getG(), timerColor.getB());
        mActiveTimerNode.setPieCirclePercentage(1f);
        mActiveTimerNode.setOpacity(1f);
    }

    /**
     * Stops and hides the active timer
     */
    public void stopActiveTimer() {
        if (mActiveTimerNode == null)
            return;
        //reset active timer
        mActiveTimerNode.setPieCirclePercentage(1f);
        mActiveTimerNode.setOpacity(0f);
    }

    private YANCircleNode getTimerNodeForPlayer(GameInfo.Player player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(HudNodes.CIRCLE_TIMER_BOTTOM_RIGHT_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(HudNodes.CIRCLE_TIMER_TOP_RIGHT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(HudNodes.CIRCLE_TIMER_TOP_LEFT_INDEX);
            default:
                return null;
        }
    }

    @Override
    public void clearServiceData() {
        //Does Nothing
    }

    public void setTimerListener(TimerListener timerListener) {
        mTimerListener = timerListener;
    }
}