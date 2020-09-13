package com.viewfunction.docg.coreRealm.realmServiceCore.structure;

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

    T getRoot();
    T getParent(T n);
    Iterable<T> getChildren(T n);
    int numOfChildren(T n);
    boolean isRoot(T n);
    int size();
    Iterable<T> path(T n1,T n2);

    Iterable<T> traversalTree(TraversalStrategy traversalStrategy);

    default Iterable<T> traversalTree(){
        return traversalTree(TraversalStrategy.BreadthFirst);
    }

    default int depth(T n) {
        if (isRoot(n)){
            return 0;
        } else{
            return 1 + depth(getParent(n));
        }
    }

    default boolean isInternal(T n) {
        return numOfChildren(n) > 0;
    }

    default boolean isExternal(T n)  {
        return numOfChildren(n) == 0;
    }
}
