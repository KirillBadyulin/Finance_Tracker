package com.company;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;


public class FinanceViewDesktop implements FinanceView {

    private BorderPane root;
    private TableView<Entry> tableView;
    GridPane metadataGridOne;
    GridPane metadataGridTwo;
    Map<String, VBox> monthlyGridpanesMap; //THRU VBOX
    Node scrollableMetadata;
    PieChart pieChart;
    BarChart<String, Number> barChart;

    // Add remaining categories...
    private Button createButton;
    private Button updateButton;
    private Button deleteButton;
    private Button listByMonth;
    private Button listAllButton;
    private Button cogButton;
    private CheckComboBox<String> metadataCategoriesCheckComboBox;

    public FinanceViewDesktop(Stage primaryStage) {
        initializeUI();

        Scene scene = new Scene(root, 927, 450); //was 800 width
        primaryStage.setScene(scene);
        primaryStage.setTitle("Finance Tracker");
    }

    private void initializeUI() {
        root = new BorderPane();
        monthlyGridpanesMap = new HashMap<>();
        scrollableMetadata = createScrollableMetadata();
        pieChart = new PieChart();

        metadataCategoriesCheckComboBox = new CheckComboBox<>();

        metadataCategoriesCheckComboBox.setTitle("            METADATA CATEGORIES");
        metadataCategoriesCheckComboBox.getItems().addAll(AppConstants.CATEGORIES); //??????????? RIGHT INITIALIZATION?


        /////////////////////////////////////////////////////////////////////
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Categories");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");
        barChart = new BarChart<>(xAxis, yAxis);

        /////////////////////////////////////////////////////////////////////

        VBox metaDataVBox = new VBox(10);
        metaDataVBox.setPadding(new Insets(5));
        metaDataVBox.setSpacing(5);
        metaDataVBox.setPrefWidth(267); //was 280, 600 to fit 2 columns (380 is enough for 2 columns)
        metaDataVBox.setAlignment(Pos.TOP_CENTER);

        Label metadataLabel = new Label("METADATA");
        metadataLabel.setStyle("-fx-font-family: 'Consolata'; -fx-font-size: 18; -fx-font-weight: bold;");

        cogButton = new Button();
        int buttonSize = 25;
        int imageSize = 25;
        ImageView imageView = new ImageView(new Image("file:C:/Users/Admin/SQLite databases/gear.png"));
        imageView.setFitWidth(imageSize); //WAS GOOD WITH SIZE 15
        imageView.setFitHeight(imageSize); //WAS GOOD WITH SIZE 15
        cogButton.setPrefWidth(buttonSize);
        cogButton.setPrefHeight(buttonSize);
        cogButton.setMinWidth(buttonSize);
        cogButton.setMinHeight(buttonSize);
        cogButton.setGraphic(imageView);

//        cogButton.setOnAction(e -> {
//            showMetadataPopup();
//            cogButton.disarm();
//            cogButton.getParent().requestFocus();
//        });
        HBox metadataCogBox = new HBox(0, metadataLabel, cogButton);

        metadataCogBox.setAlignment(Pos.CENTER);
        metaDataVBox.getChildren().addAll(metadataCogBox, new Label(LocalDate.now().getMonth() + " " + LocalDate.now().getYear()));

        metadataGridOne = new GridPane();
        metadataGridOne.setHgap(10); // Space between columns
        metadataGridOne.setVgap(0); // Space between rows
        metadataGridOne.setPadding(new Insets(10)); // Padding for the grid

        metadataGridTwo = new GridPane();
        metadataGridTwo.setHgap(10); // Space between columns
        metadataGridTwo.setVgap(0); // Space between rows
        metadataGridTwo.setPadding(new Insets(10)); // Padding for the grid

        Button chartsAndDetailsButton = new Button("Show Charts and Details");

        chartsAndDetailsButton.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.setTitle("Charts and Details");

            BorderPane layout = new BorderPane();

            // Call helper methods to add components
            layout.setCenter(createSwitchableChartArea()); //INITIALIZED HERE, THRU METHOD CALL
            layout.setRight(scrollableMetadata); //INITIALIZED IN THE BEGINNING OF METHOD


