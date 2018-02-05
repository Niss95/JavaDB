import dbServices.DbServices;

import java.sql.SQLException;

public class Main {

    public static void main(String args[]) throws SQLException {
        System.out.println("Program started...");

        String[] defaultTables={"Weapons", "GeneralLoot", "FirstAid", "ComputerPath"};
        DbServices db = new DbServices("LootDB", "admin", "admin", defaultTables);




        //app code in here



        //close current connection
        db.closeConnection();


    }
}
