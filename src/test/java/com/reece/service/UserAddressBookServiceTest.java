package com.reece.service;

import com.reece.model.AddressBook;
import com.reece.model.Contact;
import com.reece.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserAddressBookServiceTest {

    private UserAddressBookService service;

    @BeforeEach
    void setUp() {
        service = new UserAddressBookService();
    }

    @Test
    void testCreateAddressBookForNewUser() {
        boolean created = service.createAddressBookForUser("user1", "Family");
        assertTrue(created);
    }

    @Test
    void testAddContactToExistingAddressBook() {
        service.createAddressBookForUser("user1", "Work");
        Contact contact = new Contact("Sai", "1234567890");

        boolean added = service.addContactToAddressBook("user1", "Work", contact);
        assertTrue(added);
    }

    @Test
    void testAddContactToNonexistentAddressBook() {
        Contact contact = new Contact("Sai", "1234567890");

        boolean added = service.addContactToAddressBook("user2", "Friends", contact);
        assertFalse(added); // No address book created
    }

    @Test
    void testGetContacts() {
        service.createAddressBookForUser("user1", "Family");
        Contact contact = new Contact("Super", "1234567890");
        service.addContactToAddressBook("user1", "Family", contact);

        Set<Contact> contacts = service.getContacts("user1", "Family");
        assertEquals(1, contacts.size());
        assertTrue(contacts.contains(contact));
    }

    @Test
    void testRemoveContactForUser() {
        service.createAddressBookForUser("user1", "Work");
        Contact contact = new Contact("Sam", "1234567890");
        service.addContactToAddressBook("user1", "Work", contact);

        boolean removed = service.removeContactForUser("user1", "Work", contact);
        assertTrue(removed);
    }

    @Test
    void testRemoveNonexistentContact() {
        service.createAddressBookForUser("user1", "Work");
        Contact contact = new Contact("Ram", "1234567890");

        boolean removed = service.removeContactForUser("user1", "Work", contact);
        assertFalse(removed);
    }

    @Test
    void testGetUniqueContactsAcrossAllBooks() {
        service.createAddressBookForUser("user1", "Work");
        service.createAddressBookForUser("user1", "Family");

        Contact contact1 = new Contact("Anna", "1234567890");
        Contact contact2 = new Contact("Ben", "1234567890");

        service.addContactToAddressBook("user1", "Work", contact1);
        service.addContactToAddressBook("user1", "Family", contact2);
        service.addContactToAddressBook("user1", "Family", contact1); // duplicate across books

        Set<Contact> unique = service.getUniqueContactsAcrossAllBooks("user1");
        assertEquals(2, unique.size());
    }

    @Test
    void testRemoveAddressBook() {
        service.createAddressBookForUser("user1", "Friends");
        boolean removed = service.removeAddressBookForUser("user1", "Friends");
        assertTrue(removed);
    }

    @Test
    void testRemoveAddressBookNonexistent() {
        boolean removed = service.removeAddressBookForUser("user2", "Nonexistent");
        assertFalse(removed);
    }

    @Test
    void testGetAllBooks() {
        service.createAddressBookForUser("user1", "Family");
        service.createAddressBookForUser("user1", "Work");

        Map<String, AddressBook> books = service.getAllBooks("user1");
        assertEquals(2, books.size());
        assertTrue(books.containsKey("Family"));
        assertTrue(books.containsKey("Work"));
    }

    @Test
    void testUpdateContactForUser() {
        service.createAddressBookForUser("user1", "Family");
        Contact oldContact = new Contact("Jake", "1234567890");
        Contact newContact = new Contact("Jake", "1234567899");

        service.addContactToAddressBook("user1", "Family", oldContact);

        boolean updated = service.updateContactForUser("user1", "Family", oldContact, newContact);
        assertTrue(updated);

        Set<Contact> contacts = service.getContacts("user1", "Family");
        assertFalse(contacts.contains(oldContact));
        assertTrue(contacts.contains(newContact));
    }

    @Test
    void testUpdateContactNotFound() {
        service.createAddressBookForUser("user1", "Work");
        Contact oldContact = new Contact("Ghost", "1234567890");
        Contact newContact = new Contact("Ghost", "1234567899");

        boolean updated = service.updateContactForUser("user1", "Work", oldContact, newContact);
        assertFalse(updated);
    }
}
