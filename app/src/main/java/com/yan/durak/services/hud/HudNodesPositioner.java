package com.yan.durak.services.hud;

import com.yan.durak.animation.AnimationHelper;

import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANTextNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by yan.braslavsky on 6/12/2015.
 */
public class HudNodesPositioner {
    private final HudManagementService mHudManagementService;

    public HudNodesPositioner(final HudManagementService hudManagementService) {
        mHudManagementService = hudManagementService;
    }

    public void setNodesSizes(final YANReadOnlyVector2 sceneSize) {

        //set avatars sizes
        final float bottomAvatarScaleFactor = 0.3f;
        final YANTexturedNode avatar = getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX);
        float aspectRatio = avatar.getTextureRegion().getWidth() / avatar.getTextureRegion().getHeight();
        float newWidth = sceneSize.getX() * bottomAvatarScaleFactor;
        float newHeight = newWidth / aspectRatio;

        //avatars
        getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX).setSize(newWidth, newHeight);

        //top avatars is smaller than bottom one
        final float topAvatarsScaleFactor = 0.8f;
        final YANTexturedNode avatarBGTopRight = getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX);
        avatarBGTopRight.setSize(newWidth * topAvatarsScaleFactor, newHeight * topAvatarsScaleFactor);
        getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).setSize(newWidth * topAvatarsScaleFactor, newHeight * topAvatarsScaleFactor);

        //roof
        final YANTexturedNode roofImage = getNode(HudNodes.ROOF_INDEX);
        aspectRatio = roofImage.getTextureRegion().getWidth() / roofImage.getTextureRegion().getHeight();
        roofImage.setSize(sceneSize.getX(), sceneSize.getX() / aspectRatio);

        //names background
        final YANTexturedNode nameBgTopLeft = getNode(HudNodes.NAME_BG_TOP_LEFT_INDEX);
        final YANTexturedNode nameBgTopRight = getNode(HudNodes.NAME_BG_TOP_RIGHT_INDEX);
        aspectRatio = nameBgTopLeft.getTextureRegion().getWidth() / nameBgTopLeft.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() / 3f;
        nameBgTopLeft.setSize(newWidth, newWidth / aspectRatio);
        nameBgTopRight.setSize(nameBgTopLeft.getSize().getX(), nameBgTopLeft.getSize().getY());


        //speech bubbles
        final YANTexturedNode bottomSpeechBubble = getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX);
        aspectRatio = bottomSpeechBubble.getTextureRegion().getWidth() / bottomSpeechBubble.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.4f;
        newHeight = newWidth / aspectRatio;

        getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);
        getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);
        getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);

        //TODO : setup animation for button nodes
