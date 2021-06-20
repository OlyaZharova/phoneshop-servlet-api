package com.es.phoneshop.security;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {

    private static final long THRESHOLD = 20;
    private static final int LIMIT_TIME = 60000;
    private Map<String, Long> countMap = new ConcurrentHashMap();
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
        Long count = countMap.get(ip);
        if (count == null) {
            count = 1L;
        } else {
            if (count > THRESHOLD) {
                return false;
            }
            count++;
        }
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
