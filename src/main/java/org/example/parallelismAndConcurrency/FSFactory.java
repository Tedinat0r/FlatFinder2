package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FSFactory {
    HashMap<String, HashMap<String, FieldStrategy>> registry;

    public void addStrategy(String site, String field, FieldStrategy strategy) {
        if(!registry.containsKey(site)) {
            registry.put(site, new HashMap<>());
        }
        registry.get(site).put(field, strategy);
    }
    public FieldStrategy getFieldStrategy(String site, String field) {
        HashMap<String, FieldStrategy> map = registry.get(site);
        return map.get(field);
    }
}
