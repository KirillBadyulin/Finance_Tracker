package com.company;

import java.time.LocalDate;

public class Entry {
    private int id = 0;
    private int value;
    private String name;
    private Category category;
    private LocalDate date;
    private String description;


    public Entry() {

    }

    public Entry(int value, String name, Category category) {
        this(0, value, name, category, "", LocalDate.now());
    }

    public Entry(int value, String name, Category category, LocalDate date) {
        this(0, value, name, category, "",  date);
    }

    public Entry(int value, String name, Category category, String description) {
        this(0, value, name, category, description, LocalDate.now());
    }

    public Entry(int value, String name, Category category, String description, LocalDate date) {
        this(0, value, name, category, description, date);
    }

    public Entry(int id, int value, String name, Category category, String description, LocalDate date) {
        this.name = name;
        this.value = value;
        this.category = category;
        this.date = date;
        this.description = description;
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

}


