package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class RPTraverser extends Traverser{
    private ConcurrentHashMap<Integer, String> results = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> resultFields = new ConcurrentHashMap<>();

    private void addResult(int id, String markup){
        results.put(id, markup);
    }



    public RPTraverser(HashMap<String, FieldStrategy> registry, HashMap<String, HashMap<String, String>> fieldPatterns, CountDownLatch countDownLatch) {
        super(registry, fieldPatterns, countDownLatch);
    }


}
