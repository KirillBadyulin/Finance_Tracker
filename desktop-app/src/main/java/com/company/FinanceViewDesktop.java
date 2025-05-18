package com.company;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    VBox yearlyGridContainer;
    private Label currentMonthLabel;
    Map<String, Button> monthsButtons;

    // Add remaining categories...
    private Button createButton;
    private Button updateButton;
    private Button deleteButton;
    private Button listByMonth;
    private Button listAllButton;
    private Button cogButton;
    private CheckComboBox<String> metadataCategoriesCheckComboBox;
    private Text pieAndBarDateText;
    private double globalScreenWidth;
    private double globalScreenHeight;

    public FinanceViewDesktop(Stage primaryStage) {
        initializeUI();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        globalScreenWidth = screenBounds.getWidth();
        globalScreenHeight = screenBounds.getHeight();

        Scene scene = new Scene(root, globalScreenWidth * 0.58, globalScreenHeight * 0.5);
//        Scene scene = new Scene(root, 927, 450); //was 927 width
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Finance Tracker");
    }

    private void initializeUI() {
        root = new BorderPane();
        monthlyGridpanesMap = new HashMap<>();
        monthsButtons = new HashMap<>();
        scrollableMetadata = createScrollableMetadata();
        pieChart = new PieChart();
        pieAndBarDateText = new Text("");

        metadataCategoriesCheckComboBox = new CheckComboBox<>();

        metadataCategoriesCheckComboBox.setTitle("            METADATA CATEGORIES");
        metadataCategoriesCheckComboBox.getItems().addAll(AppConstants.CATEGORIES); //??????????? RIGHT INITIALIZATION?

        currentMonthLabel = new Label("Total Spent for "); // Default text
        currentMonthLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        /////////////////////////////////////////////////////////////////////
        CategoryAxis xAxis = new CategoryAxis();
//        xAxis.setLabel("Categories");
        xAxis.setTickLabelRotation(90);
        xAxis.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount($)");
        barChart = new BarChart<>(xAxis, yAxis);

        barChart.applyCss();
        barChart.layout();

        /////////////////////////////////////////////////////////////////////

        VBox metaDataVBox = new VBox(10);
        metaDataVBox.setPadding(new Insets(5));
        metaDataVBox.setSpacing(5);
        metaDataVBox.setPrefWidth(280); //was 280(267), 600 to fit 2 columns (380 is enough for 2 columns)
        metaDataVBox.setAlignment(Pos.TOP_CENTER);

        Label metadataLabel = new Label("METADATA");
        metadataLabel.setStyle("-fx-font-family: 'Consolata'; -fx-font-size: 18; -fx-font-weight: bold;");

        cogButton = new Button();
        int buttonSize = 25;
        int imageSize = 25;
        ImageView imageView = new ImageView(new Image("file:C:/Users/Kiril/SQLite databases/gear.png"));
        imageView.setFitWidth(imageSize); //WAS GOOD WITH SIZE 15
        imageView.setFitHeight(imageSize); //WAS GOOD WITH SIZE 15
        cogButton.setPrefWidth(buttonSize);
        cogButton.setPrefHeight(buttonSize);
        cogButton.setMinWidth(buttonSize);
        cogButton.setMinHeight(buttonSize);
        cogButton.setGraphic(imageView);


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


            Scene popupScene = new Scene(layout, globalScreenWidth * 0.75, globalScreenHeight * 0.67);
            popupScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
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
            this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            nameColumn.setPrefWidth(totalWidth * 0.20); // ~18% of table width
            valueColumn.setPrefWidth(totalWidth * 0.08); // ~8%
            categoryColumn.setPrefWidth(totalWidth * 0.26); // ~26%
            dateColumn.setPrefWidth(totalWidth * 0.12); // ~12%
            descriptionColumn.setPrefWidth(totalWidth * 0.34); // ~36%
        });
//        tableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
//            double totalWidth = newWidth.doubleValue();
//            this.tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
//            this.tableView.setPrefWidth(660); //660
//            //647
//            nameColumn.setPrefWidth(121); //108
//            valueColumn.setPrefWidth(51); //50-51
//            categoryColumn.setPrefWidth(172); //171.8
//            dateColumn.setPrefWidth(79); //79.2
//            descriptionColumn.setPrefWidth(237); //237.6
//        });


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

        // Setting pie chart
        pieChart.setPrefSize(600, 600);

        // Setting bar chart
        barChart.setLegendVisible(false);
        barChart.setCategoryGap(20);
        barChart.setBarGap(1);
        barChart.setPrefSize(600, 600);

        // StackPane to switch charts
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

        // Left-aligned TextFlow for the date
        String currentDate = LocalDate.now().getMonth() + " " + LocalDate.now().getYear();
        currentDate = currentDate.charAt(0) + currentDate.substring(1).toLowerCase();
        pieAndBarDateText = new Text(currentDate);
        pieAndBarDateText.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        TextFlow dateTextFlow = new TextFlow(pieAndBarDateText);


