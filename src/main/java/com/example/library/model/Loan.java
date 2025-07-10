package com.example.library.model;

import java.time.LocalDate;

public class Loan {
    private int id;
    private int userId;
    private int bookId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;

    private LibraryUser user; // Aluno associado
    private Book book;        // Livro associado


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }

    public LibraryUser getUser() { return user; }
    public void setUser(LibraryUser user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    // >>> Métodos auxiliares para exibição na Tabela
    public String getUserName() {
        return user != null ? user.getName() : "";
    }

    public String getBookTitle() {
        return book != null ? book.getTitle() : "";
    }
}