//        AnimationHelper.setupButtonNodeClickAnimation(this.<YANButtonNode>getNode(HudNodes.DONE_BUTTON_INDEX), mHudManagementService.getTweenManager());
//        AnimationHelper.setupButtonNodeClickAnimation(this.<YANButtonNode>getNode(HudNodes.TAKE_BUTTON_INDEX), mHudManagementService.getTweenManager());

        //set trump image size
        final YANTexturedNode trumpImage = getNode(HudNodes.TRUMP_IMAGE_INDEX);
        aspectRatio = trumpImage.getTextureRegion().getWidth() / trumpImage.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.1f;
        newHeight = newWidth / aspectRatio;
        trumpImage.setSize(newWidth, newHeight);

        //popups
        final YANTexturedNode youWinImage = getNode(HudNodes.YOU_WIN_IMAGE_INDEX);
        aspectRatio = youWinImage.getTextureRegion().getWidth() / youWinImage.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.9f;
        newHeight = newWidth / aspectRatio;
        youWinImage.setSize(newWidth, newHeight);
        getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX).setSize(newWidth, newHeight);

        //v button
        final YANTexturedNode vButton = getNode(HudNodes.V_BUTTON_INDEX);
        aspectRatio = vButton.getTextureRegion().getWidth() / vButton.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.2f;
        newHeight = newWidth / aspectRatio;
        vButton.setSize(newWidth, newHeight);

        //mask card
        //initial size is not matter as it will be changed once game setup message will be received
        getNode(HudNodes.MASK_CARD_INDEX).setSize(1, 1);

        //fence
        final YANTexturedNode fenceImage = getNode(HudNodes.FENCE_INDEX);
        aspectRatio = fenceImage.getTextureRegion().getWidth() / fenceImage.getTextureRegion().getHeight();
        fenceImage.setSize(sceneSize.getX(), sceneSize.getX() / aspectRatio);

        //glade
        final YANTexturedNode gladeImage = getNode(HudNodes.GLADE_INDEX);
        aspectRatio = gladeImage.getTextureRegion().getWidth() / gladeImage.getTextureRegion().getHeight();
        final float gladeWidth = Math.min(sceneSize.getX(), sceneSize.getY()) * 0.9f;
        gladeImage.setSize(gladeWidth, gladeWidth / aspectRatio);

        //later glow size will be overridden
        getNode(HudNodes.GLOW_INDEX).setSize(0, 0);
    }

    public void layoutNodes(final YANReadOnlyVector2 sceneSize) {
        //layout avatars
        final float offsetX = sceneSize.getX() * 0.01f;

        //setup avatarBg for bottom player
        YANTexturedNode avatarBg = getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX);
        avatarBg.setAnchorPoint(0.5f, 0.5f);
        avatarBg.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 1);
        avatarBg.setPosition(sceneSize.getX() - offsetX - avatarBg.getSize().getX() / 2,
                sceneSize.getY() - offsetX - avatarBg.getSize().getY() / 2);

        //TODO : remove , enable only when needed
        AnimationHelper.createInfiniteBreathingAnimation(avatarBg, mHudManagementService.getTweenManager());

        //setup avatarBg for top right player
        final float topOffset = sceneSize.getY() * 0.07f;
        avatarBg = getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX);
        avatarBg.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 1);
        avatarBg.setPosition(sceneSize.getX() - (avatarBg.getSize().getX() / 2) - offsetX,
                (avatarBg.getSize().getY() / 2) + topOffset);


        //setup avatarBg for top left player
        avatarBg = getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX);
        avatarBg.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 1);
        avatarBg.setPosition((avatarBg.getSize().getX() / 2) + offsetX, (avatarBg.getSize().getY() / 2) + topOffset);


        //trump image
        getNode(HudNodes.TRUMP_IMAGE_INDEX).setPosition(
                (sceneSize.getX() - getNode(HudNodes.TRUMP_IMAGE_INDEX).getSize().getX()) / 2, sceneSize.getY() * 0.1f);

        //setup popups
        final float popupAnchorXOffset = getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getSize().getX() / 2;
        final float popupAnchorYOffset = getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getSize().getY() / 2;
        getNode(HudNodes.YOU_WIN_IMAGE_INDEX).setPosition(
                ((sceneSize.getX() - getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getSize().getX()) / 2) + popupAnchorXOffset,
                ((sceneSize.getY() - getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getSize().getY()) / 2) + popupAnchorYOffset);
        getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX).setPosition(getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getPosition().getX(), getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getPosition().getY());

        //v button
        getNode(HudNodes.V_BUTTON_INDEX).setPosition(
                getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getPosition().getX() - (getNode(HudNodes.V_BUTTON_INDEX).getSize().getX() / 2),
                getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getPosition().getY() - ((getNode(HudNodes.V_BUTTON_INDEX).getSize().getY()) * 1.25f) + popupAnchorYOffset);

        //fence
        float centerX = (sceneSize.getX() - getNode(HudNodes.FENCE_INDEX).getSize().getX()) / 2;
        float centerY = (sceneSize.getY() - getNode(HudNodes.FENCE_INDEX).getSize().getY());
        getNode(HudNodes.FENCE_INDEX).setPosition(centerX, centerY);

        //roof
        getNode(HudNodes.ROOF_INDEX).setSortingLayer(HudManagementService.HUD_SORTING_LAYER);

        //names backgrounds
        final YANBaseNode topLeftNameBg = getNode(HudNodes.NAME_BG_TOP_LEFT_INDEX);
        final YANBaseNode topRightNameBG = getNode(HudNodes.NAME_BG_TOP_RIGHT_INDEX);
        topLeftNameBg.setSortingLayer(getNode(HudNodes.ROOF_INDEX).getSortingLayer() + 1);
        topRightNameBG.setSortingLayer(topLeftNameBg.getSortingLayer());
        topRightNameBG.setRotationY(180);
        final float offsetFromTopScreenEdge = sceneSize.getY() * 0.009f;
        topLeftNameBg.setPosition(
                getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).getPosition().getX() - (getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).getSize().getX() / 2),
                getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).getPosition().getY()
                        - (getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).getSize().getY() / 2)
                        - topLeftNameBg.getSize().getY() - offsetFromTopScreenEdge);
        topRightNameBG.setPosition(((sceneSize.getX() / 3f) * 2f) - offsetFromTopScreenEdge, topLeftNameBg.getPosition().getY());

        //names texts
        final YANTextNode topLeftNameBgText = getNode(HudNodes.NAME_BG_TOP_LEFT_TEXT_INDEX);
        final YANTextNode topRightNameBGText = getNode(HudNodes.NAME_BG_TOP_RIGHT_TEXT_INDEX);
        topLeftNameBgText.setSortingLayer(topLeftNameBg.getSortingLayer() + 1);
        topRightNameBGText.setSortingLayer(topLeftNameBgText.getSortingLayer());

        topLeftNameBgText.setPosition(topLeftNameBg.getPosition().getX() + topLeftNameBg.getSize().getX() * 0.1f
                , topLeftNameBg.getPosition().getY() + topLeftNameBg.getSize().getY() * 0.01f);
        topRightNameBGText.setPosition(topRightNameBG.getPosition().getX() + topLeftNameBg.getSize().getX() * 0.2f
                , topLeftNameBgText.getPosition().getY());

        //glade
        centerX = (sceneSize.getX() - getNode(HudNodes.GLADE_INDEX).getSize().getX()) / 2;
        centerY = (sceneSize.getY() - getNode(HudNodes.GLADE_INDEX).getSize().getY()) / 2;
        getNode(HudNodes.GLADE_INDEX).setPosition(centerX, centerY);

        //background gradient
        getNode(HudNodes.BG_GRADIENT_INDEX).setSize(sceneSize.getX(), sceneSize.getY());
        getNode(HudNodes.BG_GRADIENT_INDEX).setSortingLayer(getNode(HudNodes.GLADE_INDEX).getSortingLayer() + 1);

        //speech bubbles
        //bottom speech bubble
        final YANBaseNode bottomSpeechBubble = getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX);
        bottomSpeechBubble.setAnchorPoint(1f, 1f);
        bottomSpeechBubble.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 100);
        bottomSpeechBubble.setPosition(sceneSize.getX() - (sceneSize.getX() * 0.05f),
                getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX).getPosition().getY() - (getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX).getSize().getY() / 2));

        //top right speech bubble
        final YANBaseNode topRightSpeechBubble = getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_INDEX);
        topRightSpeechBubble.setRotationZ(180);
        topRightSpeechBubble.setRotationY(180);
        topRightSpeechBubble.setAnchorPoint(1f, 0f);
        topRightSpeechBubble.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 100);
        topRightSpeechBubble.setPosition(sceneSize.getX() - (sceneSize.getX() * 0.05f),
                getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX).getPosition().getY() + (getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX).getSize().getY() / 2));

        //top left speech bubble
        final YANBaseNode topLeftSpeechBubble = getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_INDEX);
        topLeftSpeechBubble.setRotationZ(180);
        topLeftSpeechBubble.setAnchorPoint(0f, 0f);
        topLeftSpeechBubble.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 100);
        topLeftSpeechBubble.setPosition((sceneSize.getX() * 0.05f),
                getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).getPosition().getY() + (getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).getSize().getY() / 2));

        //speech bubble texts
        final YANTextNode bottomSpeechBubbleText = getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_TEXT_INDEX);
        bottomSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        bottomSpeechBubbleText.setPosition(
                //middle of speech bubble
                bottomSpeechBubble.getPosition().getX() - (bottomSpeechBubble.getSize().getX() * 0.53f),
                bottomSpeechBubble.getPosition().getY() - (bottomSpeechBubble.getSize().getY() * 0.5f) - (bottomSpeechBubbleText.getSize().getY() * 0.05f));
        bottomSpeechBubbleText.setSortingLayer(bottomSpeechBubble.getSortingLayer() + 1);

        //top right
        final YANBaseNode topRightSpeechBubbleText = getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX);
        topRightSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        topRightSpeechBubbleText.setPosition(
                topRightSpeechBubble.getPosition().getX() - (topRightSpeechBubble.getSize().getX() * 0.5f),
                topRightSpeechBubble.getPosition().getY() + (topRightSpeechBubble.getSize().getY() * 0.5f) + (topRightSpeechBubbleText.getSize().getY() * 0.05f));
        topRightSpeechBubbleText.setSortingLayer(topRightSpeechBubble.getSortingLayer() + 1);

        //top left
        final YANBaseNode topLeftSpeechBubbleText = getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX);
        topLeftSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        topLeftSpeechBubbleText.setPosition(
                topLeftSpeechBubble.getPosition().getX() + (topLeftSpeechBubble.getSize().getX() / 2),
                topLeftSpeechBubble.getPosition().getY() + (topLeftSpeechBubble.getSize().getY() * 0.5f) + (topLeftSpeechBubbleText.getSize().getY() * 0.05f));
        topLeftSpeechBubbleText.setSortingLayer(topLeftSpeechBubble.getSortingLayer() + 1);
    }

    public <T extends YANBaseNode> T getNode(@HudNodes.HudNode final int nodeIndex) {
        return mHudManagementService.getNode(nodeIndex);
    }
}
