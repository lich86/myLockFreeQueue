package com.chervonnaya;

import lombok.Getter;
import lombok.Setter;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MyLockFreeQueue<T> {
    private final AtomicReference<Node<T>> head, tail;
    private final AtomicInteger size;

    public MyLockFreeQueue() {
        head = new AtomicReference<>(null);
        tail = new AtomicReference<>(null);
        size = new AtomicInteger();
        size.set(0);
    }

    @Getter @Setter
    private class Node<T> {
        private volatile T value;
        private volatile Node<T> next;
        private volatile Node<T> previous;

        public Node(T value) {
            this.value = value;
            this.next = null;
        }

    }

    public void enqueue(T element) {
        if (element == null) {
            throw new NullPointerException();
        }

        Node<T> node = new Node<>(element);
        Node<T> currentTail;
        do {
            currentTail = tail.get();
            node.setPrevious(currentTail);
        } while(!tail.compareAndSet(currentTail, node));

        if(node.previous != null) {
            node.previous.next = node;
        }

        head.compareAndSet(null, node);
        size.incrementAndGet();
    }


    public T dequeue() {
        if(head.get() == null) {
            return null;
        }

        Node<T> currentHead;
        Node<T> nextNode;
        do {
            currentHead = head.get();
            nextNode = currentHead.getNext();
        } while(!head.compareAndSet(currentHead, nextNode));

        size.decrementAndGet();
        return currentHead.getValue();
    }

}
