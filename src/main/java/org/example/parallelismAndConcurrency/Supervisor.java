package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;
import org.example.parallelismAndConcurrency.ThreadManager.ThreadManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Supervisor {

    private markupCache cache;
    private ArrayList<Integer> heap = new ArrayList<>();
    private HashMap<Integer, ThreadManager> threads = new HashMap<>();
    private HashMap<String, ArrayList<Integer>> siteThreadMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> resultPages = new HashMap<>();
    private HashMap<String, HashMap<Integer, HashMap<Integer, String>>> listingPages = new HashMap<>();
    // Address -> Result page number, preview number
    private HashMap<String, ArrayList<Integer>> resultToPageMappings = new HashMap<>();
    private ArrayList<HashMap<String, String>> outputQueue = new ArrayList<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private FSFactory fsFactory = new FSFactory();

    public Supervisor(ArrayList<String> sites, markupCache cache, ArrayList<String> fieldNames){
        this.cache = cache;
        HashMap<String, HashMap<String, FieldStrategy>> fieldStrategies = new HashMap<>();
        sites.forEach(site -> {
            HashMap<Integer, HashMap<Integer, ArrayList<String>>> sitePages = this.cache.getPages(site);
            HashMap<String, FieldStrategy> siteStrategies = new HashMap<>();
            fieldNames.forEach(fieldName -> {siteStrategies.put(fieldName,
                    this.fsFactory.getFieldStrategy(site, fieldName));});
            fieldStrategies.put(site, siteStrategies);
            ThreadManager manager = this.spawnThread(site, true, fieldStrategies.get(site)).get();
            feedMarkup(manager, sitePages.size() / 4);
        });


    }
    private Optional<ThreadManager> spawnThread(String site, boolean passManager, HashMap<String, FieldStrategy> fieldStrategies){
        int heapNumber = heap.size() - 1;
        ThreadManager manager = new ThreadManager(true, false, heapNumber, fieldStrategies);
        threads.put(heapNumber, manager);
        heap.addLast(1);
        heap.addLast(0);
        if(!siteThreadMap.containsKey(site)){
            siteThreadMap.put(site, new ArrayList<>(heapNumber));
        }else{
            siteThreadMap.get(site).add(heapNumber);
        }
        return passManager ? Optional.of(manager) : Optional.empty();
    };

    private void filterResults(HashMap<String, HashMap<String, String>> results){
        ArrayList<String> keys = new ArrayList<>(results.keySet());
        ArrayList<String> evictees = new ArrayList<>();
        for(int i = 0; i < keys.size(); i++){
            String key = keys.get(i);
            if(!results.get(key).containsValue("")){
                evictees.add(key);
                this.outputQueue.add(results.get(key));
            }
        }
        evictees.forEach(results::remove);
    }

    private void createChild(int parentPosition){
        ThreadManager parent = threads.get(parentPosition);
        String site = parent.site;
        int newLatchCount = this.siteThreadMap.get(site).size() + 1;
        Optional<ThreadManager> manager = spawnThread(site, true, parent.getStrategies());
        if(manager.isPresent()){
            int childPosition = heap.size() - 1;
            HashMap<String, HashMap<String, String>> cachedResults = parent.cacheResults();
            this.filterResults(cachedResults);
            HashMap<String, FieldStrategy> registry = new HashMap<>();
            HashMap<String, String> fields = cachedResults.get(cachedResults.keySet().stream().toList().getFirst());
            cachedResults.keySet().forEach(field -> {
                registry.put(field,
                    this.fsFactory.getFieldStrategy(field, fields.get(field))
                );
            });
            // Arbitrary number at the moment, but will correspond to average results per page on a given site
            CountDownLatch countDownLatch = new CountDownLatch(newLatchCount);
            ThreadManager child = new ThreadManager(false, true, parentPosition + 1, registry);
            threads.put(parentPosition + 1, child);
            manager.get().setCountDownLatch(countDownLatch);
            child.setCountDownLatch(countDownLatch);
        }
    }
    private String getInputMarkUp(String site){
        String markup = resultPages.get(site).getFirst();
        resultPages.get(site).removeFirst();
        return markup;
    }

    private void feedMarkup(ThreadManager manager, int amount){
        ArrayList<String> markup = new ArrayList<>();
        for(int i = 0; i < amount; i++){
            markup.add(this.getInputMarkUp(manager.site));
        }
        manager.setMarkup(markup);
    }

    private void feedChild(ThreadManager parent, ThreadManager child, HashMap<String, HashMap<String, String>> results){
        HashMap<String, Integer> resultsEnum = parent.getResultNumbers();
        int pageNumber = parent.pageNumber;
        String site = parent.site;
        ArrayList<String> incompleteResults = new ArrayList<>();
        results.keySet().forEach(key -> {
            int resultNum = resultsEnum.get(key);
            incompleteResults.add(this.cache.getResultPreview(site, pageNumber, resultNum));
        });
        child.setMarkup(incompleteResults);
    }

    public void scrape(){
        ArrayList<Runnable> tasks = new ArrayList<>();
        for(Integer position: this.heap){
            Runnable scrapeTask = ()->{
                try {
                    this.threads.get(position).work();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            tasks.add(scrapeTask);
            executorService.execute(scrapeTask);
        }

        executorService.shutdown();

        for(int i=0; i < this.heap.size(); i++){
            if(i < this.heap.size() - 1){
                if(heap.get(i+1) != heap.get(i) + 1 && !threads.get(heap.get(i)).hasParent){
                    createChild(heap.get(i));
                }else{

                    ThreadManager parent = threads.get(heap.get(i));
                    ThreadManager child = threads.get(heap.get(i+1));
                    HashMap<String, HashMap<String, String>> parentCachedResults = parent.cacheResults();
                    HashMap<String, HashMap<String, String>> childCachedResults = child.cacheResults();
                    filterResults(childCachedResults);
                    ArrayList<String> incompleteMarkup = new ArrayList<>();
                    String managerSite = parent.site;

                    parentCachedResults.keySet().forEach(
                        (result)->{
                            Integer pageNum = resultToPageMappings.get(result).getFirst();
                            Integer resultNum = resultToPageMappings.get(result).getLast();
                            incompleteMarkup.add(this.cache.getResultPage(managerSite, pageNum, resultNum));
                        }
                    );

                    child.setMarkup(incompleteMarkup);
                    cache.removePage(parent.site, parent.pageNumber);
                    for(int j=0; j < cache.getPages(parent.site).size(); j++){
                        if(cache.getPages(parent.site).get(j) != null){
                            ArrayList<String> newMarkup = new ArrayList<>();
                            cache.getPages(parent.site).get(j).forEach((key, value)->{
                                newMarkup.add(value.getFirst());
                            });
                            break;
                        }
                    }

                }
            }
        }
    }

    private void delegateWork(Integer parent, Integer child){}
    private String checkStatus(Integer id){return "";}
    private void performIO(ThreadManager worker){}

    public void fetchInput(ThreadManager worker){}
    public void fetchOutput(ThreadManager worker){}
}
