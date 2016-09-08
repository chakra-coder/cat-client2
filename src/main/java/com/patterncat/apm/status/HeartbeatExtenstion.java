package com.patterncat.apm.status;


import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class HeartbeatExtenstion implements StatusExtension {

    @Override
    public String getId() {
        return "MyTestId";
    }

    @Override
    public String getDescription() {
        return "MyDescription";
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> maps = new HashMap<String, String>();

        maps.put("key1", String.valueOf(1));
        maps.put("key2", String.valueOf(2));
        maps.put("key3", String.valueOf(3));

        return maps;
    }

    @PostConstruct
    public void initialize() {
        StatusExtensionRegister.getInstance().register(this);
    }

}
