package com.yan.durak.services;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.HashMap;

import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANButtonNode;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.service.IService;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by Yan-Home on 6/9/2015.
 */
public class DialogManagerService implements IService {

    private static final int DIALOG_SORTING_LAYER = 3000;
    private YANButtonNode.YanButtonNodeClickListener mConfirmListener;
    private YANButtonNode.YanButtonNodeClickListener mDeclineListener;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            DIALOG_BG_OVERLAY_INDEX,
            DIALOG_BG_INDEX,
            DIALOG_NO_BUTTON_INDEX,
            DIALOG_YES_BUTTON_INDEX
    })
    public @interface DialogNode {
    }

    //Dialog components
    public static final int DIALOG_BG_OVERLAY_INDEX = 0;
    public static final int DIALOG_BG_INDEX = 1;
    public static final int DIALOG_YES_BUTTON_INDEX = 2;
    public static final int DIALOG_NO_BUTTON_INDEX = 3;

    private final HashMap<Integer, YANBaseNode> mDialogNodesMap;

    public DialogManagerService() {
        mDialogNodesMap = new HashMap<>();
    }


    public void layoutNodes(YANReadOnlyVector2 sceneSize) {
        float halfScreenWidth = sceneSize.getX() / 2;
        float halfScreenHeight = sceneSize.getY() / 2;

        getNode(DIALOG_BG_OVERLAY_INDEX).setPosition(0, 0);

        //position dialog bg at the middle of the screen
        getNode(DIALOG_BG_INDEX).setAnchorPoint(0.5f, 0.5f);

        //position dialog items
        float halfDialogBgWidth = getNode(DIALOG_BG_INDEX).getSize().getX() / 2;

        //yes btn
        getNode(DIALOG_BG_INDEX).setPosition(halfScreenWidth, halfScreenHeight);
        float buttonsYPosition = halfScreenHeight + (halfScreenHeight * 0.012f);
        float buttonsXOffset = halfScreenWidth * 0.2f;
        getNode(DIALOG_YES_BUTTON_INDEX).setPosition(halfScreenWidth - halfDialogBgWidth + buttonsXOffset, buttonsYPosition);

        //no btn
        getNode(DIALOG_NO_BUTTON_INDEX).setAnchorPoint(1f, 0f);
        getNode(DIALOG_NO_BUTTON_INDEX).setPosition(halfScreenWidth + halfDialogBgWidth - buttonsXOffset, buttonsYPosition);
    }

    public void setNodesSizes(YANReadOnlyVector2 sceneSize) {

        //overlay is as a size of a screen
        getNode(DIALOG_BG_OVERLAY_INDEX).setSize(sceneSize.getX(), sceneSize.getY());

        //dialog background
        YANTexturedNode dialogBG = getNode(DIALOG_BG_INDEX);
        float aspectRatio = dialogBG.getTextureRegion().getWidth() / dialogBG.getTextureRegion().getHeight();
        float newWidth = sceneSize.getX() * 0.8f;
        float newHeight = newWidth / aspectRatio;
        dialogBG.setSize(newWidth, newHeight);

        //buttons
        YANTexturedNode yesButton = getNode(DIALOG_YES_BUTTON_INDEX);
        aspectRatio = yesButton.getTextureRegion().getWidth() / yesButton.getTextureRegion().getHeight();
        newWidth = dialogBG.getSize().getX() * 0.3f;
        newHeight = newWidth / aspectRatio;
        getNode(DIALOG_YES_BUTTON_INDEX).setSize(newWidth, newHeight);
        getNode(DIALOG_NO_BUTTON_INDEX).setSize(newWidth, newHeight);
    }

    public void createNodes(YANTextureAtlas dialogsAtlas) {
        //add background overlay
        putToNodeMap(DIALOG_BG_OVERLAY_INDEX, createBgOverlay(dialogsAtlas));

        //add background of exit dialog
        putToNodeMap(DIALOG_BG_INDEX, createDialogBg(dialogsAtlas));

        //add yes no buttons
        putToNodeMap(DIALOG_YES_BUTTON_INDEX, createConfirmButton(dialogsAtlas));
        putToNodeMap(DIALOG_NO_BUTTON_INDEX, createDeclineButton(dialogsAtlas));
    }

    private YANBaseNode createDeclineButton(YANTextureAtlas dialogsAtlas) {
        YANButtonNode yanButtonNode = new YANButtonNode(dialogsAtlas.getTextureRegion("no_btn.png"),
                dialogsAtlas.getTextureRegion("no_clicked_btn.png"));
        yanButtonNode.setSortingLayer(DIALOG_SORTING_LAYER + 2);
        return yanButtonNode;
    }

    private YANBaseNode createConfirmButton(YANTextureAtlas dialogsAtlas) {
        YANButtonNode yanButtonNode = new YANButtonNode(dialogsAtlas.getTextureRegion("yes_btn.png"),
                dialogsAtlas.getTextureRegion("yes_clicked_btn.png"));
        yanButtonNode.setSortingLayer(DIALOG_SORTING_LAYER + 2);
        return yanButtonNode;
    }

    private YANBaseNode createDialogBg(YANTextureAtlas dialogsAtlas) {
        YANBaseNode bgNode = new YANTexturedNode(dialogsAtlas.getTextureRegion("leave_game.png"));
        bgNode.setSortingLayer(DIALOG_SORTING_LAYER + 1);
        return bgNode;
    }

    private YANBaseNode createBgOverlay(YANTextureAtlas dialogsAtlas) {
        YANButtonNode yanButtonNode = new YANButtonNode(dialogsAtlas.getTextureRegion("screen_shadow.png"), dialogsAtlas.getTextureRegion("screen_shadow.png"));
        yanButtonNode.setSortingLayer(DIALOG_SORTING_LAYER);
        return yanButtonNode;
    }

    public Collection<? extends YANBaseNode> getNodes() {
        return mDialogNodesMap.values();
    }

    private <T extends YANBaseNode> void putToNodeMap(@DialogNode int nodeIndex, T node) {
        mDialogNodesMap.put(nodeIndex, node);
    }

    private <T extends YANBaseNode> T getNode(@DialogNode int nodeIndex) {
        return (T) mDialogNodesMap.get(nodeIndex);
    }

    public void setExitDialogClickListeners(final YANButtonNode.YanButtonNodeClickListener confirmListener,
                                            final YANButtonNode.YanButtonNodeClickListener declineListener) {
        YANButtonNode yesBtn = getNode(DIALOG_YES_BUTTON_INDEX);
        YANButtonNode noBtn = getNode(DIALOG_NO_BUTTON_INDEX);

        mConfirmListener = confirmListener;
        mDeclineListener = declineListener;


    }


    public void showExitDialog() {

        //bring dialog to front
        getNode(DIALOG_BG_OVERLAY_INDEX).setSortingLayer(DIALOG_SORTING_LAYER);
        getNode(DIALOG_BG_INDEX).setSortingLayer(DIALOG_SORTING_LAYER + 1);
        YANButtonNode yesButton = getNode(DIALOG_YES_BUTTON_INDEX);
        YANButtonNode noButton = getNode(DIALOG_NO_BUTTON_INDEX);
        yesButton.setSortingLayer(DIALOG_SORTING_LAYER + 2);
        noButton.setSortingLayer(DIALOG_SORTING_LAYER + 2);

        //set click listeners
        yesButton.setClickListener(mConfirmListener);
        noButton.setClickListener(mDeclineListener);

        //show all nodes
        for (YANBaseNode node : mDialogNodesMap.values()) {
            node.setOpacity(1f);
        }

    }

    public void hideExitDialog() {
        //bring buttons to front
        getNode(DIALOG_YES_BUTTON_INDEX).setSortingLayer(-1);
        getNode(DIALOG_NO_BUTTON_INDEX).setSortingLayer(-1);
        getNode(DIALOG_BG_INDEX).setSortingLayer(-1);
        getNode(DIALOG_BG_OVERLAY_INDEX).setSortingLayer(-1);

        YANButtonNode yesButton = getNode(DIALOG_YES_BUTTON_INDEX);
        YANButtonNode noButton = getNode(DIALOG_NO_BUTTON_INDEX);
        yesButton.setClickListener(null);
        noButton.setClickListener(null);

        //show all nodes
        for (YANBaseNode node : mDialogNodesMap.values()) {
            node.setOpacity(0f);
        }
    }
}
