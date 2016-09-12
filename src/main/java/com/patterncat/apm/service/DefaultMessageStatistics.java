package com.patterncat.apm.service;

import com.patterncat.apm.message.spi.MessageStatistics;
import com.patterncat.apm.message.spi.MessageTree;

public class DefaultMessageStatistics implements MessageStatistics {
    private long m_produced;

    private long m_overflowed;

    private long m_bytes;

    @Override
    public long getBytes() {
        return m_bytes;
    }

    @Override
    public long getOverflowed() {
        return m_overflowed;
    }

    @Override
    public long getProduced() {
        return m_produced;
    }

    @Override
    public void onBytes(int bytes) {
        m_bytes += bytes;
        m_produced++;
    }

    @Override
    public void onOverflowed(MessageTree tree) {
        m_overflowed++;
    }
}
