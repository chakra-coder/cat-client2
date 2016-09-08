package com.patterncat.apm.message.internal;


import com.patterncat.apm.message.Metric;
import com.patterncat.apm.message.spi.MessageManager;

public class DefaultMetric extends AbstractMessage implements Metric {
	private MessageManager m_manager;

	public DefaultMetric(String type, String name) {
		super(type, name);
	}

	public DefaultMetric(String type, String name, MessageManager manager) {
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
