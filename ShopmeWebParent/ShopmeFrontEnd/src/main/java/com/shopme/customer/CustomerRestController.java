package com.shopme.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

    @Autowired
    private CustomerService service;

    @PostMapping("/customers/check_unique_email")
    public String checkDuplicateEmail(String email) {
       return service.isEmailunique(email) ? "OK" : "Duplicated";
    }

    @PostMapping("/customers/check_unique_phone")
    public String checkDuplicatePhone(String phone) {
        return service.isPhoneUnique(phone) ? "OK" : "Duplicated";
    }

}
