package com.reece.service;


import com.reece.model.AddressBook;
import com.reece.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressBookServiceTest {

    private AddressBookService addressBookService;

    @BeforeEach
    void setUp() {
        addressBookService = new AddressBookService();
    }

    @Test
    void shouldCreateAddressBook() {
        AddressBook addressbook = addressBookService.createAddressBook("Friends");
        assertTrue(addressBookService.getAddressBook("Friends").equals(addressbook));
    }

    @Test
    void shouldAddAndGetContact() {
        Contact contact = new Contact("Saikiran", "0001112223");
        addressBookService.addContact("Reece Family", contact);
        AddressBook addressBook = addressBookService.getAddressBook("Reece Family");

        Set<Contact> contacts = addressBook.getContacts();

        assertEquals(1, contacts.size());
        assertTrue(contacts.contains(contact));
    }

    @Test
    void shouldRemoveContact() {
        Contact contact = new Contact("Saikiran", "0001112223");
        addressBookService.addContact("Reece Work", contact);
        addressBookService.removeContact("Reece Work", contact);
        AddressBook addressBook = addressBookService.getAddressBook("Reece Work");
        Set<Contact> contactSet = addressBook.getContacts();
        Iterator<Contact> iterator = contactSet.iterator();
        while (iterator.hasNext()) {
            Contact contact1 = iterator.next();
            assertFalse(contact1.getName().contains("Saikiran"));
        }
    }

    @Test
    void shouldReturnFalseForUpdateContact() {
        Contact oldContact = new Contact("Saikiran", "0001112223");
        Contact newContact = new Contact("Saikiran R", "0001112224");
        addressBookService.addContact("Reece Family", oldContact);

        boolean updated = addressBookService.updateContact(null, oldContact, newContact);
        assertFalse(updated);
    }

    @Test
    void shouldUpdateContact() {
        Contact oldContact = new Contact("Saikiran", "0001112223");
        Contact newContact = new Contact("Saikiran R", "0001112224");
        addressBookService.addContact("Reece Family", oldContact);

        boolean updated = addressBookService.updateContact("Reece Family", oldContact, newContact);
        assertTrue(updated);

        AddressBook addressBook = addressBookService.getAddressBook("Reece Family");
        Set<Contact> contactSet = addressBook.getContacts();
        Iterator<Contact> iterator = contactSet.iterator();
        while (iterator.hasNext()) {
            Contact contact = iterator.next(); // only advance once
            assertEquals("Saikiran R", contact.getName());
            assertEquals("0001112224", contact.getPhone());
        }
    }

    @Test
    void shouldReturnUniqueContactsAcrossBooks() {
        Contact shared = new Contact("Saikiran", "0001112223");

        addressBookService.addContact("Book1", shared);
        addressBookService.addContact("Book2", shared);

        Set<Contact> unique = addressBookService.getUniqueContactsAcrossAllBooks();
        assertEquals(1, unique.size());
        assertTrue(unique.contains(shared));
    }

    @Test
    void getAllBooks_ShouldReturnAllCreatedAddressBooks() {
        addressBookService.createAddressBook("Family");
        addressBookService.createAddressBook("Friends");
        addressBookService.createAddressBook("Work");

        Collection<AddressBook> allBooks = addressBookService.getAllBooks();
        assertEquals(3, allBooks.size(), "Should return 3 address books");

        assertTrue(allBooks.stream().anyMatch(book -> book.getName().equals("Family")));
        assertTrue(allBooks.stream().anyMatch(book -> book.getName().equals("Friends")));
        assertTrue(allBooks.stream().anyMatch(book -> book.getName().equals("Work")));
    }

    @Test
    void getAllBooks_WhenNoBooks_ReturnsEmptyCollection() {
        Collection<AddressBook> allBooks = addressBookService.getAllBooks();
        assertNotNull(allBooks);
        assertTrue(allBooks.isEmpty(), "Should return an empty collection when no books exist");
    }

    @Test
    void removeAddressBooks_ShouldRemoveExistingBook() {
        addressBookService.createAddressBook("Friends");
        assertEquals(1, addressBookService.getAllBooks().size(), "Address book should exist before deletion");
        addressBookService.removeAddressBooks("Friends");
        assertEquals(0, addressBookService.getAllBooks().size(), "Address book should be removed");
    }

}

