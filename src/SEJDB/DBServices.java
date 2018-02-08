package SEJDB;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBServices {
    
    //TODO: account bezogene sicherungen integrieren; siehe "deleteDatabase()"
    //TODO: Fehlerabfangen: Datenbank wird zwischendrin gelöscht!
    
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
    public DBServices(String dbName, String login, String password) {
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
    public DBServices(String dbName, String login, String password, String[] defaultTables) {
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
     *
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
     * TODO: finish: adding dynamic content
     * Prints the content of the given table to the console.
     *
     * @param tableName The name of the table to print.
     */
    public void printTable(String tableName) {
        
        try {
            if (existsTable(tableName)) {
                Statement stmt = getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData rsmd = rs.getMetaData();
                System.out.println("Content of table \"" + tableName + "\":");
                
                while (rs.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println("");
                }
                rs.close();
                System.out.println("");
            } else {
                System.out.println("unable to print the content of table: \"" + tableName + "\"\n");
            }
        } catch (Exception e) {
            System.out.println("unable to print the content of table: \"" + tableName + "\"\n");
        }
    }
    
    /**
     * Prints the content of the given table to the console with a limit of rows to print.
     *
     * @param tableName The name of the table to print.
     * @param limit     The limit of rows to print.
     */
    public void printTable(String tableName, int limit) {
        
        try {
            if (existsTable(tableName)) {
                Statement stmt = getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData rsmd = rs.getMetaData();
                System.out.println("\nContent of table \"" + tableName + "\":");
                
                int rowCounter = 1;
                while (rs.next() && rowCounter <= limit) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println("");
                    rowCounter++;
                }
                rs.close();
                System.out.println("");
            } else {
                System.out.println("unable to print the content of table: \"" + tableName + "\"\n");
            }
        } catch (Exception e) {
            System.out.println("unable to print the content of table: \"" + tableName + "\"\n");
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
                    rs.close();
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
     * TODO: finish: adding error correction.
     *
     * @param tableName
     */
    public void insertData(String tableName, HashMap<String, String> input) {
        if (existsTable(tableName)) {
            int ID = getFreeID(tableName);
            
            
            StringBuilder valueNames = new StringBuilder(); //Form: "NAME, DAMAGE, Derfg "
            StringBuilder values = new StringBuilder();     //Form: "'xxx', 'dfg', 'dgds' "
            
            for (Map.Entry<String, String> entry : input.entrySet()) {
                valueNames.append(", " + entry.getKey());
                values.append(", '" + entry.getValue() + "'");
            }
            
            try {
                
                String sql = "INSERT INTO " + tableName + "(ID" + valueNames.toString() + ") VALUES (" + ID + values.toString() + ")";
                Statement statement = getConn().createStatement();
                statement.execute(sql);
                System.out.println("edited table:\"" + tableName + "\".");
                
            } catch (SQLException e) {
                System.out.println("\nunable to edit table: \"" + tableName + "\"!\n");
            }
        } else {
            System.out.println("Table: \"" + tableName + "\" not found!");
        }
    }
    
    /**
     * Generates a new free ID to use.
     * Always returns the lowers ID possible.
     *
     * @param tableName Targeting table.
     * @return free ID
     */
    public int getFreeID(String tableName) {
        
        int freeID = 0;
        while (containsID(tableName, freeID)) {
            freeID++;
        }
        return freeID;
    }
    
    /**
     * Checks if the ID is already used in the table.
     *
     * @param tableName Targeting table.
     * @param ID        ID to check.
     * @return true if the table contains the ID, false if not.
     */
    public boolean containsID(String tableName, int ID) {
        try {
            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            ArrayList<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
            rs.close();
            if (ids.contains(ID)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Deletes a hole Row in the table identified by its ID.
     *
     * @param tableName Targeting table.
     * @param ID        ID of the Row to delete.
     */
    public void deleteRow(String tableName, int ID) {
        if (containsID(tableName, ID)) {
            try {
                Statement statement = getConn().createStatement();
                statement.execute("DELETE FROM " + tableName + " WHERE ID=" + ID);
                System.out.println("Row, ID: " + ID + " , in Table \"" + tableName + "\" successfully deleted.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("no row with ID: " + ID + " found in table \"" + tableName + "\"!");
        }
    }
    
    /**
     * To perform a basic SQL statement without error correction!
     * Can't show or print results.
     *
     * @param sql A SQL statement in string format.
     */
    public void sql(String sql) {
        
        if (!sql.contains(";")) {
            if (!sql.contains("SELECT") && !sql.contains("select")) {
                try {
                    Statement statement = getConn().createStatement();
                    statement.execute(sql);
                    System.out.println("SQL statement performed.");
                    return;
                } catch (SQLException e) {
                    System.out.println("unable to perform sql statement!");
                    return;
                }
            } else {
                try {
                    Statement stmt = getConn().createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while (rs.next()) {
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            System.out.print(rs.getString(i) + "\t");
                        }
                        System.out.println("");
                    }
                    rs.close();
                    System.out.println("");
                } catch (SQLException e) {
                    System.out.println("unable to perform sql statement containing 'select'!");
                    return;
                }
            }
        } else {
            System.out.println("unable to perform sql statement containing ; or multiple commands appended!");
        }
    }
}

