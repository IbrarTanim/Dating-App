package com.adda.datingapp.model;

public class OrderModel {
    String name;
    String email;
    int point;
    String taka;
    String last4DigitNumber;
    String ok;
    String applied;

    public OrderModel(String name, String email, int point, String taka, String last4DigitNumber, String ok, String applied) {
        this.name = name;
        this.email = email;
        this.point = point;
        this.taka = taka;
        this.last4DigitNumber = last4DigitNumber;
        this.ok = ok;
        this.applied = applied;
    }

    public OrderModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getTaka() {
        return taka;
    }

    public void setTaka(String taka) {
        this.taka = taka;
    }

    public String getLast4DigitNumber() {
        return last4DigitNumber;
    }

    public void setLast4DigitNumber(String last4DigitNumber) {
        this.last4DigitNumber = last4DigitNumber;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getApplied() {
        return applied;
    }

    public void setApplied(String applied) {
        this.applied = applied;
    }
}
