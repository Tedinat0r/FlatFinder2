package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.ThreadManager.ThreadManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Supervisor {


    private ArrayList<Integer> heap = new ArrayList<>();
    private HashMap<Integer, ThreadManager> threads = new HashMap<>();
    private HashMap<String, ArrayList<String>> resultPages = new HashMap<>();
    private HashMap<String, ArrayList<String>> listingPages = new HashMap<>();
    private  ArrayList<HashMap<String, String>> outputQueue = new ArrayList<>();
    private ArrayList<Integer> ioRequestQueue = new ArrayList<>();

    private void spawnThread(){};
    private void delegateWork(Integer parent, Integer child){}
    private String checkStatus(Integer id){return "";}
    private void performIO(ThreadManager worker){}

    public void fetchInput(ThreadManager worker){}
    public void fetchOutput(ThreadManager worker){}


    

}
