package org.example;

//JUnit test for the client code
//import org.junit.Test;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.rmi.Naming;
//import static org.junit.Assert.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

public class InvertedIndexClientTest {
 
 @Test
 public void testInvertedIndex() throws Exception {
     // Locate the registry and get the stub of the service
	 String endpoint = "rmi://127.0.0.1:8099/InvertedIndexService";

	
     org.example.InvertedIndexService service = (InvertedIndexService) 
     						Naming.lookup(endpoint);
     
     // Prepare a sample text
     String text = "Hello world\nHello Java\nHello RMI";
     // Invoke the service and get the inverted index
     Map<String, Integer> index = service.getInvertedIndex(text);
     // Check the results
     assertEquals(4, index.size());
     assertEquals(3, (int) index.get("Hello"));
     assertEquals(1, (int) index.get("world"));
     assertEquals(1, (int) index.get("Java"));
     assertEquals(1, (int) index.get("RMI"));
 }
}
