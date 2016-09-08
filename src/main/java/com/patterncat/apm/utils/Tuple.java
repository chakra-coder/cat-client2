package com.patterncat.apm.utils;

public interface Tuple {
    public <T> T get(int index);

    public int size();
}
