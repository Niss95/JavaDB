package dbServices;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbServices {

    //TODO: account bezogene sicherungen integrieren; siehe "deleteDatabase()"

    private Connection conn;
    private String dbName;
    private String login;
    private String password;
    private String[] defaultTables;


    //---Constructors------------------------------------------------------------------------------

    /**
     * Constructor for a Database without given default Tables.
     * Best use if the Database already exists.
     * @param dbName Name of the Database
     * @param login Account- Name for the Connection
     * @param password Login- Password for the Connection
     */
    public DbServices(String dbName, String login, String password) {
        setDbName(dbName);
        setLogin(login);
        setPassword(password);
        setUpDB();
    }
    /**
     * Constructor for a Database without given default Tables.
     * Best use if the Database doesn't exists already.
     * @param dbName Name of the Database
     * @param login Account- Name for the Connection
     * @param password Login- Password for the Connection
     * @param defaultTables Array of default Tables to create if the Database doesn't exist already
     */
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

    //---Private Methods----------------------------------------------------------------------------

    /**
     * Setting up the Database- Connection.
     *
     * Searching for a Database with the given name; don't compares the content!
     * If the Database doesn't exists it will be created; empty if no default tables are passed!
     */
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

    /**
     * Initialises a new Database with default Tables if passed.
     */
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

    //---Non Public Setter and Getter--------------------------------------------------------------

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

    //---Public Methods----------------------------------------------------------------------------

    /**
     * Closes the current Connection to the belonged Database.
     */
    public void closeConnection() {
        try {
            getConn().close();
            System.out.println("closed connection to \"" + getDbName() + "\".");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creating a new Table with a fix id: "id" if not already exists.
     * @param tableName Name fpr the new Table
     */
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

    /**
     *
     * @param tableName Name of the Table to check.
     * @return false if Table don't exists in the Database; true if it exists.
     */
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

    /**
     * Deletes all Database- files in the directory.
     * Use with caution! No recovery!!!
     */
    public void deleteDatabase() {
        try {
            getConn().close();

            File target1 = new File(System.getProperty("user.dir") + "/DataBase/" + getDbName() + ".mv.db");
            File target2 = new File(System.getProperty("user.dir") + "/DataBase/" + getDbName() + ".trace.db");

            if (target1.delete() && target2.delete()) {
                System.out.println("\nDatabase \"" + getDbName() + "\" successfully deleted!\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

}
