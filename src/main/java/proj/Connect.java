package proj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {
    Connection con;
    String name;
    public Connection getCon() {
        return con;
    }
    public String getName(){
        return name;
    }
    public Connect(String name){
        try{
            this.name=name;
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + name);
            createTableIfNotExists(name);
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Could not find JDBC driver");
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists(String TABLE_NAME) {
        try (Statement stmt = con.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "category TEXT NOT NULL," +
                    "characteristic TEXT NOT NULL," +
                    "manufacturer TEXT NOT NULL" +
                    ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Failed to create table: " + TABLE_NAME);
            e.printStackTrace();
        }
    }


}
