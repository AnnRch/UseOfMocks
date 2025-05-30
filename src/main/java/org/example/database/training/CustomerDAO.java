package org.example.database.training;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.database.enums.CustomerHeaders;
import org.example.model.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.*;


public class CustomerDAO implements DAO<Customer> {

    private List<Customer> customers;
    private String fileName;

    public CustomerDAO(){
        customers = new ArrayList<>();
        fileName = "customers-10000.csv";
    }

   public CustomerDAO(String fileName) {
       customers = new ArrayList<>();
       this.fileName = fileName;
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

   public void connectToDataSource() throws IOException {
       createWorkBook(this.fileName);
   }

    @Override
    public List<Customer> getAll() {
        return new ArrayList<>(customers);
    }

    @Override
    public Customer findById(String id) {
        return customers.stream()
                .filter(c -> c.getCustomerId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Customer> findByCountry(String country) {
        return customers.stream()
                .filter(c -> c.getCountry().equals(country))
                .sorted(Comparator.comparing(o -> o.getCountry().toLowerCase()))
                .toList();
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

    private void createWorkBook(String fileName){
        try(FileOutputStream out = new FileOutputStream("customers.xlsx");
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            XSSFWorkbook workbook = new XSSFWorkbook()) {

            String[] headers = br.readLine().split(",");

            Sheet sheet = workbook.createSheet();

            String line;
            int rowNumber = 0;
            while((line = br.readLine()) != null){
                String[] values = null;
                if(line.contains("\"") && line.indexOf('"') < line.lastIndexOf('"')){
                    values = replaceCommaInQuotesWithSemicolon(line).split(",");
                }
                else {
                    values = line.split(",");
                }

                Row row = sheet.createRow(rowNumber++);
                for(int i = 0; i < values.length; i++){
                    Cell cell = row.createCell(i);
                    cell.setCellValue(values[i]);
                }

                customers.add(convertRecordToCustomer(values));

            }

            workbook.write(out);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateCustomerInCsv(int row, Customer customer) {

        try(Workbook workbook = WorkbookFactory.create(new File("customers.xlsx"))){
            Sheet sheet = workbook.getSheetAt(0);
            row--;
            sheet.getRow(row).getCell(0).setCellValue(customer.getIndex());
            sheet.getRow(row).getCell(1).setCellValue(customer.getCustomerId());
            sheet.getRow(row).getCell(2).setCellValue(customer.getFirstName());
            sheet.getRow(row).getCell(3).setCellValue(customer.getLastName());
            sheet.getRow(row).getCell(4).setCellValue(customer.getCompany());
            sheet.getRow(row).getCell(5).setCellValue(customer.getCity());
            sheet.getRow(row).getCell(6).setCellValue(customer.getCountry());
            sheet.getRow(row).getCell(7).setCellValue(customer.getPhone1());
            sheet.getRow(row).getCell(8).setCellValue(customer.getPhone2());
            sheet.getRow(row).getCell(9).setCellValue(customer.getEmail());
            sheet.getRow(row).getCell(10).setCellValue(customer.getSubscriptionDate());
            sheet.getRow(row).getCell(11).setCellValue(customer.getWebsite());

        } catch (IOException | EncryptedDocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteCustomerFromFile(int row, Customer customer) {
            try(Workbook workbook = WorkbookFactory.create(new File("customers.xlsx"))){
                Sheet sheet = workbook.getSheetAt(0);
                System.out.println("rows -> " + sheet.getPhysicalNumberOfRows());
                System.out.println("row = " + row);
                Row currentRow = sheet.getRow(row - 1);
                sheet.removeRow(currentRow);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    private void rewriteCsvFile(){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("customers-10000.csv"))
        ){
            StringBuilder headers = new StringBuilder();
            for(int i = 0; i < CustomerHeaders.values().length; i++){
                headers.append(CustomerHeaders.values()[i])
                        .append(",");
            }
            headers.deleteCharAt(headers.length() - 1);
            bufferedWriter.write(headers.toString());

            for(int i = 0; i < customers.size(); i++){
//                System.out.println("customer index : " + customers.get(i).getIndex());
                String content = i < customers.size() - 1
                        ? convertCustomerToRecord(customers.get(i))
                        : convertCustomerToRecord(customers.get(i)).trim();

//                System.out.println((i + 1) + content.substring(content.indexOf(",")));
                bufferedWriter.write((i + 1) + content.substring(content.indexOf(",")));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer save(Customer item) throws IOException {
        Customer currentCustomer = findById(item.getCustomerId());
        if(currentCustomer == null){

            currentCustomer = Customer.builder()
                    .index( (long) customers.size() + 1)
                    .customerId(item.getCustomerId())
                    .firstName(item.getFirstName())
                    .lastName(item.getLastName())
                    .company( item.getCompany())
                    .city(item.getCity())
                    .country( item.getCountry())
                    .email(item.getEmail())
                    .phone1(item.getPhone1())
                    .phone2( item.getPhone2())
                    .subscriptionDate(item.getSubscriptionDate())
                    .website(item.getWebsite())
                    .rating(item.getRating())
                    .build();

            customers.add(currentCustomer);

            String content = convertCustomerToRecord(currentCustomer);
            Files.write(
                    Paths.get(fileName),
                    content.getBytes(),
                    StandardOpenOption.APPEND
            );
        }
        else {
           setCustomerFields(currentCustomer, item);
            System.out.println("index = " + currentCustomer.getIndex());
           customers.set((int) (currentCustomer.getIndex() - 1), currentCustomer);
           updateCustomerInCsv(Math.toIntExact(currentCustomer.getIndex()), currentCustomer);
        }
        return currentCustomer;
    }

    @Override
    public void delete(String  id) throws FileNotFoundException {
        Customer currentCustomer = findById(id);
        if(currentCustomer == null)
            throw  new NoSuchElementException("customer not found");
        customers.remove(currentCustomer);
        System.out.println("customers size -> " + customers.size());
        System.out.println("index = " + currentCustomer.getIndex());
        deleteCustomerFromFile(Math.toIntExact(currentCustomer.getIndex()), currentCustomer);
        rewriteCsvFile();
    }
}
