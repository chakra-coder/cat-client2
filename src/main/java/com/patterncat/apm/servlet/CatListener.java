package com.patterncat.apm.servlet;

import com.patterncat.apm.Cat;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class CatListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Cat.destroy();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		String catClientXml = ctx.getInitParameter("cat-client-xml");

		if (catClientXml == null) {
			catClientXml = new File(Cat.getCatHome(), "client.xml").getPath();
		}

		Cat.initialize(new File(catClientXml));
	}
}
