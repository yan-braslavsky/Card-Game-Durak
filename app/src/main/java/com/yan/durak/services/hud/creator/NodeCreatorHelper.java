package com.yan.durak.services.hud.creator;

import android.support.annotation.NonNull;

import com.yan.durak.nodes.TaggableTextureNode;
import com.yan.durak.nodes.uniform.ChildButtonNode;
import com.yan.durak.nodes.uniform.ChildCircularNode;
import com.yan.durak.nodes.uniform.ChildTexturedNode;
import com.yan.durak.services.hud.HudManagementService;

import glengine.yan.glengine.assets.atlas.YANAtlasTextureRegion;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANCircleNode;
import glengine.yan.glengine.nodes.YANIParentNode;
import glengine.yan.glengine.nodes.YANTexturedNode;

/**
 * Created by yan.braslavsky on 8/19/2015.
 */
public class NodeCreatorHelper {

    /**
     * Creates a full avatar image with background ->Timer->AvatarIcon
     *
     * @param backgroundTextureRegion
     * @param iconTextureRegion
     * @return
     */
    public static TaggableTextureNode createAvatarBgWithTimerAndIcon(final YANAtlasTextureRegion backgroundTextureRegion,
                                                                 final YANAtlasTextureRegion iconTextureRegion ,
                                                                 final boolean isIconOpacityChangesWithParent) {
        final TaggableTextureNode avatarBG = new TaggableTextureNode(backgroundTextureRegion);
        avatarBG.setSortingLayer(HudManagementService.HUD_SORTING_LAYER);
        avatarBG.setAnchorPoint(0.5f, 0.5f);

        //we creating a circle timer
        YANCircleNode circleTimer = createChildCircleTimer();
        final YANTexturedNode avatarIcon = createChildIcon(iconTextureRegion,isIconOpacityChangesWithParent);

        //parenting
        avatarBG.addChildNode(circleTimer);
        circleTimer.addChildNode(avatarIcon);
        return avatarBG;
    }

    /**
     * Create a child button node with percentage of it's parent
     */
    public static YANButtonNode createChildButtonNode(final YANAtlasTextureRegion defaultTextureRegion,
                                                      final YANAtlasTextureRegion pressedTextureRegion,
                                                      final float parentXSizePercentage,
                                                      final float parentYSizePercentage) {
        YANButtonNode buttonNode = new ChildButtonNode(defaultTextureRegion, pressedTextureRegion) {
            @Override
            public void scaleWithParent(@NonNull YANIParentNode parentNode) {
                this.setSize(
                        parentNode.getSize().getX() * parentXSizePercentage,
                        parentNode.getSize().getY() * parentYSizePercentage);
            }
        };
        return buttonNode;
    }

    private static YANTexturedNode createChildIcon(final YANAtlasTextureRegion iconTextureRegion, final boolean isOpacityChanges) {
        final YANTexturedNode avatarIcon = new ChildTexturedNode(iconTextureRegion) {
            @Override
            public void scaleWithParent(@NonNull YANIParentNode parentNode) {
                this.setSize(parentNode.getSize().getX() * 0.85f, parentNode.getSize().getY() * 0.85f);
            }

            @Override
            public void adjustOpacityInParent(YANIParentNode parentNode) {
                if (!isOpacityChanges) {
                    //parent opacity is not influencing icon
                    return;
                }
                super.adjustOpacityInParent(parentNode);
            }
        };
        avatarIcon.setAnchorPoint(0.5f, 0.5f);
        return avatarIcon;
    }

    private static YANCircleNode createChildCircleTimer() {
        final YANCircleNode yanCircleNode = new ChildCircularNode();
        yanCircleNode.setColor(HudManagementService.TIMER_RETALIATION_COLOR.getR(),
                HudManagementService.TIMER_RETALIATION_COLOR.getG(),
                HudManagementService.TIMER_RETALIATION_COLOR.getB());
        yanCircleNode.setClockWiseDraw(false);
        yanCircleNode.setPieCirclePercentage(1f);
        yanCircleNode.setAnchorPoint(0.5f, 0.5f);
        return yanCircleNode;
    }
}
