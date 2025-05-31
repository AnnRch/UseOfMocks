package org.example.service;

import org.example.database.CustomerDAO;
import org.example.model.Customer;
import org.example.service.impl.CustomerServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

    private List<Customer> customerList;
    protected Customer customer1 = new Customer(), customer2, customer3;

    @Mock
    private CustomerDAO customerDAO;

    @InjectMocks
    private CustomerServiceImpl customerService;


    @BeforeEach
    public void init(){
        customer2 = Customer.builder()
                .customerId("098876CUS")
                .firstName("Nazar")
                .lastName("Rozumeiko")
                .company("MDPU")
                .city("Zaporizhzhia")
                .country("Ukraine")
                .rating(BigDecimal.valueOf(83.05676855913279))
                .build();
    }

    private void setUp(){
        customer1 = Customer.builder()
                .customerId("122345")
                .firstName("Irina")
                .lastName("Kuznetsova")
                .company("ATB")
                .city("Chernivtsi")
                .country("Ukraine")
                .rating(BigDecimal.valueOf(91.4371696391857))
                .build();

        customer2 = Customer.builder()
                .customerId("098876CUS")
                .firstName("Nazar")
                .lastName("Rozumeiko")
                .company("MDPU")
                .city("Zaporizhzhia")
                .country("Ukraine")
                .rating(BigDecimal.valueOf(83.05676855913279))
                .build();
    }

    @Test
    public void getAllTest(){

        setUp();

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

        when(customerDAO.getAll()).thenReturn(customerList);
        Assertions.assertEquals(0, customerService.getAll().size());
    }


    @Test
    public void getAllByCountryAndRatingSuccessful(){

        setUp();

        customerList = new ArrayList<>();
        customerList.add(customer1);
        customerList.add(customer2);

        System.out.println(customer2);
        customerService = new CustomerServiceImpl(customerDAO);


        BigDecimal bound = BigDecimal.valueOf(90.0);
        BigDecimal rating = customerList.get(0).getRating();

        bound = bound.setScale(2, RoundingMode.HALF_EVEN);
        rating = rating.setScale(2,RoundingMode.HALF_EVEN);

        Assertions.assertTrue(bound.compareTo(rating) < 0);
    }

//    static Stream<Arguments> testCases(){
//        return Stream.of(
//              Arguments.of()
//        );
//    }

    @Test
    public void saveAppendTestCase() throws IOException {

        customerList = new ArrayList<>();
        customerService = new CustomerServiceImpl(customerDAO);

          when(customerDAO.getById(customer1.getCustomerId())).thenReturn(null);
          Assertions.assertNull(customerService.getCustomerById(customer1.getCustomerId()));

        when(customerDAO.save(customer1)).thenReturn(customer1);
        Assertions.assertSame(customer1, customerService.save(customer1));
    }


    @Test
    public void saveUpdateTestCase() throws IOException {

        setUp();

        customerList = new ArrayList<>();
        customerList.add(customer1);

        customerService = new CustomerServiceImpl(customerDAO);

        when(customerDAO.getById(customer1.getCustomerId())).thenReturn(customer1);
        Assertions.assertSame(customer1, customerService.getCustomerById(customer1.getCustomerId()));

        when(customerDAO.save(customer1)).thenReturn(customer1);
        Assertions.assertSame(customer1, customerService.save(customer1));
    }

    @Test
    public void saveNullTestCase(){

        customerService = new CustomerServiceImpl(customerDAO);
        when(customerDAO.getById(null)).thenThrow(NullPointerException.class);

        Assertions.assertThrows(NullPointerException.class, () -> customerService.getCustomerById(null));
    }

    @Test
    public void deleteSuccessfulTestCase() throws FileNotFoundException {

        customerList = new ArrayList<>();
        customerList.add(customer1);
        customerList.add(customer2);

        customerService = new CustomerServiceImpl(customerDAO);

        when(customerDAO.getById("122345")).thenReturn(customer1);
        Assertions.assertSame(customer1, customerService.getCustomerById("122345"));

        customerService.deleteByCustomerId("122345");
        verify(customerDAO).deleteById("122345");
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
