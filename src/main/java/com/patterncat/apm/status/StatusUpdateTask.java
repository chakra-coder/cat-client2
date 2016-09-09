package com.patterncat.apm.status;

import com.patterncat.apm.config.ClientConfigManager;
import com.patterncat.apm.message.Heartbeat;
import com.patterncat.apm.message.Message;
import com.patterncat.apm.message.MessageProducer;
import com.patterncat.apm.message.Transaction;
import com.patterncat.apm.message.internal.MilliSecondTimer;
import com.patterncat.apm.message.spi.MessageStatistics;
import com.patterncat.apm.service.Cat;
import com.patterncat.apm.status.model.entity.Extension;
import com.patterncat.apm.status.model.entity.StatusInfo;
import com.patterncat.apm.utils.NetworkInterfaceManager;
import com.patterncat.apm.utils.Threads;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class StatusUpdateTask implements Threads.Task {

    @Autowired
    private MessageStatistics m_statistics;

    @Autowired
    private ClientConfigManager m_manager;

    @Autowired
    MessageProducer cat;

    private boolean m_active = true;

    private String m_ipAddress;

    private long m_interval = 60 * 1000; // 60 seconds

    private String m_jars;

    private void buildClasspath() {
        ClassLoader loader = StatusUpdateTask.class.getClassLoader();
        StringBuilder sb = new StringBuilder();

        buildClasspath(loader, sb);
        if (sb.length() > 0) {
            m_jars = sb.substring(0, sb.length() - 1);
        }
    }

    private void buildClasspath(ClassLoader loader, StringBuilder sb) {
        if (loader instanceof URLClassLoader) {
            URL[] urLs = ((URLClassLoader) loader).getURLs();
            for (URL url : urLs) {
                String jar = parseJar(url.toExternalForm());

                if (jar != null) {
                    sb.append(jar).append(',');
                }
            }
            ClassLoader parent = loader.getParent();

            buildClasspath(parent, sb);
        }
    }

    private void buildExtensionData(StatusInfo status) {
        StatusExtensionRegister res = StatusExtensionRegister.getInstance();
        List<StatusExtension> extensions = res.getStatusExtension();

        for (StatusExtension extension : extensions) {
            String id = extension.getId();
            String des = extension.getDescription();
            Map<String, String> propertis = extension.getProperties();
            Extension item = status.findOrCreateExtension(id).setDescription(des);

            for (Entry<String, String> entry : propertis.entrySet()) {
                try {
                    double value = Double.parseDouble(entry.getValue());
                    item.findOrCreateExtensionDetail(entry.getKey()).setValue(value);
                } catch (Exception e) {
                    Cat.logError("StatusExtension can only be double type", e);
                }
            }
        }
    }

    public String getName() {
        return "StatusUpdateTask";
    }

    public void initialize() {
        m_ipAddress = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
    }

    private String parseJar(String path) {
        if (path.endsWith(".jar")) {
            int index = path.lastIndexOf('/');

            if (index > -1) {
                return path.substring(index + 1);
            }
        }
        return null;
    }

    @Override
    public void run() {
        // try to wait cat client init success
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            return;
        }

        while (true) {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);

            // try to avoid send heartbeat at 59-01 second
            if (second < 2 || second > 58) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore it
                }
            } else {
                break;
            }
        }

        try {
            buildClasspath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Transaction reboot = cat.newTransaction("System", "Reboot");

        reboot.setStatus(Message.SUCCESS);
        cat.logEvent("Reboot", NetworkInterfaceManager.INSTANCE.getLocalHostAddress(), Message.SUCCESS, null);
        reboot.complete();

        while (m_active) {
            long start = MilliSecondTimer.currentTimeMillis();

            if (m_manager.isCatEnabled()) {
                Transaction t = cat.newTransaction("System", "Status");
                Heartbeat h = cat.newHeartbeat("Heartbeat", m_ipAddress);
                StatusInfo status = new StatusInfo();

                t.addData("dumpLocked", m_manager.isDumpLocked());
                try {
                    StatusInfoCollector statusInfoCollector = new StatusInfoCollector(m_statistics, m_jars);

                    status.accept(statusInfoCollector.setDumpLocked(m_manager.isDumpLocked()));

                    buildExtensionData(status);
                    h.addData(status.toString());
                    h.setStatus(Message.SUCCESS);
                } catch (Throwable e) {
                    h.setStatus(e);
                    cat.logError(e);
                } finally {
                    h.complete();
                }
                t.setStatus(Message.SUCCESS);
                t.complete();
            }
            long elapsed = MilliSecondTimer.currentTimeMillis() - start;

            if (elapsed < m_interval) {
                try {
                    Thread.sleep(m_interval - elapsed);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public void setInterval(long interval) {
        m_interval = interval;
    }

    public void shutdown() {
        m_active = false;
    }
}
