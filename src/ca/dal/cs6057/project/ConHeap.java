package ca.dal.cs6057.project;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConHeap {
    private Node[] heap;
    private int lastElement;
    private int fullLevel;

    public ConHeap(int size) {
        this.heap = new Node[size + 1];
        for (int i = 0; i <= size; i++) {
            heap[i] = new Node();
        }
        this.lastElement = 0;
        this.fullLevel = 0;
    }


    private class Node {
        private int value;
        private String status;
        private final Lock lock = new ReentrantLock(true);

        private Node() {
            this.status = "ABSENT";
            this.value = Integer.MAX_VALUE;
        }
    }

    public InsertThread insert(int value) {
        return new InsertThread(value);
    }

    private class InsertThread implements Runnable {
        private int value;

        public InsertThread(int value) {
            this.value = value;
        }

        @Override
        public void run() {
            heap[1].lock.lock();
            if (lastElement >= heap.length - 1) {
                heap[1].lock.unlock();
                return;
            }
            lastElement++;
            int target = lastElement;
            if (lastElement >= fullLevel * 2) {
                fullLevel = lastElement;
            }
            int i = target - fullLevel;
            int j = fullLevel / 2;
            int k = 1;
            heap[target].status = "PENDING";

            while (j != 0) {
                if (heap[target].status.equals("WANTED")) {
                    break;
                }
                if (heap[k].value > value) {
                    int temp = heap[k].value;
                    heap[k].value = value;
                    value = temp;
                }
                if (i >= j) {
                    heap[k * 2 + 1].lock.lock();
                    heap[k].lock.unlock();
                    k = k * 2 + 1;
                    i -= j;
                } else {
                    heap[k * 2].lock.lock();
                    heap[k].lock.unlock();
                    k = k * 2;
                }
                j /= 2;
            }
            if (heap[target].status.equals("WANTED")) {
                heap[1].value = value;
                heap[target].status = "ABSENT";
                heap[1].status = "PRESENT";
            } else {
                heap[target].value = value;
                heap[target].status = "PRESENT";
            }
            heap[k].lock.unlock();
        }
    }


    public DeleteThread deleteMin() {
        return new DeleteThread();
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            heap[1].lock.lock();
            if (lastElement == 0) {
                heap[1].lock.unlock();
                return;
            }
            int least = heap[1].value;
            int i = 1;
            int j = lastElement;
            lastElement--;
            if (lastElement < fullLevel) {
                fullLevel = fullLevel / 2;
            }
            if (j == 1) {
                heap[1].status = "ABSENT";
                heap[1].value = Integer.MAX_VALUE;
                heap[1].lock.unlock();
                return;
            }
            heap[j].lock.lock();
            if (heap[j].status.equals("PRESENT")) {
                heap[1].value = heap[j].value;
                heap[j].status = "ABSENT";
                heap[j].value = Integer.MAX_VALUE;
            } else {
                heap[1].status = "ABSENT";
                heap[j].status = "WANTED";
            }
            heap[j].lock.unlock();
            while (heap[i].status.equals("ABSENT")) {
//                Thread.onSpinWait();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            heap[i * 2].lock.lock();
            heap[i * 2 + 1].lock.lock();
            int min = min(i);
            int max = max(i);
            while ((i < fullLevel) && heap[i].value > heap[min].value) {
                int oldI = heap[i].value;
                heap[i].value = heap[min].value;
                heap[min].value = oldI;
                heap[i].lock.unlock();
                heap[max].lock.unlock();
                i = min;
                heap[i * 2].lock.lock();
                heap[i * 2 + 1].lock.lock();
                min = min(i);
                max = max(i);
            }
            heap[i].lock.unlock();
            heap[i * 2].lock.unlock();
            heap[i * 2 + 1].lock.unlock();
        }

    }

    public void checkHeap() {
        boolean test = true;
        int check = 1;
        for (int i = 1; i <= fullLevel - 1; i++) {
            if (heap[i * 2].status.equals("PRESENT") && heap[i].value > heap[i * 2].value) {
                check = i;
                test = false;
            }
            if (heap[i * 2 + 1].status.equals("PRESENT") && heap[i].value > heap[i * 2 + 1].value) {
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


    private int max(int i) {
        if (heap[i * 2].value > heap[i * 2 + 1].value) {
            return i * 2;
        } else {
            return i * 2 + 1;
        }
    }

    private int min(int i) {
        if (heap[i * 2].value <= heap[i * 2 + 1].value) {
            return i * 2;
        } else {
            return i * 2 + 1;
        }
    }

}