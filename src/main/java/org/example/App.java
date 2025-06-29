package org.example;

import org.example.database.CustomerDAO;
import org.example.file.FileOperations;
import org.example.model.Customer;
import org.example.service.CustomerService;
import org.example.service.impl.CustomerServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        CustomerDAO customerDAO = new CustomerDAO();
        try{
            customerDAO.connectToDataSource();
            CustomerService customerService = new CustomerServiceImpl(customerDAO);
            List<Customer> customerList = customerService.getAllByCountry("Zimbabwe");
//            System.out.println(customerList.size());
//            System.out.println(customerList);

            Customer customer = Customer.builder()
                    .customerId("nastya0610")
                    .firstName("Anastasiaaa")
                    .lastName("Vahnovan")
                    .company("Dr Anastasia")
                    .city("Odessa")
                    .country("Ukraine")
                    .email("nastya@gmail.com")
                    .phone1("097-88892-17")
                    .phone2("095-557-11-99")
                    .subscriptionDate(LocalDate.now())
                    .website("https://www.baeldung.com/")
                    .rating(new BigDecimal("88.9"))
                    .build();

            customerService.save(customer);

//            customerService.deleteByCustomerId("nastya0610");

            List<Customer> listByRating = customerService.getAllByCountryAndRatingMoreThan("Zimbabwe", BigDecimal.valueOf(15.8));
            listByRating.forEach(System.out::println);
            System.out.println("count of records by rating : " + listByRating.size());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
