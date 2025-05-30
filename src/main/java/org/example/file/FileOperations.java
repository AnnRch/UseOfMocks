package org.example.file;

import org.example.model.Customer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileOperations {

    public static void addRatingDataToCsvFile(String filename) throws IOException {

        try(BufferedReader br = new BufferedReader(new FileReader(filename));
            BufferedWriter bw = new BufferedWriter(new FileWriter("customers.csv"))
        ){
            String header = br.readLine();
            bw.write(header.concat(",").concat("Rating\n"));

            String line;
            while ((line = br.readLine()) != null){
                bw.write(line
                        .concat(",")
//                        .concat(String.format("%.2f", new Random().nextDouble() * (0.1 + (100.0 - 0.1))))
                                .concat(String.valueOf(new Random().nextDouble() * (100.0 - 0.1)))
                        .concat("\n")
                );
            }
        }

    }
}
