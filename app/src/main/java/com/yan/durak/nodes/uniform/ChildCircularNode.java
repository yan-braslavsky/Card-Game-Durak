package com.yan.durak.nodes.uniform;

import android.support.annotation.NonNull;

import glengine.yan.glengine.nodes.YANCircleNode;
import glengine.yan.glengine.nodes.YANIParentNode;

/**
 * Created by yan.braslavsky on 8/17/2015.
 */
public class ChildCircularNode extends YANCircleNode {
    private final UniformlyParentedNodeComponent mComp;

    public ChildCircularNode() {
        mComp = new UniformlyParentedNodeComponent(this);
    }


    @Override
    public void onParentAttributeChanged(@NonNull final YANIParentNode parentNode,
                                         @NonNull final Attribute attribute) {
        switch (attribute) {
            case SIZE:
                scaleWithParent(parentNode);
                break;
            case SORTING_LAYER:
                adjustSortingLayerToParent(parentNode);
                break;
            case POSITION:
                adjustPositionInParent(parentNode);
                break;
            case OPACITY:
                adjustOpacityInParent(parentNode);
                break;
            case ROTATION_Y:
            case ROTATION_Z:
                adjustRotationToParent(parentNode);
                break;
        }
    }

    @Override
    public void onAttachedToParentNode(@NonNull final YANIParentNode parentNode) {
        //adjust sorting layer to be always on top of the parent
        adjustSortingLayerToParent(parentNode);

        //scale uniformly with parent
        scaleWithParent(parentNode);

        //adjust position to be in parent bounds
        adjustPositionInParent(parentNode);

        //setRotation according to parent
        adjustRotationToParent(parentNode);
    }

    private void adjustRotationToParent(final @NonNull YANIParentNode parentNode) {
        mComp.adjustRotationToParent(parentNode);
    }

    private void adjustPositionInParent(final @NonNull YANIParentNode parentNode) {
        mComp.adjustPositionInParent(parentNode);
    }

    private void adjustSortingLayerToParent(final @NonNull YANIParentNode parentNode) {
        mComp.adjustSortingLayerToParent(parentNode);
    }

    public void scaleWithParent(final @NonNull YANIParentNode parentNode) {
        mComp.scaleWithParent(parentNode);
    }

    public void adjustOpacityInParent(YANIParentNode parentNode) {
        mComp.adjustOpacityInParent(parentNode);
    }
}
