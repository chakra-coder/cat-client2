package com.patterncat.apm.service;

import com.patterncat.apm.config.ClientConfigManager;
import com.patterncat.apm.message.internal.MilliSecondTimer;
import com.patterncat.apm.utils.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.LockSupport;

public class CatClientTask {

    private static final Logger logger = LoggerFactory.getLogger(CatClientTask.class);

    public static final String ID = "cat-client";

    @Autowired
    ClientConfigManager clientConfigManager;

    @Autowired
    StatusUpdateTask statusUpdateTask;

    @PostConstruct
    protected void init() throws Exception {
        logger.info("Current working directory is " + System.getProperty("user.dir"));

        // initialize milli-second resolution level timer
        MilliSecondTimer.initialize();

        // tracking thread start/stop
        Threads.addListener(new CatThreadListener());

        if (clientConfigManager.isCatEnabled()) {
            Threads.forGroup("cat").start(statusUpdateTask);
            LockSupport.parkNanos(10 * 1000 * 1000L); // wait 10 ms
        }
    }

    public static final class CatThreadListener extends Threads.AbstractThreadListener {
        private static final Logger logger = LoggerFactory.getLogger(CatThreadListener.class);

        @Override
        public void onThreadGroupCreated(ThreadGroup group, String name) {
            logger.info(String.format("Thread group(%s) created.", name));
        }

        @Override
        public void onThreadPoolCreated(ExecutorService pool, String name) {
            logger.info(String.format("Thread pool(%s) created.", name));
        }

        @Override
        public void onThreadStarting(Thread thread, String name) {
            logger.info(String.format("Starting thread(%s) ...", name));
        }

        @Override
        public void onThreadStopping(Thread thread, String name) {
            logger.info(String.format("Stopping thread(%s).", name));
        }

        @Override
        public boolean onUncaughtException(Thread thread, Throwable e) {
            logger.error(String.format("Uncaught exception thrown out of thread(%s)", thread.getName()), e);
            return true;
        }
    }
}
