package com.example.library.dao;

import com.example.library.model.Category;
import com.example.library.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private Connection conn = Database.getConnection();

    public CategoryDAO() throws SQLException {
    }

    public List<Category> list() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";
        ResultSet rs = conn.createStatement().executeQuery(sql);

        while (rs.next()) {
            Category c = new Category();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            categories.add(c);
        }

        rs.close();
        return categories;
    }
}
