package com.example.protimer.ui.ui;

public class history {
    String history;
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public history(){}

    public history(String history, String date) {
        this.history = history;
        this.date = date;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
