package com.yan.durak.nodes.uniform;

import android.support.annotation.NonNull;

import glengine.yan.glengine.nodes.YANBaseNode;
import glengine.yan.glengine.nodes.YANIParentNode;

/**
 * Created by yan.braslavsky on 8/17/2015.
 */
public class UniformlyParentedNodeComponent {

    private final YANBaseNode mComponentOwnerNode;

    protected UniformlyParentedNodeComponent(YANBaseNode componentOwnerNode) {
        this.mComponentOwnerNode = componentOwnerNode;
        mComponentOwnerNode.setAnchorPoint(0.5f,0.5f);
    }



    protected void adjustOpacityInParent(YANIParentNode parentNode) {
        mComponentOwnerNode.setOpacity(parentNode.getOpacity());
    }


    protected void adjustRotationToParent(final @NonNull YANIParentNode parentNode) {
        mComponentOwnerNode.setRotationY(parentNode.getRotationY());
        mComponentOwnerNode.setRotationZ(parentNode.getRotationZ());
    }

    protected void adjustPositionInParent(final @NonNull YANIParentNode parentNode) {
        //both nodes have anchor to the center , so their position should be the same
        mComponentOwnerNode.setPosition(parentNode.getPosition().getX(), parentNode.getPosition().getY());
    }

    protected void adjustSortingLayerToParent(final @NonNull YANIParentNode parentNode) {
        mComponentOwnerNode.setSortingLayer(parentNode.getSortingLayer() + 1);
    }

    protected void scaleWithParent(final @NonNull YANIParentNode parentNode) {
        mComponentOwnerNode.setSize(parentNode.getSize().getX() * 0.75f, parentNode.getSize().getY() * 0.8f);
    }


}
