package org.example.database.training;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface DAO <T>{
    List<T> getAll();
    T findById(String id);
    List<T> findByCountry(String country);
    T save(T item) throws IOException;
    void delete(String id) throws FileNotFoundException;
}
