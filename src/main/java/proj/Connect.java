package proj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {
    Connection con;
    String p_name;
    String c_name;
    public Connection getCon() {
        return con;
    }
    public String getP_name(){
        return p_name;
    }
    public Connect(String p_name, String c_name){
        try{
            this.p_name =p_name;
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + p_name);
            createTableIfNotExists(p_name, c_name);
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Could not find JDBC driver");
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists(String p_name, String c_name) {
        try (Statement stmt = con.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + p_name + " (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "category TEXT NOT NULL," +
                    "characteristic TEXT NOT NULL," +
                    "manufacturer TEXT NOT NULL" +
                    ")";
            stmt.executeUpdate(sql);
            sql="CREATE TABLE IF NOT EXISTS "+ c_name +" ("+
                    "name TEXT PRIMARY KEY," +
                    "description TEXT NOT NULL"+")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Failed to create table: " + p_name);
            e.printStackTrace();
        }
    }


}
