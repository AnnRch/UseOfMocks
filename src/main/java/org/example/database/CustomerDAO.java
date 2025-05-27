package org.example.database;

import org.example.database.enums.CustomerHeaders;
import org.example.model.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CustomerDAO implements DAO<Customer>{

    private List<Customer> customers;
    private String filename = "customers-100.csv";

    public CustomerDAO(){
        customers = new ArrayList<>();
    }

    public void connectToDataSource() throws FileNotFoundException {
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String headers = br.readLine();

            String line;
            while((line = br.readLine()) != null){
                String[] values;
                if(line.contains("\"") && line.indexOf('"') < line.lastIndexOf('"')){
                    values = replaceCommaInQuotesWithSemicolon(line).split(",");
                }
                else {
                    values = line.split(",");
                }

                customers.add(convertRecordToCustomer(values));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Customer> getAll() {
        return new ArrayList<>(customers);
    }

    @Override
    public Customer getById(String id) {
        return getAll().stream()
                .filter(customer -> customer.getCustomerId().endsWith(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Customer> findByCountry(String country) {
        return getAll().stream()
                .filter(customer -> customer.getCountry().equals(country))
                .toList();
    }

    @Override
    public Customer save(Customer customer) {
        Customer currentCustomer = getById(customer.getCustomerId());
        if(currentCustomer == null){
            currentCustomer = Customer.builder()
                    .index((long)customers.size() + 1)
                    .customerId(customer.getCustomerId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .company(customer.getCompany())
                    .city(customer.getCity())
                    .country(customer.getCountry())
                    .email(customer.getEmail())
                    .phone1(customer.getPhone1())
                    .phone2(customer.getPhone2())
                    .subscriptionDate(customer.getSubscriptionDate())
                    .website(customer.getWebsite())
                    .build();

            customers.add(currentCustomer);
            appendCsvFile(currentCustomer);

        }
        else {
            setCustomerFields(currentCustomer, customer);
            customers.set(customers.indexOf(currentCustomer), currentCustomer);
            rewriteCsvFile();
        }

        return currentCustomer;
    }

    @Override
    public void deleteById(String id) {
        Customer currentCustomer = getById(id);

        if(currentCustomer == null)
            throw new NoSuchElementException("Customer not found");

        customers.remove(currentCustomer);
        rewriteCsvFile();
    }

    private Customer convertRecordToCustomer(String[] record){
        Customer customer = new Customer();
        customer.setIndex(Long.valueOf(record[0]));
        customer.setCustomerId(record[1]);
        customer.setFirstName(record[2]);
        customer.setLastName(record[3]);
        customer.setCompany(record[4]);
        customer.setCity(record[5]);
        customer.setCountry(record[6]);
        customer.setPhone1(record[7]);
        customer.setPhone2(record[8]);
        customer.setEmail(record[9]);
        customer.setSubscriptionDate(LocalDate.parse(record[10]));
        customer.setWebsite(record[11]);
        //...
        return customer;
    }

    private String convertCustomerToRecord(Customer customer){

        return customer.getIndex() +
                "," +
                customer.getCustomerId() +
                "," +
                customer.getFirstName() +
                "," +
                customer.getLastName() +
                "," +
                customer.getCompany() +
                "," +
                customer.getCity() +
                "," +
                customer.getCountry() +
                "," +
                customer.getEmail() +
                "," +
                customer.getPhone1() +
                "," +
                customer.getPhone2() +
                "," +
                customer.getSubscriptionDate() +
                "," +
                customer.getWebsite() +
                "\n";
    }

    private String replaceCommaInQuotesWithSemicolon(String line){
        return line.substring(0, line.indexOf('"')) +
                line.substring(line.indexOf('"'), line.lastIndexOf('"') + 1).replaceAll(",", ";") +
                line.substring(line.lastIndexOf('"') + 1);
    }

    private void setCustomerFields(Customer current, Customer item){

        current.setFirstName(item.getFirstName());
        current.setLastName(item.getLastName());
        current.setCompany(item.getCompany());
        current.setCity(item.getCity());
        current.setCountry(item.getCountry());
        current.setEmail(item.getEmail());
        current.setPhone1(item.getPhone1());
        current.setPhone2(item.getPhone2());
        current.setSubscriptionDate(item.getSubscriptionDate());
        current.setWebsite(item.getWebsite());
        //...
    }

    private void appendCsvFile(Customer customer) {
        String content = convertCustomerToRecord(customer);
      try {
          Files.write(
                  Paths.get("customers-100.csv"),
                  content.getBytes(),
                  StandardOpenOption.APPEND
          );
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
    }

    private void rewriteCsvFile(){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("customers-1000.csv"))){

            StringBuilder headers = new StringBuilder();
            for(int i = 0; i < CustomerHeaders.values().length; i++){
                headers.append(CustomerHeaders.values()[i])
                        .append(",");
            }

            bufferedWriter.write(headers
                    .deleteCharAt(headers.length() - 1)
                    .append("\n")
                    .toString()
            );

               for(int i = 0; i < customers.size(); i++){
                   String line = convertCustomerToRecord(customers.get(i));
                   bufferedWriter.write((i + 1) + line.substring(line.indexOf(",")));
               }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
