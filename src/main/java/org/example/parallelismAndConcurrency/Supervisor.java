package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.ThreadManager.ThreadManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Supervisor {

    private ArrayList<Integer> heap = new ArrayList<>();
    private HashMap<Integer, ThreadManager> threads = new HashMap<>();
    private HashMap<String, Integer> siteThreadMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> resultPages = new HashMap<>();
    private HashMap<String, ArrayList<String>> listingPages = new HashMap<>();
    private  ArrayList<HashMap<String, String>> outputQueue = new ArrayList<>();
    private ArrayList<Integer> ioRequestQueue = new ArrayList<>();
    private ArrayList<Integer> writeQueue = new ArrayList<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private FSFactory fsFactory = new FSFactory();

    // Using return value is optional here



    private Optional<ThreadManager> spawnThread(String site, boolean passManager){
        if(!siteThreadMap.containsKey(site) && passManager){
            int heapNumber = heap.size() - 1;
            siteThreadMap.put(site, heapNumber);
            ThreadManager manager = new ThreadManager(true, false, heapNumber, this.fsFactory);
            manager.setCurrentInput(getInputMarkUp(site));
            threads.put(heapNumber, manager);
            heap.addLast(1);
            heap.addLast(0);
            return Optional.of(manager);
        }else{return Optional.empty();}

    };
    private void createChild(int parentPosition){
        ThreadManager parent = threads.get(parentPosition);
        String site = parent.site;
        Optional<ThreadManager> manager = spawnThread(site, true);
        if(manager.isPresent()){
            int childPosition = heap.size() - 1;

        }
    }
    private String getInputMarkUp(String site){
        String markup = resultPages.get(site).getFirst();
        resultPages.get(site).removeFirst();
        return markup;
    }

    private void delegateWork(Integer parent, Integer child){}
    private String checkStatus(Integer id){return "";}
    private void performIO(ThreadManager worker){}

    public void fetchInput(ThreadManager worker){}
    public void fetchOutput(ThreadManager worker){}


    

}
