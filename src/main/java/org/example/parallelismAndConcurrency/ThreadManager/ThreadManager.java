package org.example.parallelismAndConcurrency.ThreadManager;

import org.example.parallelismAndConcurrency.Traverser;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;

public abstract class ThreadManager {

    ArrayList<Traverser> Traversers;
    CountDownLatch countDownLatch;
    ForkJoinPool forkJoinPool;

    public boolean hasParent = false;
    public boolean hasChild = false;
    public int treeID = -1;
    public abstract void addTraverser();
    public abstract int passLatchStatus();
    public abstract void abandonOperation();

}
