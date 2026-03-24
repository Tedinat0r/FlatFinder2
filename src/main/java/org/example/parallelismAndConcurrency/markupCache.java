package org.example.parallelismAndConcurrency;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class markupCache {

    HashMap<String, HashMap<Integer, String>> resultPages;
    //Site -> Result Page -> Results Markup
    HashMap<String, HashMap<
            Integer, HashMap <Integer,
            //Result -> Preview markup, Link, Page markup
            ArrayList<String>>>> propetyData;

    public markupCache(HashMap<String, HashMap<Integer, String>> resultPages, HashMap<String, HashMap<Integer, HashMap <Integer, ArrayList<String>>>> propertyData) {
        this.propetyData = propertyData;
        this.resultPages = resultPages;
    }
    public String getResultPreview(String site, int page, int resultNumber){
       return this.propetyData.get(site).get(page).get(resultNumber).getFirst();
    }
    public String getResultPage(String site, int page, int resultNumber){
        ArrayList<String> result = this.propetyData.get(site).get(page).get(resultNumber);
        if(result.getLast().isEmpty()){

        }
        return "";
    }
    public void removePage(String site, int page){
        this.propetyData.get(site).remove(page);
    }
    public HashMap<Integer, HashMap<Integer, ArrayList<String>>> getPages(String site){
        return propetyData.get(site);
    }
    public HashMap<Integer, String> getResults(String site){
        return resultPages.get(site);
    };

}
