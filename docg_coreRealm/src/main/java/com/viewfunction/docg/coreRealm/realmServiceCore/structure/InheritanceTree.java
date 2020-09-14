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

    boolean isRoot(String nodeUID);
    String getRootUID();
    T getRoot();
    T getNode(String nodeUID);
    String getParentUID(String nodeUID);
    T getParent(String nodeUID);
    Collection<String> getChildrenUID(String nodeUID);
    Collection<T> getChildren(String nodeUID);
    Collection<T> getSiblings(String nodeUID);
    int numOfChildren(String nodeUID);
    int size();
    Iterable<T> path(String ancestorNodeUID,String OffspringNodeUID);
    Iterable<T> traversalTree(String nodeUID,TraversalStrategy traversalStrategy);

    default Iterable<T> traversalTree(String nodeUID){
        return traversalTree(nodeUID,TraversalStrategy.BreadthFirst);
    }

    default boolean isLeafNode(String nodeUID)  {
        return numOfChildren(nodeUID) == 0;
    }

    default int depth(String nodeUID) {
        if (isRoot(nodeUID)){
            return 0;
        } else{
            return 1 + depth(getParentUID(nodeUID));
        }
    }
}
