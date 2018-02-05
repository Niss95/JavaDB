package dbServices;

import java.io.File;
import java.sql.*;

public class DbServices {

    //TODO: account bezogene sicherungen integrieren; siehe "deleteDatabase()"

    private Connection conn;
    private String dbName;
    private String login;
    private String password;
    private String[] defaultTables;


    //---Constructors------------------------------------------------------------------------------

    /**
     * Constructor for a database without given default tables.
     * Best use if the database already exists.
     *
     * @param dbName   Name of the Database
     * @param login    Account- Name for the Connection
     * @param password Login- Password for the Connection
     */
    public DbServices(String dbName, String login, String password) {
        setDbName(dbName);
        setLogin(login);
        setPassword(password);
        setUpDB();
    }

    /**
     * Constructor for a database without given default tables.
     * Best use if the database doesn't exists already.
     *
     * @param dbName        Name of the Database
     * @param login         Account- Name for the Connection
     * @param password      Login- Password for the connection
     * @param defaultTables Array of default tables to create if the database doesn't exist already
     */
    public DbServices(String dbName, String login, String password, String[] defaultTables) {
        setDbName(dbName);
        setLogin(login);
        setPassword(password);
        setDefaultTables(defaultTables);
        if (getDefaultTables().length <= 0) {
            System.out.println("no default tables detected!");
        }
        setUpDB();
    }

    //---Private Methods----------------------------------------------------------------------------

    /**
     * Setting up the Database- Connection.
     * <p>
     * Searching for a Database with the given name; don't compares the content!
     * If the database doesn't exists it will be created; empty if no default tables are passed!
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


        try {
            String url = "jdbc:derby:" + System.getProperty("user.dir") + "/DataBases/" + getDbName();
            setConn(DriverManager.getConnection(url));//, getLogin(), getPassword()));    //TODO: add account support later
            System.out.println("Database excise...");
            System.out.println("connected to database: \"" + getDbName() + "\"\n");
        } catch (SQLException e) {
            initDb();
        }
    }

    /**
     * Initialises a new Database with default Tables if passed.
     */
    private void initDb() {

        try {
            String url = "jdbc:derby:" + System.getProperty("user.dir") + "/DataBases/" + getDbName() + ";create=true";
            setConn(DriverManager.getConnection(url));//, getLogin(), getPassword()));    //TODO: add account support later

            if (getDefaultTables() != null) {
                System.out.println("creating new tables:");
                for (String s : getDefaultTables()) {
                    createTable(s);
                }
            } else {
                System.out.println("no default tables set!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("\nDatabase initialisation completed!\n");
    }

    /**
     * Deleting a file and if file is a directory; deleting its sub-directory's and sub-files.
     *
     * @param file File to delete.
     */
    private void delete(File file) {
        if (file.isDirectory()) {
            for (File c : file.listFiles())
                delete(c);
        }
        if (!file.delete())
            System.out.println("Failed to delete file: " + file);
    }

    //---Non Public Setter and Getter--------------------------------------------------------------

    private String getDbName() {
        return this.dbName;
    }

    private void setDbName(String dbName) {
        this.dbName = dbName;
    }

    private Connection getConn() {
        return this.conn;
    }

    private void setConn(Connection conn) {
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

    private String[] getDefaultTables() {
        return this.defaultTables;
    }

    private void setDefaultTables(String[] defaultTables) {
        this.defaultTables = defaultTables;
    }

    //---Public Methods----------------------------------------------------------------------------

    /**
     * Closes the current connection to the belonged database.
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
     * Creating a new table (with a fix primary-key: "id") if not already exists.
     *
     * @param tableName Name for the new table
     */
    public void createTable(String tableName) {
        //create table with a default id "id"
        try {
            if (!existsTable(tableName)) {
                if (!tableName.equals("")) {

                    Statement statement = getConn().createStatement();
                    statement.executeUpdate("CREATE TABLE " + tableName + " (ID INT PRIMARY KEY, NAME VARCHAR (50))");
                    System.out.println("table \"" + tableName + "\" created successfully.");

                } else {
                    System.out.println("ignoring empty table!");
                }
            }
        } catch (SQLException e) {
            System.out.println("unable to create table: \"" + tableName + "\"");
        }
    }

    /**
     * Deletes the hole table if exists.
     * @param tableName Name of the Table to delete.
     */
    public void deleteTable(String tableName) {

        try {
            if (existsTable(tableName)) {
                String sql = "DROP TABLE " + tableName;
                Statement statement = getConn().createStatement();
                statement.executeUpdate(sql);
                System.out.println("deleted table:\"" + tableName + "\" successfully!");
            } else {
                System.out.println("Table: \"" + tableName + "\" not found!");
            }
        } catch (SQLException e) {
            System.out.println("\nunable to delete table: \"" + tableName + "\"!\n");
            e.printStackTrace();
        }

    }

    /**
     * Printing the names of all current tables in the database.
     */
    public void printTableNames() {

        try {
            DatabaseMetaData meta = getConn().getMetaData();
            ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
            System.out.println("\nList of tables: ");
            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
            }
            System.out.println("\n");
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO: finish
     * Prints the content of the given table to the console.
     * @param tableName
     */
    public void printTable(String tableName) {

        try {
            if (existsTable(tableName)) {
                Statement stmt = getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
                System.out.println("Content of table \"" + tableName + "\":");

                while (rs.next()) {
                    int id = rs.getInt(1);
                    String name = rs.getString(2);
                    System.out.println(id+"\t"+name);
                }
                System.out.println("");
            } else {
                System.out.println("unable to print the content of table: \"" + tableName + "\"\n");
            }
        } catch (Exception e) {

        }
    }

    /**
     * Checks if a table exists in the database.
     *
     * @param tableName Name of the table to check.
     * @return false if table don't exists in the Database; true if it exists.
     */
    public boolean existsTable(String tableName) {
        try {
            DatabaseMetaData metadata = getConn().getMetaData();
            ResultSet rs = metadata.getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"});
            if (rs.next()) {
                if (rs.getString("TABLE_NAME").equals(tableName.toUpperCase())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes the hole database and everything belong to it.
     * Use with caution! No recovery!!!
     */
    public void deleteDatabase() {
        try {
            System.out.println("deleting database \"" + getDbName() + "\"...");
            getConn().close();

            File target = new File(System.getProperty("user.dir") + "/DataBases/" + getDbName());
            delete(target);
            System.out.println("Database \"" + getDbName() + "\" successfully deleted!\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * TODO: finish
     * @param tableName
     */
    public void insertData(String tableName){

        try {
            if (existsTable(tableName)) {
                String sql = "INSERT INTO " + tableName + "(ID, NAME) VALUES (78, 'ErsteWaffe')";
                Statement statement = getConn().createStatement();
                statement.execute(sql);
                System.out.println("edited table:\"" + tableName + "\".");
            } else {
                System.out.println("Table: \"" + tableName + "\" not found!");
            }
        } catch (SQLException e) {
            System.out.println("\nunable to edit table: \"" + tableName + "\"!\n");
        }
    }

    /**
     * To perform a basic SQL statement without error correction!
     * @param sql A SQL statement in string format.
     */
    public void sql(String sql){

        try {
            Statement statement = getConn().createStatement();
            statement.execute(sql);
            System.out.println("SQL statement performed.");
        } catch (SQLException e) {
            System.out.println("unable to perform sql statement!");
        }
    }
}

