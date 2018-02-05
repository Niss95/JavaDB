package dbServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbServices {

    private Connection conn;
    private String dbName;
    private String login;
    private String password;
    private String[] defaultTables;

    public DbServices(String dbName, String login, String password) {
        setDbName(dbName);
        setLogin(login);
        setPassword(password);
        setUpDB();
    }

    public DbServices(String dbName, String login, String password, String[] defaultTables) {
        setDbName(dbName);
        setLogin(login);
        setPassword(password);
        setDefaultTables(defaultTables);
        if (defaultTables.length <= 0) {
            System.out.println("no default Tables detected!");
        }
        setUpDB();
    }

    private void setUpDB() {
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
            System.out.println("connected to Database: \"" + dbName + "\"");
        } catch (SQLException e) {
            initDb(dbName, login, password);
        }
    }

    //extension for a fix Database
    private void initDb(String dbName, String login, String password) {
        String url = "jdbc:h2:" + System.getProperty("user.dir") + "/DataBase/" + dbName;
        try {
            conn = DriverManager.getConnection(url, login, password);

            if (defaultTables != null) {
                for (String s : defaultTables) {
                    createTable(s);
                    //System.out.println("default Table \"" + s + "\" created.");
                }
            } else {
                System.out.println("no default Tables set!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database initialisation completed!");
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(String tableName) {
        //create table with a default id "id"
        try {
            if (existsTable(tableName) == false) {
                if (!tableName.equals("")) {
                    String sql = "create TABLE " + tableName + "(id INT NOT NULL )";

                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    System.out.println("table \"" + tableName + "\" created successfully.");
                }else{System.out.println("ignoring empty Table!");}
            }
        } catch (SQLException e) {
            System.out.println("unable to create TABLE: \"" + tableName + "\"");
        }
    }

    public boolean existsTable(String tableName) {
        try {
            String sql = "IF EXISTS " +
                    "(SELECT * FROM INFORMATION_SCHEMA.TABLES " +
                    "WHERE TABLE_NAME = '" + tableName + "') ";

            Statement statement = conn.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            return false;
        }
        return true;
    }


    private String getDbName() {
        return dbName;
    }

    private void setDbName(String dbName) {
        this.dbName = dbName;
    }

    private String getLogin() {
        return login;
    }

    private void setLogin(String login) {
        this.login = login;
    }

    private String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setDefaultTables(String[] defaultTables) {
        this.defaultTables = defaultTables;
    }
}
