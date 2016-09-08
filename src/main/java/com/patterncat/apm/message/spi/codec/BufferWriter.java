package com.patterncat.apm.message.spi.codec;

import io.netty.buffer.ByteBuf;

public interface BufferWriter {
	public int writeTo(ByteBuf buf, byte[] data);
}
