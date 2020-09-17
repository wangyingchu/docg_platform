package com.viewfunction.docg.coreRealm.realmServiceCore.structure;

import java.util.Collection;

public interface InheritanceTree <T>{

    public enum TraversalStrategy{
        BreadthFirst,PreOrder,PostOrder
    }

    /*
    Two nodes that are children of the same parent are known as siblings.
    A node is external if it has no children. Such nodes are also known as leaves or leaf nodes.
    A node is internal if it has one or more children.
    An edge of a tree is a pair of nodes (u, v) such that u is the parent of v or vice versa.
    A path in a tree is a sequence of nodes such that any two consecutive nodes in the sequence form an edge.
    */

    boolean isRoot(String nodeID);
    String getRootID();
    T getRoot();
    T getNode(String nodeID);
    String getParentID(String nodeID);
    T getParent(String nodeID);
    Collection<String> getChildrenID(String nodeID);
    Collection<T> getChildren(String nodeID);
    Collection<T> getSiblings(String nodeID);
    int numOfChildren(String nodeID);
    int size();
    Iterable<T> path(String ancestorNodeID,String OffspringNodeID);
    Iterable<T> traversalTree(String nodeID,TraversalStrategy traversalStrategy);

    default Iterable<T> traversalTree(String nodeID){
        return traversalTree(nodeID,TraversalStrategy.BreadthFirst);
    }

    default boolean isLeafNode(String nodeID)  {
        return numOfChildren(nodeID) == 0;
    }

    default int depth(String nodeID) {
        if (isRoot(nodeID)){
            return 0;
        } else{
            return 1 + depth(getParentID(nodeID));
        }
    }
}
