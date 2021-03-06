package com.brave.dubbo.trace;

import com.brave.dubbo.trace.model.TraceExtend;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracer
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-03-19 19:15
 */
public class Tracer {

    /**
     * traceId
     */
    private String traceId;

    /**
     * spanId
     */
    private String spanId;

    /**
     * logicId
     */
    private AtomicInteger logicId = new AtomicInteger();

    /**
     * trace 扩展数据
     */
    private TraceExtend traceExtend;

    public Tracer() {
    }

    public Tracer(String traceId) {
        this.traceId = traceId;
    }

    public Tracer(String traceId, String spanId) {
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public AtomicInteger getLogicId() {
        return logicId;
    }

    public void setLogicId(AtomicInteger logicId) {
        this.logicId = logicId;
    }

    public static Tracer build(String traceId, String spanId){
        return new Tracer(traceId, spanId);
    }

    public TraceExtend getTraceExtend() {
        return traceExtend;
    }

    public void setTraceExtend(TraceExtend traceExtend) {
        this.traceExtend = traceExtend;
    }

    @Override
    public String toString() {
        return "Tracer{" +
                "traceId='" + traceId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", logicId=" + logicId +
                ", traceExtend=" + traceExtend +
                '}';
    }
}
