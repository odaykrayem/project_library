package com.digitalminds.projectlibrary.offlinedata;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Booksdb {

    @NonNull
    @PrimaryKey
    String id;

    @ColumnInfo(name = "bookName_db")
    String bookName;

    @ColumnInfo(name = "authorName_db")
    String authorName;

    @ColumnInfo(name = "bookImage_db")
    String bookImage;

    @ColumnInfo(name = "pdfUrl_db")
    String pdfUrl;

    @ColumnInfo(name = "categoryNameEn_db")
    String categoryNameEn;

    @ColumnInfo(name = "categoryNameAr_db")
    String categoryNameAr;

    @ColumnInfo(name = "categoryNameKu_db")
    String categoryNameKu;

    @ColumnInfo(name = "ratingBar_db")
    String ratingBar;

    @ColumnInfo(name = "description_en_db")
    String descriptionEN;

    @ColumnInfo(name = "description_ar_db")
    String descriptionAR;

    @ColumnInfo(name = "description_kurd_db")
    String descriptionKURD;

    @ColumnInfo(name = "language_db")
    String language;

    @ColumnInfo(name = "pages_db")
    String pagesNumber;

    @ColumnInfo(name = "downloads_db")
    String downloads;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCategoryNameEn() {
        return categoryNameEn;
    }

    public void setCategoryNameEn(String categoryNameEn) {
        this.categoryNameEn = categoryNameEn;
    }

    public String getCategoryNameAr() {
        return categoryNameAr;
    }

    public void setCategoryNameAr(String categoryNameAr) {
        this.categoryNameAr = categoryNameAr;
    }

    public String getCategoryNameKu() {
        return categoryNameKu;
    }

    public void setCategoryNameKu(String categoryNameKu) {
        this.categoryNameKu = categoryNameKu;
    }

    public String getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(String ratingBar) {
        this.ratingBar = ratingBar;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getDescriptionAR() {
        return descriptionAR;
    }

    public void setDescriptionAR(String descriptionAR) {
        this.descriptionAR = descriptionAR;
    }

    public String getDescriptionKURD() {
        return descriptionKURD;
    }

    public void setDescriptionKURD(String descriptionKURD) {
        this.descriptionKURD = descriptionKURD;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPagesNumber() {
        return pagesNumber;
    }

    public void setPagesNumber(String pagesNumber) {
        this.pagesNumber = pagesNumber;
    }
}
