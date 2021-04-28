package com.skyruler.filechecklibrary.command.result;

public class Session {
    boolean logged;
    String session;
    String code;
    boolean updateSoftware;
    boolean updateConfig;


    public Session(boolean logged, String session, String code,
                   boolean updateSoftware, boolean updateConfig) {
        this.logged = logged;
        this.session = session;
        this.code = code;
        this.updateSoftware = updateSoftware;
        this.updateConfig = updateConfig;
    }

    public boolean isLogged() {
        return logged;
    }


    public String getSession() {
        return session;
    }


    public String getCode() {
        return code;
    }

    public boolean isUpdateSoftware() {
        return updateSoftware;
    }

    public boolean isUpdateConfig() {
        return updateConfig;
    }

    @Override
    public String toString() {
        return "Session{" +
                "logged=" + logged +
                ", session='" + session + '\'' +
                ", code='" + code + '\'' +
                ", updateSoftware=" + updateSoftware +
                ", updateConfig=" + updateConfig +
                '}';
    }
}
