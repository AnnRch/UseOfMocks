package org.example.service.impl;

import org.example.database.CustomerDAO;
import org.example.model.Customer;
import org.example.service.CustomerService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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
    public List<Customer> getAllByCountryAndRatingMoreThan(String country, BigDecimal rating) {
        return customerDAO.findByCountry(country).stream()
                .filter(customer -> customer.getRating().compareTo(rating) > 0)
                .toList();
    }

    @Override
    public List<Customer> getAllByCountryAndRatingMoreThanOrderByRatingAsc(String country, BigDecimal rating) {
        return getAllByCountryAndRatingMoreThan(country, rating)
                .stream()
                .sorted(Comparator.comparing(Customer::getRating))
                .toList();
    }

    @Override
    public Customer save(Customer customer) throws IOException {
        if(customer == null){
            throw new NullPointerException("customer is null");
        }
        return customerDAO.save(customer);
    }

    @Override
    public void deleteByCustomerId(String id) throws FileNotFoundException {
        customerDAO.deleteById(id);
    }
}