            Scene popupScene = new Scene(layout, 1200, 600);  //PIE CHART AND GRAPH SIZE (was 800 instead of 1200)
            popupStage.setScene(popupScene);
            popupStage.initOwner(chartsAndDetailsButton.getScene().getWindow());
            popupStage.show();
        });

        metaDataVBox.getChildren().add(metadataGridOne);
        metaDataVBox.getChildren().add(metadataGridTwo);
        VBox.setMargin(chartsAndDetailsButton, new Insets(13, 0, 0, 0));
        metaDataVBox.getChildren().addAll(chartsAndDetailsButton); //XTRA LABEL IN BETWEEN?

        createButton = new Button("Create");
        updateButton = new Button("Update");
        deleteButton = new Button("Delete");
        listByMonth = new Button("List By Month");
        listAllButton = new Button("List All");

        // Add buttons to a toolbar
        ToolBar toolBar = new ToolBar(createButton, updateButton, deleteButton, listByMonth, listAllButton);
        root.setTop(toolBar);

        // Initialize the TableView
        tableView = new TableView<>();
        initializeTableView(tableView);
        initializeTableColumns();

        // Add TableView to the center
        root.setCenter(tableView);
        root.setRight(metaDataVBox);
    }

    public TableView<Entry> getTableView() {
        return this.tableView;
    }

    private void initializeTableColumns() {
        // ID column
//        TableColumn<Entry, Integer> idColumn = new TableColumn<>("ID");
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Name column
        TableColumn<Entry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setPadding(new Insets(0, 0, 0, 10)); // Add 10px padding to the left
                }
            }
        });

        // Value column
        TableColumn<Entry, Integer> valueColumn = new TableColumn<>("Value");  //"Value($)" ???
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        //LOGIC FOR ADDING $ SIGN IN FRONT OF ALL VALUES. MAKES IT LESS READABLE
//        valueColumn.setCellFactory(column -> new TableCell<Entry, Integer>() {
//            @Override
//            protected void updateItem(Integer value, boolean empty) {
//                super.updateItem(value, empty);
//                if (empty || value == null) {
//                    setText(null); // Clear cell content for empty rows
//                } else {
//                    setText("$" + value); // Add $ sign before the value
//                }
//            }
//        });

        // Category column
        TableColumn<Entry, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Date column
        TableColumn<Entry, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(column -> new TableCell<Entry, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null); // Blank for empty cells
                } else {
                    String formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(java.util.Locale.ENGLISH));
                    setText(formattedDate.toUpperCase());
                }
            }
        });

        TableColumn<Entry, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));


        // Add columns to the TableView
        tableView.getColumns().addAll(nameColumn, valueColumn, categoryColumn, dateColumn, descriptionColumn);
        tableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue();
            this.tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            this.tableView.setPrefWidth(660); //660
            //647
            nameColumn.setPrefWidth(121); //108
            valueColumn.setPrefWidth(51); //50-51
            categoryColumn.setPrefWidth(172); //171.8
            dateColumn.setPrefWidth(79); //79.2
            descriptionColumn.setPrefWidth(237); //237.6
        });
    }

    public BorderPane getRoot() {
        return root;
    }

    // Update table data dynamically
    public void updateTableData(List<Entry> entries) {
        tableView.getItems().clear();
        tableView.getItems().addAll(entries);
    }

    // Getters for buttons to set event handlers in the controller
    public Button getCreateButton() {
        return createButton;
    }

    public Button getUpdateButton() {
        return updateButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getListByMonth() {
        return listByMonth;
    }

    public Button getListAllButton() {
        return listAllButton;
    }

    public Button getCogButton() {
        return cogButton;
    }

    @Override
    public void displayEntries(List<Entry> entries) {

    }

    public void showError(String message, String headerText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("GENERIC METHOD.GENERIC ERROR");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Input Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Node createSwitchableChartArea() {
        ToggleButton switchButton = new ToggleButton("Switch to Bar Chart");
        //setting pie chart
        pieChart.setPrefSize(600, 600);
        //setting bar chart
        barChart.setLegendVisible(false);
        barChart.setCategoryGap(20);
        barChart.setBarGap(1);
        barChart.setPrefSize(600, 600);

        //////////////////////////////////////////////////////////////////////
        StackPane chartArea = new StackPane(pieChart);
        switchButton.setOnAction(e -> {
            if (switchButton.isSelected()) {
                chartArea.getChildren().setAll(barChart);
                switchButton.setText("Switch to Pie Chart");
            } else {
                chartArea.getChildren().setAll(pieChart);
                switchButton.setText("Switch to Bar Chart");
            }
        });

        VBox chartBox = new VBox(10, switchButton, chartArea);
        chartBox.setAlignment(Pos.TOP_CENTER);
        chartBox.setPadding(new Insets(10));
        return chartBox;
    }


    private Node createScrollableMetadata() {
        VBox scrollableMetadataContainer = new VBox(10);                 //HIGHEST LEVEL FOR SCROLLABLE METADATA
        scrollableMetadataContainer.setPadding(new Insets(10));
        scrollableMetadataContainer.setPrefWidth(500);
        scrollableMetadataContainer.setStyle("-fx-border-color: lightgray;"); // Optional styling (I LIKE THAT)

        for (int month = 1; month <= 12; month++) {
            VBox monthlyGridContainer = new VBox(5); // MONTHLY SCROLLABLE
            Label monthHeader = new Label("Total Spent for " + AppConstants.MONTHS_FULL.get(month - 1) + ": $0");
            monthHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Grid for categories
            GridPane monthlyMetadataGrid = new GridPane(); //MONTHLY SCROLLABLE GRID
            monthlyMetadataGrid.setHgap(10);
            monthlyMetadataGrid.setVgap(5);

            // Add categories in two columns
            for (int i = 0; i < AppConstants.CATEGORIES.size(); i++) {
                int col = (i / 5) * 2; // 0 or 2 (two columns)
                int row = i % 5; // 0-4 (row index within a column)

                monthlyMetadataGrid.add(new Label(AppConstants.CATEGORIES.get(i) + ":"), col, row); //CAN MAKE LABEL BOLD
                monthlyMetadataGrid.add(new TextField("0") {{
                    setPrefWidth(50);
                    setEditable(false);
                    setFocusTraversable(false);
                }}, col + 1, row);
            }

            // Combine header and grid
            monthlyGridContainer.getChildren().addAll(monthHeader, monthlyMetadataGrid);
            scrollableMetadataContainer.getChildren().add(monthlyGridContainer);

            monthlyGridpanesMap.put(AppConstants.MONTHS_SHORT.get(month - 1), monthlyGridContainer); //CLASS-DEFINED VARIABLE. CREATING...
            // ...MAP OF GRIDPANES FOR UPDATING
        }

        // Add the container to a ScrollPane
        ScrollPane scrollPane = new ScrollPane(scrollableMetadataContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(LocalDate.now().getMonthValue());

        return scrollPane;
    }

    public void updatePieChart(Map<String, Integer> categoryValues) {
        //FOR NOW, PASSING THE CURRENT MONTH. LATER, CAN SWITCH BETWEEN YEARLY AND MONTHLY VALUES
        //IN SCROLLABLE METADATA MONTHS WOULD WORK AS BUTTONS, CLICKING ON THEM WOULD UPDATE THE PIE CHART AND GRAPH CHART
        //MAYBE ADDING YEARLY DATA TOO

        if (this.pieChart != null) {
            pieChart.getData().clear(); // Clear existing data
            for (Map.Entry<String, Integer> entry : categoryValues.entrySet()) {
                pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue().intValue()));
            }
        } else {
            System.out.println("PIE CHART IS NULL!!!");
        }
    }

    public void updateBarChart(Map<String, Integer> categoryValues) {
        Map<String, Number> numberCategoryValues = AppConstants.convertIntegerIntoNumber(categoryValues);
        if (this.barChart != null) {

            barChart.getData().clear(); // Clear existing data
            XYChart.Series<String, Number> barSeries = new XYChart.Series<>();

            for (Map.Entry<String, Number> entry : numberCategoryValues.entrySet()) {
                barSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(barSeries);
        } else {
            System.out.println("BAR CHART IS NULL");
        }
    }

    //ORIGINAL METHOD
    public void updateMetadataGrid(Map<String, Integer> monthlyValuesPassed, Map<String, Integer> yearlyValuesPassed,
                                   List<String> chosenCategories, int yearlySum, int monthlySum, MONTH_OR_PERIOD monthOrPeriod) {

        //SIMPLIEST METHOD; COULD UPGRADE FOR MULTIPLE MONTHS
        metadataGridOne.getChildren().clear();
        metadataGridTwo.getChildren().clear();

        String monthlySumString = "$" + monthlySum;
        String monthlyValueOne = "$" + monthlyValuesPassed.get(chosenCategories.get(0));
        String monthlyValueTwo = "$" + monthlyValuesPassed.get(chosenCategories.get(1));
        String monthlyValueThree = "$" + monthlyValuesPassed.get(chosenCategories.get(2));
        String monthlyValueFour = "$" + monthlyValuesPassed.get(chosenCategories.get(3));

        String yearlySumString = "";
        String yearlyValueOne = "";
        String yearlyValueTwo = "";
        String yearlyValueThree = "";
        String yearlyValueFour = "";


        if (monthOrPeriod != MONTH_OR_PERIOD.PERIOD) {
            yearlySumString = "$" + yearlySum;
            yearlyValueOne = "$" + yearlyValuesPassed.get(chosenCategories.get(0));
            yearlyValueTwo = "$" + yearlyValuesPassed.get(chosenCategories.get(1));
            yearlyValueThree = "$" + yearlyValuesPassed.get(chosenCategories.get(2));
            yearlyValueFour = "$" + yearlyValuesPassed.get(chosenCategories.get(3));
        }

        String metadataGridOneLabel = "SPENT THIS MONTH TOTAL:        ";
        String metadataGridTwoLabel = "SPENT THIS YEAR TOTAL:            ";

        if(monthOrPeriod == monthOrPeriod.PERIOD) {
            metadataGridOneLabel = "SPENT CHOSEN PERIOD TOTAL:  ";
            metadataGridTwoLabel = "SPENT CHOSEN YEAR(S) TOTAL:       ";
        }

        metadataGridOne.add(new Label(metadataGridOneLabel), 0, 0);
        metadataGridOne.add(new TextField(monthlySumString) {{
            setPrefWidth(50);
            setEditable(false);
            setFocusTraversable(false);
            setAlignment(Pos.CENTER);
        }}, 1, 0);
        metadataGridOne.add(new Label(chosenCategories.get(0)), 0, 1);
        metadataGridOne.add(new TextField(monthlyValueOne) {{
            setPrefWidth(50);
            setEditable(false);
            setFocusTraversable(false);
            setAlignment(Pos.CENTER);
        }}, 1, 1);
        metadataGridOne.add(new Label(chosenCategories.get(1)), 0, 2);
        metadataGridOne.add(new TextField(monthlyValueTwo) {{
            setPrefWidth(50);
            setEditable(false);
            setFocusTraversable(false);
            setAlignment(Pos.CENTER);
        }}, 1, 2);
        metadataGridOne.add(new Label(chosenCategories.get(2)), 0, 3);
        metadataGridOne.add(new TextField(monthlyValueThree) {{
            setPrefWidth(50);
            setEditable(false);
            setFocusTraversable(false);
            setAlignment(Pos.CENTER);
        }}, 1, 3);
        metadataGridOne.add(new Label(chosenCategories.get(3)), 0, 4);
        metadataGridOne.add(new TextField(monthlyValueFour) {{
            setPrefWidth(50);
            setEditable(false);
            setFocusTraversable(false);
            setAlignment(Pos.CENTER);
        }}, 1, 4);

        if (monthOrPeriod != MONTH_OR_PERIOD.PERIOD) {
            metadataGridTwo.add(new Label(metadataGridTwoLabel), 0, 0);
            metadataGridTwo.add(new TextField(yearlySumString) {{
                setPrefWidth(50);
                setEditable(false);
                setFocusTraversable(false);
                setAlignment(Pos.CENTER);
            }}, 1, 0);
            metadataGridTwo.add(new Label(chosenCategories.get(0)), 0, 1);
            metadataGridTwo.add(new TextField(yearlyValueOne) {{
                setPrefWidth(50);
                setEditable(false);
                setFocusTraversable(false);
                setAlignment(Pos.CENTER);
            }}, 1, 1);
            metadataGridTwo.add(new Label(chosenCategories.get(1)), 0, 2);
            metadataGridTwo.add(new TextField(yearlyValueTwo) {{
                setPrefWidth(50);
                setEditable(false);
                setFocusTraversable(false);
                setAlignment(Pos.CENTER);
            }}, 1, 2);
            metadataGridTwo.add(new Label(chosenCategories.get(2)), 0, 3);
            metadataGridTwo.add(new TextField(yearlyValueThree) {{
                setPrefWidth(50);
                setEditable(false);
                setFocusTraversable(false);
                setAlignment(Pos.CENTER);
            }}, 1, 3);
            metadataGridTwo.add(new Label(chosenCategories.get(3)), 0, 4);
            metadataGridTwo.add(new TextField(yearlyValueFour) {{
                setPrefWidth(50);
                setEditable(false);
                setFocusTraversable(false);
                setAlignment(Pos.CENTER);
            }}, 1, 4);
        }
    }

    //USELESS METHOD? THERE'S 1/10 CHANCE that DELETED/CREATED/UPDATED action WILL CHANGE VALUE. AND ITS EASIER TO UPDATE WHOLE METADATA
    public void updateMetadataGridOneMonthlyValue(int value) {
            metadataGridOne.getChildren().removeIf(node ->
                    GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 1);

            TextField newTextField = new TextField("$" + value);
            newTextField.setPrefWidth(50);
            newTextField.setEditable(false);
            newTextField.setFocusTraversable(false);
            newTextField.setAlignment(Pos.CENTER);

        metadataGridOne.add(newTextField, 1, 0);
    }

    public void updateMetadataGridTwoYearlyValue(int value) {
        metadataGridTwo.getChildren().removeIf(node ->
                GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 1);

        TextField newTextField = new TextField("$" + value);
        newTextField.setPrefWidth(50);
        newTextField.setEditable(false);
        newTextField.setFocusTraversable(false);
        newTextField.setAlignment(Pos.CENTER);

        metadataGridTwo.add(newTextField, 1, 0);    }

//    public void updateMetadataGrid(Map<String, Map<String, Integer>> monthlyValues, Map<String, Integer> yearlyValues,
//                                   List<String> chosenCategories, int yearlySum, int monthlySum) {
//
//        // Clear the grids
//        metadataGridOne.getChildren().clear();
//        metadataGridTwo.getChildren().clear();
//
//        String currentMonth = LocalDate.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
//
//        // Helper to format values
//        String formatValue(int value) {
//            return "$" + value;
//        }
//
//        // Populate metadataGridOne
//        populateGrid(metadataGridOne, "SPENT THIS MONTH TOTAL:", formatValue(monthlySum), chosenCategories, monthlyValues.get(currentMonth));
//
//        // Populate metadataGridTwo
//        populateGrid(metadataGridTwo, "SPENT THIS YEAR TOTAL:", formatValue(yearlySum), chosenCategories, yearlyValues);
//    }
//
//    private void populateGrid(GridPane grid, String headerLabel, String totalValue, List<String> categories, Map<String, Integer> values) {
//        grid.add(new Label(headerLabel), 0, 0);
//        grid.add(createTextField(totalValue), 1, 0);
//
//        for (int i = 0; i < categories.size(); i++) {
//            String category = categories.get(i);
//            String value = values.containsKey(category) ? "$" + values.get(category) : "$0"; // Default to $0 if missing
//            grid.add(new Label(category), 0, i + 1);
//            grid.add(createTextField(value), 1, i + 1);
//        }
//    }
//
//    private TextField createTextField(String text) {
//        TextField textField = new TextField(text);
//        textField.setPrefWidth(50);
//        textField.setEditable(false);
//        textField.setFocusTraversable(false);
//        textField.setAlignment(Pos.CENTER);
//        return textField;
//    }




    public void updateScrollableMetadata(Map<String, Map<String, Integer>> metadataValues, Map<String, Integer> monthlySums) {
        for (Map.Entry<String, Map<String, Integer>> entry : metadataValues.entrySet()) {
            String month = entry.getKey(); // MONTH
            Map<String, Integer> categoryValues = entry.getValue();

            VBox monthlyVBox = monthlyGridpanesMap.get(month);
            Label monthLabel = (Label) monthlyVBox.getChildren().get(0);
            GridPane gridPane = (GridPane) monthlyVBox.getChildren().get(1);

            monthLabel.setText("Total Spent for " + AppConstants.MONTHS_DICTIONARY.get(month) + ": $" + monthlySums.get(month));

            if (gridPane != null) {

                for (int i = 0; i < AppConstants.CATEGORIES.size(); i++) {

                    String category = AppConstants.CATEGORIES.get(i);
                    int col = (i / 5) * 2;
                    int row = i % 5;

                    TextField textField = (TextField) getNodeByRowColumnIndex(row, col + 1, gridPane);

                    if (textField != null) {
                        textField.setText("$" + categoryValues.get(category));
                        textField.setStyle("-fx-font-weight: bold;");
                    }
                }
            } else {
                System.out.println("GRIDPANE IS NULL");
            }
        }
    }

    private Node getNodeByRowColumnIndex(int row, int column, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row &&
                    GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }

    private void initializeTableView(TableView<Entry> tableView) {
        // Add double-click event handler
        tableView.setRowFactory(tv -> {
            TableRow<Entry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Entry selectedEntry = row.getItem();
                    showEntryPopupStage(selectedEntry);
                }
            });
            return row;
        });
    }

    private void showEntryPopupStage(Entry selectedEntry) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Details");

        // Content container with left alignment
        VBox content = new VBox(5);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT); // Align fields to the top-left

        // Create labels for entry details
        Label idLabel = new Label("ID: " + selectedEntry.getId());
        Label nameLabel = new Label("Name: " + selectedEntry.getName());
        Label valueLabel = new Label("Value: $" + selectedEntry.getValue());
        Label categoryLabel = new Label("Category: " + selectedEntry.getCategory());
        Label dateLabel = new Label("Date: " + selectedEntry.getDate());

        // Apply larger font size to all labels
        String labelStyle = "-fx-font-family: 'Consolas'; -fx-font-size: 16;";
        idLabel.setStyle(labelStyle);
        nameLabel.setStyle(labelStyle);
        valueLabel.setStyle(labelStyle);
        categoryLabel.setStyle(labelStyle);
        dateLabel.setStyle(labelStyle);

        // Non-editable description area
        Label descriptionLabel = new Label("Description");
        descriptionLabel.setStyle("-fx-font-family: 'Consolas';-fx-font-size: 16;");
        TextArea descriptionArea = new TextArea(selectedEntry.getDescription());
        descriptionArea.setMouseTransparent(true);
        descriptionArea.setEditable(false); // Make it non-editable
        descriptionArea.setWrapText(true);  // Wrap text for better readability
        descriptionArea.setPrefHeight(120); // Adjust height
        descriptionArea.setStyle("-fx-font-family: 'Consolas';-fx-font-size: 16;");

        content.getChildren().addAll(
                idLabel,
                nameLabel,
                valueLabel,
                categoryLabel,
                dateLabel,
                new Label(""),
                descriptionLabel,
                descriptionArea
        );

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popupStage.close());
        okButton.setPrefWidth(80);
        okButton.setStyle("-fx-font-family: 'Consolas';-fx-font-size: 14;");

        VBox mainLayout = new VBox(5, content, okButton);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout, 450, 350); // Adjust window size
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        popupStage.showAndWait();
    }

    public void chooseMetadataCategories() {
        metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().size() > 4) {
                    // Undo the last added selection
                    if (change.wasAdded()) {
                        metadataCategoriesCheckComboBox.getCheckModel().clearCheck(change.getAddedSubList().get(0));
                    }
                } else if (metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().size() == 4) {
                    // When exactly 4 items are selected, log or act on the list
                    List<String> selectedChoices = new ArrayList<>(metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems());
                    System.out.println("Selected exactly 4 items: " + selectedChoices);
                }
            }
        });

