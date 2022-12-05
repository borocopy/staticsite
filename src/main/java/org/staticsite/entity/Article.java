package org.staticsite.entity;

import com.ibm.icu.text.Transliterator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Article {
    private String author;
    private String title;
    private Date dateWritten;
    private String content;

    public Article(String author, String title, Date dateWritten, String content) {
        this.author = author;
        this.title = title;
        this.dateWritten = dateWritten;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title.trim();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateWritten() {
        return dateWritten;
    }

    public void setDateWritten(Date dateWritten) {
        this.dateWritten = dateWritten;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");
        Transliterator toLatinTrans = Transliterator.getInstance("Cyrillic-Latin");
        String result = String.join("-", toLatinTrans.transliterate(getTitle()).split(" "));
        return dateParser.format(getDateWritten()) + "-" + result.toLowerCase();
    }
}
