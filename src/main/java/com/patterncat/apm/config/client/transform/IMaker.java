package com.patterncat.apm.config.client.transform;


import com.patterncat.apm.config.client.entity.*;

public interface IMaker<T> {

   public Bind buildBind(T node);

   public ClientConfig buildConfig(T node);

   public Domain buildDomain(T node);

   public Property buildProperty(T node);

   public Server buildServer(T node);
}
