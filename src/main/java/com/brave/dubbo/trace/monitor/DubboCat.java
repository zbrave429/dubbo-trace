package com.brave.dubbo.trace.monitor;

import com.brave.dubbo.trace.thread.TraceFutureTask;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboCat {

    private static final Logger logger = LoggerFactory.getLogger(TraceFutureTask.class);

    private static boolean isEnable=true;

    /**
     * 禁用dubbo cat
     */
    public static void disable(){
        isEnable=false;
    }

    /**
     * 启用dubbo cat
     */
    public static void enable(){
        isEnable=true;
    }

    /**
     * 是否有效
     * @return
     */
    public static boolean isEnable(){
        boolean isCatEnabled = false;
        try {
            isCatEnabled = Cat.getManager().isCatEnabled();
        } catch (Throwable e) {
            logger.error("[DUBBO] Cat init error.", e);
        }
        return isCatEnabled && isEnable;
    }
}
