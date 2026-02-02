package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Traverser {

    private CountDownLatch countDownLatch;
    private HashMap<String, FieldStrategy> registry;
    private HashMap<String, HashMap<String, String>> fieldPatterns;
    private String inputMarkup;
    public HashMap<String, Boolean> fieldCompletionMap = new HashMap<String, Boolean>();

    public Traverser(HashMap<String, FieldStrategy> registry, HashMap<String, HashMap<String, String>> fieldPatterns, CountDownLatch countDownLatch){
        this.registry = registry;
        this.fieldPatterns = fieldPatterns;
        this.countDownLatch = countDownLatch;
    }







}
