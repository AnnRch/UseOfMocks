package org.example;

import org.example.file.FileOperations;

import java.io.IOException;

public class AppOperations {
    public static void main(String[] args) {
        try{
            FileOperations.addRatingDataToCsvFile("customers-100.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
