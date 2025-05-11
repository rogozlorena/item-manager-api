package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // Executor for executing tasks in parallel
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    // Regex pattern to validate a valid email
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    // Method to validate an email
    public boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Returns all items
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    // Finds an item by its ID
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    // Saves an item after validating its email
    public Item save(Item item) {
        if (!validateEmail(item.getEmail())) {
            // If the email is invalid, throw an exception
            throw new IllegalArgumentException("Invalid email: " + item.getEmail());
        }
        return itemRepository.save(item);
    }

    // Deletes an item by its ID
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Asynchronous method that processes each item in parallel
     * - Uses CompletableFuture to do so
     * - Waits for all tasks to finish before returning the result
     *
     * @return CompletableFuture containing the list of successfully processed items
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        // Retrieve all item IDs from the database
        List<Long> itemIds = itemRepository.findAllIds();

        // Create a stream of asynchronous tasks for each item
        List<CompletableFuture<Item>> futures = itemIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> {
                    try {
                        // Simulate processing of an item with a short delay
                        Thread.sleep(100);
                        Optional<Item> optionalItem = itemRepository.findById(id);

                        // If the item exists, process it and update its status
                        if (optionalItem.isPresent()) {
                            Item item = optionalItem.get();
                            item.setStatus("PROCESSED");
                            return itemRepository.save(item);  // Save the processed item
                        } else {
                            return null;  // If the item doesn't exist, return null
                        }
                    } catch (Exception e) {
                        // If any error occurs, print the stack trace
                        e.printStackTrace();
                        return null;  // Return null in case of error
                    }
                }, executor))  // Use the executor to execute tasks in parallel
                .collect(Collectors.toList());

        // Wait for all asynchronous tasks to complete
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // After all tasks are done, collect the results of all completed tasks (non-null ones)
        return allDone.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)  // Get the result of each future
                        .filter(Objects::nonNull)      // Filter out any null results (failed items)
                        .collect(Collectors.toList())  // Collect the successfully processed items
        );
    }
}