//        HBox buttonAndDateHBox = new HBox(5, dateTextFlow, new Label("                  "), switchButton);
        HBox buttonBox = new HBox(5, switchButton);
        buttonBox.setAlignment(Pos.CENTER);
        HBox dateBox = new HBox(5, dateTextFlow);
        dateBox.setAlignment(Pos.CENTER);

        // Combine into VBox
        VBox chartBox = new VBox(10, dateBox, buttonBox, chartArea);
        chartBox.setAlignment(Pos.TOP_CENTER);
        chartBox.setPadding(new Insets(10));

        return chartBox;
    }



    private Node createScrollableMetadata() {
        VBox scrollableMetadataContainer = new VBox(10); // Top-level container
        scrollableMetadataContainer.setPadding(new Insets(10));
        scrollableMetadataContainer.setPrefWidth(500);
        scrollableMetadataContainer.setStyle("-fx-border-color: lightgray;");

        // Add the yearly summary grid
        scrollableMetadataContainer.getChildren().add(createYearlyGridpaneContainer());

        // Create grids for each month
        for (int month = 1; month <= 12; month++) {
            VBox monthlyGridContainer = new VBox(5); // Container for each month
            HBox monthHeaderContainer = new HBox(10); // HBox for the header
            monthHeaderContainer.setAlignment(Pos.CENTER);

            // Create a button for the month
            Button monthButton = new Button(AppConstants.MONTHS_SHORT.get(month - 1)); // e.g., "January"
            monthButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); // Button styling
            monthButton.setAlignment(Pos.CENTER);
            monthButton.setPrefWidth(300); //310 looks nice

            monthsButtons.put(AppConstants.MONTHS_FULL.get(month - 1), monthButton);
            monthHeaderContainer.getChildren().add(monthButton);

            GridPane monthlyMetadataGrid = new GridPane();
            monthlyMetadataGrid.setHgap(10);
            monthlyMetadataGrid.setVgap(5);

            // Add categories in two columns
            for (int i = 0; i < AppConstants.CATEGORIES.size(); i++) {
                int col = (i / 5) * 2; // Two columns: 0 or 2
                int row = i % 5; // Rows: 0-4

                monthlyMetadataGrid.add(new Label(AppConstants.CATEGORIES.get(i) + ":"), col, row);
                monthlyMetadataGrid.add(new TextField("0") {{
                    setPrefWidth(50);
                    setEditable(false);
                    setFocusTraversable(false);
                }}, col + 1, row);
            }

            // Add button header and grid to the container
            monthlyGridContainer.getChildren().addAll(monthHeaderContainer, monthlyMetadataGrid);
            scrollableMetadataContainer.getChildren().add(monthlyGridContainer);

            // Map the month grid container for updates
            monthlyGridpanesMap.put(AppConstants.MONTHS_SHORT.get(month - 1), monthlyGridContainer);
        }

        // Add everything to a ScrollPane
        ScrollPane scrollPane = new ScrollPane(scrollableMetadataContainer);
        scrollPane.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(LocalDate.now().getMonthValue());
        scrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() != 0) {
                double delta = e.getDeltaY();
                scrollPane.setVvalue(scrollPane.getVvalue() - delta / 500); // Adjust sensitivity
                e.consume();
            }
        });

        scrollPane.setOnMouseEntered(e -> scrollPane.requestFocus());

        return scrollPane;
    }

    public void updateScrollableMetadata(Map<String, Map<String, Integer>> metadataValues, Map<String, Integer> monthlySums,
                                         Map<String, Integer> yearlyValues) {
        // Update yearly gridpane container
        updateYearlyGridpaneContainer(yearlyValues);

        // Iterate through each month's metadata
        for (Map.Entry<String, Map<String, Integer>> entry : metadataValues.entrySet()) {
            String month = entry.getKey(); // Short month name (e.g., "Jan", "Feb")
            Map<String, Integer> categoryValues = entry.getValue();

            // Retrieve the VBox for the month
            VBox monthlyVBox = monthlyGridpanesMap.get(month);

            // Get the HBox header and GridPane from the VBox
            HBox monthHeaderContainer = (HBox) monthlyVBox.getChildren().get(0); // First child is the header HBox
            GridPane gridPane = (GridPane) monthlyVBox.getChildren().get(1);     // Second child is the GridPane

            // Update the button text in the HBox
            Button monthButton = (Button) monthHeaderContainer.getChildren().get(0); // First child in HBox is the Button
            monthButton.setText("Total Spent for " + AppConstants.MONTHS_DICTIONARY.get(month) + ": $" + monthlySums.get(month));

            // Update the GridPane with category values
            if (gridPane != null) {
                for (int i = 0; i < AppConstants.CATEGORIES.size(); i++) {
                    String category = AppConstants.CATEGORIES.get(i);
                    int col = (i / 5) * 2;  // Determine column based on category index
                    int row = i % 5;        // Determine row based on category index

                    // Find the TextField in the GridPane
                    TextField textField = (TextField) getNodeByRowColumnIndex(row, col + 1, gridPane);

                    if (textField != null) {
                        textField.setText("$" + categoryValues.getOrDefault(category, 0)); // Update with category value
                        textField.setStyle("-fx-font-weight: bold;"); // Style the updated text
                    }
                }
            } else {
                System.out.println("GRIDPANE IS NULL");
            }
        }
    }

    // Utility method to retrieve a node by its row and column index in a GridPane
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

    private VBox createYearlyGridpaneContainer() {
        yearlyGridContainer = new VBox(5);
        HBox yearHeaderContainer = new HBox(10);
        yearHeaderContainer.setAlignment(Pos.CENTER);

        Button yearButton = new Button();
        yearButton.setAlignment(Pos.CENTER);
        yearButton.setPrefWidth(300); //310 looks nice

        monthsButtons.put("YEAR", yearButton);
        yearHeaderContainer.getChildren().add(yearButton);


        GridPane yearlyMetadataGrid = new GridPane();
        yearlyMetadataGrid.setHgap(10);
        yearlyMetadataGrid.setVgap(5);
        for (int i = 0; i < AppConstants.CATEGORIES.size(); i++) {
            int col = (i / 5) * 2;
            int row = i % 5;

            yearlyMetadataGrid.add(new Label(AppConstants.CATEGORIES.get(i) + ":"), col, row); //CAN MAKE LABEL BOLD
            yearlyMetadataGrid.add(new TextField("0") {{
                setPrefWidth(50);
                setEditable(false);
                setFocusTraversable(false);
            }}, col + 1, row);
        }
        yearlyGridContainer.getChildren().addAll(yearHeaderContainer, yearlyMetadataGrid);
        return yearlyGridContainer;
    }

    public void updateYearlyGridpaneContainer(Map<String, Integer> yearlyValues) {
        int yearlySum = 0;
        TextFlow textflowLabel = new TextFlow();
        textflowLabel.setMaxWidth(300);
        textflowLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
        textflowLabel.setTextAlignment(TextAlignment.CENTER);

        HBox yearlyHeader = (HBox) yearlyGridContainer.getChildren().get(0);
        GridPane yearlyGridpane = (GridPane) yearlyGridContainer.getChildren().get(1);

        for (int i = 0; i < AppConstants.CATEGORIES.size(); i++) {
            String category = AppConstants.CATEGORIES.get(i);
            int col = (i / 5) * 2;
            int row = i % 5;

            TextField textField = (TextField) getNodeByRowColumnIndex(row, col + 1, yearlyGridpane);
            yearlySum += yearlyValues.get(category);

            if (textField != null) {
                textField.setText("$" + yearlyValues.get(category));
                textField.setStyle("-fx-font-weight: bold;");
            }
        }

        Text staticText = new Text("Total Spent for ");
        Text yearText = new Text(LocalDate.now().getYear() + "");
        Text totalText = new Text(": $" + yearlySum);

        staticText.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;");
        yearText.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        totalText.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;");
        textflowLabel.getChildren().addAll(staticText, yearText, totalText);

        Button yearButton = (Button) yearlyHeader.getChildren().get(0);
        yearButton.setPrefHeight(35); // Set a consistent height
        yearButton.setMaxHeight(35);  // Prevent it from stretching vertically
        yearButton.setGraphic(textflowLabel);
    }



    public void updatePieChart(Map<String, Integer> categoryValues) {

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

        String metadataGridOneLabel = "SPENT THIS MONTH TOTAL:       ";
        String metadataGridTwoLabel = "SPENT THIS YEAR TOTAL:            ";

        if(monthOrPeriod == monthOrPeriod.PERIOD) {
            metadataGridOneLabel = "SPENT CHOSEN PERIOD TOTAL:  ";
            metadataGridTwoLabel = "SPENT CHOSEN YEAR(S) TOTAL:       ";
        }

        metadataGridOne.add(new Label(metadataGridOneLabel), 0, 0);
        metadataGridOne.add(new TextField(monthlySumString) {{
            setPrefWidth(65);
            setEditable(false);
            setFocusTraversable(false);
            setAlignment(Pos.CENTER);
        }}, 1, 0); //changed prefwidth to 65
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
                setPrefWidth(65);
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

        Scene scene = new Scene(mainLayout, globalScreenWidth * 0.28,globalScreenHeight * 0.39); // Adjust window size
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        popupStage.showAndWait();
    }

//    public void chooseMetadataCategories() {
//        metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
//            while (change.next()) {
//                if (metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().size() > 4) {
//                    // Undo the last added selection
//                    if (change.wasAdded()) {
//                        metadataCategoriesCheckComboBox.getCheckModel().clearCheck(change.getAddedSubList().get(0));
//                    }
//                } else if (metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems().size() == 4) {
//                    // When exactly 4 items are selected, log or act on the list
//                    List<String> selectedChoices = new ArrayList<>(metadataCategoriesCheckComboBox.getCheckModel().getCheckedItems());
//                    System.out.println("Selected exactly 4 items: " + selectedChoices);
//                }
//            }
//        });
//    }

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

        Scene popupScene = new Scene(popupContent, globalScreenWidth * 0.19, globalScreenHeight * 0.22);
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

    public Map<String, Button> getMonthsButtons() {
        return this.monthsButtons;
    }
    public void setPieAndBarDateText(String text) {
        this.pieAndBarDateText.setText(text);
    }
}