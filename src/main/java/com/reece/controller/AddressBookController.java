package com.reece.controller;

import com.reece.exception.AddressBookAPIException;
import com.reece.model.AddressBook;
import com.reece.model.ApiResponse;
import com.reece.model.Contact;
import com.reece.model.UpdateContact;
import com.reece.service.AddressBookService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/addressbooks")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    //   AC1: Users should be able to add new contact entries
    @PostMapping("/{addressbookName}/contacts")
    public ResponseEntity<ApiResponse<Void>> addContact(@PathVariable String addressbookName,
                                                        @Valid @RequestBody Contact contact) {
        addressBookService.addContact(addressbookName, contact);
        return ResponseEntity.ok(new ApiResponse<>(true, "Contact added successfully under addressbook "
                + addressbookName, null));
    }

    //  AC2:  Users should be able to remove existing contact entries
    @DeleteMapping("/{addressbookName}/contacts")
    public ResponseEntity<ApiResponse<Void>> removeContact(@PathVariable String addressbookName,
                                                           @Valid @RequestBody Contact contact) {
        addressBookService.removeContact(addressbookName, contact);
        return ResponseEntity.ok(new ApiResponse<>(true,
                "Contact removed successfully from addressbook : " + addressbookName, null));
    }

    //  AC3:   Users should be able to print all contacts in an Addressbook
    @GetMapping("/{addressbookName}/contacts")
    public ResponseEntity<ApiResponse<Set<Contact>>> getContactsFromAddressbook(@PathVariable String addressbookName) {
        AddressBook book = addressBookService.getAddressBook(addressbookName);
        if (book == null) {
            throw new AddressBookAPIException("addressbook '" + addressbookName + "' not found");
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Contacts fetched", book.getContacts()));
    }

    // AC4:  Users should be able to maintain multiple addressbooks
    @GetMapping
    public ResponseEntity<ApiResponse<Collection<AddressBook>>> getAllAddressBooks() {
        return ResponseEntity.ok(new ApiResponse<>(true, "All Addressbooks retrieved",
                addressBookService.getAllBooks()));
    }

    //  AC4:   Users should be able to maintain multiple addressbooks : updating the books
    @PutMapping("/{addressbookName}/contacts")
    public String updateContact(@PathVariable String addressbookName, @RequestBody UpdateContact updateContact) {
        boolean updated = addressBookService.updateContact(addressbookName, updateContact.getOldContact(),
                updateContact.getNewContact());
        return updated ? "Contact updated successfully in addressbook : " + addressbookName
                : " Old contact not found in the addressbook.";
    }

    //  AC4:   Users should be able to maintain multiple addressbooks : creating addressbook
    @PostMapping("/{addressbookName}")
    public ResponseEntity<ApiResponse<AddressBook>> createAddressBook(@PathVariable String addressbookName) {
        AddressBook book = addressBookService.createAddressBook(addressbookName);
        if (book == null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Addressbook " + book
                    + " already exists or addressbookName is null.", book));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, addressbookName + " Addressbook created", book));

    }

    //  AC4:   Users should be able to maintain multiple addressbooks
    @DeleteMapping("/{addressbook}")
    public ResponseEntity<ApiResponse<Void>> removeAddressbooks(@PathVariable String addressbook) {
        addressBookService.removeAddressBooks(addressbook);
        return ResponseEntity.ok(new ApiResponse<>(true, "Addressbook " + addressbook
                + " removed successfully", null));
    }

    //    AC5:  Users should be able to print a unique set of all contacts across multiple addressbooks
    @GetMapping("/contacts/unique")
    public ResponseEntity<ApiResponse<Set<Contact>>> getUniqueContacts() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Unique contacts retrieved from all addressbooks.",
                addressBookService.getUniqueContactsAcrossAllBooks()));
    }
}

