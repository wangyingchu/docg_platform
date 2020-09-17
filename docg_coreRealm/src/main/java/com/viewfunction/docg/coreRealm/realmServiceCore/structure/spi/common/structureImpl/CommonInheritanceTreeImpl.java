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
                return  getChildrenID(elementUID) ;
            }
        };
    }

    @Override
    public boolean isRoot(String nodeID) {
        return nodeID.equals(this.rootNodeUID);
    }

    @Override
    public String getRootID() {
        return this.rootNodeUID;
    }

    @Override
    public T getRoot() {
        return getNode(this.rootNodeUID);
    }

    @Override
    public T getNode(String nodeID) {
        Map<String,T> nodeMap = this.treeElementsTable.column(nodeID);
        //nodeMap should only have one element
        if(nodeMap != null && nodeMap.size()>0){
            return nodeMap.values().iterator().next();
        }
        return null;
    }

    @Override
    public String getParentID(String nodeID) {
        String parentNodeUID = null;
        Map<String,T> nodeMap = this.treeElementsTable.column(nodeID);
        //nodeMap should only have one element
        if(nodeMap != null && nodeMap.size()>0){
            parentNodeUID = nodeMap.keySet().iterator().next();
        }
        return parentNodeUID;
    }

    @Override
    public T getParent(String nodeID){
        return getNode(getParentID(nodeID));
    }

    @Override
    public Collection<String> getChildrenID(String nodeID) {
        Map<String,T> nodeMap = this.treeElementsTable.row(nodeID);
        return nodeMap != null? nodeMap.keySet() : null;
    }

    @Override
    public Collection<T> getChildren(String nodeID) {
        Map<String,T> nodeMap = this.treeElementsTable.row(nodeID);
        return nodeMap != null ? nodeMap.values() : null;
    }

    @Override
    public Collection<String> getSiblingsID(String nodeID) {
        String parentNodeUID = getParentID(nodeID);
        Collection<String> siblingsIDCollection = getChildrenID(parentNodeUID);
        List<String> resultList = new ArrayList<>();
        Iterator<String> iterator = siblingsIDCollection.iterator();
        while(iterator.hasNext()){
            String currentElementID = iterator.next();
            if(!currentElementID.equals(nodeID)){
                resultList.add(currentElementID);
            }
        }
        return resultList;
    }

    @Override
    public Collection<T> getSiblings(String nodeID) {
        String parentNodeUID = getParentID(nodeID);
        Collection<T> siblingsCollection = getChildren(parentNodeUID);
        List<T> resultList = new ArrayList<>();
        Iterator<T> iterator = siblingsCollection.iterator();
        T selfElement = getNode(nodeID);
        while(iterator.hasNext()){
            T currentElement = iterator.next();
            if(currentElement != selfElement){
                resultList.add(currentElement);
            }
        }
        return resultList;
    }

    @Override
    public int numOfChildren(String nodeID) {
        return treeElementsTable.row(nodeID).size();
    }

    @Override
    public int size() {
        return treeElementsTable.size();
    }

    @Override
    public Iterable<T> path(String ancestorNodeID, String OffspringNodeID) {
        List<String> pathNodeUIDList = new ArrayList<>();
        String currentParentNodeUID = getParentID(OffspringNodeID);
        pathNodeUIDList.add(currentParentNodeUID);

        boolean matchedFlag = false;
        boolean needExecuteNextLoop = true;

        while(needExecuteNextLoop){



            System.out.println("step");



            currentParentNodeUID = getParentID(currentParentNodeUID);
            if(currentParentNodeUID != null & !currentParentNodeUID.equals(ancestorNodeID) & !currentParentNodeUID.equals(this.rootNodeUID)){
                pathNodeUIDList.add(currentParentNodeUID);
            }else if(currentParentNodeUID != null & currentParentNodeUID.equals(ancestorNodeID)){
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
    public Iterable<T> traversalTree(String nodeID, TraversalStrategy traversalStrategy) {
        FluentIterable<String> nodeUIDFluentIterable = null;
        switch(traversalStrategy){
            case BreadthFirst:
                nodeUIDFluentIterable = this.uidTreeTraverser.breadthFirstTraversal(nodeID);
            case PreOrder:
                nodeUIDFluentIterable = this.uidTreeTraverser.preOrderTraversal(nodeID);
            case PostOrder:
                nodeUIDFluentIterable = this.uidTreeTraverser.postOrderTraversal(nodeID);
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
