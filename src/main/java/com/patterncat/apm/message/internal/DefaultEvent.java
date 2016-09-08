package com.patterncat.apm.message.internal;


import com.patterncat.apm.message.Event;
import com.patterncat.apm.message.spi.MessageManager;

public class DefaultEvent extends AbstractMessage implements Event {
    private MessageManager m_manager;

    public DefaultEvent(String type, String name) {
        super(type, name);
    }

    public DefaultEvent(String type, String name, MessageManager manager) {
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
