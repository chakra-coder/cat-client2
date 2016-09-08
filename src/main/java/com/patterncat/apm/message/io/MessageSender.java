package com.patterncat.apm.message.io;


import com.patterncat.apm.message.spi.MessageTree;

public interface MessageSender {
	public void initialize();

	public void send(MessageTree tree);

	public void shutdown();
}
