package org.example.parallelismAndConcurrency;

import org.example.parallelismAndConcurrency.FieldStrategy.FieldStrategy;
import org.example.parallelismAndConcurrency.ThreadManager.ThreadManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

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
    private FSFactory fsFactory;

    public Supervisor(ArrayList<String> sites, markupCache cache, ArrayList<String> fieldNames, FSFactory fsFactory) {
        this.cache = cache;
        this.fsFactory = fsFactory;
        HashMap<String, HashMap<String, FieldStrategy>> fieldStrategies = new HashMap<>();
        sites.forEach(site -> {
            System.out.println(site);
            HashMap<Integer, HashMap<Integer, ArrayList<String>>> sitePages = this.cache.getPages(site);
            HashMap<String, FieldStrategy> siteStrategies = new HashMap<>();
            fieldNames.forEach(fieldName -> {siteStrategies.put(fieldName,
                    this.fsFactory.getFieldStrategy(site, fieldName));});
            fieldStrategies.put(site, siteStrategies);
            ThreadManager manager = this.spawnThread(site, true, fieldStrategies.get(site)).get();
            populatePages(site);
            feedMarkup(manager, sitePages.size() / 4);
        });


    }
    private Optional<ThreadManager> spawnThread(String site, boolean passManager, HashMap<String, FieldStrategy> fieldStrategies){
        int heapNumber = !heap.isEmpty() ? heap.size() : 0;
        ThreadManager manager = new ThreadManager(true, false, heapNumber, fieldStrategies, site);
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
        for(int i = 0; i < keys.size(); i++){
            String key = keys.get(i);
            if(!results.get(key).containsValue("")){
                HashMap<String, String> result = results.get(key);
                this.outputQueue.add(result);
                results.remove(key);
            }
        }
    }

    private void populatePages(String site){
        ArrayList<String> sitePage = new ArrayList<>();
        this.cache.getResults(site).forEach((key, value) -> {
            sitePage.add(value);
            System.out.println(value);
        });

        this.resultPages.put(site, sitePage);
    }
    private void createChild(int parentPosition){
        ThreadManager parent = threads.get(parentPosition);
        String site = parent.site;
        int newLatchCount = this.siteThreadMap.get(site).size() + 1;
        Optional<ThreadManager> manager = spawnThread(site, true, parent.getStrategies());
        if(manager.isPresent()){
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
            ThreadManager child = new ThreadManager(false, true, parentPosition + 1, registry, parent.site);
            threads.put(parentPosition + 1, child);
            heap.set(parentPosition + 1, 1 );
            manager.get().setCountDownLatch(countDownLatch);
            child.setCountDownLatch(countDownLatch);
        }
    }
    private String getInputMarkUp(String site){
        return resultPages.get(site).removeFirst();
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

    public void scrape() throws InterruptedException {
        ArrayList<Runnable> tasks = new ArrayList<>();
        for(int i = 0; i < heap.size(); i++) {
            if (heap.get(i) != 0) {
                ThreadManager thread = this.threads.get(i);
                thread.setCountDownLatch(new CountDownLatch(thread.getMarkUpSize()));
                Runnable scrapeTask = () -> {
                    try {
                        thread.work();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                tasks.add(scrapeTask);
                executorService.execute(scrapeTask);
            }
        }
        executorService.close();
        executorService.awaitTermination(100, TimeUnit.NANOSECONDS);

        for(int i=0; i < this.heap.size(); i++){
            if(i < this.heap.size() - 1 && i % 2 == 0){
                System.out.println(i);
                if(heap.get(i) != 0 && heap.get(i+1) == 0){
                    createChild(i);
                }else{

                    ThreadManager parent = threads.get(i);
                    ThreadManager child = threads.get(i+1);
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
