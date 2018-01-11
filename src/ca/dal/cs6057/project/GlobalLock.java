package ca.dal.cs6057.project;

import java.util.concurrent.locks.ReentrantLock;

public class GlobalLock {
    private int sizeOfQueue;
    private int openPos;
    private int[] queue;
    private ReentrantLock lock;

    public GlobalLock(int sizeOfQueue) {
        this.sizeOfQueue = sizeOfQueue;
        this.openPos = 0;
        this.queue = new int[sizeOfQueue];
        this.lock = new ReentrantLock(true);
    }

    public InsertThread insert(int n) {
        return new InsertThread(n);
    }

    private class InsertThread implements Runnable {
        private int insertValue;

        private InsertThread(int insertValue) {
            this.insertValue = insertValue;
        }

        @Override
        public void run() {
            lock.lock();

            if (openPos < sizeOfQueue) {
                queue[openPos] = insertValue;
                openPos++;
                swapUP(openPos - 1);
            }

            lock.unlock();

        }

        private void swapUP(int curr) {
            int parent = (curr - 1) / 2;
            if (parent >= 0 && queue[parent] < queue[curr]) {
                int temp = queue[parent];
                queue[parent] = queue[curr];
                queue[curr] = temp;
                swapUP(parent);
            }
        }
    }

    public DeleteThread deleteMin() {
        return new DeleteThread();
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            lock.lock();
            if (openPos > 0) {
                queue[0] = queue[openPos - 1];
                openPos--;
                swapDown(0);
            }
            lock.unlock();
        }
    }

    private void swapDown(int curr) {
        int leftChild = curr * 2 + 1;
        int rightChild = curr * 2 + 2;
        if (rightChild < openPos) {
            int prev = curr;
            if (queue[leftChild] < queue[curr]) {
                curr = leftChild;
            }
            if (queue[rightChild] < queue[curr]) {
                curr = rightChild;
            }
            if (curr != prev) {
                int temp = queue[prev];
                queue[prev] = queue[curr];
                queue[curr] = temp;
                swapDown(curr);
            }
        } else if (leftChild < openPos && queue[leftChild] < queue[curr]) {
            int temp = queue[curr];
            queue[curr] = queue[leftChild];
            queue[leftChild] = temp;
        }
    }

    public void checkHeap() {
        boolean test = true;
        int check = 1;
        for (int i = 0; i <=  (openPos - 2) / 2; i++) {
            if (queue[i * 2 + 1] < queue[i] || queue[i * 2 + 2] < queue[i]) {
                check = i;
                test = false;
            }
        }
        if (!test) {
            System.out.println("index " + check + " is not valid.");
        } else {
            System.out.println("This heap is valid!");
        }
    }
}

