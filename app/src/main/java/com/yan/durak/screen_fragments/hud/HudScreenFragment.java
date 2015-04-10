package com.yan.durak.screen_fragments.hud;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.nodes.YANTexturedScissorNode;
import glengine.yan.glengine.tween.YANTweenNodeAccessor;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by Yan-Home on 1/25/2015.
 */
public class HudScreenFragment implements IHudScreenFragment {

    /**
     * By default hud will be placed on hud sorting layer and above
     */
    private static final int HUD_SORTING_LAYER = 50;

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
    private Map<Integer, YANTexturedNode> mHudNodesMap;
    private float mScissoringCockVisibleStartY;

    private INodeAttachmentChangeListener mNodeVisibilityChangeListener;
    private YANTextureAtlas mHudAtlas;


    public HudScreenFragment(TweenManager tweenManager) {
        mTweenManager = tweenManager;
        mHudNodesMap = new HashMap<>();
    }

    @Override
    public void createNodes(YANTextureAtlas hudAtlas) {

        //cache HUD atlas for later use
        mHudAtlas = hudAtlas;

        //add image of the trump
        putToNodeMap(TRUMP_IMAGE_INDEX, createTrumpImage(hudAtlas));

        //create avatars
        putToNodeMap(AVATAR_BOTTOM_RIGHT_INDEX, createAvatar(hudAtlas));
        putToNodeMap(AVATAR_TOP_RIGHT_INDEX, createAvatar(hudAtlas));
        putToNodeMap(AVATAR_TOP_LEFT_INDEX, createAvatar(hudAtlas));

        //create cocks
        putToNodeMap(COCK_BOTTOM_RIGHT_INDEX, createCock(hudAtlas));
        putToNodeMap(COCK_TOP_RIGHT_INDEX, createCock(hudAtlas));
        putToNodeMap(COCK_TOP_LEFT_INDEX, createCock(hudAtlas));
        putToNodeMap(COCK_SCISSOR_INDEX, createScissorCock(hudAtlas));

        //create action buttons
        putToNodeMap(BITO_BUTTON_INDEX, createBitoButton(hudAtlas));
        putToNodeMap(TAKE_BUTTON_INDEX, createTakeButton(hudAtlas));

        //end game popups
        putToNodeMap(YOU_WIN_IMAGE_INDEX, createYouWonImage(hudAtlas));
        putToNodeMap(YOU_LOOSE_IMAGE_INDEX, createYouLooseImage(hudAtlas));

        //at the beginning some nodes might have a different state
        setupInitialState();

    }

    private void setupInitialState() {

        //top left cock is looking the other way
        getNode(COCK_TOP_LEFT_INDEX).setRotationY(180);

        //popup images are invisible
        getNode(YOU_WIN_IMAGE_INDEX).setOpacity(0);
        getNode(YOU_LOOSE_IMAGE_INDEX).setOpacity(0);

        //popups anchor is at the middle
        getNode(YOU_WIN_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);
        getNode(YOU_LOOSE_IMAGE_INDEX).setAnchorPoint(0.5f, 0.5f);

    }

    private YANTexturedNode createYouWonImage(YANTextureAtlas hudAtlas) {
        YANTexturedNode popupImage = new YANTexturedNode(hudAtlas.getTextureRegion("you_won.png"));
        popupImage.setSortingLayer(HUD_SORTING_LAYER + 100);
        return popupImage;
    }

    private YANTexturedNode createYouLooseImage(YANTextureAtlas hudAtlas) {
        YANTexturedNode popupImage = new YANTexturedNode(hudAtlas.getTextureRegion("you_lose.png"));
        popupImage.setSortingLayer(HUD_SORTING_LAYER + 100);
        return popupImage;
    }

    private YANTexturedNode createTrumpImage(YANTextureAtlas hudAtlas) {
        YANTexturedNode trumpImage = new YANTexturedNode(hudAtlas.getTextureRegion("trump_marker_hearts.png"));
        trumpImage.setSortingLayer(-50);
        return trumpImage;
    }

