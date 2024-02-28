package org.example;

import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
//RMI service implementation
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class InvertedIndexServiceImpl extends UnicastRemoteObject implements InvertedIndexService {
 // A ForkJoinPool to handle concurrent tasks
 private ForkJoinPool pool;

 public InvertedIndexServiceImpl() throws RemoteException {
     super();
     // Initialize the pool with the number of available processors
     pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
 }

 @Override
 public Map<String, Integer> getInvertedIndex(String text) throws RemoteException {
     // Split the text into lines
     String[] lines = text.split("\n");
     // Create a map to store the results
     Map<String, Integer> index = new HashMap<>();
     // For each line, submit a task to the pool that computes the inverted index for that line
     for (String line : lines) {
         pool.submit(() -> {
             // Split the line into words and count their occurrences
             String[] words = line.split("\\s+");
             Map<String, Integer> lineIndex = new HashMap<>();
             for (String word : words) {
                 lineIndex.put(word, lineIndex.getOrDefault(word, 0) + 1);
             }
             // Merge the line index with the global index
             synchronized (index) {
                 for (Map.Entry<String, Integer> entry : lineIndex.entrySet()) {
                     index.put(entry.getKey(), index.getOrDefault(entry.getKey(), 0) + entry.getValue());
                 }
             }
         });
     }
     // Wait for all tasks to finish
     pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
     // Return the inverted index
     return index;
 }
 
 public static void main(String args[]) {
     try {

         
         //creating an instance of the interface implementation
         InvertedIndexServiceImpl server = new InvertedIndexServiceImpl();

         // this fixed: java.rmi.ConnectException: Connection refused to host: 127.0.0.1;
         LocateRegistry.createRegistry(8099);

         Naming.rebind("rmi://127.0.0.1:8099/InvertedIndexService", server);
         System.out.println("InvertedIndexService ready...");
     } catch (Exception e) {
         e.printStackTrace();
         System.exit(1);
     }
 }
 
 
}
