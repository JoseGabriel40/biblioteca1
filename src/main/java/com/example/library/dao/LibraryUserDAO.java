package com.example.library.dao;

import com.example.library.model.LibraryUser;
import com.example.library.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryUserDAO {

    public void add(LibraryUser user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getCourse()); // course = email
            ps.setString(3, user.getTurma());  // turma = password
            ps.executeUpdate();
        }
    }

    public void update(LibraryUser user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getCourse());
            ps.setString(3, user.getTurma());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<LibraryUser> list() throws SQLException {
        String sql = "SELECT * FROM users";
        List<LibraryUser> users = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LibraryUser user = new LibraryUser();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setCourse(rs.getString("email")); // email = course
                user.setTurma(rs.getString("password")); // password = turma
                users.add(user);
            }
        }

        return users;
    }

    public LibraryUser getById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                LibraryUser user = new LibraryUser();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setCourse(rs.getString("email"));
                user.setTurma(rs.getString("password"));
                return user;
            }
        }
        return null;
    }

    public LibraryUser getByCourseAndClass(String course, String turma) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course);
            ps.setString(2, turma);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                LibraryUser user = new LibraryUser();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setCourse(rs.getString("email"));
                user.setTurma(rs.getString("password"));
                return user;
            }
        }
        return null;
    }
}
