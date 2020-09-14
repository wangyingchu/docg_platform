package com.viewfunction.docg.coreRealm.realmServiceCore.structure.spi.common.structureImpl;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeTraverser;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.*;

public class CommonInheritanceTreeImpl<T> implements InheritanceTree<T> {

    private String rootNodeUID;
    private TreeTraverser<String> uidTreeTraverser;
    private Table<String, String, T> treeElementsTable;

    public CommonInheritanceTreeImpl(String rootNodeUID, Table<String, String, T> treeElementsTable){
        if(treeElementsTable != null){
            this.treeElementsTable = treeElementsTable;
        }else{
            this.treeElementsTable = HashBasedTable.create();
        }
        this.rootNodeUID = rootNodeUID;
        this.uidTreeTraverser = new TreeTraverser<String>() {
            @Override
            public Iterable<String> children(String elementUID) {
                return  getChildrenUID(elementUID) ;
            }
        };
    }

    @Override
    public boolean isRoot(String nodeUID) {
        return nodeUID.equals(this.rootNodeUID);
    }

    @Override
    public String getRootUID() {
        return this.rootNodeUID;
    }

    @Override
    public T getRoot() {
        return getNode(this.rootNodeUID);
    }

    @Override
    public T getNode(String nodeUID) {
        Map<String,T> nodeMap = this.treeElementsTable.column(nodeUID);
        //nodeMap should only have one element
        if(nodeMap != null && nodeMap.size()>0){
            return nodeMap.values().iterator().next();
        }
        return null;
    }

    @Override
    public String getParentUID(String nodeUID) {
        String parentNodeUID = null;
        Map<String,T> nodeMap = this.treeElementsTable.column(nodeUID);
        //nodeMap should only have one element
        if(nodeMap != null && nodeMap.size()>0){
            parentNodeUID = nodeMap.keySet().iterator().next();
        }
        return parentNodeUID;
    }

    @Override
    public T getParent(String nodeUID){
        return getNode(getParentUID(nodeUID));
    }

    @Override
    public Collection<String> getChildrenUID(String nodeUID) {
        Map<String,T> nodeMap = this.treeElementsTable.row(nodeUID);
        return nodeMap != null? nodeMap.keySet() : null;
    }

    @Override
    public Collection<T> getChildren(String nodeUID) {
        Map<String,T> nodeMap = this.treeElementsTable.row(nodeUID);
        return nodeMap != null ? nodeMap.values() : null;
    }

    @Override
    public Collection<T> getSiblings(String nodeUID) {
        String parentNodeUID = getParentUID(nodeUID);
        return getChildren(parentNodeUID);
    }

    @Override
    public int numOfChildren(String nodeUID) {
        return treeElementsTable.row(nodeUID).size();
    }

    @Override
    public int size() {
        return treeElementsTable.size();
    }

    @Override
    public Iterable<T> path(String ancestorNodeUID, String OffspringNodeUID) {
        List<String> pathNodeUIDList = new ArrayList<>();
        String currentParentNodeUID = getParentUID(OffspringNodeUID);
        pathNodeUIDList.add(currentParentNodeUID);

        boolean matchedFlag = false;
        boolean needExecuteNextLoop = true;

        while(needExecuteNextLoop){
            currentParentNodeUID = getParentUID(currentParentNodeUID);
            if(currentParentNodeUID != null & !currentParentNodeUID.equals(ancestorNodeUID) & !currentParentNodeUID.equals(this.rootNodeUID)){
                pathNodeUIDList.add(currentParentNodeUID);
            }else if(currentParentNodeUID != null & currentParentNodeUID.equals(ancestorNodeUID)){
                matchedFlag = true;
                needExecuteNextLoop = false;
                //break;
            }else if(currentParentNodeUID != null & currentParentNodeUID.equals(this.rootNodeUID)){
                matchedFlag = false;
                needExecuteNextLoop = false;
            }else if(currentParentNodeUID == null){
                matchedFlag = false;
                needExecuteNextLoop = false;
            }
        }
        if(matchedFlag){
            List<T> nodeOnPathList = new LinkedList<>();
            for(String currentNodeUID : pathNodeUIDList){
                nodeOnPathList.add(getNode(currentNodeUID));
            }
            return nodeOnPathList;
        }else{
            return null;
        }
    }

    @Override
    public Iterable<T> traversalTree(String nodeUID,TraversalStrategy traversalStrategy) {
        FluentIterable<String> nodeUIDFluentIterable = null;
        switch(traversalStrategy){
            case BreadthFirst:
                nodeUIDFluentIterable = this.uidTreeTraverser.breadthFirstTraversal(nodeUID);
            case PreOrder:
                nodeUIDFluentIterable = this.uidTreeTraverser.preOrderTraversal(nodeUID);
            case PostOrder:
                nodeUIDFluentIterable = this.uidTreeTraverser.postOrderTraversal(nodeUID);
        }
        if(nodeUIDFluentIterable != null) {
            List<T> resultElementList = new LinkedList<>();
            for (String currentNodeUID : nodeUIDFluentIterable) {
                resultElementList.add(getNode(currentNodeUID));
            }
            return resultElementList;
        }
        return null;
    }
}
