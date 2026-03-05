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
    private ArrayList<Integer> ioRequestQueue = new ArrayList<>();
    private ArrayList<Integer> writeQueue = new ArrayList<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private FSFactory fsFactory = new FSFactory();

    // Using return value is optional here



    private Optional<ThreadManager> spawnThread(String site, boolean passManager){
        if(!siteThreadMap.containsKey(site) && passManager){
            int heapNumber = heap.size() - 1;
            siteThreadMap.put(site, new ArrayList<>(heapNumber));
            ThreadManager manager = new ThreadManager(true, false, heapNumber, this.fsFactory, new ArrayList<Traverser>());
            manager.setCurrentInput(getInputMarkUp(site));
            threads.put(heapNumber, manager);
            heap.addLast(1);
            heap.addLast(0);
            return Optional.of(manager);
        }else{return Optional.empty();}

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
        Optional<ThreadManager> manager = spawnThread(site, true);
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
            ArrayList<Traverser> traversers = new ArrayList<>();
            CountDownLatch countDownLatch = manager.get().getCountDownLatch();
            for(int i = 0; i < 8; i++){
                traversers.add(new PPTraverser(registry, countDownLatch));
            }
            ThreadManager child = new ThreadManager(false, true, parentPosition + 1, this.fsFactory, traversers);
            threads.put(parentPosition + 1, child);
        }
    }
    private String getInputMarkUp(String site){
        String markup = resultPages.get(site).getFirst();
        resultPages.get(site).removeFirst();
        return markup;
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
