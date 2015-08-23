package com.yan.durak.services.hud;


import android.support.annotation.NonNull;

import com.yan.durak.animation.AnimationHelper;
import com.yan.durak.gamelogic.utils.math.MathHelper;
import com.yan.durak.nodes.TaggableTextureNode;
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
import aurelienribon.tweenengine.equations.Quad;
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
    private static final float SCALE_UP_SCALE_DOWN_ANIMATION_DURATION = 0.25f;

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
     * Take button will be added and removed to his parent
     * as needed.
     */
    private YANButtonNode mTakeActionBtn;

    /**
     * Done button will be added and removed to his parent
     * as needed.
     */
    private YANButtonNode mDoneActionBtn;


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
     * Cached bottom right icon node for efficiency
     */
    private YANTexturedNode cachedBottomRightIcon;


    /**
     * Text can appear differently on different screens.
     * In order to scale the node properly we need to do some calculations.
     *
     * @param text                text that will be presented by the text node
     * @param maxAllowedTextWidth maximum allowed width of the text node
     * @param textNode            text node that will be used
     */
    public static void adjustTextScaleToFitBounds(@NonNull final String text, final float maxAllowedTextWidth, @NonNull final YANTextNode textNode) {
        final float rangeDelta = 5f;
        float neededScale = 1.0f;
        textNode.calculateSizeForString(text, neededScale, _cachedVector);
        if (!MathHelper.isFloatInRange(_cachedVector.getX(), maxAllowedTextWidth - rangeDelta, maxAllowedTextWidth + rangeDelta)) {
            neededScale = maxAllowedTextWidth / _cachedVector.getX();
        }
        textNode.setTextScale(neededScale);
        textNode.setText(text);
    }


    private YANTextureAtlas mHudAtlas;
    private TweenCallback showVButtonTweenCallback = new TweenCallback() {
        @Override
        public void onEvent(final int type, final BaseTween<?> baseTween) {
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
    public void removePlayerUI(final GameInfo.PlayerLocation player) {

        //TODO : remove the nodes rather than just hiding them
        getSpeechBubbleTextNodeForPlayer(player).setOpacity(0);
        getAvatarForPlayer(player).setOpacity(0);
        //remove icon only for now , as it doesn't change opacity according to parent
        getAvatarForPlayer(player).getChildNodes().iterator().next().removeAllChildNodes();

        getTimerNodeForPlayer(player).setOpacity(0);
        getNameBgForPlayer(player).setOpacity(0);
        getNameTextNodeForPlayer(player).setOpacity(0);
    }

    private YANTexturedNode getNameBgForPlayer(final GameInfo.PlayerLocation player) {
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

    private YANTextNode getNameTextNodeForPlayer(final GameInfo.PlayerLocation player) {
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
        final SceneSizeProviderService screenSize = ServiceLocator.locateService(SceneSizeProviderService.class);
        //trump image
        final float originalYPos = getNode(HudNodes.TRUMP_IMAGE_INDEX).getPosition().getY();
        final float xPos = getNode(HudNodes.MASK_CARD_INDEX).getPosition().getX() + screenSize.getSceneWidth() * 0.05f;
        getNode(HudNodes.TRUMP_IMAGE_INDEX).setPosition(xPos, originalYPos);

    }

    public void setNameForPlayer(@NonNull final GameInfo.PlayerLocation player, @NonNull final String name) {
        final YANTexturedNode nameBgForPlayer = getNameBgForPlayer(player);
        final YANTextNode nameTextNodeForPlayer = getNameTextNodeForPlayer(player);
        adjustTextScaleToFitBounds(name,
                nameBgForPlayer.getSize().getX() * 0.6f, nameTextNodeForPlayer);
        //now we need to center vertically
        nameTextNodeForPlayer.setPosition(nameTextNodeForPlayer.getPosition().getX()
                , nameBgForPlayer.getPosition().getY() + (nameBgForPlayer.getSize().getY() - nameTextNodeForPlayer.getSize().getY()) / 2);
    }


    public interface TimerListener {
        void onTimerExpired(YANCircleNode activeTimerNode);
    }

    public HudManagementService(final TweenManager tweenManager) {
        mTweenManager = tweenManager;
        mHudNodesMap = new HashMap<>();
        mHudNodesCreator = new HudNodesCreator(this);
        mHudNodesPositioner = new HudNodesPositioner(this);
    }

    public void createNodes(final YANTextureAtlas hudAtlas) {

        //cache HUD atlas for later use
        mHudAtlas = hudAtlas;
        mHudNodesCreator.createNodes(hudAtlas);

        //create action button nodes
        mTakeActionBtn = mHudNodesCreator.createTakeButton(hudAtlas);
        mDoneActionBtn = mHudNodesCreator.createDoneButton(hudAtlas);

        //we are caching the icon nodes for later usage
        YANBaseNode timer = (YANBaseNode) getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX).getChildNodes().iterator().next();
        cachedBottomRightIcon = (YANTexturedNode) timer.getChildNodes().iterator().next();

        //at the beginning some nodes might have a different state
        setupInitialState();
    }

    private void setupInitialState() {

        //popup images are invisible
        getNode(HudNodes.YOU_WIN_IMAGE_INDEX).setOpacity(0);
        getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX).setOpacity(0);

        //timers are invisible
        getTimerNodeForPlayer(GameInfo.PlayerLocation.BOTTOM_PLAYER).setOpacity(0);
        getTimerNodeForPlayer(GameInfo.PlayerLocation.TOP_LEFT_PLAYER).setOpacity(0);
        getTimerNodeForPlayer(GameInfo.PlayerLocation.TOP_RIGHT_PLAYER).setOpacity(0);

        //popups anchor is at the middle
        getNode(HudNodes.YOU_WIN_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);
        getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);

        //v button
        getNode(HudNodes.V_BUTTON_INDEX).setOpacity(0);
        getNode(HudNodes.GLOW_INDEX).setOpacity(0);

        //all speech bubbles and their texts are invisible at the beginning
        getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_INDEX).setOpacity(0f);
        getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
        getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
        getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX).setOpacity(0f);
    }

    public void setNodesSizes(final YANReadOnlyVector2 sceneSize) {
        mHudNodesPositioner.setNodesSizes(sceneSize);
    }

    public void layoutNodes(final YANReadOnlyVector2 sceneSize) {
        mHudNodesPositioner.layoutNodes(sceneSize);
    }

    protected <T extends YANBaseNode> void putToNodeMap(@HudNodes.HudNode final int nodeIndex, final T node) {
        mHudNodesMap.put(nodeIndex, node);
    }

    public <T extends YANBaseNode> T getNode(@HudNodes.HudNode final int nodeIndex) {
        return (T) mHudNodesMap.get(nodeIndex);
    }

    public Collection<? extends YANBaseNode> getNodes() {
        return mHudNodesMap.values();
    }

    public void update(final float deltaTimeSeconds) {

        if (mActiveTimerNode == null)
            return;

        //we assume that default state is retaliation or request for attack
        float speed = 1.0f / TOTAL_RETALIATION_TIMER_DURATION_SECONDS;

        //if it is a throw in request than speed should be different
        final IActivePlayerState.ActivePlayerStateDefinition stateDefinition = ServiceLocator.locateService(GameInfo.class).getActivePlayerState().getStateDefinition();
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

    public void setTakeButtonClickListener(final YANButtonNode.YanButtonNodeClickListener listener) {
        mTakeButtonClickListener = listener;
    }

    public void setFinishButtonClickListener(final YANButtonNode.YanButtonNodeClickListener listener) {
        mDoneBtnClickListener = listener;
    }

    public void showFinishButton() {
        attachActionButton(mDoneActionBtn, mDoneBtnClickListener);
    }

    public void showTakeButton() {
        attachActionButton(mTakeActionBtn, mTakeButtonClickListener);
    }

    private void attachActionButton(YANButtonNode actionBtn, YANButtonNode.YanButtonNodeClickListener buttonClickListener) {
        //attach the button as a child of an icon
        cachedBottomRightIcon.addChildNode(actionBtn);
        actionBtn.setClickListener(buttonClickListener);

        //enable breathing animation
        AnimationHelper.createInfiniteBreathingAnimation(getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX), getTweenManager());

        //create press animation
        AnimationHelper.createButtonNodeClickAnimation(actionBtn, getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX),
                getTweenManager(), new Runnable() {
                    @Override
                    public void run() {
                        //once button is back from pressed state , we want it to continue playing breathing animation
                        AnimationHelper.createInfiniteBreathingAnimation(getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX), getTweenManager());
                    }
                });

    }

    /**
     * Hides the action button that appears at the bottom right corner icon.
     */
    public void hideActionButton() {
        //remove all children from the icon which are the action buttons
        cachedBottomRightIcon.removeAllChildNodes();

        //disable breathing animation for bottom avatar
        TaggableTextureNode taggableNode = getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX);
        getTweenManager().killTarget(taggableNode);

        //we have the original size as a tag for this avatar node
        YANVector2 originalSize = taggableNode.getTag();

        //we don't want to immediately change size , but rather animate to it
        final float duration = 0.2f;
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(taggableNode, YANTweenNodeAccessor.SIZE_X, duration).target(originalSize.getX()))
                .push(Tween.to(taggableNode, YANTweenNodeAccessor.SIZE_Y, duration).target(originalSize.getY()))
                .end()
                .start(getTweenManager());
    }

    public void setTrumpSuit(final String suit) {
        //change texture region
        final YANTexturedNode trumpImage = getNode(HudNodes.TRUMP_IMAGE_INDEX);
        trumpImage.setTextureRegion(mHudAtlas.getTextureRegion("trump_marker_" + suit.toLowerCase() + ".png"));

        //fix aspect ratio
        final float aspectRatio = trumpImage.getTextureRegion().getWidth() / trumpImage.getTextureRegion().getHeight();
        trumpImage.setSize(trumpImage.getSize().getX(), trumpImage.getSize().getX() / aspectRatio);
    }

    public void showYouWonMessage() {
        makeNodeAppearWithAnimation(getNode(HudNodes.YOU_WIN_IMAGE_INDEX), showVButtonTweenCallback);
    }

    public void showYouLooseMessage() {
        makeNodeAppearWithAnimation(getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX), showVButtonTweenCallback);
    }

    private void makeNodeAppearWithAnimation(final YANBaseNode node, final TweenCallback cbk) {
        final Timeline sequence = Timeline.createSequence()
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


    public void animateScaleUpPlayerAvatar(@NonNull final GameInfo.PlayerLocation player) {

        final YANBaseNode avatarIconNode = getAvatarForPlayer(player);
        final float originSize = avatarIconNode.getSize().getX();
        final float targetSize = originSize * 1.3f;

        getTweenManager().killTarget(avatarIconNode);
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(avatarIconNode, YANTweenNodeAccessor.SIZE_X, SCALE_UP_SCALE_DOWN_ANIMATION_DURATION).target(targetSize).ease(Quad.OUT))
                .push(Tween.to(avatarIconNode, YANTweenNodeAccessor.SIZE_Y, SCALE_UP_SCALE_DOWN_ANIMATION_DURATION).target(targetSize).ease(Quad.OUT))
                .end()
                .beginParallel()
                .push(Tween.to(avatarIconNode, YANTweenNodeAccessor.SIZE_X, SCALE_UP_SCALE_DOWN_ANIMATION_DURATION).target(originSize).ease(Quad.IN))
                .push(Tween.to(avatarIconNode, YANTweenNodeAccessor.SIZE_Y, SCALE_UP_SCALE_DOWN_ANIMATION_DURATION).target(originSize).ease(Quad.IN))
                .end()
                .start(getTweenManager());
    }

    private YANTexturedNode getAvatarForPlayer(final GameInfo.PlayerLocation player) {
        switch (player) {
            case BOTTOM_PLAYER:
                return getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX);
            case TOP_RIGHT_PLAYER:
                return getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX);
            case TOP_LEFT_PLAYER:
                return getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX);
            default:
                return null;
        }
    }

    /**
     * Sets icon avatar for given playerLocation location
     *
     * @param playerLocation
     * @param avatarResource
     * @throws NullPointerException if texture resource is not found !
     */
    public void setIconForPlayer(final GameInfo.PlayerLocation playerLocation, final String avatarResource) {
        getIconForPlayer(playerLocation).setTextureRegion(mHudAtlas.getTextureRegion(avatarResource));
    }

    private YANTexturedNode getIconForPlayer(final GameInfo.PlayerLocation playerLocation) {
        switch (playerLocation) {
            case BOTTOM_PLAYER:
                return cachedBottomRightIcon;
            case TOP_RIGHT_PLAYER:
                return (YANTexturedNode) ((YANBaseNode) getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX)
                        .getChildNodes().iterator().next()).getChildNodes().iterator().next();
            case TOP_LEFT_PLAYER:
                return (YANTexturedNode) ((YANBaseNode) getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX)
                        .getChildNodes().iterator().next()).getChildNodes().iterator().next();
            default:
                throw new UnsupportedOperationException("Not supported player : " + playerLocation);
        }
    }

    public void showSpeechBubbleWithText(@NonNull @HudNodes.SpeechBubbleText final String text, @NonNull final GameInfo.PlayerLocation player) {
        final YANBaseNode speechBubbleNode = getSpeechBubbleForPlayer(player);
        final YANTextNode textNode = getSpeechBubbleTextNodeForPlayer(player);

        //TODO : it is not an elegant way
        final float percentageOfSpeechBubbleWidth = (text == HudNodes.SPEECH_BUBBLE_PASS_TEXT) ? 0.37f
                : (text == HudNodes.SPEECH_BUBBLE_THROW_IN_END_TEXT) ? 0.55f : 0.65f;
        final float maxAllowedTextWidth = speechBubbleNode.getSize().getX() * percentageOfSpeechBubbleWidth;
        adjustTextScaleToFitBounds(text, maxAllowedTextWidth, textNode);

        //kill all previous animations
        mTweenManager.killTarget(speechBubbleNode);
        mTweenManager.killTarget(textNode);

        final Timeline sequence = Timeline.createSequence()
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


    private YANTextNode getSpeechBubbleTextNodeForPlayer(@NonNull final GameInfo.PlayerLocation player) {
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

    private YANBaseNode getSpeechBubbleForPlayer(@NonNull final GameInfo.PlayerLocation player) {
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
    public void startTimerForPlayer(@NonNull final GameInfo.PlayerLocation player, final YANColor timerColor) {

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

    private YANCircleNode getTimerNodeForPlayer(final GameInfo.PlayerLocation player) {
        switch (player) {
            case BOTTOM_PLAYER:
                //TODO : make it sane
                return (YANCircleNode) getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX).getChildNodes().iterator().next();
            case TOP_RIGHT_PLAYER:
                return (YANCircleNode) getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX).getChildNodes().iterator().next();
            case TOP_LEFT_PLAYER:
                return (YANCircleNode) getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).getChildNodes().iterator().next();
            default:
                return null;
        }
    }

    @Override
    public void clearServiceData() {
        //Does Nothing
    }

    public void setTimerListener(final TimerListener timerListener) {
        mTimerListener = timerListener;
    }

    protected TweenManager getTweenManager() {
        return mTweenManager;
    }
}