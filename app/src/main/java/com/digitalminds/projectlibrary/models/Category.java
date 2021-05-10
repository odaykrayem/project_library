package com.digitalminds.projectlibrary.models;


public class Category {
    private String name;
    private int iconId;
    private int numOfBooks;

    public Category(String name, int iconId) {
        this.name = name;
        this.iconId = iconId;

        //it will need a special treatment 0_0
        this.numOfBooks = 150;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getNumOfBooks() {
        return numOfBooks;
    }

    public void setNumOfBooks(int numOfBooks) {
        this.numOfBooks = numOfBooks;
    }
}
