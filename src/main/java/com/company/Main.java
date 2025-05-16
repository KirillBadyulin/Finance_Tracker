package com.company;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Main {

    static List<Entry> items = new LinkedList<Entry>();
    static String insertStatement = "INSERT INTO testFinance (name, value, category, date, description) VALUES (?, ?, ?, ?, ?)";
    static String removeStatement = "DELETE FROM testFinance WHERE id = ?";      //IMPLEMENTED VIA ID;

    static String updateNameStatement = "UPDATE testFinance SET name = ? WHERE id = ?";  //DO I NEED MASS UPDATE ANYWHERE?
    static String updateValueStatement = "UPDATE testFinance SET value = ? WHERE id = ?";
    static String updateCategoryStatement = "UPDATE testFinance SET category = ? WHERE id = ?";
    static String updateDateStatement = "UPDATE testFinance SET date = ? WHERE id = ?";
    static String updateDescriptionStatement = "UPDATE testFinance SET description = ? WHERE id = ?";

    public static void main(String[] args) {

//        items.add(new Entry(10, "Fruit and meat", Category.GROCERIES, "Going bananas for bananas"));
//        items.add(new Entry(1070, "Ski Pass", Category.ENTERTAINMENT, "Shred that snow!"));
//        items.add(new Entry(530, "Car Payment", Category.CAR_AND_TRANSPORTATION, "Vrum-vrum, revving up the engine!"));
//        items.add(new Entry(40, "Gas", Category.CAR_AND_TRANSPORTATION, "High Octane Guzzoline!"));
//        items.add(new Entry(22, 111, "Iron Sword", Category.MISC, "SLASH", LocalDate.parse("2025-02-15")));
//        items.add(new Entry(23, 222, "Silver Sword", Category.MISC, "SLASH-SLASH", LocalDate.parse("2025-07-15")));
//        items.add(new Entry(24, 333, "Diamond Sword", Category.MISC, "SLASH-SLASH-SLASH", LocalDate.parse("2025-08-15")));




//        String insertStatement = "INSERT INTO testFinance (name, value, category, date, description) VALUES (?, ?, ?, ?, ?)";
//        String removeStatement = "DELETE FROM testFinance WHERE id = ?";      //IMPLEMENTED VIA ID;
//
//        String updateNameStatement = "UPDATE testFinance SET name = ? WHERE id = ?";  //DO I NEED MASS UPDATE ANYWHERE?
//        String updateValueStatement = "UPDATE testFinance SET value = ? WHERE id = ?";
//        String updateCategoryStatement = "UPDATE testFinance SET category = ? WHERE id = ?";
//        String updateDateStatement = "UPDATE testFinance SET date = ? WHERE id = ?";
//        String updateDescriptionStatement = "UPDATE testFinance SET description = ? WHERE id = ?";



        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Admin\\SQLite databases\\ProperTestDB.db")) {
            Statement statement = conn.createStatement();
            PreparedStatement insertPrepared = conn.prepareStatement(insertStatement);
//        for (Entry item : items) {
//            insertPrepared.setString(1, item.getName());
//            insertPrepared.setInt(2, item.getValue());
//            insertPrepared.setString(3, item.getCategory().toString());
//            insertPrepared.setString(4, item.getDate().toString());
//            insertPrepared.setString(5, item.getDescription());
//            insertPrepared.execute();
//      }
            //INSERTING
//            createItem(conn, new Entry(75, "SUGAR", Category.GROCERIES));
            //DELETING
            //deleteItem(conn, );
            //UPDATING
//            updateItemName(conn, 101, "Healthy Fruit");
//            System.out.println(getSumPerMonths(conn,new int[]{2,7,8,11},new int[]{2024, 2025}));
//            printEntries(getAllItemsSorted(conn,ColumnChoice.value, SortingOrder.DESC));

        } catch (SQLException e) {
            System.out.println("Something went wrong" + e.getMessage());
        }


    }

    public static void printEntries(List<Entry> entries) {
        System.out.println("CONTENTS of testFinance: ");
        for (Entry entry: entries) {
            System.out.println(entry.getId() + " " + entry.getName() + " " + entry.getValue() + " " + entry.getCategory()
            + " " + entry.getDate() + " " + entry.getDescription());
        }

    }

