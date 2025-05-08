package com.github.chenqimiao.qmmusic.core.pool;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Qimiao Chen
 * @since 2025/4/24 15:52
 **/
public class DirectBufferPool {

    private final BlockingQueue<ByteBuffer> pool;
    private final int bufferSize;

    public DirectBufferPool(int poolSize, int bufferSize) {
        this.bufferSize = bufferSize;
        this.pool = new LinkedBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            pool.offer(ByteBuffer.allocateDirect(bufferSize));
        }
    }

    public ByteBuffer borrowBuffer() throws InterruptedException {
        return pool.take();
    }

    public void returnBuffer(ByteBuffer buffer) {
        buffer.clear(); // 重置 Buffer 状态
        pool.offer(buffer);
    }
}
