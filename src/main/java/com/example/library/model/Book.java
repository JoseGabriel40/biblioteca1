package com.example.library.model;

import javafx.beans.property.*;

public class Book {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private StringProperty author = new SimpleStringProperty();
    private StringProperty publisher = new SimpleStringProperty();
    private StringProperty barcode = new SimpleStringProperty();
    private IntegerProperty publicationYear = new SimpleIntegerProperty();
    private int categoryId;
    private IntegerProperty quantity = new SimpleIntegerProperty();

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String value) { author.set(value); }
    public StringProperty authorProperty() { return author; }

    public String getPublisher() { return publisher.get(); }
    public void setPublisher(String value) { publisher.set(value); }
    public StringProperty publisherProperty() { return publisher; }

    public String getBarcode() { return barcode.get(); }
    public void setBarcode(String value) { barcode.set(value); }
    public StringProperty barcodeProperty() { return barcode; }

    public int getPublicationYear() { return publicationYear.get(); }
    public void setPublicationYear(int value) { publicationYear.set(value); }
    public IntegerProperty publicationYearProperty() { return publicationYear; }

    @Override
    public String toString() {
        return getTitle(); // Exibe o título no ComboBox
    }
}
