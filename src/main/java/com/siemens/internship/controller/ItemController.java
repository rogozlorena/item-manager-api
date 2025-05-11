package com.siemens.internship.controller;

import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/items") // Base path for all item-related endpoints
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * GET /api/items
     * Retrieve all items from the database.
     */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.findAll());
    }

    /**
     * POST /api/items
     * Create a new item. Validates the request body.
     * If validation fails or email is invalid, return 400 Bad Request.
     */
    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            // Return validation error messages
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        try {
            itemService.save(item);
            // Return 201 Created on success
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (IllegalArgumentException e) {
            // Return 400 Bad Request for invalid email format
            return ResponseEntity.badRequest().body("Email invalid: " + item.getEmail());
        }
    }

    /**
     * GET /api/items/{id}
     * Retrieve an item by its ID.
     * Return 404 Not Found if item does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> ResponseEntity.ok(item))               // Return 200 OK
                .orElse(ResponseEntity.notFound().build());         // Return 404 if not found
    }

    /**
     * PUT /api/items/{id}
     * Update an existing item by ID.
     * Validates input and ensures item exists before updating.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            // Return validation errors
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            item.setId(id); // Ensure we keep the original ID
            return ResponseEntity.ok(itemService.save(item)); // Return updated item
        } else {
            return ResponseEntity.notFound().build(); // Item not found
        }
    }

    /**
     * DELETE /api/items/{id}
     * Delete an item by ID.
     * Return 204 No Content if successful, 404 if item doesn't exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemService.findById(id).isPresent()) {
            itemService.deleteById(id);
            return ResponseEntity.noContent().build(); // Successfully deleted
        } else {
            return ResponseEntity.notFound().build(); // Item not found
        }
    }

    /**
     * GET /api/items/process
     * Asynchronously processes all items by updating their status.
     * Returns a list of successfully processed items.
     */
    @GetMapping("/process")
    public ResponseEntity<List<Item>> processItems() {
        try {
            List<Item> processedItems = itemService.processItemsAsync().get();
            return ResponseEntity.ok(processedItems); // Return processed items
        } catch (InterruptedException | ExecutionException e) {
            // In case of failure, return 500 with an empty list
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }
}
