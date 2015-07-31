package com.yan.durak.services.hud;

import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANTextNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by yan.braslavsky on 6/12/2015.
 */
public class HudNodesPositioner {
    private final HudManagementService mHudManagementService;

    public HudNodesPositioner(HudManagementService hudManagementService) {
        mHudManagementService = hudManagementService;
    }

    public void setNodesSizes(YANReadOnlyVector2 sceneSize) {

        //set avatars sizes
        float bottomAvatarScaleFactor = 0.3f;
        YANTexturedNode avatar = getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX);
        float aspectRatio = avatar.getTextureRegion().getWidth() / avatar.getTextureRegion().getHeight();
        float newWidth = sceneSize.getX() * bottomAvatarScaleFactor;
        float newHeight = newWidth / aspectRatio;

        //avatars
        getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX).setSize(newWidth, newHeight);

        //top avatars is smaller than bottom one
        float topAvatarsScaleFactor = 0.8f;
        YANTexturedNode avatarBGTopRight = getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX);
        avatarBGTopRight.setSize(newWidth * topAvatarsScaleFactor, newHeight * topAvatarsScaleFactor);
        getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX).setSize(newWidth * topAvatarsScaleFactor, newHeight * topAvatarsScaleFactor);

        //speech bubbles
        YANTexturedNode bottomSpeechBubble = getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX);
        aspectRatio = bottomSpeechBubble.getTextureRegion().getWidth() / bottomSpeechBubble.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.4f;
        newHeight = newWidth / aspectRatio;

        getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);
        getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);
        getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_INDEX).setSize(newWidth, newHeight);

        //set avatar_1 icons
        //check how much the icon smaller than background
        YANTexturedNode bottomRightAvatarIcon = getNode(HudNodes.AVATAR_ICON_BOTTOM_RIGHT_INDEX);
        float avatarIconToAvatarBgScaleFactor = bottomRightAvatarIcon.getTextureRegion().getWidth() / avatarBGTopRight.getTextureRegion().getWidth();

        float bottomIconSize = getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX).getSize().getX() * avatarIconToAvatarBgScaleFactor;
        //set bottom avatar_1 icon
        bottomRightAvatarIcon.setSize(bottomIconSize, bottomIconSize);

        //setup bottom timer size
        //calculate timer scale factor
        float timerToIconScaleFactor = avatarIconToAvatarBgScaleFactor + 0.105f;
        float bottomTimerSize = getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX).getSize().getX() * timerToIconScaleFactor;
        getNode(HudNodes.CIRCLE_TIMER_BOTTOM_RIGHT_INDEX).setSize(bottomTimerSize, bottomTimerSize);

        //set top avatar_1 icons
        float topIconsSize = avatarBGTopRight.getSize().getX() * avatarIconToAvatarBgScaleFactor;
        getNode(HudNodes.AVATAR_ICON_TOP_RIGHT_INDEX).setSize(topIconsSize, topIconsSize);
        getNode(HudNodes.AVATAR_ICON_TOP_LEFT_INDEX).setSize(topIconsSize, topIconsSize);

        //set top timers size
        timerToIconScaleFactor += 0.335f;
        float topTimerSize = topIconsSize * timerToIconScaleFactor;
        getNode(HudNodes.CIRCLE_TIMER_TOP_RIGHT_INDEX).setSize(topTimerSize, topTimerSize);
        getNode(HudNodes.CIRCLE_TIMER_TOP_LEFT_INDEX).setSize(topTimerSize, topTimerSize);

        //set action buttons size
        getNode(HudNodes.DONE_BUTTON_INDEX).setSize(bottomIconSize, bottomIconSize);
        getNode(HudNodes.TAKE_BUTTON_INDEX).setSize(bottomIconSize, bottomIconSize);

        //set trump image size
        YANTexturedNode trumpImage = getNode(HudNodes.TRUMP_IMAGE_INDEX);
        aspectRatio = trumpImage.getTextureRegion().getWidth() / trumpImage.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.1f;
        newHeight = newWidth / aspectRatio;
        trumpImage.setSize(newWidth, newHeight);

        //popups
        YANTexturedNode youWinImage = getNode(HudNodes.YOU_WIN_IMAGE_INDEX);
        aspectRatio = youWinImage.getTextureRegion().getWidth() / youWinImage.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.9f;
        newHeight = newWidth / aspectRatio;
        youWinImage.setSize(newWidth, newHeight);
        getNode(HudNodes.YOU_LOOSE_IMAGE_INDEX).setSize(newWidth, newHeight);

        //v button
        YANTexturedNode vButton = getNode(HudNodes.V_BUTTON_INDEX);
        aspectRatio = vButton.getTextureRegion().getWidth() / vButton.getTextureRegion().getHeight();
        newWidth = sceneSize.getX() * 0.2f;
        newHeight = newWidth / aspectRatio;
        vButton.setSize(newWidth, newHeight);

        //mask card
        //initial size is not matter as it will be changed once game setup message will be received
        getNode(HudNodes.MASK_CARD_INDEX).setSize(1, 1);

        //fence
        YANTexturedNode fenceImage = getNode(HudNodes.FENCE_INDEX);
        aspectRatio = fenceImage.getTextureRegion().getWidth() / fenceImage.getTextureRegion().getHeight();
        fenceImage.setSize(sceneSize.getX(), sceneSize.getX() / aspectRatio);

        //rood
        YANTexturedNode roofImage = getNode(HudNodes.ROOF_INDEX);
        aspectRatio = roofImage.getTextureRegion().getWidth() / roofImage.getTextureRegion().getHeight();
        roofImage.setSize(sceneSize.getX(), sceneSize.getX() / aspectRatio);

        //glade
        YANTexturedNode gladeImage = getNode(HudNodes.GLADE_INDEX);
        aspectRatio = gladeImage.getTextureRegion().getWidth() / gladeImage.getTextureRegion().getHeight();
        float gladeWidth = Math.min(sceneSize.getX(), sceneSize.getY()) * 0.9f;
        gladeImage.setSize(gladeWidth, gladeWidth / aspectRatio);

        //later glow size will be overridden
        getNode(HudNodes.GLOW_INDEX).setSize(0, 0);
    }

    public void layoutNodes(YANReadOnlyVector2 sceneSize) {
        //layout avatars
        float offsetX = sceneSize.getX() * 0.01f;

        //setup avatarBg for bottom player
        YANTexturedNode avatarBg = getNode(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX);
        avatarBg.setAnchorPoint(1f, 1f);
        avatarBg.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 1);
        avatarBg.setPosition(sceneSize.getX() - offsetX, sceneSize.getY() - offsetX);

        //setup bottom timer
        YANBaseNode bottomTimer = getNode(HudNodes.CIRCLE_TIMER_BOTTOM_RIGHT_INDEX);
        float offsetSize = (avatarBg.getSize().getX() - bottomTimer.getSize().getX()) / 2;
        bottomTimer.setSortingLayer(avatarBg.getSortingLayer() + 1);
        bottomTimer.setAnchorPoint(1f, 1f);
        bottomTimer.setPosition(avatarBg.getPosition().getX() - offsetSize, avatarBg.getPosition().getY() - offsetSize);

        //setup bottom avatar_1 icon
        YANTexturedNode bottomAvatarIcon = getNode(HudNodes.AVATAR_ICON_BOTTOM_RIGHT_INDEX);
        float bottomAvatarIconHalfSize = bottomAvatarIcon.getSize().getX() / 2;
        bottomAvatarIcon.setAnchorPoint(0.5f, 0.5f);
        bottomAvatarIcon.setSortingLayer(bottomTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - bottomAvatarIcon.getSize().getX()) / 2;
        bottomAvatarIcon.setPosition(avatarBg.getPosition().getX() - offsetSize - bottomAvatarIconHalfSize, avatarBg.getPosition().getY() - offsetSize - bottomAvatarIconHalfSize);

        //take action is at the same place as bottom avatarBg
        YANTexturedNode takeButton = getNode(HudNodes.TAKE_BUTTON_INDEX);
        takeButton.setAnchorPoint(0.5f, 0.5f);
        takeButton.setSortingLayer(bottomAvatarIcon.getSortingLayer() + 1);
        takeButton.setPosition(bottomAvatarIcon.getPosition().getX(), bottomAvatarIcon.getPosition().getY());

        //finish action is at the same place as bottom avatarBg
        YANTexturedNode doneButton = getNode(HudNodes.DONE_BUTTON_INDEX);
        doneButton.setSortingLayer(bottomAvatarIcon.getSortingLayer() + 1);
        doneButton.setAnchorPoint(0.5f, 0.5f);
        doneButton.setPosition(takeButton.getPosition().getX(), takeButton.getPosition().getY());

        //setup avatarBg for top right player
        float topOffset = sceneSize.getY() * 0.07f;
        avatarBg = getNode(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX);
        avatarBg.setAnchorPoint(1f, 0f);
        avatarBg.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 1);
        avatarBg.setPosition(sceneSize.getX() - offsetX, topOffset);

        //setup top right timer
        YANBaseNode topRightTimer = getNode(HudNodes.CIRCLE_TIMER_TOP_RIGHT_INDEX);
        offsetSize = (avatarBg.getSize().getX() - topRightTimer.getSize().getX()) / 2;
        topRightTimer.setSortingLayer(avatarBg.getSortingLayer() + 1);
        topRightTimer.setAnchorPoint(1f, 0f);
        topRightTimer.setPosition(avatarBg.getPosition().getX() - offsetSize, avatarBg.getPosition().getY() + offsetSize);

        //setup icon for top right player
        YANTexturedNode topRightAvatarIcon = getNode(HudNodes.AVATAR_ICON_TOP_RIGHT_INDEX);
        float topRightAvatarHalfSize = topRightAvatarIcon.getSize().getX() / 2;
        topRightAvatarIcon.setAnchorPoint(0.5f, 0.5f);
        topRightAvatarIcon.setSortingLayer(topRightTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - topRightAvatarIcon.getSize().getX()) / 2;
        topRightAvatarIcon.setPosition(avatarBg.getPosition().getX() - offsetSize - topRightAvatarHalfSize, avatarBg.getPosition().getY() + offsetSize + topRightAvatarHalfSize);

        //setup avatarBg for top left player
        avatarBg = getNode(HudNodes.AVATAR_BG_TOP_LEFT_INDEX);
        avatarBg.setAnchorPoint(0f, 0f);
        avatarBg.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 1);
        avatarBg.setPosition(offsetX, topOffset);

        //setup top left timer
        YANBaseNode topLeftTimer = getNode(HudNodes.CIRCLE_TIMER_TOP_LEFT_INDEX);
        offsetSize = (avatarBg.getSize().getX() - topLeftTimer.getSize().getX()) / 2;
        topLeftTimer.setSortingLayer(avatarBg.getSortingLayer() + 1);
        topLeftTimer.setAnchorPoint(0f, 0f);
        topLeftTimer.setPosition(avatarBg.getPosition().getX() + offsetSize, avatarBg.getPosition().getY() + offsetSize);

        //setup icon for top left player
        YANTexturedNode topLeftAvatarIcon = getNode(HudNodes.AVATAR_ICON_TOP_LEFT_INDEX);
        float topLeftAvatarHalfSize = topLeftAvatarIcon.getSize().getX() / 2;
        topLeftAvatarIcon.setAnchorPoint(0.5f, 0.5f);
        topLeftAvatarIcon.setSortingLayer(topLeftTimer.getSortingLayer() + 1);
        offsetSize = (avatarBg.getSize().getX() - topLeftAvatarIcon.getSize().getX()) / 2;
        topLeftAvatarIcon.setPosition(avatarBg.getPosition().getX() + offsetSize + topLeftAvatarHalfSize, avatarBg.getPosition().getY() + offsetSize + topLeftAvatarHalfSize);

        //trump image
        getNode(HudNodes.TRUMP_IMAGE_INDEX).setPosition(
                (sceneSize.getX() - getNode(HudNodes.TRUMP_IMAGE_INDEX).getSize().getX()) / 2, sceneSize.getY() * 0.1f);

        //setup popups
        float popupAnchorXOffset = getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getSize().getX() / 2;
        float popupAnchorYOffset = getNode(HudNodes.YOU_WIN_IMAGE_INDEX).getSize().getY() / 2;
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
        getNode(HudNodes.ROOF_INDEX).setSortingLayer(1000);

        //glade
        centerX = (sceneSize.getX() - getNode(HudNodes.GLADE_INDEX).getSize().getX()) / 2;
        centerY = (sceneSize.getY() - getNode(HudNodes.GLADE_INDEX).getSize().getY()) / 2;
        getNode(HudNodes.GLADE_INDEX).setPosition(centerX, centerY);


        //speech bubbles
        //bottom speech bubble
        YANBaseNode bottomSpeechBubble = getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX);
        bottomSpeechBubble.setAnchorPoint(1f, 1f);
        bottomSpeechBubble.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 100);
        bottomSpeechBubble.setPosition(sceneSize.getX() - (sceneSize.getX() * 0.05f),
                bottomAvatarIcon.getPosition().getY() - (bottomAvatarIcon.getSize().getY() / 2));

        //top right speech bubble
        YANBaseNode topRightSpeechBubble = getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_INDEX);
        topRightSpeechBubble.setRotationZ(180);
        topRightSpeechBubble.setRotationY(180);
        topRightSpeechBubble.setAnchorPoint(1f, 0f);
        topRightSpeechBubble.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 100);
        topRightSpeechBubble.setPosition(sceneSize.getX() - (sceneSize.getX() * 0.05f),
                topRightAvatarIcon.getPosition().getY() + (topRightAvatarIcon.getSize().getY() / 2));

        //top left speech bubble
        YANBaseNode topLeftSpeechBubble = getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_INDEX);
        topLeftSpeechBubble.setRotationZ(180);
        topLeftSpeechBubble.setAnchorPoint(0f, 0f);
        topLeftSpeechBubble.setSortingLayer(HudManagementService.HUD_SORTING_LAYER + 100);
        topLeftSpeechBubble.setPosition((sceneSize.getX() * 0.05f),
                topLeftAvatarIcon.getPosition().getY() + (topLeftAvatarIcon.getSize().getY() / 2));

        //speech bubble texts
        YANTextNode bottomSpeechBubbleText = getNode(HudNodes.BOTTOM_SPEECH_BUBBLE_TEXT_INDEX);
        bottomSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        bottomSpeechBubbleText.setPosition(
                //middle of speech bubble
                bottomSpeechBubble.getPosition().getX() - (bottomSpeechBubble.getSize().getX() * 0.53f),
                bottomSpeechBubble.getPosition().getY() - (bottomSpeechBubble.getSize().getY() * 0.5f) - (bottomSpeechBubbleText.getSize().getY() * 0.05f));
        bottomSpeechBubbleText.setSortingLayer(bottomSpeechBubble.getSortingLayer() + 1);

        //top right
        YANBaseNode topRightSpeechBubbleText = getNode(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX);
        topRightSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        topRightSpeechBubbleText.setPosition(
                topRightSpeechBubble.getPosition().getX() - (topRightSpeechBubble.getSize().getX() * 0.5f),
                topRightSpeechBubble.getPosition().getY() + (topRightSpeechBubble.getSize().getY() * 0.5f) + (topRightSpeechBubbleText.getSize().getY() * 0.05f));
        topRightSpeechBubbleText.setSortingLayer(topRightSpeechBubble.getSortingLayer() + 1);

        //top left
        YANBaseNode topLeftSpeechBubbleText = getNode(HudNodes.TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX);
        topLeftSpeechBubbleText.setAnchorPoint(0.5f, 0.5f);
        topLeftSpeechBubbleText.setPosition(
                topLeftSpeechBubble.getPosition().getX() + (topLeftSpeechBubble.getSize().getX() / 2),
                topLeftSpeechBubble.getPosition().getY() + (topLeftSpeechBubble.getSize().getY() * 0.5f) + (topLeftSpeechBubbleText.getSize().getY() * 0.05f));
        topLeftSpeechBubbleText.setSortingLayer(topLeftSpeechBubble.getSortingLayer() + 1);
    }

    public <T extends YANBaseNode> T getNode(@HudNodes.HudNode int nodeIndex) {
        return mHudManagementService.getNode(nodeIndex);
    }
}
