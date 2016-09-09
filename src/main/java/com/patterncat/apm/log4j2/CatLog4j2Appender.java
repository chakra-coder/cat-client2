package com.patterncat.apm.log4j2;

import com.patterncat.apm.message.Trace;
import com.patterncat.apm.message.spi.MessageManager;
import com.patterncat.apm.service.Cat;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Created by patterncat on 2016-09-07.
 */
@Plugin(name="CatLog4j2Appender", category="Core", elementType="appender", printObject=true)
public class CatLog4j2Appender extends AbstractAppender {
    protected CatLog4j2Appender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        MessageManager messageManager = Cat.getManager();
        if(messageManager == null){
            return ;
        }
        boolean isTraceMode = messageManager.isTraceMode();
        Level level = event.getLevel();

        if (level.isMoreSpecificThan(Level.ERROR)) {
            logError(event);
        } else if (isTraceMode) {
            logTrace(event);
        }
    }

    private void logError(LogEvent event) {
        Throwable exception = event.getThrown();
        if (exception != null) {
            Object message = event.getMessage();
            if (message != null) {
                Cat.logError(String.valueOf(message), exception);
            } else {
                Cat.logError(exception);
            }
        }else{
            Object msg = event.getMessage();
            if(!StringUtils.isEmpty(msg)){
                Cat.getProducer().logError(msg.toString());
            }
        }
    }

    private void logTrace(LogEvent event) {
        String type = "Log4j";
        String name = event.getLevel().toString();
        Object message = event.getMessage();
        String data;

        if (message instanceof Throwable) {
            data = buildExceptionStack((Throwable) message);
        } else {
            data = event.getMessage().toString();
        }

        Throwable exception = event.getThrown();

        if (exception != null) {
            data = data + '\n' + buildExceptionStack(exception);
        }
        Cat.logTrace(type, name, Trace.SUCCESS, data);
    }

    private String buildExceptionStack(Throwable exception) {
        if (exception != null) {
            StringWriter writer = new StringWriter(2048);

            exception.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        } else {
            return "";
        }
    }

    @PluginFactory
    public static CatLog4j2Appender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new CatLog4j2Appender(name, filter, layout, true);
    }
}
