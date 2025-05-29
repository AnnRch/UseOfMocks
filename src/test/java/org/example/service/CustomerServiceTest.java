package org.example.service;

import org.example.database.CustomerDAO;
import org.example.model.Customer;
import org.example.service.impl.CustomerServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

    private List<Customer> customerList;
    private Customer customer1, customer2, customer3;

    @Mock
    private CustomerDAO customerDAO;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void init(){
        customer1 = Customer.builder()
                .customerId("122345")
                .firstName("Irina")
                .lastName("Kuznetsova")
                .company("ATB")
                .city("Chernivtsi")
                .country("Ukraine")
                .build();

        customer2 = Customer.builder()
                .customerId("098876CUS")
                .firstName("Nazar")
                .lastName("Rozumeiko")
                .company("MDPU")
                .city("Zaporizhzhia")
                .country("Ukraine")
                .build();
    }

    @Test
    public void getAllTest(){

        customerList = new ArrayList<>();
        customerList.add(customer1);
        customerList.add(customer2);

        customerService = new CustomerServiceImpl(customerDAO);

        when(customerDAO.getAll()).thenReturn(customerList);

        Assertions.assertNotNull(customerService.getAll());
        Assertions.assertEquals(customerList, customerService.getAll());
        Assertions.assertEquals(2, customerService.getAll().size());

        verify(customerDAO, atLeastOnce()).getAll();
    }

    @Test
    public void getAllEmptyCase(){

        customerList = new ArrayList<>();
        customerService = new CustomerServiceImpl(customerDAO);


    }

    @Test
    public void saveAppendTestCase() throws IOException {

        customerList = new ArrayList<>();
        customerService = new CustomerServiceImpl(customerDAO);

        when(customerDAO.getById("122345")).thenReturn(null);
        when(customerDAO.save(customer1)).thenReturn(customer1);
        Assertions.assertSame(customer1, customerService.save(customer1));
    }

    @Test
    public void saveUpdateTestCase() throws IOException {
        customerList = new ArrayList<>();
        customerList.add(customer1);

        customerService = new CustomerServiceImpl(customerDAO);

        when(customerDAO.getById("122345")).thenReturn(customer1);
        when(customerDAO.save(customer1)).thenReturn(customer1);
        Assertions.assertSame(customer1, customerService.save(customer1));
    }

    @Test
    public void deleteSuccessfulTestCase(){

        customerList = new ArrayList<>();
        customerList.add(customer1);
        customerList.add(customer2);

        customerService = new CustomerServiceImpl(customerDAO);

        when(customerDAO.getById("122345")).thenReturn(customer1);
        Assertions.assertSame(customer1, customerService.getCustomerById("122345"));

        doAnswer(invocation -> {
            customerList.remove(customer1);
            Assertions.assertEquals(1, customerList.size());
            return  null;
        }).when(customerDAO).deleteById(anyString());

    }


    @Test
    public void deleteThrowsException() throws FileNotFoundException {
        customerList = new ArrayList<>();
        customerList.add(customer1);

        customerService = new CustomerServiceImpl(customerDAO);

        when(customerDAO.getById(anyString())).thenReturn(null);
        Assertions.assertNull(customerService.getCustomerById(anyString()));

        Assertions.assertThrows(Exception.class, () -> doThrow().when(customerService).deleteByCustomerId(anyString()), (String) isNull());
//        Assertions.assertThrows(NoSuchElementException.class, () -> customerService.deleteByCustomerId(anyString()));
    }
}