//    //CREATING ITEMS
//    public static void createItem(Connection conn, Entry item) throws SQLException {
//        String insertStatement = "INSERT INTO testFinance (name, value, category, date, description) VALUES (?, ?, ?, ?, ?)";
//        PreparedStatement insertPrepared = conn.prepareStatement(insertStatement);
//
//        insertPrepared.setString(1, item.getName());
//        insertPrepared.setInt(2, item.getValue());
//        insertPrepared.setString(3, item.getCategory().toString());
//        insertPrepared.setString(4, item.getDate().toString());
//        insertPrepared.setString(5, item.getDescription());
//        insertPrepared.execute();
//    }
//
//    public static void createItem(Connection conn, int value, String name, Category category) throws SQLException{
//        Entry tempItem = new Entry(value, name, category);
//        createItem(conn, tempItem);
//    }
//    public static void createItem(Connection conn, int value, String name, Category category, LocalDate date) throws SQLException{
//        Entry tempItem = new Entry(value, name, category, date);
//        createItem(conn, tempItem);
//    }
//    public static void createItem(Connection conn, int value, String name, Category category, LocalDate date, String description) throws SQLException {
//        Entry tempItem = new Entry(value, name, category, description, date);
//        createItem(conn, tempItem);
//    }
//
//    //DELETING ITEMS
//    public static boolean deleteItem(Connection conn, int itemId) throws SQLException {
//        if (itemId == 0) {
//            return false;
//        }
//        String removeStatement = "DELETE FROM testFinance WHERE id = ?";
//        PreparedStatement removePrepared = conn.prepareStatement(removeStatement);
//
//        removePrepared.setInt(1, itemId);
//        removePrepared.execute();
//
//        return true;
//    }
//    public static boolean deleteItem(Connection conn, Entry entry) throws SQLException{
//        if (entry.getId() == 0) {
//            return false;
//        }
//        deleteItem(conn, entry.getId());
//        return true;
//    }
//
//    //UPDATING ITEMS
//
//    public static boolean updateItemName(Connection conn, int entryId, String name) throws SQLException {
//        String updateNameStatement = "UPDATE testFinance SET name = ? WHERE id = ?";
//        PreparedStatement preparedUpdateNameStatement = conn.prepareStatement(updateNameStatement);
//        preparedUpdateNameStatement.setString(1, name);
//        preparedUpdateNameStatement.setInt(2, entryId);
//        return preparedUpdateNameStatement.execute();
//    }
//    public static boolean updateItemValue(Connection conn, int entryId, int value) throws SQLException {
//        String updateValueStatement = "UPDATE testFinance SET value = ? WHERE id = ?";
//        PreparedStatement preparedUpdateValueStatement = conn.prepareStatement(updateValueStatement);
//        preparedUpdateValueStatement.setInt(1, value);
//        preparedUpdateValueStatement.setInt(2, entryId);
//        return preparedUpdateValueStatement.execute();
//    }
//    public static boolean updateItemCategory(Connection conn, int entryId, Category category) throws SQLException {
//        String updateCategoryStatement = "UPDATE testFinance SET category = ? WHERE id = ?";
//        PreparedStatement preparedUpdateCategoryStatement = conn.prepareStatement(updateCategoryStatement);
//        preparedUpdateCategoryStatement.setString(1, category.toString());
//        preparedUpdateCategoryStatement.setInt(2, entryId);
//        return preparedUpdateCategoryStatement.execute();
//    }
//    public static boolean updateItemDate(Connection conn, int entryId, LocalDate date) throws SQLException {
//        String updateDateStatement = "UPDATE testFinance SET date = ? WHERE id = ?";
//        PreparedStatement preparedUpdateDateStatement = conn.prepareStatement(updateDateStatement);
//        preparedUpdateDateStatement.setString(1, date.toString());
//        preparedUpdateDateStatement.setInt(2, entryId);
//        return preparedUpdateDateStatement.execute();
//    }
//    public static boolean updateItemDescription(Connection conn, int entryId, String description) throws SQLException {
//        String updateDescriptionStatement = "UPDATE testFinance SET description = ? WHERE id = ?";
//        PreparedStatement preparedUpdateDescriptionStatement = conn.prepareStatement(updateDescriptionStatement);
//        preparedUpdateDescriptionStatement.setString(1, description);
//        preparedUpdateDescriptionStatement.setInt(2, entryId);
//        return preparedUpdateDescriptionStatement.execute();
//    }
//
//    //READING ITEMS, RETURNS RESULTSET
//
//    public static Entry parseSingleResultSet(ResultSet rs) throws SQLException {
//
//        int id = rs.getInt("id");
//        int value = rs.getInt("value");
//        String name = rs.getString("name");
//        Category category = Category.valueOf(rs.getString("category"));
//        String description =  rs.getString("description");
//        LocalDate date = LocalDate.parse(rs.getString("date"));
//        return new Entry(id, value, name, category, description, date);
//    }
//
//    public static List<Entry> parseMultipleResultSet(ResultSet rs) throws SQLException {
//        List<Entry> temp = new LinkedList<>();
//        while (rs.next()) {
//            int id = rs.getInt("id");
//            int value = rs.getInt("value");
//            String name = rs.getString("name");
//            Category category = Category.valueOf(rs.getString("category"));
//            String description = rs.getString("description");
//            LocalDate date = LocalDate.parse(rs.getString("date"));
//            temp.add(new Entry(id, value, name, category, description, date));
//        }
//        return temp;
//    }
//
//    public Entry getItem(int itemId, Connection conn) throws SQLException {
//        String getItemStatement = "SELECT * FROM testFinance WHERE id = ?";
//        PreparedStatement preparedGetItemStatement = conn.prepareStatement(getItemStatement);
//        preparedGetItemStatement.setInt(1, itemId);
//
//        ResultSet rs = preparedGetItemStatement.executeQuery(); //ADDING VALIDATION CHECKING IF RS != NULL or empty
//        return parseSingleResultSet(rs);
//    }
//    public static List<Entry> getAllItems(Connection conn) throws SQLException {
//        String getAllItems = "SELECT * FROM testFinance";
//        PreparedStatement preparedGetAllItems = conn.prepareStatement(getAllItems);
//
//        ResultSet rs = preparedGetAllItems.executeQuery();  //ADDING VALIDATION CHECKING IF RS != NULL or empty
//        return parseMultipleResultSet(rs);
//    }
//
//    public static List<Entry> getAllItemsSorted(Connection conn, ColumnChoice choice, SortingOrder order) throws SQLException {
//        if ((choice == ColumnChoice.NONE) || (order == SortingOrder.NONE)) {
//            return getAllItems(conn);
//        }
//        String getItemsBy = "SELECT * FROM testFinance ORDER BY " + choice.toString() + " " + order.toString();
//        PreparedStatement preparedGetItemsBy = conn.prepareStatement(getItemsBy);
//
//        ResultSet rs = preparedGetItemsBy.executeQuery();
//        return parseMultipleResultSet(rs);
//    }
//
//    public List<Entry> getItemsPerMonth(Connection conn, int[] months, int[] years, SortingOrder order, ColumnChoice choice) throws SQLException {
//        StringBuilder sb = new StringBuilder("SELECT * FROM testFinance WHERE strftime('%m', date) IN (");
//
//        for (int i = 0; i < months.length; i++) {
//            sb.append('\'');
//            if(months[i] < 10) {
//                sb.append('0');
//            }
//            sb.append(months[i]);
//            sb.append('\'');
//            if (i < months.length - 1) {
//                sb.append(", ");
//            }
//        }
//        sb.append(") AND strftime('%Y', date) IN (");
//        for (int i = 0; i < years.length; i++) {
//            sb.append('\'');
//            sb.append(years[i]);
//            sb.append('\'');
//            if (i < years.length - 1) {
//                sb.append(", ");
//            }
//        }
//        sb.append(')');
//        if((order != SortingOrder.NONE) && (choice != ColumnChoice.NONE)) {
//            sb.append(" ORDER BY ");
//            sb.append(choice.toString());
//            sb.append(", ");
//            sb.append(order.toString());
//        }
//        System.out.println(sb);
//        PreparedStatement prepped = conn.prepareStatement(sb.toString());
//        ResultSet rs = prepped.executeQuery();
//        return parseMultipleResultSet(rs);
//    }
//
//
//    public int getSumm(Connection conn) throws SQLException {   //CHECKED
//        List<Entry> temp = getAllItems(conn);
//        int tempInt = 0;
//        for (Entry entry : temp) {
//            tempInt += entry.getValue();
//        }
//        return tempInt;
//    }
//
//    public static int getSumPerMonths(Connection conn, int[] months, int[] years) throws SQLException {  //CHECKED
//        StringBuilder sb = new StringBuilder("SELECT value FROM testFinance WHERE strftime('%m', date) IN (");
//
//        for (int i = 0; i < months.length; i++) {
//            sb.append('\'');
//            if(months[i] < 10) {
//                sb.append('0');
//            }
//            sb.append(months[i]);
//            sb.append('\'');
//            if (i < months.length - 1) {
//                sb.append(", ");
//            }
//        }
//        sb.append(") AND strftime('%Y', date) IN (");
//        for (int i = 0; i < years.length; i++) {
//            sb.append('\'');
//            sb.append(years[i]);
//            sb.append('\'');
//            if (i < years.length - 1) {
//                sb.append(", ");
//            }
//        }
//        sb.append(')');
//        PreparedStatement prepped = conn.prepareStatement(sb.toString());
//        ResultSet rs = prepped.executeQuery();
//        int summ = 0;
//
//        while(rs.next()) {
////            System.out.println(rs.getInt("value"));
//            summ += rs.getInt("value");
//        }
//        return summ;
//    }
}
