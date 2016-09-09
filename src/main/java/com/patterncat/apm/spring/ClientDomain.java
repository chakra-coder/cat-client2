package com.patterncat.apm.spring;

/**
 * Created by patterncat on 2016-09-09.
 */
public class ClientDomain {

    private String id;

    private boolean enabled;

    public ClientDomain() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
