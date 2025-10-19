package com.reece.service;

import com.reece.model.AddressBook;
import com.reece.model.Contact;
import com.reece.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserAddressBookService {
    private Map<String, User> userStore = new HashMap<>();

    public boolean createAddressBookForUser(String userId, String bookName) {
        User user = userStore.get(userId);
        if (user == null) {
            User user1 = new User();//creating user for first time.
            user1.setUserId(userId);
            userStore.put(userId, user1);
            return user1.createAddressBook(bookName);
        }
        return user.createAddressBook(bookName);
    }

    public boolean addContactToAddressBook(String userId, String bookName, Contact contact) {
        User user = userStore.get(userId);
        if (user == null) {
            return false;
            //userStore.put(userId, user);
        }

        AddressBook book = user.getAddressBook(bookName);
        if (book == null) return false;

        return book.addContact(contact);
    }

    public Set<Contact> getContacts(String userId, String bookName) {
        User user = userStore.get(userId);
        if (user == null) return Set.of();

        AddressBook book = user.getAddressBook(bookName);
        if (book == null) return Set.of();

        return book.getContacts();
    }

    public boolean removeContactForUser(String userId, String bookName, Contact contact) {
        User user = userStore.get(userId);
        if (user == null) return false;

        AddressBook addressBook = user.getAddressBook(bookName);
        if (addressBook == null) {
            return false;
        }
        return addressBook.removeContact(contact);
    }

    public Set<Contact> getUniqueContactsAcrossAllBooks(String userId) {
        User user = userStore.get(userId);
        if (user == null) return Set.of();

        Set<Contact> uniqueContacts = new HashSet<>();

        for (AddressBook addressBook : user.getAllAddressBooks()) {
            uniqueContacts.addAll(addressBook.getContacts());
        }
        return uniqueContacts;
    }

    public boolean removeAddressBookForUser(String userId, String bookName) {
        User user = userStore.get(userId);
        if (user == null) return false;

        AddressBook addressBook = user.removeAddressBook(bookName);
        return addressBook == null ? false : true;
    }

    public Map<String, AddressBook>  getAllBooks(String userId) {
        User user = userStore.get(userId);
        if (user == null) return null;
        return user.getAddressBooks();
    }

    public boolean updateContactForUser(String userId, String addressBookName, Contact oldContact, Contact newContact) {
        User user = userStore.get(userId);
        if (user == null) return false;

        AddressBook addressBook = user.getAddressBook(addressBookName);
        if (addressBook != null && addressBook.getContacts().contains(oldContact)) {
            addressBook.removeContact(oldContact);
            addressBook.addContact(newContact);
            return true;
        }
        return false;
    }
}

