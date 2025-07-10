package com.example.library.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LibraryUser {
    private int id;
    private String name;
    private String course;
    private String turma; // "class" é palavra reservada em Java

    public IntegerProperty idProperty() { return new SimpleIntegerProperty(id); }
    public StringProperty nameProperty() { return new SimpleStringProperty(name); }
    public StringProperty courseProperty() { return new SimpleStringProperty(course); }
    public StringProperty turmaProperty() { return new SimpleStringProperty(turma); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    @Override
    public String toString() {
        return name;
    }
}
