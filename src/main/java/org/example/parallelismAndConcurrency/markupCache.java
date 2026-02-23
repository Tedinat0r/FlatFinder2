package org.example.parallelismAndConcurrency;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class markupCache {

    //Site -> Result Page -> Results Markup
    HashMap<String, HashMap<
            Integer, HashMap <Integer,
            //Result -> Preview markup, Link, Page markup
            ArrayList<String>>>> data;

    public String getResultPreview(String site, int page, int resultNumber){
       return this.data.get(site).get(page).get(resultNumber).getFirst();
    }
    public String getResultPage(String site, int page, int resultNumber){
        ArrayList<String> result = this.data.get(site).get(page).get(resultNumber);
        if(result.getLast().isEmpty()){

        }
        return "";
    }
}
