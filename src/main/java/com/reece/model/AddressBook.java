package com.reece.model;

import java.util.HashSet;
import java.util.Set;

public class AddressBook {
    private String name;
    private Set<Contact> contacts = new HashSet<>();

    public AddressBook() {
    }

    public AddressBook(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
    }
    /*public void updateContact(Contact contact) {
        if (contacts.contains(contact.getName())) {
            contacts.remove(contact);
            contacts.add(contact);
        }
    }*/
}

