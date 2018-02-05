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
        if (getDefaultTables().length <= 0) {
            System.out.println("no default Tables detected!");
        }
        setUpDB();
    }

    private void setUpDB() {
        //set up local server and connect; by default creating the DB if not already exist!
        if (getDbName().equals("") || getDbName() == null) {
            setDbName("testDB");
        }
        if (getLogin().equals("") || getLogin() == null) {
            setLogin("login");
        }
        if (getPassword().equals("") || getPassword() == null) {
            setPassword("password");
        }
        String url = "jdbc:h2:" + System.getProperty("user.dir") + "/DataBase/" + getDbName() + ";IFEXISTS=TRUE";

        try {
            setConn(DriverManager.getConnection(url, getLogin(), getPassword()));
            System.out.println("Database excise...");
            System.out.println("connected to Database: \"" + getDbName() + "\"");
        } catch (SQLException e) {
            initDb();
        }
    }

    //extension for a fix Database
    private void initDb() {
        String url = "jdbc:h2:" + System.getProperty("user.dir") + "/DataBase/" + getDbName();
        try {
            setConn(DriverManager.getConnection(url, getLogin(), getPassword()));

            if (getDefaultTables() != null) {
                System.out.println("creating new Tables:");
                for (String s : getDefaultTables()) {
                    createTable(s);
                }
            } else {
                System.out.println("no default Tables set!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("\nDatabase initialisation completed!\n");
    }

    public void closeConnection() {
        try {
            getConn().close();
            System.out.println("closed connection to \"" + getDbName() + "\".");
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

                    Statement statement = getConn().createStatement();
                    statement.execute(sql);
                    System.out.println("table \"" + tableName + "\" created successfully.");
                } else {
                    System.out.println("ignoring empty Table!");
                }
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

            Statement statement = getConn().createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            return false;
        }
        return true;
    }


    private String getDbName() {
        return this.dbName;
    }

    private void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Connection getConn() {
        return this.conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    private String getLogin() {
        return this.login;
    }

    private void setLogin(String login) {
        this.login = login;
    }

    private String getPassword() {
        return this.password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public String[] getDefaultTables() {
        return this.defaultTables;
    }

    public void setDefaultTables(String[] defaultTables) {
        this.defaultTables = defaultTables;
    }
}
