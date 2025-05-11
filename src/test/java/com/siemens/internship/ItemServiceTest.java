package com.siemens.internship;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    /**
     * Initialize mocks before each test to prepare for dependency injection.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test saving an item through the service.
     * The test verifies that the repository's save method is called once
     * and that the returned item is not null and matches the expected values.
     */
    @Test
    public void testSaveItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setEmail("test@example.com");

        // Define behavior of mock repository
        when(itemRepository.save(item)).thenReturn(item);

        // Call the service method
        Item savedItem = itemService.save(item);

        // Validate results
        assertNotNull(savedItem);
        assertEquals("Test Item", savedItem.getName());
        verify(itemRepository, times(1)).save(item);  // Ensure save was called exactly once
    }

    /**
     * Test finding an item by its ID through the service.
     * The test ensures that the returned Optional contains the correct item.
     */
    @Test
    public void testFindById() {
        Item item = new Item();
        item.setId(1L);

        // Mock the repository response
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Call the service method
        Optional<Item> foundItem = itemService.findById(1L);

        // Assertions to verify behavior
        assertTrue(foundItem.isPresent());
        assertEquals(1L, foundItem.get().getId());
    }
}
