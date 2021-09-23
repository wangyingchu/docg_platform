package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import java.util.Set;

public class PersonalizedArticleRankAlgorithmConfig extends ArticleRankAlgorithmConfig{

    private Set<String> personalizedArticleRankEntityUIDs;

    public Set<String> getPersonalizedArticleRankEntityUIDs() {
        return personalizedArticleRankEntityUIDs;
    }

    public void setPersonalizedArticleRankEntityUIDs(Set<String> personalizedArticleRankEntityUIDs) {
        this.personalizedArticleRankEntityUIDs = personalizedArticleRankEntityUIDs;
    }
}