//        return new ArrayList<>(metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems());
    }

//    public void showMetadataPopup() {
////        chooseMetadataCategories();
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL);
//        popupStage.setTitle("Metadata Categories");
//        Label promt = new Label("Choose 4 categories to show");
//
//        Button applyButton = new Button("Apply");
//        applyButton.setOnAction(e -> {
//
////            List<String> chosenCategories = new ArrayList<>(metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems());
////            for (String entry : chosenCategories) {
////                System.out.println(entry);
////            }
//
//            popupStage.close();
//        });
//
//        Button cancelButton = new Button("Cancel");
//        cancelButton.setOnAction(e -> popupStage.close());
//
//        // Layout for buttons
//        HBox buttonBox = new HBox(10, applyButton,new Label("                                        "), cancelButton);
//        buttonBox.setAlignment(Pos.BOTTOM_LEFT);
//
//        // Main layout
//        VBox popupContent = new VBox(10,promt, metadataCategoriesCheckComboBox, new Label(""), new Label(""), buttonBox); //second podition dropdownmenu
//        popupContent.setPadding(new Insets(20));
//        popupContent.setAlignment(Pos.CENTER);
//
//        Scene popupScene = new Scene(popupContent, 300, 200);
//        popupStage.setScene(popupScene);
//        popupStage.showAndWait();
//    }

    public List<String> showMetadataPopupORIGINAL(List<String> currentCategories) {
        chooseMetadataCategories();
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Metadata Categories");

        Label promt = new Label("Choose 4 categories:");
        Label warningLabel = new Label("Less than 4 categories were selected");  //"Please select exactly 4 categories."
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14;"); // Style the label
        warningLabel.setVisible(false);

        Timeline shimmer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(warningLabel.opacityProperty(), 0.2)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(warningLabel.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(warningLabel.opacityProperty(), 0.2))
        );
        shimmer.setCycleCount(Animation.INDEFINITE);

        List<String> selectedCategories = new ArrayList<>(currentCategories);

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            selectedCategories.clear();
            selectedCategories.addAll(metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems());
            popupStage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(10, applyButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox popupContent = new VBox(10,promt, metadataCategoriesCheckComboBox, new Label(" "),warningLabel, buttonBox);
        popupContent.setPadding(new Insets(20));
        popupContent.setAlignment(Pos.CENTER);

        Scene popupScene = new Scene(popupContent, 300, 200);
        popupStage.setScene(popupScene);

        popupStage.showAndWait();

        if(selectedCategories.size() < 4) {
            warningLabel.setVisible(true);
            shimmer.play();
        } else {
            warningLabel.setVisible(false);
            shimmer.stop();
        }
        if(selectedCategories.size() < 4) {
            return currentCategories;
        } else {
            return selectedCategories;
        }
    }

    public List<String> showMetadataPopup(List<String> currentCategories) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Metadata Categories");

        Label prompt = new Label("Choose 4 categories:");
        Label warningLabel = new Label("Less than 4 categories were selected");
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14;");
        warningLabel.setVisible(false);

        // Adjust shimmering speed (2 times slower)
        Timeline shimmer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(warningLabel.opacityProperty(), 0.2)),
                new KeyFrame(Duration.seconds(1), new KeyValue(warningLabel.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(warningLabel.opacityProperty(), 0.2))
        );
        shimmer.setCycleCount(Animation.INDEFINITE);

        List<String> selectedCategories = new ArrayList<>(currentCategories);

        Button applyButton = new Button("Apply");
        applyButton.setDisable(true); // Initially disabled
        applyButton.setOnAction(e -> {
            selectedCategories.clear();
            selectedCategories.addAll(metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems());
            popupStage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> popupStage.close());

        // Limit selection to 4 and handle apply button state
        metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().size() > 4) {
                    // Undo the last added selection
                    if (change.wasAdded()) {
                        metadataCategoriesCheckComboBox.getCheckModel().clearCheck(change.getAddedSubList().get(0));
                    }
                } else if (metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().size() == 4) {
                    applyButton.setDisable(false); // Enable Apply button when exactly 4 are selected
                    warningLabel.setVisible(false);
                    shimmer.stop();
                } else {
                    applyButton.setDisable(true); // Disable Apply button when not exactly 4
                    warningLabel.setVisible(true);
                    shimmer.play();
                }
            }
        });

        HBox buttonBox = new HBox(10, applyButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox popupContent = new VBox(10, prompt, metadataCategoriesCheckComboBox, new Label(" "), warningLabel, buttonBox);
        popupContent.setPadding(new Insets(20));
        popupContent.setAlignment(Pos.CENTER);

        Scene popupScene = new Scene(popupContent, 300, 200);
        popupStage.setScene(popupScene);

        popupStage.showAndWait();

        // Return the selected categories if valid, or the current categories if not
        return selectedCategories.size() == 4 ? selectedCategories : currentCategories;
    }











//    public List<String> chooseMetadataCategories() {
//        // Limit selections to 4
//        metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
//            if (metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().size() > 4) {
//                while (change.next()) {
//                    if (change.wasAdded()) {
//                        metadataCategoriesCheckComboBox.getCheckModel().clearCheck(change.getAddedSubList().get(0));
//                    }
//                }
//            }
//        });
//        // Return the current list of selected items
//        return new ArrayList<>(metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems());
//    }

}