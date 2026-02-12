package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class PPTraverser extends Traverser  {
    public PPTraverser(HashMap<String, FieldStrategy> registry, HashMap<String, HashMap<String, String>> fieldPatterns, CountDownLatch countDownLatch) {
        super(registry, fieldPatterns, countDownLatch);
    }

    @Override
    public void compute(){}
}
