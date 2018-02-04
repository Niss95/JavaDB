import dbServices.DbServices;

import java.sql.SQLException;

public class Main {

    static class db extends dbServices.DbServices {
        //just to use it as alias: DbServices -> db
    }


    public static void main(String args[]) throws SQLException {
        System.out.println("Program started...");

        db.setUpDB("LootDB", "admin", "admin");


        //app code in here



        //close current connection
        db.closeConnection();


    }
}
