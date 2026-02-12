package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class RPTraverser extends Traverser{
    private ConcurrentHashMap<Integer, String> results = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> resultFields = new ConcurrentHashMap<>();
    public int resultCount = 0;

    private void addResult(int id, String markup){
        results.put(id, markup);
    }
    public ArrayList<String> getFields(int id){
        ArrayList<String> fields = new ArrayList<>();
        ConcurrentHashMap<String, String> resultMap = resultFields.get(id);
        resultMap.forEach((k, v) -> fields.add(v));
        return fields;
    };



    public RPTraverser(HashMap<String, FieldStrategy> registry, HashMap<String, HashMap<String, String>> fieldPatterns, CountDownLatch countDownLatch) {
        super(registry, fieldPatterns, countDownLatch);
    }


    @Override
    public void compute(){
        ArrayList<String> entries = new ArrayList<>();
    }


}
