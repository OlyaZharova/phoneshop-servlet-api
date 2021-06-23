package com.es.phoneshop.security;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultDosProtectionService implements DosProtectionService {

    private static final int THRESHOLD = 20;
    private static final int LIMIT_TIME = 60000;
    private Map<String, AtomicInteger> countMap = new ConcurrentHashMap();
    private volatile Date lastResetDate = new Date();

    private static class SingletonHelper {
        private static final DefaultDosProtectionService INSTANCE = new DefaultDosProtectionService();
    }

    public static DefaultDosProtectionService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public boolean isAllowed(String ip) {
        deleteOld();
        AtomicInteger count = countMap.computeIfAbsent(ip, k -> new AtomicInteger(1));
        int value = count.get();
        if (value > THRESHOLD) {
            return false;
        }
        count.incrementAndGet();
        countMap.put(ip, count);
        return true;
    }

    private void deleteOld() {
        Date nowDate = new Date();
        if (nowDate.getTime() - lastResetDate.getTime() >= LIMIT_TIME) {
            countMap.clear();
            lastResetDate = nowDate;
        }
    }
}
