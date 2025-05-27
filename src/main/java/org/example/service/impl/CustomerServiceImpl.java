package org.example.service.impl;

import org.example.database.CustomerDAO;
import org.example.model.Customer;
import org.example.service.CustomerService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private List<Customer> customers;
    private CustomerDAO customerDAO;

    public CustomerServiceImpl(){
        customers = new ArrayList<>();
    }

    public CustomerServiceImpl(CustomerDAO customerDAO){
       this.customerDAO = customerDAO;
    }

    @Override
    public List<Customer> getAll() {
        return customerDAO.getAll();
    }

    @Override
    public Customer getCustomerById(String customerId) {
        return customerDAO.getById(customerId);
    }

    @Override
    public List<Customer> getAllByCountry(String country) {
        return customerDAO.findByCountry(country);
    }

    @Override
    public Customer save(Customer customer) throws IOException {
        return customerDAO.save(customer);
    }

    @Override
    public void deleteByCustomerId(String id) throws FileNotFoundException {
        customerDAO.deleteById(id);
    }
}
