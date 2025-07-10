package com.example.library.dao;

import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.LibraryUser;
import com.example.library.util.Database;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class LoanDAO {

    public void registerLoan(Loan loan) throws SQLException {
        Connection conn = Database.getConnection();
        String sql = "INSERT INTO loans (user_id, book_id, loan_date, return_date, returned) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, loan.getUserId());
        ps.setInt(2, loan.getBookId());
        ps.setDate(3, Date.valueOf(loan.getLoanDate()));
        ps.setDate(4, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
        ps.setBoolean(5, false);
        ps.execute();
        ps.close();
    }

    public List<Loan> listOpenLoansWithDetails() throws SQLException {
        Connection conn = Database.getConnection();
        String sql = "SELECT l.*, u.name AS user_name, b.title AS book_title " +
                "FROM loans l " +
                "JOIN users u ON l.user_id = u.id " +
                "JOIN books b ON l.book_id = b.id " +
                "WHERE l.returned = false";

        ResultSet rs = conn.createStatement().executeQuery(sql);
        List<Loan> loans = new ArrayList<>();

        while (rs.next()) {
            Loan l = extractLoan(rs);

            LibraryUser u = new LibraryUser();
            u.setId(l.getUserId());
            u.setName(rs.getString("user_name"));
            l.setUser(u);

            Book b = new Book();
            b.setId(l.getBookId());
            b.setTitle(rs.getString("book_title"));
            l.setBook(b);

            loans.add(l);
        }

        rs.close();
        return loans;
    }

    public List<Loan> listUserHistory(int userId) throws SQLException {
        String sql = "SELECT l.*, b.title as book_title FROM loans l " +
                "JOIN books b ON l.book_id = b.id WHERE l.user_id = ?";
        List<Loan> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Loan loan = extractLoan(rs);

                Book b = new Book();
                b.setId(rs.getInt("book_id"));
                b.setTitle(rs.getString("book_title"));
                loan.setBook(b);

                list.add(loan);
            }
        }

        return list;
    }

    public void registerReturn(int loanId) throws SQLException {
        Connection conn = Database.getConnection();
        String sql = "UPDATE loans SET return_date = CURRENT_DATE, returned = true WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, loanId);
        ps.execute();
        ps.close();
    }

    public List<Loan> listReturnedLoansWithDetails() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE returned = true";
        ResultSet rs = Database.getConnection().createStatement().executeQuery(sql);

        LibraryUserDAO userDAO = new LibraryUserDAO();
        BookDAO bookDAO = new BookDAO();

        while (rs.next()) {
            Loan l = extractLoan(rs);
            l.setReturned(true);
            l.setUser(userDAO.getById(l.getUserId()));
            l.setBook(bookDAO.getById(l.getBookId()));
            loans.add(l);
        }

        rs.close();
        return loans;
    }

    public boolean hasActiveLoan(int userId, int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND book_id = ? AND returned = false";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Map<String, Integer> countLoansByBook() throws SQLException {
        String sql = "SELECT b.title, COUNT(*) as total FROM loans l " +
                "JOIN books b ON l.book_id = b.id GROUP BY b.title";
        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("title"), rs.getInt("total"));
            }
        }

        return map;
    }

    public int countActiveLoansByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND returned = false";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Loan> findReturnedLoansBetweenDates(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT l.*, u.name AS user_name, b.title AS book_title " +
                "FROM loans l " +
                "JOIN users u ON l.user_id = u.id " +
                "JOIN books b ON l.book_id = b.id " +
                "WHERE l.returned = true AND l.return_date BETWEEN ? AND ?";

        List<Loan> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setId(rs.getInt("id"));
                loan.setUserId(rs.getInt("user_id"));
                loan.setBookId(rs.getInt("book_id"));
                loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
                loan.setReturnDate(rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
                loan.setReturned(rs.getBoolean("returned"));

                LibraryUser user = new LibraryUser();
                user.setId(loan.getUserId());
                user.setName(rs.getString("user_name"));
                loan.setUser(user);

                Book book = new Book();
                book.setId(loan.getBookId());
                book.setTitle(rs.getString("book_title"));
                loan.setBook(book);

                list.add(loan);
            }
        }

        return list;
    }



    // ✅ Método auxiliar que extrai e monta um objeto Loan a partir do ResultSet
    private Loan extractLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
        loan.setReturnDate(rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
        loan.setReturned(rs.getBoolean("returned"));
        return loan;
    }
}
