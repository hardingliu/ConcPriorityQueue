package ca.dal.cs6057.project;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        int nThreads = 10000;
        GlobalLock globalLock = new GlobalLock(65535);
        ConHeap conHeap = new ConHeap(65535);
        HuntHeap huntHeap = new HuntHeap(65535);


        ExecutorService executorG = Executors.newFixedThreadPool(nThreads);
        long startG = System.currentTimeMillis();
        for (int i = 1; i <= nThreads / 2; i++) {
            if (i <= nThreads / 2) {
                executorG.execute(globalLock.insert(new Random().nextInt()));
            } else {
                executorG.execute(globalLock.deleteMin());
            }
        }

        executorG.shutdown();
        // Wait until all threads are finish
        executorG.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        long durationG = System.currentTimeMillis() - startG;

        System.out.println(durationG);

        globalLock.checkHeap();


        ExecutorService executorC = Executors.newFixedThreadPool(nThreads);

        long startC = System.currentTimeMillis();
        for (int i = 1; i <= nThreads / 2; i++) {
            if (i <= nThreads / 2) {
                executorC.execute(conHeap.insert(new Random().nextInt()));
            } else {
                executorC.execute(conHeap.deleteMin());
            }
        }

        executorC.shutdown();
        // Wait until all threads are finish
        executorC.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        long durationC = System.currentTimeMillis() - startC;

        System.out.println(durationC);
        conHeap.checkHeap();


        ExecutorService executorH = Executors.newFixedThreadPool(nThreads);

        long startH = System.currentTimeMillis();

        for (int i = 1; i <= nThreads / 2; i++) {
            if (i <= nThreads / 2) {
                executorH.execute(huntHeap.insert(new Random().nextInt()));
            } else {
                executorH.execute(huntHeap.deleteMin());
            }
        }

        executorH.shutdown();
        // Wait until all threads are finish
        executorH.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        long durationH = System.currentTimeMillis() - startH;

        System.out.println(durationH);
        huntHeap.checkHeap();


    }

}
