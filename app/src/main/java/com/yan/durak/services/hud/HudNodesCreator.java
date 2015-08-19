package com.yan.durak.services.hud;

import android.opengl.GLES20;

import com.yan.durak.screens.BaseGameScreen;
import com.yan.durak.services.hud.creator.NodeCreatorHelper;

import glengine.yan.glengine.assets.YANAssetManager;
import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANTextNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.renderer.YANGLRenderer;
import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.colors.YANColor;
import glengine.yan.glengine.util.loggers.YANLogger;

/**
 * Created by yan.braslavsky on 6/12/2015.
 */
public class HudNodesCreator {

    private static final YANColor SPEECH_BUBBLE_TEXT_COLOR = YANColor.createFromHexColor(0x4F3723);
    private static final YANColor PLAYER_NAMES_TEXT_COLOR = YANColor.createFromHexColor(0x4F3723);

    private final HudManagementService mHudManagementService;

    public HudNodesCreator(final HudManagementService hudManagementService) {
        mHudManagementService = hudManagementService;
    }

    public void createNodes(final YANTextureAtlas hudAtlas) {
        //add image of glade
        putToNodeMap(HudNodes.GLADE_INDEX, createGladeImage(hudAtlas));

        //add background gradient
        putToNodeMap(HudNodes.BG_GRADIENT_INDEX, createBgGradientImage(hudAtlas));

        //add image of fence
        putToNodeMap(HudNodes.FENCE_INDEX, createFenceImage(hudAtlas));

        //add image of the trump
        putToNodeMap(HudNodes.TRUMP_IMAGE_INDEX, createTrumpImage(hudAtlas));

        //create full avatars with background and icon
        putToNodeMap(HudNodes.AVATAR_BG_BOTTOM_RIGHT_INDEX, NodeCreatorHelper.createAvatarBgWithTimerAndIcon(hudAtlas.getTextureRegion("stump_bg.png"), hudAtlas.getTextureRegion("avatar_1.png")));
        putToNodeMap(HudNodes.AVATAR_BG_TOP_RIGHT_INDEX, NodeCreatorHelper.createAvatarBgWithTimerAndIcon(hudAtlas.getTextureRegion("stump_bg.png"), hudAtlas.getTextureRegion("avatar_2.png")));
        putToNodeMap(HudNodes.AVATAR_BG_TOP_LEFT_INDEX, NodeCreatorHelper.createAvatarBgWithTimerAndIcon(hudAtlas.getTextureRegion("stump_bg.png"), hudAtlas.getTextureRegion("avatar_3.png")));

        //create name backgrounds
        putToNodeMap(HudNodes.NAME_BG_TOP_RIGHT_INDEX, createNameBackground(hudAtlas));
        putToNodeMap(HudNodes.NAME_BG_TOP_LEFT_INDEX, createNameBackground(hudAtlas));

        //create name background texts
        putToNodeMap(HudNodes.NAME_BG_TOP_RIGHT_TEXT_INDEX, createPlayerNameText());
        putToNodeMap(HudNodes.NAME_BG_TOP_LEFT_TEXT_INDEX, createPlayerNameText());

        //speech bubbles
        putToNodeMap(HudNodes.BOTTOM_SPEECH_BUBBLE_INDEX, createSpeechBubble(hudAtlas));
        putToNodeMap(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_INDEX, createSpeechBubble(hudAtlas));
        putToNodeMap(HudNodes.TOP_LEFT_SPEECH_BUBBLE_INDEX, createSpeechBubble(hudAtlas));

        //speechBubble text
        putToNodeMap(HudNodes.BOTTOM_SPEECH_BUBBLE_TEXT_INDEX, createSpeechBubbleText());
        putToNodeMap(HudNodes.TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX, createSpeechBubbleText());
        putToNodeMap(HudNodes.TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX, createSpeechBubbleText());

        //create action buttons
        putToNodeMap(HudNodes.DONE_BUTTON_INDEX, createDoneButton(hudAtlas));
        putToNodeMap(HudNodes.TAKE_BUTTON_INDEX, createTakeButton(hudAtlas));

        //end game popups
        putToNodeMap(HudNodes.YOU_WIN_IMAGE_INDEX, createYouWonImage(hudAtlas));
        putToNodeMap(HudNodes.YOU_LOOSE_IMAGE_INDEX, createYouLooseImage(hudAtlas));

        //create v button for popup
        putToNodeMap(HudNodes.V_BUTTON_INDEX, createVButton(hudAtlas));

        //create v button for popup
        putToNodeMap(HudNodes.MASK_CARD_INDEX, createMaskCard(hudAtlas));
        putToNodeMap(HudNodes.GLOW_INDEX, createCardGlow(hudAtlas));
        putToNodeMap(HudNodes.ROOF_INDEX, createRoof(hudAtlas));
    }


