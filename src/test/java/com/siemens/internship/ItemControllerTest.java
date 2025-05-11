package com.siemens.internship;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.internship.controller.ItemController;
import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    /**
     * Set up a standalone mock MVC environment before each test.
     * This avoids loading the entire Spring context.
     */
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    /**
     * Test creating an item with valid data.
     * The mocked service should return the item, and we expect a 201 CREATED status.
     */
    @Test
    public void testCreateItem() throws Exception {
        Item item = new Item();
        item.setName("Test Item");
        item.setEmail("test@example.com");

        when(itemService.save(any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    /**
     * Test creating an item with an invalid email.
     * We expect a 400 BAD REQUEST and a specific error message in the response body.
     */
    @Test
    public void testCreateItemInvalidEmail() throws Exception {
        Item item = new Item();
        item.setName("Invalid Item");
        item.setEmail("invalid-email");

        // No mocking necessary, validation should trigger before service call
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email invalid: invalid-email"));
    }

    /**
     * Test retrieving an item by its ID.
     * When the item exists, the response should be 200 OK and contain the item's ID.
     */
    @Test
    public void testGetItemById() throws Exception {
        Item item = new Item();
        item.setId(1L);

        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
