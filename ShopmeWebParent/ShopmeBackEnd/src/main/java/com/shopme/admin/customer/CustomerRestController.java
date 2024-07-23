package com.shopme.admin.customer;

import com.shopme.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

    @Autowired
    private CustomerService service;

    @PostMapping("/customers/check_email")
    public String checkDuplicateEmail(Integer id, String email) {
        return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
