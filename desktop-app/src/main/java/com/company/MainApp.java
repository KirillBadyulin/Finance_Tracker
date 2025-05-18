package com.company;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        //USING DATABASEUTIL VIA INSTANCE DEFEATS THE PURPOSE OF UTILITY CLASS
        System.out.println("DPI " + Screen.getPrimary().getDpi());
        //CHANGED NAME OF USER!!!
//        DatabaseUtil databaseUtil = new DatabaseUtil("jdbc:sqlite:C:\\Users\\Admin\\SQLite databases\\ProperTestDB.db");
        DatabaseUtil databaseUtil = new DatabaseUtil("jdbc:sqlite:C:\\Users\\Kiril\\SQLite databases\\ProperTestDB.db");
        //CHANGED NAME OF USER!!!
        Connection conn = databaseUtil.getConnection();
        FinanceDAO financeDAO = new FinanceDAO(conn);
        FinanceViewDesktop view = new FinanceViewDesktop(primaryStage);
        FinanceController controller = new FinanceController(financeDAO, view);


//        String textFilePath = "C:\\Users\\Admin\\SQLite databases\\WrittenFromDatabase.txt";
//        try {
//            writeEntryListToFile(financeDAO.getAllItems(), textFilePath);
//        } catch (Exception e) {
//
//        }

//        primaryStage is passed through the start() method
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Node createSwitchableChartArea(Map<String, Integer> categoryValues) { //METHOD FOR TESTING; UNNECESSARY
        Map<String, Number> numberCategoryValues = AppConstants.convertIntegerIntoNumber(categoryValues);
        ToggleButton switchButton = new ToggleButton("Switch to Bar Chart");

        // Pie Chart
        PieChart pieChart = new PieChart();
        pieChart.setPrefSize(600, 600);
        updatePieChart(pieChart, numberCategoryValues);

        // Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Categories");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setCategoryGap(20);
        barChart.setBarGap(1);
        barChart.setPrefSize(600, 600);
        updateBarChart(barChart, numberCategoryValues);

        // Chart switching logic
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

    private void updateBarChart(BarChart<String, Number> barChart, Map<String, Number> categoryValues) { //METHOD FOR TESTING; UNNECESSARY
        barChart.getData().clear();
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        for (Map.Entry<String, Number> entry : categoryValues.entrySet()) {
            barSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(barSeries);
    }

    private void updatePieChart(PieChart pieChart, Map<String, Number> categoryValues) {  //METHOD FOR TESTING; UNNECESSARY
        pieChart.getData().clear();
        for (Map.Entry<String, Number> entry : categoryValues.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue().doubleValue()));
        }
    }

    public void writeEntryListToFile(List<Entry> passedList, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Entry entry : passedList) {
                writer.write(
                        entry.getId() + ";" +
                                entry.getValue() + ";" +
                                entry.getName() + ";" +
                                entry.getCategory() + ";" +
                                entry.getDate() + ";" +
                                entry.getDescription()
                );
                writer.newLine();
            }
        }
    }

    public List<Entry> readEntryListFromFile(String filePath) throws IOException {
        List<Entry> entryList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";", 6); // Split into exactly 6 parts
                if (fields.length == 6) { // Ensure all fields are present
                    int id = Integer.parseInt(fields[0]);
                    int value = Integer.parseInt(fields[1]);
                    String name = fields[2];
                    Category category = Category.valueOf(fields[3]); // Convert string to enum
                    LocalDate date = LocalDate.parse(fields[4]); // Parse ISO-8601 date format
                    String description = fields[5];

                    Entry entry = new Entry(id, value, name, category, description, date);
                    entryList.add(entry);
                }
            }
        }
        return entryList;
    }
}
