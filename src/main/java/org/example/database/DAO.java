package org.example.database;

import java.math.BigDecimal;
import java.util.List;

public interface DAO <T>{
    List<T> getAll();
    T getById(String id);
    List<T> findByCountry(String country);
    T save(T t);
    void deleteById(String id);
}
