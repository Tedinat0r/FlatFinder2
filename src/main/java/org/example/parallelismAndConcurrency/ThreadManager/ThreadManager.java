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
    private ForkJoinPool forkJoinPool;
    private String currentInput = "";
    private ArrayList<String> markup = new ArrayList<>();
    private HashMap<String, HashMap<String, String>> resultsCache = new HashMap<>();
    private FSFactory fsFactory;
    private String site;
    public boolean hasParent = false;
    public boolean hasChild = false;
    public int treeID = -1;

    public ThreadManager(boolean isParent, boolean isChild, int treeID, FSFactory fsFactory) {
        this.hasChild = isParent ? true : false;
        this.hasParent = isChild ? true : false;
        this.treeID = treeID;
        this.fsFactory = fsFactory;
    }

    public boolean work(){
        int traverserRef = 0;
        for(String html: markup){
            if(traverserRef < Traversers.size()){
                Traverser traverser = Traversers.get(traverserRef);
                traverser.setInputMarkup(html);
                forkJoinPool.invoke(traverser);
                traverserRef++;
            }
        }
        return true;
    }

    public void spawnChild(){
        for(Traverser traverser: Traversers){
            if(traverser instanceof RPTraverser){
                /*
                * Check each result in hashmap of RP
                * If any are "", then pass onto PP
                *
                */
                for(int i = 1; i <= ((RPTraverser) traverser).resultCount; i++){
                    HashMap<String, FieldStrategy> strategies = new HashMap<>();
                    ((RPTraverser) traverser).getFields(i).forEach((k, v) -> {if(v == null){
                                strategies.put(k, this.fsFactory.getFieldStrategy(site, k));
                            }else{
                                if(k.equals("address")){
                                    resultsCache.put(k, new HashMap<>());
                                }
                                resultsCache.get(k).put(v, v);
                            }
                        }
                    );

                }

            }
        }
    }

    public void addTraverser(){};
    public  int passLatchStatus(){return 1;};
    public void abandonOperation(){};
    public void setCurrentInput(String input){
        currentInput = input;
    }
}