    private YANButtonNode createTakeButton(YANTextureAtlas hudAtlas) {
        return new YANButtonNode(hudAtlas.getTextureRegion("take.png"), hudAtlas.getTextureRegion("take.png"));
    }

    private YANButtonNode createBitoButton(YANTextureAtlas hudAtlas) {
        return new YANButtonNode(hudAtlas.getTextureRegion("bito.png"), hudAtlas.getTextureRegion("bito.png"));
    }

    private <T extends YANTexturedNode> void putToNodeMap(@HudNode int nodeIndex, T node) {
        mHudNodesMap.put(nodeIndex, node);
    }

    private <T extends YANTexturedNode> T getNode(@HudNode int nodeIndex) {
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
        YANTexturedNode avatar = new YANTexturedNode(hudAtlas.getTextureRegion("stump.png"));
        avatar.setSortingLayer(HUD_SORTING_LAYER);
        return avatar;
    }

    @Override
    public void setNodesSizes(YANReadOnlyVector2 sceneSize) {
        //set avatars sizes
        YANTexturedNode avatar = getNode(AVATAR_BOTTOM_RIGHT_INDEX);
        float aspectRatio = avatar.getTextureRegion().getWidth() / avatar.getTextureRegion().getHeight();
        float newWidth = sceneSize.getX() * 0.2f;
        float newHeight = newWidth / aspectRatio;

        //set action buttons size
        getNode(BITO_BUTTON_INDEX).setSize(newWidth, newHeight);
        getNode(TAKE_BUTTON_INDEX).setSize(newWidth, newHeight);

        //avatars
        getNode(AVATAR_BOTTOM_RIGHT_INDEX).setSize(newWidth, newHeight);
        getNode(AVATAR_TOP_RIGHT_INDEX).setSize(newWidth, newHeight);
        getNode(AVATAR_TOP_LEFT_INDEX).setSize(newWidth, newHeight);

        //set cock sizes
        YANTexturedNode cock = getNode(COCK_BOTTOM_RIGHT_INDEX);
        aspectRatio = cock.getTextureRegion().getWidth() / cock.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.1f;
        newHeight = newWidth / aspectRatio;

        //all cocks
        getNode(COCK_BOTTOM_RIGHT_INDEX).setSize(newWidth, newHeight);
        getNode(COCK_TOP_RIGHT_INDEX).setSize(newWidth, newHeight);
        getNode(COCK_TOP_LEFT_INDEX).setSize(newWidth, newHeight);
        getNode(COCK_SCISSOR_INDEX).setSize(newWidth, newHeight);

        //set trump image size
        YANTexturedNode trumpImage = getNode(TRUMP_IMAGE_INDEX);
        aspectRatio = trumpImage.getTextureRegion().getWidth() / trumpImage.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.1f;
        newHeight = newWidth / aspectRatio;
        trumpImage.setSize(newWidth, newHeight);

        //popups
        aspectRatio = getNode(YOU_WIN_IMAGE_INDEX).getTextureRegion().getWidth() / getNode(YOU_WIN_IMAGE_INDEX).getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.9f;
        newHeight = newWidth / aspectRatio;
        getNode(YOU_WIN_IMAGE_INDEX).setSize(newWidth, newHeight);
        getNode(YOU_LOOSE_IMAGE_INDEX).setSize(newWidth, newHeight);
    }

    @Override
    public Collection<? extends YANTexturedNode> getFragmentNodes() {
        return mHudNodesMap.values();
    }

