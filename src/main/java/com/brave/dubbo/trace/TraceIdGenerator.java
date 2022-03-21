package com.brave.dubbo.trace;

import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * traceId生成算法
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-03-20 11:50
 */
public class TraceIdGenerator {

    private static final char[] chars = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final SecureRandom rnd = new SecureRandom();

    private static final AtomicLong inc = new AtomicLong();

    public static String getTraceId(IdGenEnum idGenEnum, String prefix){
        return switch (idGenEnum){
            case CURRENT_TIME -> getTraceIdByCurrentTime(prefix);
            default -> String.valueOf(getTraceIdByUUID());
        };
    }

    /**
     * 时间戳+ 自增位+ 随机数
     * @return 19位字符串
     */
    private static String getTraceIdByCurrentTime(String prefix){
        return StringUtils.defaultString(prefix)
                + Long.toHexString(System.currentTimeMillis())
                + getInc()
                + random(4);
    }

    /**
     * UUID
     * @return 19位long
     */
    private static long getTraceIdByUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.getLeastSignificantBits() ^ uuid.getMostSignificantBits();
    }

    private static String random(int count) {
        if (count <= 0) {
            return "";
        }

        char[] result = new char[count];

        for (int i = 0;i < count; i++){
            result[i] = chars[rnd.nextInt(chars.length)];
        }
        return new String(result);
    }

    private static String getInc(){
        long i = inc.addAndGet(1);
        return String.format("%04d", (int)(i % 10000));
    }

}
