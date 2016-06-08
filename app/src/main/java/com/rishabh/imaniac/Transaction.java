package com.rishabh.imaniac;

/**
 * Created by user on 01-06-2016.
 */
public class Transaction {
    private int amount;
    private String category;
    private String description;
    private String id;
    private String state;
    private String time;

    public String getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public String getTime() {
        return time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setState(String state) {
        this.state = state;
    }
}