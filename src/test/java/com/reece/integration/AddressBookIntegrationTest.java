package com.reece.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.reece.model.Contact;
import com.reece.model.UpdateContact;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressBookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Contact contact1;
    private Contact contact2;

    @BeforeEach
    public void setup() throws Exception {
        contact1 = new Contact("Saikiran", "0001112224");
        contact2 = new Contact("Sai", "0001112226");

        // Create address book
        mockMvc.perform(post("/api/v1/users/user1/addressbooks/Friends"));

        // Add contacts
        mockMvc.perform(post("/api/v1/users/user1/addressbooks/Friends/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contact1)));

        mockMvc.perform(post("/api/v1/users/user1/addressbooks/Friends/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contact2)));
    }

    @Test
    public void testGetAllContacts() throws Exception {
        mockMvc.perform(get("/api/v1/users/user1/addressbooks/Friends/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All contacts retrieved under addressbook: Friends"))
                .andExpect(jsonPath("$.data[0].name").value("Saikiran"))
                .andExpect(jsonPath("$.data[0].phone").value("0001112224"))
                .andExpect(jsonPath("$.data[1].name").value("Saikiran RM"))
                .andExpect(jsonPath("$.data[1].phone").value("0001112224"));
    }

    @Test
    public void testUpdateContact() throws Exception {
        Contact updatedContact = new Contact("Saikiran RM", "0001112224");
        UpdateContact request = new UpdateContact();
        request.setOldContact(contact1);
        request.setNewContact(updatedContact);

        mockMvc.perform(put("/api/v1/users/user1/addressbooks/Friends/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contact updated successfully in addressbook: Friends"));


        mockMvc.perform(get("/api/v1/users/user1/addressbooks/Friends/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All contacts retrieved under addressbook: Friends"))
                .andExpect(jsonPath("$.data[0].name").value("Saikiran RM"))
                .andExpect(jsonPath("$.data[0].phone").value("0001112224"))
                .andExpect(jsonPath("$.data[1].name").value("Sai"))
                .andExpect(jsonPath("$.data[1].phone").value("0001112226"));
    }

    @Test
    public void testDeleteContact() throws Exception {
        mockMvc.perform(delete("/api/v1/users/user1/addressbooks/Friends/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contact removed successfully from addressbook : Friends"));

        mockMvc.perform(get("/api/v1/users/user1/addressbooks/Friends/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All contacts retrieved under addressbook: Friends"))
                .andExpect(jsonPath("$.data[0].name").value("Saikiran"))
                .andExpect(jsonPath("$.data[0].phone").value("0001112224"));
    }

    @Test
    public void testUniqueContactsAcrossBooks() throws Exception {
        // Create another address book and add a duplicate contact
        mockMvc.perform(post("/api/v1/users/user1/addressbooks/Work"));

        mockMvc.perform(post("/api/v1/users/user1/addressbooks/Work/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contact1)));

        // Should still return only 2 unique contacts
        mockMvc.perform(get("/api/v1/users/user1/addressbooks/contacts/unique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Unique contacts retrieved from all addressbooks."))
                .andExpect(jsonPath("$.data[0].name").value("Saikiran"))
                .andExpect(jsonPath("$.data[0].phone").value("0001112224"))
                .andExpect(jsonPath("$.data[1].name").value("Sai"))
                .andExpect(jsonPath("$.data[1].phone").value("0001112226"));
    }
}

