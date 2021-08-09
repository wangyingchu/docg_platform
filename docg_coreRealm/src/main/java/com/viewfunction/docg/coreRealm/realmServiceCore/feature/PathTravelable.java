package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.structure.Path;

import java.util.List;

public interface PathTravelable {

    public List<Path> expandPath(int minJump,int maxJump);
}
