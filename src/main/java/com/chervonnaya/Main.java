package com.chervonnaya;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        MyLockFreeQueue <Integer> queue = new MyLockFreeQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        Runnable producer = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Produced: " + i);
                queue.enqueue(i);

            }
        };

        Runnable consumer = () -> {
            for (int i = 0; i < 10; i++) {
                Integer item;
                while ((item = queue.dequeue()) == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("Consumed: " + item);
            }
        };

        executor.execute(producer);
        executor.execute(consumer);

        executor.shutdown();
    }
}
