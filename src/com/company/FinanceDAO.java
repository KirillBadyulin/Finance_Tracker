package com.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FinanceDAO {

    private final Connection conn;
//    private final String oldTableName = "actualFinance";
//    private final String tableName = "testFinance";
private final String oldTableName = "testFinance";
    private final String tableName = "actualFinance";
    private final String createStatement = "CREATE TABLE actualFinance (\n" +
            "        id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "        value INTEGER NOT NULL,\n" +
            "        name TEXT NOT NULL,\n" +
            "        category TEXT NOT NULL,\n" +
            "        date TEXT NOT NULL,\n" +
            "        description TEXT\n" +
            "    );";

    public FinanceDAO(Connection conn) {
        this.conn = conn;
    }

    //CREATING ITEMS
    public void createItem(Entry item) throws SQLException {
        String insertStatement = "INSERT INTO " + tableName + " (name, value, category, date, description) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement insertPrepared = conn.prepareStatement(insertStatement);

        insertPrepared.setString(1, item.getName());
        insertPrepared.setInt(2, item.getValue());
        insertPrepared.setString(3, item.getCategory().toString());
        insertPrepared.setString(4, item.getDate().toString());
        insertPrepared.setString(5, item.getDescription());
        insertPrepared.execute();
    }

    public void createItem(int value, String name, Category category) throws SQLException{
        Entry tempItem = new Entry(value, name, category);
        createItem(tempItem);
    }
    public void createItem(int value, String name, Category category, LocalDate date) throws SQLException{
        Entry tempItem = new Entry(value, name, category, date);
        createItem(tempItem);
    }
    public void createItem(int value, String name, Category category, LocalDate date, String description) throws SQLException {
        Entry tempItem = new Entry(value, name, category, description, date);
        createItem(tempItem);
    }

    //DELETING ITEMS
    public boolean deleteItem(int itemId) throws SQLException {
        if (itemId == 0) {
            return false;
        }
        String removeStatement = "DELETE FROM " + tableName + " WHERE id = ?";
        PreparedStatement removePrepared = conn.prepareStatement(removeStatement);

        removePrepared.setInt(1, itemId);
        removePrepared.execute();

        return true;
    }
    public boolean deleteItem(Entry entry) throws SQLException{
        if (entry.getId() == 0) {
            return false;
        }
        deleteItem(entry.getId());
        return true;
    }

    //UPDATING ITEMS
    public boolean updateItem(Entry oldEntry, Entry newEntry) throws SQLException {
        String updateItem = "UPDATE " + tableName + " SET name = ?, value = ?, category = ?, date = ?, description = ? WHERE id = ?";
        PreparedStatement preparedUpdateItem = conn.prepareStatement(updateItem);
        preparedUpdateItem.setString(1, newEntry.getName());
        preparedUpdateItem.setInt(2, newEntry.getValue());
        preparedUpdateItem.setString(3, newEntry.getCategory().toString());
        preparedUpdateItem.setString(4, newEntry.getDate().toString());
        preparedUpdateItem.setString(5, newEntry.getDescription());
        preparedUpdateItem.setInt(6, oldEntry.getId());

        return preparedUpdateItem.execute();
    }

    public boolean updateItemName(int entryId, String name) throws SQLException {
        String updateNameStatement = "UPDATE " + tableName + " SET name = ? WHERE id = ?";
        PreparedStatement preparedUpdateNameStatement = conn.prepareStatement(updateNameStatement);
        preparedUpdateNameStatement.setString(1, name);
        preparedUpdateNameStatement.setInt(2, entryId);
        return preparedUpdateNameStatement.execute();
    }
    public boolean updateItemValue(int entryId, int value) throws SQLException {
        String updateValueStatement = "UPDATE " + tableName + " SET value = ? WHERE id = ?";
        PreparedStatement preparedUpdateValueStatement = conn.prepareStatement(updateValueStatement);
        preparedUpdateValueStatement.setInt(1, value);
        preparedUpdateValueStatement.setInt(2, entryId);
        return preparedUpdateValueStatement.execute();
    }
    public boolean updateItemCategory(int entryId, Category category) throws SQLException {
        String updateCategoryStatement = "UPDATE " + tableName + " SET category = ? WHERE id = ?";
        PreparedStatement preparedUpdateCategoryStatement = conn.prepareStatement(updateCategoryStatement);
        preparedUpdateCategoryStatement.setString(1, category.toString());
        preparedUpdateCategoryStatement.setInt(2, entryId);
        return preparedUpdateCategoryStatement.execute();
    }
    public boolean updateItemDate(int entryId, LocalDate date) throws SQLException {
        String updateDateStatement = "UPDATE " + tableName + " SET date = ? WHERE id = ?";
        PreparedStatement preparedUpdateDateStatement = conn.prepareStatement(updateDateStatement);
        preparedUpdateDateStatement.setString(1, date.toString());
        preparedUpdateDateStatement.setInt(2, entryId);
        return preparedUpdateDateStatement.execute();
    }
    public boolean updateItemDescription(int entryId, String description) throws SQLException {
        String updateDescriptionStatement = "UPDATE " + tableName + " SET description = ? WHERE id = ?";
        PreparedStatement preparedUpdateDescriptionStatement = conn.prepareStatement(updateDescriptionStatement);
        preparedUpdateDescriptionStatement.setString(1, description);
        preparedUpdateDescriptionStatement.setInt(2, entryId);
        return preparedUpdateDescriptionStatement.execute();
    }

    //READING ITEMS, RETURNS RESULTSET

    public Entry parseSingleResultSet(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        int value = rs.getInt("value");
        String name = rs.getString("name");
        Category category = Category.valueOf(rs.getString("category"));
        String description =  rs.getString("description");
        LocalDate date = LocalDate.parse(rs.getString("date"));
        return new Entry(id, value, name, category, description, date);
    }

    public List<Entry> parseMultipleResultSet(ResultSet rs) throws SQLException {
        List<Entry> temp = new LinkedList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int value = rs.getInt("value");
            String name = rs.getString("name");
            Category category = Category.valueOf(rs.getString("category"));
            String description = rs.getString("description");
            LocalDate date = LocalDate.parse(rs.getString("date"));
            temp.add(new Entry(id, value, name, category, description, date));
        }
        return temp;
    }

    //ENTRIES
    //////////////////////////////////////////////////////////////////
    //#1. GET SINGLE ITEM
    public Entry getItem(int itemId) throws SQLException {
        String getItemStatement = "SELECT * FROM " + tableName + " WHERE id = ?";
        PreparedStatement preparedGetItemStatement = conn.prepareStatement(getItemStatement);
        preparedGetItemStatement.setInt(1, itemId);

        ResultSet rs = preparedGetItemStatement.executeQuery(); //ADDING VALIDATION CHECKING IF RS != NULL or empty
        return parseSingleResultSet(rs);
    }

    //#2. GET ALL ITEMS
    public List<Entry> getAllItems() throws SQLException {
        String getAllItems = "SELECT * FROM " + tableName;
        PreparedStatement preparedGetAllItems = conn.prepareStatement(getAllItems);

        ResultSet rs = preparedGetAllItems.executeQuery();  //ADDING VALIDATION CHECKING IF RS != NULL or empty
        return parseMultipleResultSet(rs);
    }

    //#3. GET ALL ITEMS SORTED (CAN DELETE #2 BECAUSE HAVING ITEMS UN-SORTED DOESNT MAKE SENSE
    public List<Entry> getAllItemsSorted(ColumnChoice choice, SortingOrder order) throws SQLException {
        if ((choice == ColumnChoice.NONE)) {
            return getAllItems();
        }
        String getItemsBy = "SELECT * FROM " + tableName + " ORDER BY " + choice.toString() + " " + order.toString();
        PreparedStatement preparedGetItemsBy = conn.prepareStatement(getItemsBy);

        ResultSet rs = preparedGetItemsBy.executeQuery();
        return parseMultipleResultSet(rs);
    }

    //#4 GET ALL ITEMS FOR ONE/MULTIPLE MONTHS. CAN BE SORTED
    public List<Entry> getItemsPerMonth(int[] months, int[] years, SortingOrder order, ColumnChoice choice) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT * FROM " + tableName + " WHERE strftime('%m', date) IN (");

        for (int i = 0; i < months.length; i++) {
            sb.append('\'');
            if(months[i] < 10) {
                sb.append('0');
            }
            sb.append(months[i]);
            sb.append('\'');
            if (i < months.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(") AND strftime('%Y', date) IN (");
        for (int i = 0; i < years.length; i++) {
            sb.append('\'');
            sb.append(years[i]);
            sb.append('\'');
            if (i < years.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(')');
        if(choice != ColumnChoice.NONE) {
            sb.append(" ORDER BY ");
            sb.append(choice.toString());
            sb.append(" ");
            sb.append(order.toString());
        }

        PreparedStatement prepped = conn.prepareStatement(sb.toString());
        ResultSet rs = prepped.executeQuery();
        return parseMultipleResultSet(rs);
    }
    //INTEGERS(int)
    //////////////////////////////////////////////////////////////////
    public int getSumPerPeriod(int[] months, int[] years) throws SQLException {
        List<Entry> temp = getItemsPerMonth(months, years, SortingOrder.ASC, ColumnChoice.NONE);
            int tempInt = 0;
            for (Entry entry : temp) {
                tempInt += entry.getValue();
            }
            return tempInt;

    }

    public int getSumPerMonth(int month, int year) throws SQLException { //WORKS CORRECTLY
        List<Entry> temp = getItemsPerMonth(new int[] {month}, new int[] {year}, SortingOrder.ASC, ColumnChoice.NONE);
            int tempInt = 0;
            for (Entry entry : temp) {
                tempInt += entry.getValue();
            }
            return tempInt;
    }

    public int getSumPerYears(int[] years) throws SQLException{
        List<Entry> temp = getItemsPerMonth(new int[] {1,2,3,4,5,6,7,8,9,10,11,12}, years, SortingOrder.ASC, ColumnChoice.NONE);
        int tempInt = 0;
        for (Entry entry : temp) {
            tempInt += entry.getValue();
        }
        return tempInt;
    }

    public int getSumm() throws SQLException {
        List<Entry> temp = getAllItems();
        int tempInt = 0;
        for (Entry entry : temp) {
            tempInt += entry.getValue();
        }
        return tempInt;
    }

    public int getSumPerMonths(int[] months, int[] years) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT value FROM " + tableName + " WHERE strftime('%m', date) IN (");

        for (int i = 0; i < months.length; i++) {
            sb.append('\'');
            if(months[i] < 10) {
                sb.append('0');
            }
            sb.append(months[i]);
            sb.append('\'');
            if (i < months.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(") AND strftime('%Y', date) IN (");
        for (int i = 0; i < years.length; i++) {
            sb.append('\'');
            sb.append(years[i]);
            sb.append('\'');
            if (i < years.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(')');
        PreparedStatement prepped = conn.prepareStatement(sb.toString());
        ResultSet rs = prepped.executeQuery();
        int summ = 0;

        while(rs.next()) {
            summ += rs.getInt("value");
        }
        return summ;
    }

    public int getSumPerCategory(int[] months, int[] years, Category category) throws SQLException { //BEST METHOD???
        StringBuilder sb = new StringBuilder("SELECT value FROM " + tableName +
                " WHERE category = '" + category.toString() + "'" +
                " AND strftime('%m', date) IN (");

        for (int i = 0; i < months.length; i++) {
            sb.append('\'');
            if(months[i] < 10) {
                sb.append('0');
            }
            sb.append(months[i]);
            sb.append('\'');
            if (i < months.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(") AND strftime('%Y', date) IN (");
        for (int i = 0; i < years.length; i++) {
            sb.append('\'');
            sb.append(years[i]);
            sb.append('\'');
            if (i < years.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(')');

        PreparedStatement prepped = conn.prepareStatement(sb.toString());
        ResultSet rs = prepped.executeQuery();
        int summ = 0;

        while(rs.next()) {
            summ += rs.getInt("value");
        }
        return summ;
    }
    //MAPS OF ENTRIES
    //////////////////////////////////////////////////////////////////
         //Map<Month, Map<Category, Value>
    //RETURNS 12 months FOR SELECTED YEAR
    public Map<String, Map<String, Integer>> getYearlySumByMonthAndCategory(int year) throws SQLException { //WORKS CORRECTLY
        Map<String, Map<String, Integer>> outerMap = new HashMap();

        for (int y = 0; y < 12; y++) {
            Map<String, Integer> innerMap = new HashMap<>();

            for (int i = 0; i < 10; i++) {
                String category = AppConstants.CATEGORIES.get(i);
                int value = getSumPerCategory(new int[]{y+1}, new int[]{year}, Category.valueOf(category)); //Y+1, VERY IMPORTANT!!! BECAUSE getSumPerCategory adds number into the statement
                innerMap.put(category, value);
            }
            outerMap.put(AppConstants.MONTHS_SHORT.get(y),innerMap);
        }
        return outerMap;
    }

    public Map<String, Integer> getYearlySumPerCategory(int year) throws SQLException {

        Map<String, Integer> categorySumMap = new HashMap<>();
        List<String> categoryNames = Stream.of(Category.values()).map(Enum::name).collect(Collectors.toList());

        for (int i = 0; i < 10; i++) {
            String categoryName = categoryNames.get(i);
            int sumPerCategory = getSumPerCategory(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12},
                    new int [] {year}, Category.valueOf(categoryName));
            categorySumMap.put(categoryName, sumPerCategory);
        }
        return categorySumMap;
    }

    public Map<String, Integer> getSumForYearPerMonth(int year) throws SQLException { // WORKS CORRECTLY
        Map<String, Integer> monthlySums = new HashMap<>();
        int sum = 0;
        for (int i = 1; i < 13; i++) {
            sum = getSumPerMonth(i, year);
            monthlySums.put(AppConstants.MONTHS_SHORT.get(i-1),sum);
        }
        return monthlySums;
    }
    //     Map<Category string, Value integer>
    public Map<String, Integer> getValuesPerCategoryForChosenYear(int[] months, int[] years) throws SQLException {
        List<Entry> toTraverse = getItemsPerMonth(months, years, SortingOrder.ASC, ColumnChoice.NONE);
        List<String> categories = AppConstants.CATEGORIES.stream().toList();
        Map<String, Integer> mapToReturn = new HashMap<>();

        int sumForCategory = 0;

        for (String category : categories) {

            for (Entry entry : toTraverse) {
                if (entry.getCategory().toString().equals(category)) {
                    sumForCategory += entry.getValue();
                }
            }
            mapToReturn.put(category, sumForCategory);
            sumForCategory = 0;
        }
        return mapToReturn;
    }
}
