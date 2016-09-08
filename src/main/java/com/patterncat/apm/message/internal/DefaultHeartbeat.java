package com.patterncat.apm.message.internal;


import com.patterncat.apm.message.Heartbeat;
import com.patterncat.apm.message.spi.MessageManager;

public class DefaultHeartbeat extends AbstractMessage implements Heartbeat {
    private MessageManager m_manager;

    public DefaultHeartbeat(String type, String name) {
        super(type, name);
    }

    public DefaultHeartbeat(String type, String name, MessageManager manager) {
        super(type, name);

        m_manager = manager;
    }

    @Override
    public void complete() {
        setCompleted(true);

        if (m_manager != null) {
            m_manager.add(this);
        }
    }
}
