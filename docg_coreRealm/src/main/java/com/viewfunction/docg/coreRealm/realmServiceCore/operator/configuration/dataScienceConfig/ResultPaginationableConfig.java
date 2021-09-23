package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

public class ResultPaginationableConfig extends DataScienceBaseAlgorithmConfig{

    int pageSize = 50;
    int startPage = 1;
    int endPage = 21;
    int resultNumber = 0;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }
}
