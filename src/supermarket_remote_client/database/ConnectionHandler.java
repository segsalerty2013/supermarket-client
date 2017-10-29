/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.database;
import java.sql.*;

/**
 *
 * @author MUSTAFA
 */
public class ConnectionHandler {

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private String dbHost = "";
    private static final String dbName = "super_market";
    private static final String user = "sup_remote";
    private static final String password = "alert01";
    private static final int port = 3336;

    private Connection con;

    public ConnectionHandler(String h){
        dbHost = h;
        loadDriver();
        connectDb();
    }

    private void loadDriver(){
        try{
            Class.forName(DRIVER);
        }
        catch(ClassNotFoundException e){
            System.err.println("Driver cannot be loaded : " + e.getMessage());
        }
    }

    private void connectDb(){
        try{
            con = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + port + "/" + dbName , user, password);
            System.out.println("Connected to Remote database");
        }
        catch(SQLException sE){
            System.err.println("Error at connecting to remote DB : " + sE.getMessage());
        }
    }

    public Connection getConnectionInstance(){
        return con;
    }

    public void disconnectDb(){
        try{
            con.close();
        }
        catch(SQLException e){
            System.err.println("Error at discnnecting from remote DB : " + e.getMessage());
        }
    }
}
