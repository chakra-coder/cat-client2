package com.patterncat.apm.status;

import java.util.Map;

public interface StatusExtension {

    public String getId();

    public String getDescription();

    public Map<String, String> getProperties();
}
