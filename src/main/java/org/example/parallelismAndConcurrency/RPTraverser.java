package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class RPTraverser extends Traverser{
    private ConcurrentHashMap<Integer, String> results = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> resultFields = new ConcurrentHashMap<>();
    public int pageNumber = -1;
    public int resultCount = 0;

    private void addResult(int id, String markup){
        results.put(id, markup);
    }
    public ConcurrentHashMap<String, String> getFields(int id){
        return resultFields.get(id);
    };



    public RPTraverser(HashMap<String, FieldStrategy> registry, CountDownLatch countDownLatch) {
        super(registry, countDownLatch);
    }


    @Override
    public void compute(){
        ArrayList<String> entries = new ArrayList<>();

        //Dividing result page into the respective results
        String[] results = this.inputMarkup.split(this.entryPattern);
        for(int i = 0; i < results.length; i++){
            addResult(i+1, results[i]);
            for(String field : registry.keySet()){
                String fieldData = registry.get(field).extractField(results[i]);
                this.resultFields.get(i+1).put(field, fieldData);
            }
        }


        this.countDownLatch.countDown();
    }


}
