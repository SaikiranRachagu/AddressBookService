package com.reece.controller;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.reece.model.AddressBook;
import com.reece.model.Contact;
import com.reece.model.UpdateContact;
import com.reece.service.AddressBookService;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
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
    private AddressBookService addressBookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAddressBook() throws Exception {

        mockMvc.perform(post("/api/v1/addressbooks/Reece Friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Addressbook null already exists or addressbookName is null."));
    }

    @Test
    void shouldAddContact() throws Exception {
        Contact contact = new Contact("Saikiran", "0001112224");

        mockMvc.perform(post("/api/v1/addressbooks/Friends/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contact added successfully under addressbook Friends"));

    }

    @Test
    void shouldNotAddInValidPhoneNoContact() throws Exception {
        Contact contact = new Contact("Saikiran", "12345");

        mockMvc.perform(post("/api/v1/addressbooks/Friends/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void removeContact_ShouldReturnSuccessResponse() throws Exception {
        String bookName = "Friends";
        Contact contact = new Contact("Kiran", "0001112224");

        Mockito.when(addressBookService.removeContact(bookName, contact)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/addressbooks/{bookName}/contacts", bookName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contact removed successfully from addressbook : " + bookName))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getContactsFromAddressbook_ShouldReturnContacts() throws Exception {
        String bookName = "Friends";
        Set<Contact> contacts = Set.of(new Contact("Kiran", "0001112224"));
        AddressBook book = new AddressBook(bookName);
        book.getContacts().addAll(contacts);

        when(addressBookService.getAddressBook(bookName)).thenReturn(book);

        mockMvc.perform(get("/api/v1/addressbooks/{bookName}/contacts", bookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contacts fetched"))
                .andExpect(jsonPath("$.data[0].name").value("Kiran"))
                .andExpect(jsonPath("$.data[0].phone").value("0001112224"));
    }

    @Test
    void getContactsFromAddressbook_WhenBookNotFound_ShouldReturnFalse() throws Exception {
        String bookName = "UnknownBook";
        when(addressBookService.getAddressBook(bookName)).thenReturn(null);

        mockMvc.perform(get("/api/v1/addressbooks/{bookName}/contacts", bookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("addressbook 'UnknownBook' not found"));
    }

    @Test
    void shouldUpdateContact() throws Exception {
        Contact oldC = new Contact("Old", "0001112224");
        Contact newC = new Contact("New", "0001112225");

        UpdateContact req = new UpdateContact();
        req.setOldContact(oldC);
        req.setNewContact(newC);

        when(addressBookService.updateContact("Friends", oldC, newC)).thenReturn(true);

        mockMvc.perform(put("/api/v1/addressbooks/Friends/contacts")
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

        when(addressBookService.getAllBooks()).thenReturn(List.of(book1, book2));

        mockMvc.perform(get("/api/v1/addressbooks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All Addressbooks retrieved"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("Friends"))
                .andExpect(jsonPath("$.data[1].name").value("Work"));
    }

    @Test
    void removeAddressbook_ShouldReturnSuccessResponse() throws Exception {
        String bookName = "Friends";

        doNothing().when(addressBookService).removeAddressBooks(bookName);

        mockMvc.perform(delete("/api/v1/addressbooks/{addressbook}", bookName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Addressbook Friends removed successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getUniqueContacts_ShouldReturnUniqueContactsAcrossAddressBooks() throws Exception {
        Set<Contact> uniqueContacts = Set.of(
                new Contact("Saikiran", "0001112223"),
                new Contact("Saikiran RM", "0001112244")
        );

        when(addressBookService.getUniqueContactsAcrossAllBooks()).thenReturn(uniqueContacts);

        mockMvc.perform(get("/api/v1/addressbooks/contacts/unique")
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

        when(addressBookService.createAddressBook(bookName)).thenReturn(newBook);

        mockMvc.perform(post("/api/v1/addressbooks/{addressbookName}", bookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(bookName + " Addressbook created"))
                .andExpect(jsonPath("$.data.name").value(bookName));
    }

    @Test
    void createAddressBook_ShouldReturnAlreadyExistsMessage_WhenBookIsNull() throws Exception {
        String bookName = "Friends";

        when(addressBookService.createAddressBook(bookName)).thenReturn(null);

        mockMvc.perform(post("/api/v1/addressbooks/{addressbookName}", bookName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Addressbook null already exists or addressbookName is null."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}

