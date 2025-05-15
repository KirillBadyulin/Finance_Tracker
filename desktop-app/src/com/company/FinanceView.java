package com.company;

import java.util.List;

public interface FinanceView {
    void displayEntries(List<Entry> entries);
    void showError(String message);
}
