package org.example.parallelismAndConcurrency.ThreadManager;

import org.example.parallelismAndConcurrency.FSFactory;
import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;
import org.example.parallelismAndConcurrency.PPTraverser;
import org.example.parallelismAndConcurrency.RPTraverser;
import org.example.parallelismAndConcurrency.Traverser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ThreadManager {

    private ArrayList<Traverser> Traversers;
    private CountDownLatch countDownLatch;
    private int latchSize;
    private ForkJoinPool forkJoinPool;
    private String currentInput = "";
    private ArrayList<String> markup = new ArrayList<>();
    // Results per page - Address, fields ¬
    private HashMap<String, HashMap<String, String>> resultsCache = new HashMap<>();
    // Address -> Result page number, preview number
    private FSFactory fsFactory;
    public String site;
    public boolean hasParent = false;
    public boolean hasChild = false;
    public int treeID = -1;
    public int pageNumber = 0;

    public ThreadManager(boolean isParent, boolean isChild, int treeID, FSFactory fsFactory, ArrayList<Traverser> traversers) {
        this.hasChild = isParent ? true : false;
        this.hasParent = isChild ? true : false;
        this.treeID = treeID;
        this.fsFactory = fsFactory;
        this.Traversers = traversers;
    }

    public void setCache(HashMap<String, HashMap<String, String>> cache){
        this.resultsCache = cache;
    }
    public CountDownLatch getCountDownLatch(){
        return this.countDownLatch;
    }
    public boolean work() throws InterruptedException {

        resultsCache.clear();

        int traverserRef = 0;
        for(String html: markup){
            if(traverserRef < Traversers.size()){
                Traverser traverser = Traversers.get(traverserRef);
                traverser.setInputMarkup(html);
                forkJoinPool.invoke(traverser);
                traverserRef++;
            }
        }

        // Case of first run with no children, must wait for other RP traversers before I/O then spawn a child

        return true;
    }

    public HashMap<String, HashMap<String, String>> cacheResults(){
        ArrayList<String> keys = new ArrayList<>();
        for(Traverser traverser: Traversers){
            if(traverser instanceof RPTraverser){
                for(int i = 1; i <= ((RPTraverser) traverser).resultCount; i++){
                    ((RPTraverser) traverser).getFields(i).forEach((k, v) -> {if(v == null)
                        if(k.equals("address")){
                            resultsCache.put(k, new HashMap<>());
                            keys.add(k);
                        }resultsCache.get(k).put(v, v);
                    });
                }
            }
        }
        return this.resultsCache;
    }


    private void distributeMarkup(){}

    public void addTraverser(){};
    public  int passLatchStatus(){return 1;};
    public void abandonOperation(){};
    public void setCurrentInput(String input){
        currentInput = input;
    }
    public void setMarkup(ArrayList<String> markup){ this.markup = markup; }
}
