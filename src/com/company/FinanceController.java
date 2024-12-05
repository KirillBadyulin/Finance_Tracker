package com.company;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class FinanceController {
    private final FinanceDAO financeDAO;
    private final FinanceViewDesktop view;
    private Entry selectedEntry;


    Map<String, Map<String, Integer>> entriesByMonthCurrentYear;
    Map<String, Integer> yearlySumByCategory;
    Map<String, Integer> monthlyCategoriesValues;
    Map<String, Button> monthsButtons;
    List<Entry> limitedEntrySelection;


    String currentMonth;
    int currentMonthInteger;
    int currentYear;
    List<String> defaultCategories;
    int thisYearSum;
    int thisMonthSum;

    int[] selectedAndSavedMonths;
    int[] selectedAndSavedYears;
    SortingOrder savedOrderChoice;
    ColumnChoice savedColumnChoice;

    boolean limitedTableSelection = false;


    public FinanceController(FinanceDAO financeDAO, FinanceViewDesktop view) {
        this.financeDAO = financeDAO;
        this.view = view;
        currentMonth = LocalDate.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
        currentMonthInteger = LocalDate.now().getMonthValue();
        currentYear = LocalDate.now().getYear();
        monthsButtons = view.getMonthsButtons();//IS IT IN CORRECT TURN, AFTER CREATING THAT CHART AND SCROLLABLE METADATA?


        defaultCategories = new LinkedList<>(List.of("RESTAURANTS", "ENTERTAINMENT", "GROCERIES", "SUGARS_AND_JUNKFOOD"));
        try {
            entriesByMonthCurrentYear = financeDAO.getYearlySumByMonthAndCategory(currentYear);
            yearlySumByCategory = financeDAO.getYearlySumPerCategory(currentYear);
            monthlyCategoriesValues = entriesByMonthCurrentYear.get(currentMonth);

            thisYearSum = financeDAO.getSumPerYears(new int[] {currentYear});
            thisMonthSum = financeDAO.getSumPerMonth(currentMonthInteger, currentYear);
        } catch (Exception e) {
            System.out.println("Initialization of Controller class failed");
        }

        initializeHandlers();
        listAllEntries();
    }

    private void initializeHandlers() {
        view.getTableView().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("ITEM SELECTED");
                selectedEntry = newSelection;
            }
        });

        view.getCreateButton().setOnAction(e -> createEntry());

        view.getUpdateButton().setOnAction(e -> {
            if (selectedEntry != null) {
                updateEntry();
                clearSelection();
            } else {
                view.showError("Please select an item to update", "SELECTION ERROR");
            }
        });

        view.getDeleteButton().setOnAction(e -> {
            if(selectedEntry != null) {
                showDeleteConfirmationPopup(selectedEntry);
                clearSelection();
            } else {
                view.showError("Please select an item to delete", "SELECTION ERROR");
                            }
        });

        view.getListByMonth().setOnAction(e -> listByMonth());

        view.getListAllButton().setOnAction(e -> listAllEntries());

        view.getCogButton().setOnAction(e -> {
            defaultCategories = view.showMetadataPopup(defaultCategories); //CHECK IF ITS RETURNING RIGHT VALUE
            view.getCogButton().disarm();
            view.getCogButton().getParent().requestFocus();

            //WRONG VALUE FOR LIMITED SELECTION
            if (!limitedTableSelection) {
                view.updateMetadataGrid(monthlyCategoriesValues, yearlySumByCategory, defaultCategories, thisYearSum, thisMonthSum, MONTH_OR_PERIOD.MONTH);
            } else {
                //MONTHLY SUM SHOULD BE DIFFERENT. MONTHLY SUM SAME AS IN listEntries() method, passed into updateMetadata
                view.updateMetadataGrid(monthlyCategoriesValues, null, defaultCategories, 0, thisMonthSum, MONTH_OR_PERIOD.PERIOD);

            }
        });

        //INITIALIZING MONTHSBUTONS MAP<STRING,BUTTON>
        for (Map.Entry<String, Button> entry : monthsButtons.entrySet()) {
            String monthName = entry.getKey();
            Button button = entry.getValue();

            if (monthName.equals("YEAR")) {
                button.setOnAction(k -> updatePieAndBarCharts(monthName));
            } else {
                button.setOnAction(k -> updatePieAndBarCharts(AppConstants.MONTHS_DICTIONARY.get(monthName))); // Attach listener
            }
        }
    }

    private void createEntry() {
        Stage creationWindow = new Stage();
        creationWindow.setTitle("Create Entry");

        VBox listOfFields = new VBox(10); //???
        listOfFields.setPadding(new Insets(10));//???

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField valueField = new TextField();
        valueField.setPromptText("Value");

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(
                "BILLS_AND_UTILITIES",
                "CAR_AND_TRANSPORTATION",
                "HEALTH",
                "INSURANCE",
                "GROCERIES",
                "SUGARS_AND_JUNKFOOD",
                "RESTAURANTS",
                "INVESTMENTS",
                "ENTERTAINMENT",
                "MISC");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e-> {
            try {
                String name = nameField.getText();
                if (valueField.getText().isEmpty()) {
                    view.showErrorPopup("Value must be provided and should be a number.");
                    return;
                }
                int value = Integer.parseInt(valueField.getText());
                String category = categoryBox.getValue();
                String description = descriptionArea.getText();
                LocalDate date = datePicker.getValue();

                if (name.isEmpty() || category == null) {
                    view.showErrorPopup("Please fill all required fields.");
                    return;
                }

                Entry newEntry = new Entry(value, name, Category.valueOf(category), description, date);
                financeDAO.createItem(newEntry);

                updateCurrentLocalMapsAndValues(); //UPDATES ALL CURRENT VALUES;
                if (limitedTableSelection) {
                    updateLimitedEntrySelection();
                }

                updateGUI();
                creationWindow.close();

            } catch (Exception creationException) {
                view.showErrorPopup("INVALID OR EMPTY VALUE");
                System.out.println("Problem creating the entry: " + creationException.getMessage() + " EXCEPTION: " + creationException.getClass());
            }
        });

        cancelButton.setOnAction(e-> creationWindow.close());

        listOfFields.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Value:"), valueField,
                new Label("Category:"), categoryBox,
                new Label("Description:"), descriptionArea,
                new Label("Date:"), datePicker,
                new HBox(10, saveButton, cancelButton)
        );

        Scene ActionScene = new Scene(listOfFields, 400, 400);
        creationWindow.setScene(ActionScene);
        creationWindow.initModality(Modality.APPLICATION_MODAL);
        creationWindow.showAndWait();
    }

    private void updateEntry() {
        Stage updateWindow = new Stage();
        updateWindow.setTitle("Update Entry");
        VBox listOfFields = new VBox(10); //???
        listOfFields.setPadding(new Insets(10));//???

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setText(selectedEntry.getName());

        TextField valueField = new TextField();
        valueField.setPromptText("Value");
        valueField.setText(String.valueOf(selectedEntry.getValue()));  //STRING VALUE OF???

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("BILLS_AND_UTILITIES",
                "CAR_AND_TRANSPORTATION",
                "HEALTH",
                "INSURANCE",
                "GROCERIES",
                "SUGARS_AND_JUNKFOOD",
                "RESTAURANTS",
                "INVESTMENTS",
                "ENTERTAINMENT",
                "MISC");
        categoryBox.setValue(selectedEntry.getCategory().toString());

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setText(selectedEntry.getDescription());

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(selectedEntry.getDate());

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e-> {
            try {
                String name = nameField.getText();
                if (valueField.getText().isEmpty()) {
                    view.showErrorPopup("Value must be provided and should be a number.");
                    return;
                }
                int value = Integer.parseInt(valueField.getText());
                String category = categoryBox.getValue();
                String description = descriptionArea.getText();
                LocalDate date = datePicker.getValue();

                if (name.isEmpty() || category == null) {
                    view.showErrorPopup("Please fill all required fields.");
                    return;
                }

                //Calling confirmation here
                    Entry newEntry = new Entry(value, name, Category.valueOf(category), description, date);
                if (showUpdateConfirmationPopup(selectedEntry, newEntry)) {
                    financeDAO.updateItem(selectedEntry, newEntry);
                    updateCurrentLocalMapsAndValues();
                    if (limitedTableSelection) {
                        updateLimitedEntrySelection();
                    }
                    updateGUI();
                } else {
                    clearSelection();
                    return;
                }
                selectedEntry = null;
                updateWindow.close();

            } catch (Exception creationException) {
                view.showErrorPopup("INVALID OR EMPTY VALUE");
                System.out.println("Problem updating the entry: " + creationException.getMessage() + " EXCEPTION: " + creationException.getClass());
            }
        });

        cancelButton.setOnAction(e-> {
            updateWindow.close();
        });

        listOfFields.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Value:"), valueField,
                new Label("Category:"), categoryBox,
                new Label("Description:"), descriptionArea,
                new Label("Date:"), datePicker,
                new HBox(10, saveButton, cancelButton)
        );

        Scene ActionScene = new Scene(listOfFields, 400, 400);
        updateWindow.setScene(ActionScene);
        updateWindow.initModality(Modality.APPLICATION_MODAL);
        updateWindow.showAndWait();
    }

    private void showDeleteConfirmationPopup(Entry selectedEntry) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("R U SURE???");
        alert.setContentText("Entry: " + selectedEntry.getName() + " (Value: " + selectedEntry.getValue() + ")");

        // Show the dialog and wait for user input
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteEntry(selectedEntry);
            }
        });
    }


     private boolean showUpdateConfirmationPopup(Entry selectedEntry, Entry newEntry) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().setPrefSize(450, 250);
        alert.setTitle("Confirm Update");
        alert.setHeaderText("R U SURE???");

         alert.setContentText("CHANGING: \n"
         + selectedEntry.getName() + " || " + selectedEntry.getValue() + " || "
         + selectedEntry.getCategory() + " || " + selectedEntry.getDate() + "\n\n"
         + "TO: \n"
         + newEntry.getName() + " || " + newEntry.getValue() + " || "
                 + newEntry.getCategory() + " || " + newEntry.getDate());

        // Show the dialog and wait for user input
         return alert.showAndWait()
                 .filter(response -> response == ButtonType.OK)
                 .isPresent();
    }

    private void deleteEntry(Entry entry) {
        try {
            financeDAO.deleteItem(entry.getId());
            updateCurrentLocalMapsAndValues();
            if (limitedTableSelection) {
                updateLimitedEntrySelection();
            }
            updateGUI();
        } catch (SQLException e) {
            view.showError("Error deleting the entry: " + entry.getName());
        }
    }

    private void listAllEntries() {
        limitedTableSelection = false;
        updateCurrentLocalMapsAndValues();
        updateGUI();
    }

    private void listByMonth() {
        Stage listSortingWindow = new Stage();
        listSortingWindow.setTitle("Sort and List");

        VBox sortingCriteria = new VBox(20);
        sortingCriteria.setPadding(new Insets(20));

        ToggleGroup columnCategoryToggleGroup = new ToggleGroup();
        RadioButton nameButton = new RadioButton("Name");
        nameButton.setToggleGroup(columnCategoryToggleGroup);

        RadioButton valueButton = new RadioButton("Value");
        valueButton.setToggleGroup(columnCategoryToggleGroup);

        RadioButton dateButton = new RadioButton("Date");
        dateButton.setToggleGroup(columnCategoryToggleGroup);
        dateButton.setSelected(true);

        RadioButton categoryButton = new RadioButton("Category");
        categoryButton.setToggleGroup(columnCategoryToggleGroup);

        RadioButton noColumnButton = new RadioButton("NONE");
        noColumnButton.setToggleGroup(columnCategoryToggleGroup);


        ToggleGroup orderToggleGroup = new ToggleGroup();
        RadioButton ascButton = new RadioButton("ASC");
        ascButton.setToggleGroup(orderToggleGroup);

        RadioButton descButton = new RadioButton("DESC");
        descButton.setToggleGroup(orderToggleGroup);
        descButton.setSelected(true);


        CheckComboBox<Integer> yearsCheckComboBox = new CheckComboBox();
        yearsCheckComboBox.getItems().addAll(2023,2024,2025,2026);
        yearsCheckComboBox.setTitle("YEARS");
        yearsCheckComboBox.getCheckModel().check(1);

        CheckComboBox<String> monthsStringCheckComboBox = new CheckComboBox<>();
        monthsStringCheckComboBox.getItems().addAll(AppConstants.MONTHS_SHORT);
        monthsStringCheckComboBox.setTitle("MONTHS");
        monthsStringCheckComboBox.getCheckModel().check(LocalDate.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase());

        Button listButton = new Button("List");
        Button cancelButton = new Button("Cancel");

        cancelButton.setOnAction(e-> listSortingWindow.close());
        listButton.setOnAction(e-> {
            try {
                Toggle selectedToggle = columnCategoryToggleGroup.getSelectedToggle();
                String columnChoice = selectedToggle != null ? ((RadioButton) selectedToggle).getText() : null;

                if (!columnChoice.equals("NONE")) {
                    savedColumnChoice = ColumnChoice.valueOf(columnChoice.toLowerCase());
                } else {
                    savedColumnChoice = ColumnChoice.valueOf(columnChoice);
                }

                selectedToggle = orderToggleGroup.getSelectedToggle();
                String orderChoice = selectedToggle != null ? ((RadioButton) selectedToggle).getText() : null;
                savedOrderChoice = SortingOrder.valueOf(orderChoice);


                List<String> selectedMonths = monthsStringCheckComboBox.getCheckModel().getCheckedItems();
                selectedAndSavedMonths = selectedMonths
                        .stream()
                        .map(AppConstants.MONTH_MAP::get)
                        .mapToInt(Integer::intValue)
                        .toArray();

                List<Integer> selectedYears = yearsCheckComboBox.getCheckModel().getCheckedItems();
                selectedAndSavedYears = selectedYears
                        .stream()
                        .mapToInt(Integer::intValue)
                        .toArray();

                ////////////////////////////////////////////////////////////////
                //THATS WHERE MAGIC HAPPENS

                //Marking selection mode:
                limitedTableSelection = true;

                //Updating the tableview
                updateLimitedEntrySelection();
                updateTableView();

                //Updating LIMITED local maps and values
                updateLimitedLocalMapsAndValues(selectedAndSavedMonths, selectedAndSavedYears);

                //Updating metadata, bearing in mind selection is limited:
                updateCurrentMetadata();

                /////////////////////////////////////////////////////////////////
                listSortingWindow.close();

            } catch (Exception listingException) {
                view.showErrorPopup("PROBLEM SORTING THE LIST");
                System.out.println("Problem sorting the list: " + listingException.getMessage() + " EXCEPTION: " + listingException.getClass());
            }

        });

        HBox categoryBox = new HBox(10);
        categoryBox.setPadding(new Insets(20));
        HBox orderBox = new HBox(10);
        orderBox.setPadding(new Insets(20));
        categoryBox.getChildren().addAll(nameButton, valueButton, categoryButton, dateButton, noColumnButton);
        orderBox.getChildren().addAll(ascButton, descButton);

        sortingCriteria.getChildren().addAll(
                categoryBox,
                orderBox,
                monthsStringCheckComboBox,
                yearsCheckComboBox,
                new HBox(10, listButton, cancelButton));
        Scene choiceScene = new Scene(sortingCriteria, 400, 300);
        listSortingWindow.setScene(choiceScene);
        listSortingWindow.initModality(Modality.APPLICATION_MODAL);
        listSortingWindow.showAndWait();
    }

    private String getSelectedToggleText(ToggleGroup group) {
        Toggle selectedToggle = group.getSelectedToggle();
        return selectedToggle != null ? ((RadioButton) selectedToggle).getText() : null;
    }

    private void clearSelection() {
        view.getTableView().getSelectionModel().clearSelection();
        selectedEntry = null;
        System.out.println("Item deselected");
    }


    private void updateGUI() {
        updateCurrentMetadata();
        updateTableView();
    }

    private void updateTableView() {
        try {
            if (!limitedTableSelection) {
                view.updateTableData(financeDAO.getAllItemsSorted(ColumnChoice.date, SortingOrder.DESC)); //UPDATING TABLE
            } else {
                view.updateTableData(limitedEntrySelection);
            }
        } catch (SQLException e) {
            System.out.println("Problem updating tableview. " + e.getMessage());
        }
    }

    private void updateCurrentMetadata() {
        try {
            //UPDATE MAIN METADATA
            if (!limitedTableSelection) {
                view.updateMetadataGrid(monthlyCategoriesValues, yearlySumByCategory, defaultCategories, thisYearSum, thisMonthSum, MONTH_OR_PERIOD.MONTH);
            } else {
                view.updateMetadataGrid(monthlyCategoriesValues, null, defaultCategories, 0, thisMonthSum, MONTH_OR_PERIOD.PERIOD);
            }
            //UPDATE SCROLLABLE METADATA
            view.updateScrollableMetadata(entriesByMonthCurrentYear, financeDAO.getSumForYearPerMonth(LocalDate.now().getYear()), yearlySumByCategory);
            //UPDATE PIE CHART
            view.updatePieChart(monthlyCategoriesValues);
            //UPDATE BAR CHART
            view.updateBarChart(monthlyCategoriesValues);
        } catch (SQLException e) {
            System.out.println("Problem updating metadata values. " + e.getMessage());
        }
    }

    private void updateCurrentLocalMapsAndValues() {
        try {
            entriesByMonthCurrentYear = financeDAO.getYearlySumByMonthAndCategory(currentYear);
            yearlySumByCategory = financeDAO.getYearlySumPerCategory(currentYear);
            monthlyCategoriesValues = entriesByMonthCurrentYear.get(currentMonth);

            thisYearSum = financeDAO.getSumPerYears(new int[] {currentYear});
            thisMonthSum = financeDAO.getSumPerMonth(currentMonthInteger, currentYear);
        } catch (Exception e) {
            System.out.println("Update current local containers failed.");
        }
    }

    private void updateLimitedLocalMapsAndValues(int[] monthNumbers, int[] yearsNumbers) {
        try {
//            entriesByMonthCurrentYear = financeDAO.getYearlySumByMonthAndCategory(yearsNumbers); ???
            yearlySumByCategory = null;
            monthlyCategoriesValues = financeDAO.getValuesPerCategoryForChosenYear(monthNumbers, yearsNumbers);

            thisYearSum = 0;
            thisMonthSum = financeDAO.getSumPerMonths(monthNumbers, yearsNumbers);
        } catch (Exception e) {
            System.out.println("Update LIMITED local containers failed.");
        }
    }

    public void updateLimitedEntrySelection() {
        try {
            limitedEntrySelection = financeDAO.getItemsPerMonth(selectedAndSavedMonths, selectedAndSavedYears, savedOrderChoice, savedColumnChoice);
        } catch (SQLException e) {
            System.out.println("Problem updating LimitedEntrySelection. " + e.getMessage());
        }
    }

    public void updatePieAndBarCharts(String monthName) {
        if (monthName.equals("YEAR")) {
            view.updatePieChart(yearlySumByCategory);
            view.updateBarChart(yearlySumByCategory);
            view.setPieAndBarDateText("Yearly: " + LocalDate.now().getYear());
        } else {
            view.updatePieChart(entriesByMonthCurrentYear.get(monthName));
            view.updateBarChart(entriesByMonthCurrentYear.get(monthName));
            String fullMonthName = AppConstants.MONTHS_DICTIONARY.get(monthName);
            String formattedString = fullMonthName.charAt(0) + fullMonthName.substring(1).toLowerCase();
            view.setPieAndBarDateText( formattedString + " " + LocalDate.now().getYear());
        }
    }
}
