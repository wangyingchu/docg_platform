package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.util.httpResponseVO;

public class ListDatabasesVO {

    private String user;
    private String version;
    private String serverName;
    private String[] result;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String[] getResult() {
        return result;
    }

    public void setResult(String[] result) {
        this.result = result;
    }
}
