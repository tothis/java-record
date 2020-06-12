package com.example.base.io;

import java.nio.ByteBuffer;

/**
 * @author 李磊
 * @datetime 2020/6/12 13:07
 * @description
 */
public class DirectBufferTest {
    private static final int COUNT = 100_000;
    private static final int SIZE = 100;

    /**
     * 在JDK 1.4中新加入了NIO(New Input/Output)类 引入了一种基于通道与缓冲区的I/O方式
     * 可通过native函数使用堆外内存 即直接内存(Direct Memory) 通过DirectByteBuffer对象作为这块内存的引用进行操作
     * 能在一些场景提高性能 避免在java堆和native堆中来回复制数据
     *
     * @param args
     */
    public static void main(String[] args) {
        ByteBuffer buffer1 = ByteBuffer.allocate(SIZE * 4);
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(SIZE * 4);
        long start = System.currentTimeMillis();
        access(buffer1);
        System.out.println("HeapByteBuffer访问内存 : " + time(start) + "ms");

        start = System.currentTimeMillis();
        access(buffer2);
        System.out.println("DirectByteBuffer访问内存 : " + time(start) + "ms");

        start = System.currentTimeMillis();
        allocate(true);
        System.out.println("HeapByteBuffer申请内存 : " + time(start) + "ms");

        start = System.currentTimeMillis();
        allocate(true);
        System.out.println("DirectByteBuffer申请内存 : " + time(start) + "ms");
    }

    private static void access(ByteBuffer buffer) {
        for (int i = 0; i < COUNT; i++) {
            for (int j = 0; j < SIZE; j++) {
                // 向buffer中写数据
                buffer.putInt(j);
            }
            buffer.flip();
            for (int j = 0; j < SIZE; j++) {
                // 从buffer中读数据
                buffer.get();
            }
            buffer.clear();
        }
    }

    private static void allocate(boolean flag) {
        if (flag) {
            for (int i = 0; i < COUNT; i++) {
                ByteBuffer.allocate(SIZE);
            }
        } else {
            for (int i = 0; i < COUNT; i++) {
                ByteBuffer.allocateDirect(SIZE);
            }
        }
    }

    private static long time(long start) {
        return (System.currentTimeMillis() - start);
    }
}