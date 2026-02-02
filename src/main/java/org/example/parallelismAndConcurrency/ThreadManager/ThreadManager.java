package org.example.parallelismAndConcurrency.ThreadManager;

import org.example.parallelismAndConcurrency.Traverser;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;

public class ThreadManager {

    ArrayList<Traverser> Traversers;
    CountDownLatch countDownLatch;
    ForkJoinPool forkJoinPool;
    String currentInput = "";
    ArrayList<String> markup = new ArrayList<>();
    public boolean hasParent = false;
    public boolean hasChild = false;
    public int treeID = -1;

    public ThreadManager(boolean isParent, boolean isChild, int treeID){
        this.hasChild = isParent ? true : false;
        this.hasParent = isChild ? true : false;
        this.treeID = treeID;
    }

    public boolean work(){
        if(!hasParent){
            int traverserRef = 0;
            for(String html: markup){
                if(traverserRef < Traversers.size()){
                    Traverser traverser = Traversers.get(traverserRef);

                }
            }
        }
        return true;
    }
    public void addTraverser(){};
    public  int passLatchStatus(){return 1;};
    public void abandonOperation(){};
    public void setCurrentInput(String input){
        currentInput = input;
    }

}
