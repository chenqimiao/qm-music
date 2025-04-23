package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.service.SystemService;
import com.github.chenqimiao.service.complex.MediaFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 15:11
 **/
@Service
public class SystemServiceImpl implements SystemService {

    private final Object lock = new Object();
    private final static Set<Object> lockCache = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final ExecutorService REFRESH_THREAD_POOL = Executors.newSingleThreadExecutor();


    @Value("${qm.music.dir}")
    private String musicDir;

    @Autowired
    private MediaFetcherService mediaFetcherService;


    @Override
    public void refreshSongs() {
        if (!lockCache.isEmpty()) {
            return;
        }
        REFRESH_THREAD_POOL.execute( () -> {
            boolean locked = false;
            try {
                locked = lockCache.add(lock);
                if (locked) {
                    // 并发控制
                    mediaFetcherService.fetchMusic(musicDir);
                }
            }finally {
                if (locked) {
                    lockCache.remove(lock);
                }
            }
        });
    }

    @Override
    public boolean scanning() {
        return !lockCache.isEmpty();
    }
}
