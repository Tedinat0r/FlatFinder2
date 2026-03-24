package org.example;

import org.example.parallelismAndConcurrency.FSFactory;
import org.example.parallelismAndConcurrency.FieldStrategy.ConcreteFS;
import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;
import org.example.parallelismAndConcurrency.Supervisor;
import org.example.parallelismAndConcurrency.markupCache;

import java.util.ArrayList;
import java.util.HashMap;



public class Main {


    public static HashMap<Integer, HashMap<Integer, ArrayList<String>>> generateData() {
        HashMap<Integer, HashMap<Integer, ArrayList<String>>> outerMap = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            HashMap<Integer, ArrayList<String>> innerMap = new HashMap<>();

            for (int j = 1; j <= 3; j++) {
                ArrayList<String> content = new ArrayList<>();

                content.add("<p>Item " + i + "-" + j + " description</p>");
                content.add("https://example.com/item/" + i + "/" + j);
                content.add("<div>Footer for item " + i + "-" + j + "</div>");

                innerMap.put(j, content);
            }

            outerMap.put(i, innerMap);
        }

        return outerMap;
    }


    public static void main(String[] args) throws InterruptedException {
        ArrayList<String> sites = new ArrayList<>();
        sites.add("Rightmove");
        sites.add("Zoopla");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("address");
        fields.add("pcm");

        HashMap<String, HashMap<
                Integer, HashMap<Integer,
                //Result -> Preview markup, Link, Page markup
                ArrayList<String>>>> data = new HashMap<>();

        HashMap<Integer, String> dummyPages = new HashMap<>();
        dummyPages.put(1, "");
        dummyPages.put(2, "");
        HashMap<String, HashMap<Integer, String>> pages = new HashMap<>();

        pages.put("Rightmove", dummyPages);
        pages.put("Zoopla", dummyPages);


        data.put("Rightmove", generateData());
        data.put("Zoopla", generateData());
        markupCache cache = new markupCache(pages, data);

        HashMap<String, HashMap<String, FieldStrategy>> fieldStrategies = new HashMap<>();
        HashMap<String, FieldStrategy> fieldStrategy = new HashMap<>();
        fieldStrategy.put("address", new ConcreteFS());
        fieldStrategy.put("pcm", new ConcreteFS());
        fieldStrategies.put("Rightmove", fieldStrategy);
        fieldStrategies.put("Zoopla", fieldStrategy);
        FSFactory fsFactory = new FSFactory(fieldStrategies);

        Supervisor supervisor = new Supervisor(sites, cache, fields, fsFactory);

        supervisor.scrape();
        // Dummy data

    }
}