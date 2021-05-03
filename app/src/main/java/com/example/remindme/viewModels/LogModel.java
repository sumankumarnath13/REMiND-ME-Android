package com.example.remindme.viewModels;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class LogModel {
    private final ArrayList<String> logs = new ArrayList<>();

    public void clear() {
        logs.clear();
    }

    public void add(String message) {

    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < logs.size(); i++) {
            builder.append(String.format("%s\n", logs.get(i)));
        }

        return builder.toString();
    }
}
