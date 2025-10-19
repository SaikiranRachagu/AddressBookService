package com.reece.service;

import com.reece.model.AddressBook;
import com.reece.model.Contact;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AddressBookService {
    private final Map<String, AddressBook> addressBookMap = new HashMap<>();

    public AddressBook createAddressBook(String name) {
        if (addressBookMap.containsKey(name) || name == null) return null;
        AddressBook addressBook = new AddressBook(name);
        addressBookMap.put(name, addressBook);
        return addressBook;
    }

    public AddressBook getAddressBook(String name) {
        return addressBookMap.get(name);
    }

    public void removeAddressBooks(String addressbook) {
        addressBookMap.remove(addressbook);
    }

    public Collection<AddressBook> getAllBooks() {
        return addressBookMap.values();
    }

    public void addContact(String bookName, Contact contact) {
        addressBookMap.computeIfAbsent(bookName, AddressBook::new).addContact(contact);
    }

    public void removeContact(String bookName, Contact contact) {
        AddressBook addressBook = addressBookMap.get(bookName);
        if (addressBook != null) {
            addressBook.removeContact(contact);
        }
    }

    public boolean updateContact(String addressBookName, Contact oldContact, Contact newContact) {
        AddressBook addressBook = addressBookMap.get(addressBookName);
        if (addressBook != null && addressBook.getContacts().contains(oldContact)) {
            addressBook.removeContact(oldContact);
            addressBook.addContact(newContact);
            return true;
        }
        return false;
    }

    public Set<Contact> getUniqueContactsAcrossAllBooks() {
        Set<Contact> uniqueContacts = new HashSet<>();
        for (AddressBook addressBook : addressBookMap.values()) {
            uniqueContacts.addAll(addressBook.getContacts());
        }
        return uniqueContacts;
    }
}

