package com.patterncat.apm.config.client.transform;

import com.patterncat.apm.config.client.entity.*;

public interface ILinker {

   public boolean onBind(ClientConfig parent, Bind bind);

   public boolean onDomain(ClientConfig parent, Domain domain);

   public boolean onProperty(ClientConfig parent, Property property);

   public boolean onServer(ClientConfig parent, Server server);
}
