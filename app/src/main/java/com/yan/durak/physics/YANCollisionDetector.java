package com.yan.durak.physics;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        public int compare(final YANBaseNode lhs, final YANBaseNode rhs) {
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
    public static final <T extends YANBaseNode> YANBaseNode findClosestNodeToWorldTouchPoint(final float worldTouchPointX, final float worldTouchPointY, final Collection<T> searchCollection) {

        if (searchCollection == null || searchCollection.isEmpty())
            return null;

        //clear the temporary storage
        mTouchedNodes.clear();

        //find all touched nodes and put them into collection
        for (final YANBaseNode node : searchCollection) {
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

    public static boolean areTwoNodesCollide(final YANBaseNode nodeA, final YANBaseNode nodeB) {
        return (nodeA != null && nodeB != null) && nodeA.getBoundingRectangle().contains(nodeB.getBoundingRectangle());
    }

    /**
     * Finds all the nodes that are collide with the given node in the provided search collection.
     *
     * @param testedNode       node that is tested
     * @param searchCollection collection of nodes to be tested against
     * @return new allocated array containing all the collided nodes.
     */
    public static final <T extends YANBaseNode> List<T> findAllNodesThatCollideWithGivenNode(final T testedNode, final Collection<YANBaseNode> searchCollection) {

        //we are not initializing the actual list until there are no collided nodes
        List<T> allCollidedNodes = Collections.emptyList();

        //check edge cases
        if (searchCollection == null || searchCollection.isEmpty())
            return allCollidedNodes;

        //search collided nodes
        for (final YANBaseNode searchNode : searchCollection) {
            if (areTwoNodesCollide(testedNode, searchNode)) {

                //lazy instantiating the list
                if (allCollidedNodes.isEmpty())
                    allCollidedNodes = new ArrayList<>();

                //add the node
                allCollidedNodes.add((T) searchNode);
            }
        }
        return allCollidedNodes;
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
