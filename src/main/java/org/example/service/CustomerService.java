package org.example.service;

import org.example.model.Customer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface CustomerService {
    List<Customer> getAll();
    Customer getCustomerById(String customerId);
    List<Customer> getAllByCountry(String country);
    Customer save(Customer customer) throws IOException;
    void deleteByCustomerId(String id) throws FileNotFoundException;
}
