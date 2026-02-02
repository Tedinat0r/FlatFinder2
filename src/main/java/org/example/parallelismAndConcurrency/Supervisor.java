package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.ThreadManager.ThreadManager;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private void spawnThread(){
        for(String site: resultPages.keySet()){
            if(!threads.containsKey(site)){
                int heapNumber = heap.size() - 1;
                siteThreadMap.put(site, heapNumber);
                ThreadManager manager = new ThreadManager(false, false, heapNumber);
                manager.setCurrentInput(getInputMarkUp(site));
                threads.put(heapNumber, manager);
                Runnable task = manager::work;
                executorService.execute(task);
            }
        }

    };
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
