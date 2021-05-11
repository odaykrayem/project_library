package com.digitalminds.projectlibrary.models;

public class Book {

    private String bookImagePath;
    private String bookTitle;
    private String bookAuthorName;

    public Book(String bookTitle, String bookAuthorName, String bookImagePath) {
        this.bookImagePath = bookImagePath;
        this.bookTitle = bookTitle;
        this.bookAuthorName = bookAuthorName;
    }

    //for dummy data purpose
    public Book(String bookTitle, String bookAuthorName) {
        this.bookTitle = bookTitle;
        this.bookAuthorName = bookAuthorName;
    }


    public String getBookImagePath() {
        return bookImagePath;
    }

    public void setBookImagePath(String bookImagePath) {
        this.bookImagePath = bookImagePath;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }


    public String getBookAuthorName() {
        return bookAuthorName;
    }

    public void setBookAuthorName(String bookAuthorName) {
        this.bookAuthorName = bookAuthorName;
    }

    @Override
    public String toString() {
        return "book{" +
                "bookTitle='" + bookTitle + '\'' +
                ", bookAuthorName='" + bookAuthorName + '\'' +
                '}';
    }

}