    @Override
    public void layoutNodes(YANReadOnlyVector2 sceneSize) {
        //layout avatars
        float offsetX = sceneSize.getX() * 0.01f;

        //setup avatar for bottom player
        YANTexturedNode avatar = getNode(AVATAR_BOTTOM_RIGHT_INDEX);
        avatar.setAnchorPoint(1f, 1f);
        avatar.setSortingLayer(HUD_SORTING_LAYER + 1);
        avatar.setPosition(sceneSize.getX() - offsetX, sceneSize.getY() - offsetX);
        getNode(COCK_BOTTOM_RIGHT_INDEX).setSortingLayer(avatar.getSortingLayer());
        getNode(COCK_BOTTOM_RIGHT_INDEX).setPosition(avatar.getPosition().getX() - avatar.getSize().getX() / 2 - getNode(COCK_BOTTOM_RIGHT_INDEX).getSize().getX() / 2,
                avatar.getPosition().getY() - avatar.getSize().getY() - getNode(COCK_BOTTOM_RIGHT_INDEX).getSize().getY());

        //take action is at the same place as bottom avatar
        getNode(TAKE_BUTTON_INDEX).setSortingLayer(avatar.getSortingLayer() + 1);
        getNode(TAKE_BUTTON_INDEX).setPosition(avatar.getPosition().getX() - avatar.getSize().getX(), avatar.getPosition().getY() - avatar.getSize().getY());

        //bito action is at the same place as bottom avatar
        getNode(BITO_BUTTON_INDEX).setSortingLayer(avatar.getSortingLayer() + 1);
        getNode(BITO_BUTTON_INDEX).setPosition(avatar.getPosition().getX() - avatar.getSize().getX(), avatar.getPosition().getY() - avatar.getSize().getY());


        //setup avatar for top right player
        float topOffset = sceneSize.getY() * 0.07f;
        avatar = getNode(AVATAR_TOP_RIGHT_INDEX);
        avatar.setAnchorPoint(1f, 0f);
        avatar.setSortingLayer(HUD_SORTING_LAYER + 1);
        avatar.setPosition(sceneSize.getX() - offsetX, topOffset);
        getNode(COCK_TOP_RIGHT_INDEX).setSortingLayer(avatar.getSortingLayer());
        getNode(COCK_TOP_RIGHT_INDEX).setPosition(avatar.getPosition().getX() - avatar.getSize().getX() / 2 - getNode(COCK_TOP_RIGHT_INDEX).getSize().getX() / 2,
                avatar.getPosition().getY() - getNode(COCK_TOP_RIGHT_INDEX).getSize().getY());


        //setup avatar for top left player
        avatar = getNode(AVATAR_TOP_LEFT_INDEX);
        avatar.setAnchorPoint(0f, 0f);
        avatar.setSortingLayer(HUD_SORTING_LAYER + 1);
        avatar.setPosition(offsetX, topOffset);
        getNode(COCK_TOP_LEFT_INDEX).setSortingLayer(avatar.getSortingLayer());
        getNode(COCK_TOP_LEFT_INDEX).setPosition(avatar.getPosition().getX() + getNode(COCK_TOP_LEFT_INDEX).getSize().getX() / 2, avatar.getPosition().getY() - getNode(COCK_TOP_LEFT_INDEX).getSize().getY());

        //filled in cock by default is out of the screen
        getNode(COCK_SCISSOR_INDEX).setPosition(-sceneSize.getX(), 0);

        //trump image
        getNode(TRUMP_IMAGE_INDEX).setPosition((sceneSize.getX() - getNode(TRUMP_IMAGE_INDEX).getSize().getX()) / 2, sceneSize.getY() * 0.06f);

        //setup popups
        getNode(YOU_WIN_IMAGE_INDEX).setPosition(
                ((sceneSize.getX() - getNode(YOU_WIN_IMAGE_INDEX).getSize().getX()) / 2) + getNode(YOU_WIN_IMAGE_INDEX).getSize().getX() / 2,
                ((sceneSize.getY() - getNode(YOU_WIN_IMAGE_INDEX).getSize().getY()) / 2) + getNode(YOU_WIN_IMAGE_INDEX).getSize().getY() / 2);
        getNode(YOU_LOOSE_IMAGE_INDEX).setPosition(getNode(YOU_WIN_IMAGE_INDEX).getPosition().getX(), getNode(YOU_WIN_IMAGE_INDEX).getPosition().getY());
    }

