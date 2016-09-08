package com.patterncat.apm.service;

import com.google.common.io.Resources;
import com.patterncat.apm.CatConstants;
import com.patterncat.apm.message.*;
import com.patterncat.apm.message.spi.MessageManager;
import com.patterncat.apm.message.spi.MessageTree;
import com.patterncat.apm.utils.ApplicationContextHolder;
import com.patterncat.apm.utils.Properties;

import java.text.MessageFormat;
import java.util.Date;

/**
 * This is the main entry point to the system.
 */
public class Cat {

    public static String createMessageId() {
        return Cat.getProducer().createMessageId();
    }

    public static String getCatHome() {
        String catHome = Properties.forString().fromEnv().fromSystem().getProperty("CAT_HOME", Resources.getResource("META-INF/cat").getPath());
        return catHome;
    }

    public static String getCurrentMessageId() {
        MessageTree tree = Cat.getManager().getThreadLocalMessageTree();

        if (tree != null) {
            String messageId = tree.getMessageId();

            if (messageId == null) {
                messageId = Cat.createMessageId();
                tree.setMessageId(messageId);
            }
            return messageId;
        } else {
            return null;
        }
    }

    public static MessageManager getManager() {
        return ApplicationContextHolder.getContext().getBean(DefaultMessageManager.class);
    }

    public static MessageProducer getProducer() {
        return ApplicationContextHolder.getContext().getBean(DefaultMessageProducer.class);
    }

    public static void log(String severity, String message) {
        MessageFormat format = new MessageFormat("[{0,date,MM-dd HH:mm:ss.sss}] [{1}] [{2}] {3}");

        System.out.println(format.format(new Object[]{new Date(), severity, "cat", message}));
    }

    public static void logError(String message, Throwable cause) {
        Cat.getProducer().logError(message, cause);
    }

    public static void logError(Throwable cause) {
        Cat.getProducer().logError(cause);
    }

    public static void logEvent(String type, String name) {
        Cat.getProducer().logEvent(type, name);
    }

    public static void logEvent(String type, String name, String status, String nameValuePairs) {
        Cat.getProducer().logEvent(type, name, status, nameValuePairs);
    }

    public static void logHeartbeat(String type, String name, String status, String nameValuePairs) {
        Cat.getProducer().logHeartbeat(type, name, status, nameValuePairs);
    }

    public static void logMetric(String name, Object... keyValues) {
        // TO REMOVE ME
    }

    /**
     * Increase the counter specified by <code>name</code> by one.
     *
     * @param name the name of the metric default count value is 1
     */
    public static void logMetricForCount(String name) {
        logMetricInternal(name, "C", "1");
    }

    /**
     * Increase the counter specified by <code>name</code> by one.
     *
     * @param name the name of the metric
     */
    public static void logMetricForCount(String name, int quantity) {
        logMetricInternal(name, "C", String.valueOf(quantity));
    }

    /**
     * Increase the metric specified by <code>name</code> by <code>durationInMillis</code>.
     *
     * @param name             the name of the metric
     * @param durationInMillis duration in milli-second added to the metric
     */
    public static void logMetricForDuration(String name, long durationInMillis) {
        logMetricInternal(name, "T", String.valueOf(durationInMillis));
    }

    /**
     * Increase the sum specified by <code>name</code> by <code>value</code> only for one item.
     *
     * @param name  the name of the metric
     * @param value the value added to the metric
     */
    public static void logMetricForSum(String name, double value) {
        logMetricInternal(name, "S", String.format("%.2f", value));
    }

    /**
     * Increase the metric specified by <code>name</code> by <code>sum</code> for multiple items.
     *
     * @param name     the name of the metric
     * @param sum      the sum value added to the metric
     * @param quantity the quantity to be accumulated
     */
    public static void logMetricForSum(String name, double sum, int quantity) {
        logMetricInternal(name, "S,C", String.format("%s,%.2f", quantity, sum));
    }

    private static void logMetricInternal(String name, String status, String keyValuePairs) {
        Cat.getProducer().logMetric(name, status, keyValuePairs);
    }

    public static void logRemoteCallClient(Context ctx) {
        MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
        String messageId = tree.getMessageId();

        if (messageId == null) {
            messageId = Cat.createMessageId();
            tree.setMessageId(messageId);
        }

        String childId = Cat.createMessageId();
        Cat.logEvent(CatConstants.TYPE_REMOTE_CALL, "", Event.SUCCESS, childId);

        String root = tree.getRootMessageId();

        if (root == null) {
            root = messageId;
        }

        ctx.addProperty(Context.ROOT, root);
        ctx.addProperty(Context.PARENT, messageId);
        ctx.addProperty(Context.CHILD, childId);
    }

    public static void logRemoteCallServer(Context ctx) {
        MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
        String messageId = ctx.getProperty(Context.CHILD);
        String rootId = ctx.getProperty(Context.ROOT);
        String parentId = ctx.getProperty(Context.PARENT);

        if (messageId != null) {
            tree.setMessageId(messageId);
        }
        if (parentId != null) {
            tree.setParentMessageId(parentId);
        }
        if (rootId != null) {
            tree.setRootMessageId(rootId);
        }
    }

    public static void logTrace(String type, String name) {
        Cat.getProducer().logTrace(type, name);
    }

    public static void logTrace(String type, String name, String status, String nameValuePairs) {
        Cat.getProducer().logTrace(type, name, status, nameValuePairs);
    }

    public static Event newEvent(String type, String name) {
        return Cat.getProducer().newEvent(type, name);
    }

    public static ForkedTransaction newForkedTransaction(String type, String name) {
        return Cat.getProducer().newForkedTransaction(type, name);
    }

    public static Heartbeat newHeartbeat(String type, String name) {
        return Cat.getProducer().newHeartbeat(type, name);
    }

    public static TaggedTransaction newTaggedTransaction(String type, String name, String tag) {
        return Cat.getProducer().newTaggedTransaction(type, name, tag);
    }

    public static Trace newTrace(String type, String name) {
        return Cat.getProducer().newTrace(type, name);
    }

    public static Transaction newTransaction(String type, String name) {
        return Cat.getProducer().newTransaction(type, name);
    }

    // this should be called when a thread ends to clean some thread local data
    public static void reset() {
        // remove me
    }

    // this should be called when a thread starts to create some thread local data
    public static void setup(String sessionToken) {
        Cat.getManager().setup();
    }

    private Cat() {
    }

    public static boolean isInitialized() {
        return false;
    }

    public static interface Context {

        public final String ROOT = "_catRootMessageId";

        public final String PARENT = "_catParentMessageId";

        public final String CHILD = "_catChildMessageId";

        public void addProperty(String key, String value);

        public String getProperty(String key);
    }

}
