package com.example.crabfood.model;

public class SimpleCategory {
    private Long id;
    private String name;

    public SimpleCategory() {
    }

    public SimpleCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
