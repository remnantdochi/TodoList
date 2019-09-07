package com.example.dochi;

public class todo{

    private String title;

    private String detail;

    private String duedate;

    private boolean isSelected;

    public todo() {

    }

    public todo(String title, String detail, String date) {

        this.title = title;
        this.detail = detail;
        this.duedate = duedate;
        this.isSelected = false;

    }

    public todo(String title, String detail, String duedate, boolean isSelected) {

        this.title = title;
        this.detail = detail;
        this.duedate = duedate;
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDate() { return duedate; }

    public void setDate(String duedate) { this.duedate = duedate; }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}