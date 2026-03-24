package org.example.parallelismAndConcurrency.FieldStrategy;

import java.util.HashMap;

public class ConcreteFS implements FieldStrategy{
    protected final String fieldPattern;
    protected final String entryPattern;
    public ConcreteFS(String fieldPattern, String entryPattern) {
        this.fieldPattern = fieldPattern;
        this.entryPattern = entryPattern;
    }
    public String extractField(String markup) {
        return "";
    }
}
