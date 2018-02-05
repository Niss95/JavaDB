import dbServices.DbServices;

public class Main {

    public static void main(String args[]) {
        System.out.println("Program started...\n");

        String[] defaultTables={"Weapons", "GeneralLoot", "FirstAid", "ComputerPath", ""};
        DbServices db = new DbServices("LootDB", "admin", "admin", defaultTables);
        //DbServices db = new DbServices("LootDB", "admin", "admin");

        //app code in here


        db.printTableNames();
        db.deleteTable(defaultTables[0]);
        db.printTableNames();
        //db.deleteTable("Weapons");


        //close current connection
        //db.deleteDatabase();
        db.closeConnection();
        System.out.println("\nProgram ending...");
    }
}
