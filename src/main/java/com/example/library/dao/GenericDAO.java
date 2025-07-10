package com.example.library.dao;

import java.sql.SQLException;
import java.util.List;

public interface GenericDAO<T> {
    void add(T obj) throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
    T getById(int id) throws SQLException;
    List<T> list() throws SQLException;
}

