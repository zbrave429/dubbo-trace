package com.brave.dubbo.trace.monitor.constants;

public class CatConstants {

    public  static final String CROSS_CONSUMER ="dubbo.call";

    public static final String CROSS_SERVER = "dubbo.service";
    
    public static final String PROVIDER_APPLICATION_NAME="serverApplicationName";
    
    public static final String CONSUMER_CALL_SERVER="dubbo.call.server";
    
    public static final String CONSUMER_CALL_APP="dubbo.call.app";
    
    public static final String CONSUMER_CALL_PORT="dubbo.call.port";
    
    public static final String PROVIDER_CALL_SERVER="dubbo.service.client";
    
    public static final String PROVIDER_CALL_APP="dubbo.service.app";

    public static final String FORK_MESSAGE_ID="m_forkedMessageId";

    public static final String FORK_ROOT_MESSAGE_ID="m_rootMessageId";

    public static final String FORK_PARENT_MESSAGE_ID="m_parentMessageId";
    
}