    private YANBaseNode createNameBackground(final YANTextureAtlas hudAtlas) {
        final YANTexturedNode nameBg = new YANTexturedNode(hudAtlas.getTextureRegion("name_bg.png"));
        nameBg.setSortingLayer(HudManagementService.HUD_SORTING_LAYER);
        return nameBg;
    }

    private YANTexturedNode createBgGradientImage(final YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("bg_gradient.png")) {
            @Override
            protected void onBeforeRendering(final YANGLRenderer renderer) {
                //This gradient requires multiply blending function
                //instead of normal blending function
                GLES20.glBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            }
        };
    }

    private YANButtonNode createVButton(final YANTextureAtlas hudAtlas) {
        final YANButtonNode node = new YANButtonNode(hudAtlas.getTextureRegion("v_btn.png"), hudAtlas.getTextureRegion("v_btn_clicked.png"));
        node.setClickListener(new YANButtonNode.YanButtonNodeClickListener() {
            @Override
            public void onButtonClick() {
                YANLogger.log("v button clicked");
            }
        });
        node.setSortingLayer(HudManagementService.HUD_SORTING_LAYER - 2);
        return node;
    }

    private YANTexturedNode createMaskCard(final YANTextureAtlas hudAtlas) {
        final YANTexturedNode maskCard = new YANTexturedNode(hudAtlas.getTextureRegion("cards_back.png"));
        maskCard.setSortingLayer(HudManagementService.HUD_SORTING_LAYER);
        return maskCard;
    }

    private YANTexturedNode createYouWonImage(final YANTextureAtlas hudAtlas) {
        final YANTexturedNode popupImage = new YANTexturedNode(hudAtlas.getTextureRegion("you_won.png"));
        popupImage.setSortingLayer(HudManagementService.HUD_SORTING_LAYER - 3);
        return popupImage;
    }

    private YANTexturedNode createYouLooseImage(final YANTextureAtlas hudAtlas) {
        final YANTexturedNode popupImage = new YANTexturedNode(hudAtlas.getTextureRegion("you_lose.png"));
        popupImage.setSortingLayer(HudManagementService.HUD_SORTING_LAYER - 3);
        return popupImage;
    }

    private YANTexturedNode createGladeImage(final YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("glade.png"));
    }

    private YANTexturedNode createFenceImage(final YANTextureAtlas hudAtlas) {
        final YANTexturedNode image = new YANTexturedNode(hudAtlas.getTextureRegion("fence.png"));
        image.setSortingLayer(HudManagementService.HUD_SORTING_LAYER);
        return image;
    }

    private YANTexturedNode createTrumpImage(final YANTextureAtlas hudAtlas) {
        final YANTexturedNode trumpImage = new YANTexturedNode(hudAtlas.getTextureRegion("trump_marker_hearts.png"));
        trumpImage.setSortingLayer(-50);
        return trumpImage;
    }

    private YANButtonNode createTakeButton(final YANTextureAtlas hudAtlas) {
        return new YANButtonNode(hudAtlas.getTextureRegion("btn_take.png"), hudAtlas.getTextureRegion("btn_take.png"));
    }

    private YANButtonNode createDoneButton(final YANTextureAtlas hudAtlas) {
        return new YANButtonNode(hudAtlas.getTextureRegion("btn_done.png"), hudAtlas.getTextureRegion("btn_done.png"));
    }

    private YANBaseNode createSpeechBubbleText() {
        final YANTextNode yanTextNode = new YANTextNode(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(BaseGameScreen.SPEECH_BUBBLES_FONT_NAME), "I will Take This !".length());
        yanTextNode.setTextColor(SPEECH_BUBBLE_TEXT_COLOR.getR(), SPEECH_BUBBLE_TEXT_COLOR.getG(), SPEECH_BUBBLE_TEXT_COLOR.getB());

        //we are setting the longest text that will be used
        yanTextNode.setText(HudNodes.SPEECH_BUBBLE_TAKING_TEXT);
        return yanTextNode;
    }

    private YANBaseNode createPlayerNameText() {
        final YANTextNode textNode = new YANTextNode(ServiceLocator.locateService(YANAssetManager.class).getLoadedFont(BaseGameScreen.PLAYERS_NAMES_FONT_NAME), "*********".length());
        textNode.setTextColor(PLAYER_NAMES_TEXT_COLOR.getR(), PLAYER_NAMES_TEXT_COLOR.getG(), PLAYER_NAMES_TEXT_COLOR.getB());
        //we are setting the longest text that will be used
        textNode.setText("");
        return textNode;
    }

    private YANBaseNode createSpeechBubble(final YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("speech_bubble.png"));
    }

    private YANBaseNode createRoof(final YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("roof.png"));
    }

    private YANTexturedNode createCardGlow(final YANTextureAtlas hudAtlas) {
        return new YANTexturedNode(hudAtlas.getTextureRegion("card_glow.png"));
    }

    protected <T extends YANBaseNode> void putToNodeMap(@HudNodes.HudNode final int nodeIndex, final T node) {
        mHudManagementService.putToNodeMap(nodeIndex, node);
    }
}