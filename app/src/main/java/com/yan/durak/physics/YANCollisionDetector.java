package com.yan.durak.physics;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import glengine.yan.glengine.nodes.YANBaseNode;

/**
 * Created by ybra on 24/04/15.
 * <p/>
 * PURPOSE :
 * General purpose collision helper.
 */
public class YANCollisionDetector {


    //used to store temporary collections
    private static ArrayList<YANBaseNode> mTouchedNodes = new ArrayList<>();

    //used for sorting by sorting layer
    private static final Comparator<YANBaseNode> mSortingLayerComparator = new Comparator<YANBaseNode>() {
        @Override
        public int compare(YANBaseNode lhs, YANBaseNode rhs) {
            return lhs.getSortingLayer() - rhs.getSortingLayer();
        }
    };


    /**
     * Finds closest node to the touch point among provided nodes.
     *
     * @param worldTouchPointX world touch point x
     * @param worldTouchPointY world touch point y
     * @param searchCollection collection of nodes that will be used to find the node
     * @return the closest node for touch point or null if none is found.
     */
    public static final YANBaseNode findClosestNodeToWorldTouchPoint(float worldTouchPointX, float worldTouchPointY, Collection<YANBaseNode> searchCollection) {

        if (searchCollection == null || searchCollection.isEmpty())
            return null;

        //clear the temporary storage
        mTouchedNodes.clear();

        //find all touched nodes and put them into collection
        for (YANBaseNode node : searchCollection) {
            if (node.getBoundingRectangle().contains(worldTouchPointX, worldTouchPointY)) {
                mTouchedNodes.add(node);
            }
        }

        //maybe no nodes were found
        if (mTouchedNodes.isEmpty())
            return null;

        //sort touched cards by layer
        Collections.sort(mTouchedNodes, mSortingLayerComparator);

        //the latest node is the one that was touched
        return mTouchedNodes.get(mTouchedNodes.size() - 1);
    }

    public static boolean areTwoNodesCollide(YANBaseNode nodeA, YANBaseNode nodeB) {
        return (nodeA != null && nodeB != null) && nodeA.getBoundingRectangle().contains(nodeB.getBoundingRectangle());
    }

//    public static final YANBaseNode findClosestNodeToNormalizedTouchPoint(float normalizedTouchPointX, float normalizedTouchPointY) {
//
//        //TODO : provide a possibility to send reusable vector by reference to this method , to avoid allocations
//        //adapt to world touch point
//        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedTouchPointX, normalizedTouchPointY,
//                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());
//
//        //delegate processing
//        return findClosestNodeToWorldTouchPoint(touchToWorldPoint.getX(), touchToWorldPoint.getY());
//    }

}
