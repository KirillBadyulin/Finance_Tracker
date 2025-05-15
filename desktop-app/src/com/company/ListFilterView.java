package com.company;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ListFilterView {
    private VBox root;

    private ComboBox<Integer> startMonthComboBox;
    private ComboBox<Integer> endMonthComboBox;
    private ComboBox<Integer> yearComboBox;
    private ComboBox<String> sortingValueComboBox;
    private ComboBox<String> orderComboBox;
    private Button applyButton;

    public ListFilterView() {
        initializeUI();
    }

    private void initializeUI() {
        root = new VBox(10); // Vertical layout with spacing
        root.setPadding(new Insets(10));

        // Month selection
        startMonthComboBox = new ComboBox<>();
        endMonthComboBox = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            startMonthComboBox.getItems().add(i);
            endMonthComboBox.getItems().add(i);
        }

        // Year selection
        yearComboBox = new ComboBox<>();
        for (int i = 2020; i <= 2030; i++) {
            yearComboBox.getItems().add(i);
        }

        // Sorting value selection
        sortingValueComboBox = new ComboBox<>();
        sortingValueComboBox.getItems().addAll("Name", "Value", "Category", "Date");

        // Order selection
        orderComboBox = new ComboBox<>();
        orderComboBox.getItems().addAll("Ascending", "Descending");

        // Apply button
        applyButton = new Button("Apply");

        // Add components to layout
        root.getChildren().addAll(
                new Label("Start Month:"), startMonthComboBox,
                new Label("End Month:"), endMonthComboBox,
                new Label("Year:"), yearComboBox,
                new Label("Sort By:"), sortingValueComboBox,
                new Label("Order:"), orderComboBox,
                applyButton
        );
    }

    // Getters for components (for event handling)
    public VBox getRoot() {
        return root;
    }

    public ComboBox<Integer> getStartMonthComboBox() {
        return startMonthComboBox;
    }

    public ComboBox<Integer> getEndMonthComboBox() {
        return endMonthComboBox;
    }

    public ComboBox<Integer> getYearComboBox() {
        return yearComboBox;
    }

    public ComboBox<String> getSortingValueComboBox() {
        return sortingValueComboBox;
    }

    public ComboBox<String> getOrderComboBox() {
        return orderComboBox;
    }

    public Button getApplyButton() {
        return applyButton;
    }
}
