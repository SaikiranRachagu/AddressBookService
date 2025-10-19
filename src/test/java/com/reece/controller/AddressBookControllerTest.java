package com.reece.controller;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.reece.model.AddressBook;
import com.reece.model.Contact;
import com.reece.model.UpdateContact;

import com.reece.service.UserAddressBookService;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;


@WebMvcTest(AddressBookController.class)
class AddressBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAddressBookService addressBookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldNotCreateAddressBookForUnknownUser() throws Exception {

        mockMvc.perform(post("/api/v1/users/user1/addressbooks/ReeceFriends"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("user not found: user1"));
    }

    @Test
    void shouldAddContact() throws Exception {
        Contact contact = new Contact("Saikiran", "0001112224");

        mockMvc.perform(post("/api/v1/users/user1/addressbooks/Friends/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Address book or contact not found: Friends"));

    }

    @Test
    void shouldNotAddInValidPhoneNoContact() throws Exception {
        Contact contact = new Contact("Saikiran", "12345");

        mockMvc.perform(post("/api/v1/users/user1/addressbooks/Friends/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void removeContact_ShouldReturnSuccessResponse() throws Exception {
        String bookName = "Friends";
        Contact contact = new Contact("Kiran", "0001112224");

        Mockito.when(addressBookService.removeContactForUser("user1", bookName, contact)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/users/user1/addressbooks/{bookName}/contacts", bookName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contact removed successfully from addressbook : " + bookName))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldUpdateContact() throws Exception {
        Contact oldC = new Contact("Old", "0001112224");
        Contact newC = new Contact("New", "0001112225");

        UpdateContact req = new UpdateContact();
        req.setOldContact(oldC);
        req.setNewContact(newC);

        when(addressBookService.updateContactForUser("user1", "Friends", oldC, newC)).thenReturn(true);

        mockMvc.perform(put("/api/v1/users/user1/addressbooks/Friends/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contact updated successfully in addressbook: Friends"));
    }

    @Test
    void getAllAddressBooks_ShouldReturnAllBooks() throws Exception {
        AddressBook book1 = new AddressBook("Friends");
        AddressBook book2 = new AddressBook("Work");
        Map<String, AddressBook> addressBookMap = new HashMap<>();
        addressBookMap.put("user1", book1);
        addressBookMap.put("user1", book2);

        when(addressBookService.getAllBooks("user1")).thenReturn(addressBookMap);

        mockMvc.perform(get("/api/v1/users/user1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All Addressbooks retrieved"));
    }

    @Test
    void getUniqueContacts_ShouldReturnUniqueContactsAcrossAddressBooks() throws Exception {
        Set<Contact> uniqueContacts = Set.of(
                new Contact("Saikiran", "0001112223"),
                new Contact("Saikiran RM", "0001112244")
        );

        when(addressBookService.getUniqueContactsAcrossAllBooks("user1")).thenReturn(uniqueContacts);

        mockMvc.perform(get("/api/v1/users/user1/addressbooks/contacts/unique")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Unique contacts retrieved from all addressbooks."))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[?(@.name == 'Saikiran')].phone").value(hasItem("0001112223")))
                .andExpect(jsonPath("$.data[?(@.name == 'Saikiran RM')].phone").value(hasItem("0001112244")));
    }

    @Test
    void createAddressBook_ShouldReturnCreatedBook() throws Exception {
        String bookName = "Friends";
        AddressBook newBook = new AddressBook(bookName);

        when(addressBookService.createAddressBookForUser("user1",bookName)).thenReturn(true);

        mockMvc.perform(post("/api/v1/users/user1/addressbooks/{addressbookName}", bookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Addressbook created successfully: Friends"));
    }

    @Test
    void createAddressBook_ShouldReturnAlreadyExistsMessage_WhenBookIsNull() throws Exception {
        String bookName = "Friends";
        when(addressBookService.createAddressBookForUser("user1",bookName)).thenReturn(false);


        mockMvc.perform(post("/api/v1/users/user1/addressbooks/{addressbookName}", bookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("user not found: user1"));
    }

    @Test
    void testGetAllAddressBooksForUser_whenContactsExist_returnsOk() throws Exception {
        // Arrange
        String userId = "user123";
        String addressBookName = "Family";
        Contact contact = new Contact("Alice", "123456");
        Set<Contact> contacts = Set.of(contact);

        when(addressBookService.getContacts(userId, addressBookName)).thenReturn(contacts);

        // Act + Assert
        mockMvc.perform(get("/api/v1/users/{userId}/addressbooks/{addressbookName}/contacts", userId, addressBookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All contacts retrieved under addressbook: " + addressBookName))
                .andExpect(jsonPath("$.data[0].name").value("Alice"))
                .andExpect(jsonPath("$.data[0].phone").value("123456"));
    }

    @Test
    void testGetAllAddressBooksForUser_whenContactsEmpty_returnsNotFound() throws Exception {
        // Arrange
        String userId = "user123";
        String addressBookName = "EmptyBook";

        when(addressBookService.getContacts(userId, addressBookName)).thenReturn(Set.of());

        // Act + Assert
        mockMvc.perform(get("/api/v1/users/{userId}/addressbooks/{addressbookName}/contacts", userId, addressBookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Address book or contact not found: " + addressBookName))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testRemoveAddressbooks_shouldReturnSuccessResponse() throws Exception {
        // Arrange
        String userId = "user123";
        String addressBookName = "Work";

        // Mocking service call
        Mockito.when(addressBookService.removeAddressBookForUser(userId, addressBookName))
                .thenReturn(true);

        // Act + Assert
        mockMvc.perform(delete("/api/v1/users/{userId}/addressbooks/{addressbook}", userId, addressBookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Addressbook " + addressBookName + " removed successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

}

