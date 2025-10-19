package com.reece.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String username;
    private Map<String, AddressBook> addressBooks = new HashMap<>();

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public boolean createAddressBook(String name) {
        if (addressBooks.containsKey(name)) {
            return false; // Duplicate address book
        }
        addressBooks.put(name, new AddressBook(name));
        return true;
    }

    public AddressBook getAddressBook(String name) {
        return addressBooks.get(name);
    }

    public Collection<AddressBook> getAllAddressBooks() {
        return addressBooks.values();
    }

    public AddressBook removeAddressBook(String book) {
        return addressBooks.remove(book);
    }
}
