package com.viewfunction.docg.coreRealm.realmServiceCore.structure.spi.neo4j.structureImpl;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeTraverser;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.Iterator;
import java.util.List;

public class Neo4JInheritanceTreeImpl<T> implements InheritanceTree<T> {

    private TreeTraverser<T> treeTraverser;
    private T rootElement;
    //private List<T> treeElements;

    private Table<String, String, T> treeElementsTable;

    public Neo4JInheritanceTreeImpl(T rootElement,Table<String, String, T> treeElementsTable){
        this.treeElementsTable = treeElementsTable;
        this.rootElement = rootElement;
        treeTraverser = new TreeTraverser<T>() {
            @Override
            public Iterable<T> children(T t) {



                return null;
            }
        };
    }


    //Table<String, String, T> table= HashBasedTable.create();







    @Override
    public T getRoot() {
        return rootElement;
    }

    @Override
    public T getParent(T n) {
        return null;
    }

    @Override
    public Iterable<T> getChildren(T n) {
        return treeTraverser.children(n);
    }

    @Override
    public int numOfChildren(T n) {


        return treeElementsTable.row("kkkk").size();


        //treeElementsTable.rowMap()


       // Iterable<T> childrenIterable = getChildren(n);




        //FluentIterable<T> fit=treeTraverser.children(n);
        //return treeTraverser.children(n).iterator();
    }

    @Override
    public boolean isRoot(T n) {
        return false;
    }

    @Override
    public int size() {
        return treeElementsTable != null ? treeElementsTable.size() : 0;
    }

    @Override
    public Iterable path(T n1, T n2) {
        return null;
    }

    @Override
    public Iterable<T> traversalTree(TraversalStrategy traversalStrategy) {
        switch(traversalStrategy){
            case BreadthFirst:
                return treeTraverser.breadthFirstTraversal(rootElement);
            case PreOrder:
                return treeTraverser.preOrderTraversal(rootElement);
            case PostOrder:
                return treeTraverser.postOrderTraversal(rootElement);
        }
        return null;
    }
}
