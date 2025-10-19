package com.reece.controller;

import com.reece.model.AddressBook;
import com.reece.model.ApiResponse;
import com.reece.model.Contact;
import com.reece.model.UpdateContact;
import com.reece.service.UserAddressBookService;

import jakarta.validation.Valid;

import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/")
public class AddressBookController {

    @Autowired
    private UserAddressBookService userAddressBookService;

    //AC1
    @PostMapping("users/{userId}/addressbooks/{addressbookName}/contacts")
    public ResponseEntity<ApiResponse<Void>> addContactToUser(@PathVariable String addressbookName,
                                                              @Valid @RequestBody Contact contact,
                                                              @PathVariable @NotBlank String userId) {
        Boolean isContactAdded = userAddressBookService.addContactToAddressBook(userId, addressbookName, contact);
        if (isContactAdded) {
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Contact added successfully under addressbook: " + addressbookName, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false,
                            "Address book or contact not found: " + addressbookName, null));
        }
    }

    //AC2
    @DeleteMapping("/users/{userId}/addressbooks/{addressbookName}/contacts")
    public ResponseEntity<ApiResponse<Void>> removeContactForUser(@PathVariable @NotBlank String userId,
                                                                  @PathVariable @NotBlank String addressbookName,
                                                                  @Valid @RequestBody Contact contact) {
        if (userAddressBookService.removeContactForUser(userId, addressbookName, contact)) {
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Contact removed successfully from addressbook : " + addressbookName, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "Address book or contact not found: " + addressbookName,
                            null));
        }
    }

    //AC3
    @GetMapping("/users/{userId}/addressbooks/{addressbookName}/contacts")
    public ResponseEntity<ApiResponse<Set<Contact>>> getAllAddressBooksForUser(@PathVariable @NotBlank String userId,
                                                                               @PathVariable String addressbookName) {
        Set<Contact> result = userAddressBookService.getContacts(userId, addressbookName);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "Address book or contact not found: " + addressbookName,
                            null));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(true, "All contacts retrieved under addressbook: "
                    + addressbookName, result));
        }
    }

    // AC4:  Users should be able to maintain multiple addressbooks
    @GetMapping("users/{userId}")
    public ResponseEntity<ApiResponse<Map<String, AddressBook>>> getAllAddressBooks(@PathVariable @NotBlank String userId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "All Addressbooks retrieved",
                userAddressBookService.getAllBooks(userId)));
    }

    //  AC4:   Users should be able to maintain multiple addressbooks : updating the books
    @PutMapping("/users/{userId}/addressbooks/{addressbookName}/contacts")
    public ResponseEntity<ApiResponse<Void>> updateContact(@PathVariable @NotBlank String userId,
                                                           @PathVariable String addressbookName,
                                                           @RequestBody UpdateContact updateContact) {
        boolean updated = userAddressBookService.updateContactForUser(userId, addressbookName, updateContact.getOldContact(),
                updateContact.getNewContact());

        String message = updated
                ? "Contact updated successfully in addressbook: " + addressbookName
                : "Old contact not found in the addressbook.";
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));

    }

    //  AC4:   Users should be able to maintain multiple addressbooks : delete addbook
    @DeleteMapping("/users/{userId}/addressbooks/{addressbook}")
    public ResponseEntity<ApiResponse<Void>> removeAddressbooks(@PathVariable @NotBlank String userId,
                                                                @PathVariable String addressbook) {
        userAddressBookService.removeAddressBookForUser(userId, addressbook);
        return ResponseEntity.ok(new ApiResponse<>(true, "Addressbook " + addressbook
                + " removed successfully", null));
    }

    //AC4: create addbook
    @PostMapping("users/{userId}/addressbooks/{addressbookName}")
    public ResponseEntity<ApiResponse<Void>> createAddressBookForUser(@PathVariable String addressbookName,
                                                                      @PathVariable @NotBlank String userId) {
        boolean isAddressBookCreated = userAddressBookService.createAddressBookForUser(userId, addressbookName);
        if (isAddressBookCreated) {
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Addressbook created successfully: " + addressbookName, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false,
                            "user not found: " + userId, null));
        }
    }

    //AC5
    @GetMapping("/users/{userId}/addressbooks/contacts/unique")
    public ResponseEntity<ApiResponse<Set<Contact>>> getUniqueContactsForUser(@PathVariable @NotBlank String userId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Unique contacts retrieved from all addressbooks.",
                userAddressBookService.getUniqueContactsAcrossAllBooks(userId)));
    }
}

