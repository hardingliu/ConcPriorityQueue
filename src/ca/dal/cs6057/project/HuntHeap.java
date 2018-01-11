package ca.dal.cs6057.project;

import java.util.concurrent.locks.ReentrantLock;

public class HuntHeap {
    private BitReversedCounter counter;
    private int size;
    private Node[] heap;
    private final ReentrantLock heapLock = new ReentrantLock(true);
    private static final long EMPTY = -1;
    private static final long AVAILABLE = -2;


    public HuntHeap(int size) {
        this.size = size;
        this.heap = new Node[size + 1];
        counter = new BitReversedCounter();
        for (int i = 0; i < size + 1; i++) {
            heap[i] = new Node();
        }
    }

    private class Node {
        private int value;
        private long tag;
        private final ReentrantLock nodeLock = new ReentrantLock(true);

        private Node() {
            this.value = Integer.MAX_VALUE;
            tag = EMPTY;
        }
    }


    public Insert insert(int value) {
        return new Insert(value);
    }

    private class Insert implements Runnable {
        int value;

        private Insert(int value) {
            this.value = value;
        }

        @Override
        public void run() {
            heapLock.lock();
            if (counter.getCounter() >= size) {
                heapLock.unlock();
                return;
            }
            int i = counter.increment();
            heap[i].nodeLock.lock();
            heapLock.unlock();
            heap[i].value = value;
            heap[i].tag = Thread.currentThread().getId();
            heap[i].nodeLock.unlock();

            while (i > 1) {
                int parent = i / 2;
                heap[parent].nodeLock.lock();
                heap[i].nodeLock.lock();
                int oldI = i;
                if (heap[parent].tag == AVAILABLE && heap[i].tag == Thread.currentThread().getId()) {

                    if (heap[i].value < heap[parent].value) {
                        long iTag = heap[i].tag;
                        int iValue = heap[i].value;
                        heap[i].tag = heap[parent].tag;
                        heap[i].value = heap[parent].value;
                        heap[parent].tag = iTag;
                        heap[parent].value = iValue;
                        i = parent;
                    } else {
                        heap[i].tag = AVAILABLE;
                        i = 0;
                    }
                } else if (heap[parent].tag == EMPTY) {
                    i = 0;
                } else if (heap[i].tag != Thread.currentThread().getId()) {
                    i = parent;
                }
                heap[oldI].nodeLock.unlock();
                heap[parent].nodeLock.unlock();
            }


            if (i == 1) {
                heap[i].nodeLock.lock();
                if (heap[i].tag == Thread.currentThread().getId()) {
                    heap[i].tag = AVAILABLE;
                }
                heap[i].nodeLock.unlock();
            }
        }
    }

    public Delete deleteMin() {
        return new Delete();
    }


    private class Delete implements Runnable {
        @Override
        public void run() {
            heapLock.lock();
            if (counter.getCounter() <= 0) {
                heapLock.unlock();
                return;
            }
            int bottom = counter.decrement();
            heap[bottom].nodeLock.lock();
            heapLock.unlock();
            int priority = heap[bottom].value;
            heap[bottom].tag = EMPTY;
            heap[bottom].nodeLock.unlock();

            heap[1].nodeLock.lock();
            if (heap[1].tag == EMPTY) {
                heap[1].nodeLock.unlock();
                return;
            }
            int temp = heap[1].value;
            heap[1].value = priority;
            priority = temp;
            heap[1].tag = AVAILABLE;

            int i = 1;
            while (i <= (heap.length - 1) / 2) {
                int left = i * 2;
                int right = i * 2 + 1;

                int child;

                heap[left].nodeLock.lock();
                heap[right].nodeLock.lock();

                if (heap[left].tag == EMPTY) {
                    heap[right].nodeLock.unlock();
                    heap[left].nodeLock.unlock();
                    break;
                } else if (heap[right].tag == EMPTY || heap[left].value < heap[right].value) {
                    heap[right].nodeLock.unlock();
                    child = left;
                } else {
                    heap[left].nodeLock.unlock();
                    child = right;
                }

                if (heap[child].value < heap[i].value) {
                    long iTag = heap[i].tag;
                    int iValue = heap[i].value;
                    heap[i].tag = heap[child].tag;
                    heap[i].value = heap[child].value;
                    heap[child].tag = iTag;
                    heap[child].value = iValue;
                    heap[i].nodeLock.unlock();
                    i = child;
                } else {
                    heap[child].nodeLock.unlock();
                    break;
                }
            }

            heap[i].nodeLock.unlock();
        }
    }

    public void checkHeap() {
        boolean bad = false;
        int left, right;

        for (int i = 1; i <= size / 2; i++) {

            left = 2 * i;
            right = 2 * i + 1;

            if (heap[i].tag == EMPTY) {

                if (left <= size && heap[left].tag != EMPTY) {
                    bad = true;
                    System.out.println("*** Non-empty left child of empty node at index: " + i);
                }

                if (right <= size && heap[right].tag != EMPTY) {
                    bad = true;
                    System.out.println("*** Non-empty right child of empty node at index: " + i);
                }
            } else {


                if (left <= size && (heap[left].tag != EMPTY) && (heap[left].value < heap[i].value)) {
                    bad = true;
                    System.out.println("*** Left child bigger at index: " + i);
                }


                if (right <= size && (heap[right].tag != EMPTY) && (heap[right].value < heap[i].value)) {
                    bad = true;
                    System.out.println("*** Right child bigger at index: " + i);
                }


                if (left <= size && right <= size && heap[left].tag == EMPTY && heap[right].tag != EMPTY) {
                    bad = true;
                    System.out.println("*** Left child empty but right child non-empty at index: " + i);
                }
            }

        }


        if (!bad) {
            System.out.println("Heap passes sanity check!!!");
        }
    }


}
