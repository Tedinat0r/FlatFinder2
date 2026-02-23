package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RecursiveAction;

public class Traverser extends RecursiveAction {

    protected CountDownLatch countDownLatch;
    protected HashMap<String, FieldStrategy> registry;
    protected String entryPattern;
    protected String inputMarkup;

    public Traverser(HashMap<String, FieldStrategy> registry, CountDownLatch countDownLatch){
        this.registry = registry;
        this.countDownLatch = countDownLatch;
    }
    public void setInputMarkup(String inputMarkup){this.inputMarkup = inputMarkup;}

    @Override
    public void compute(){}









}
