package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RecursiveAction;

public class Traverser extends RecursiveAction {

    private CountDownLatch countDownLatch;
    private HashMap<String, FieldStrategy> registry;
    private HashMap<String, HashMap<String, String>> fieldPatterns;
    private String entryPattern;
    private String inputMarkup;

    public Traverser(HashMap<String, FieldStrategy> registry, HashMap<String, HashMap<String, String>> fieldPatterns, CountDownLatch countDownLatch){
        this.registry = registry;
        this.fieldPatterns = fieldPatterns;
        this.countDownLatch = countDownLatch;
    }
    public void setInputMarkup(String inputMarkup){this.inputMarkup = inputMarkup;}

    @Override
    public void compute(){}









}
