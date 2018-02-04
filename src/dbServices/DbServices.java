package dbServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbServices {

    public static Connection conn;

    public static void setUpDB(String dbName, String login, String password) {

        //set up local server and connect; by default creating the DB if not already exist!
        if (dbName.equals("") || dbName == null) {
            dbName = "testDB";
        }
        if (login.equals("") || login == null) {
            login = "login";
        }
        if (password.equals("") || password == null) {
            password = "password";
        }
        String url = "jdbc:h2:" + System.getProperty("user.dir") + "/DataBase/" + dbName + ";IFEXISTS=TRUE";

        try {
            conn = DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            initDb(dbName, login, password);
        }

    }

    //extension for a fix Database
    private static void initDb(String dbName, String login, String password) {
        String url = "jdbc:h2:" + System.getProperty("user.dir") + "/DataBase/" + dbName;
        try {
            conn = DriverManager.getConnection(url, login, password);
            createTable("Weapons");
            createTable("GeneralLoot");
            createTable("FirstAid");
            createTable("ComputerPath");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database initialisation completed!");
    }

    public static void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String tableName) {

        //create table with a default id "id"
        try {
            if(existsTable(tableName) == false) {
                String sql =    "create TABLE " + tableName +
                                "(id INT NOT NULL )";

                Statement statement = conn.createStatement();
                statement.execute(sql);
                System.out.println("table \"" + tableName + "\" created successfully.");
            }


        } catch (SQLException e) {
            System.out.println("unable to create TABLE: \"" + tableName + "\"");
        }

    }

    public static boolean existsTable(String tableName){

        try {

            String sql =    "IF EXISTS " +
                            "(SELECT * FROM INFORMATION_SCHEMA.TABLES " +
                            "WHERE TABLE_NAME = '" + tableName + "') ";

            Statement statement = conn.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            return false;
        }


        return true;
    }

}
