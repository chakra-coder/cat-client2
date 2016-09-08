package com.patterncat.apm.message;

public interface ForkedTransaction extends Transaction {
    public void fork();

    public String getForkedMessageId();
}
