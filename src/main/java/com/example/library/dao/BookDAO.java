package com.example.library.dao;

import com.example.library.model.Book;
import com.example.library.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final Connection conn = Database.getConnection();

    public BookDAO() throws SQLException {
    }

    // Adiciona um novo livro
    public void add(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, publisher, publication_year, barcode, quantity, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, book.getTitle());
        ps.setString(2, book.getAuthor());
        ps.setString(3, book.getPublisher());
        ps.setInt(4, book.getPublicationYear());
        ps.setString(5, book.getBarcode());
        ps.setInt(6, book.getQuantity());
        ps.setInt(7, book.getCategoryId());
        ps.execute();
        ps.close();
    }

    // Atualiza um livro existente
    public void update(Book b) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, publisher=?, publication_year=?, barcode=?, quantity=?, category_id=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, b.getTitle());
        ps.setString(2, b.getAuthor());
        ps.setString(3, b.getPublisher());
        ps.setInt(4, b.getPublicationYear());
        ps.setString(5, b.getBarcode());
        ps.setInt(6, b.getQuantity());
        ps.setInt(7, b.getCategoryId());
        ps.setInt(8, b.getId());
        ps.execute();
        ps.close();
    }

    // Remove um livro
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.execute();
        ps.close();
    }

    // Busca um livro por ID
    public Book getById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        Book b = null;
        if (rs.next()) {
            b = new Book();
            b.setId(rs.getInt("id"));
            b.setTitle(rs.getString("title"));
            b.setAuthor(rs.getString("author"));
            b.setPublisher(rs.getString("publisher"));
            b.setPublicationYear(rs.getInt("publication_year"));
            b.setBarcode(rs.getString("barcode"));
            b.setQuantity(rs.getInt("quantity"));
            b.setCategoryId(rs.getInt("category_id"));
        }

        rs.close();
        ps.close();
        return b;
    }

    // Lista todos os livros
    public List<Book> list() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Book b = new Book();
            b.setId(rs.getInt("id"));
            b.setTitle(rs.getString("title"));
            b.setAuthor(rs.getString("author"));
            b.setPublisher(rs.getString("publisher"));
            b.setPublicationYear(rs.getInt("publication_year"));
            b.setBarcode(rs.getString("barcode"));
            b.setQuantity(rs.getInt("quantity"));
            b.setCategoryId(rs.getInt("category_id"));
            books.add(b);
        }

        rs.close();
        ps.close();
        return books;
    }
    public void decrementQuantity(int bookId) throws SQLException {
        String sql = "UPDATE books SET quantity = quantity - 1 WHERE id = ? AND quantity > 0";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, bookId);
        ps.execute();
        ps.close();
    }

    public void incrementQuantity(int bookId) throws SQLException {
        String sql = "UPDATE books SET quantity = quantity + 1 WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, bookId);
        ps.execute();
        ps.close();
    }


    // Verifica se ainda há exemplares disponíveis
    public boolean isAvailable(int bookId) throws SQLException {
        String sql = "SELECT quantity FROM books WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, bookId);
        ResultSet rs = ps.executeQuery();

        boolean available = false;
        if (rs.next()) {
            available = rs.getInt("quantity") > 0;
        }

        rs.close();
        ps.close();
        return available;
    }


}