    @Override
    public void update(float deltaTimeSeconds) {
        //animate scissoring cock
        YANTexturedScissorNode scissorCock = getNode(COCK_SCISSOR_INDEX);
        scissorCock.setVisibleArea(0, mScissoringCockVisibleStartY, 1, 1);
        mScissoringCockVisibleStartY += 0.001;
        if (mScissoringCockVisibleStartY > 1.0)
            mScissoringCockVisibleStartY = 0.0f;
    }

    @Override
    public void setTakeButtonClickListener(YANButtonNode.YanButtonNodeClickListener listener) {
        ((YANButtonNode) getNode(TAKE_BUTTON_INDEX)).setClickListener(listener);
    }

    @Override
    public void setBitoButtonClickListener(YANButtonNode.YanButtonNodeClickListener listener) {
        ((YANButtonNode) getNode(BITO_BUTTON_INDEX)).setClickListener(listener);
    }

    @Override
    public void setFinishButtonAttachedToScreen(boolean isVisible) {
        mNodeVisibilityChangeListener.onNodeVisibilityChanged(getNode(BITO_BUTTON_INDEX), isVisible);
    }

    @Override
    public void setTakeButtonAttachedToScreen(boolean isVisible) {
        mNodeVisibilityChangeListener.onNodeVisibilityChanged(getNode(TAKE_BUTTON_INDEX), isVisible);
    }

    @Override
    public void resetCockAnimation(@HudNode int index) {

        float rotationAngle = 0;
        YANReadOnlyVector2 newCockPosition = getNode(index).getPosition();
        getNode(COCK_SCISSOR_INDEX).setPosition(newCockPosition.getX(), newCockPosition.getY());
        if (index == COCK_TOP_LEFT_INDEX) {
            rotationAngle = 180;
        }

        getNode(COCK_SCISSOR_INDEX).setRotationY(rotationAngle);

        //start animating the cock down
        mScissoringCockVisibleStartY = 1;
    }

    @Override
    public void setNodeNodeAttachmentChangeListener(INodeAttachmentChangeListener nodeVisibilityChangeListener) {
        mNodeVisibilityChangeListener = nodeVisibilityChangeListener;
    }

    @Override
    public void setTrumpSuit(String suit) {
        //change texture region
        YANTexturedNode trumpImage = getNode(TRUMP_IMAGE_INDEX);
        trumpImage.setTextureRegion(mHudAtlas.getTextureRegion("trump_marker_" + suit.toLowerCase() + ".png"));
    }

    @Override
    public void showYouWonMessage() {
        makeNodeAppearWithAnimation(getNode(YOU_WIN_IMAGE_INDEX));
    }

    @Override
    public void showYouLooseMessage() {
        makeNodeAppearWithAnimation(getNode(YOU_LOOSE_IMAGE_INDEX));
    }

    private void makeNodeAppearWithAnimation(YANTexturedNode node) {
        Timeline sequence = Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_X, POPUP_ANIMATION_DURATION).target(node.getSize().getX()))
                .push(Tween.to(node, YANTweenNodeAccessor.SIZE_Y, POPUP_ANIMATION_DURATION).target(node.getSize().getY()))
                .push(Tween.to(node, YANTweenNodeAccessor.OPACITY, POPUP_ANIMATION_DURATION).target(1f))
                .push(Tween.to(node, YANTweenNodeAccessor.ROTATION_Z_CW, POPUP_ANIMATION_DURATION).target(360));

        //make the popup barley visible
        node.setOpacity(0f);
        node.setSize(0.1f, 0.1f);

        //animate
        sequence.start(mTweenManager);
    }
}