/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.database;
import java.sql.*;
import java.util.Vector;
import javax.swing.SwingWorker;

/**
 *
 * @author MUSTAFA
 */
public class PingResources extends Thread{

    private PingResources self = this;
    
    private Connection con = null;
    private Statement stat = null;
    private ResultSet result = null;

    /////////////
    private Vector<String> clientNameV;
    private Vector<String> clientPassV;
    ////////////

    private String compName = "NOT REGISTERED";//means the software is not registered
    private String addRess = "";
    private String contact = "";
    private String userName = "";
    
    public PingResources(Connection c){
        con = c; //get the connection instance to work on
        ///initialize the vectors
        clientNameV = new Vector<String>();
        clientPassV = new Vector<String>();
    }

    @Override
    public void run(){
        try{
            loadUserCredentials();
        }
        catch(Exception E){}
        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                try{
                    stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    result = stat.executeQuery("SELECT * FROM reg_info");
                    if(result.first()){
                        compName = result.getString("name");
                        addRess = result.getString("address");
                        contact = "Telephone : ( " + result.getString("tel") + " ) , Email : ( " + result.getString("email") + " )";
                    }
                }
                catch(SQLException sE){
                    System.err.println("Error at getting company name from DB : " + sE.getMessage());
                }
                return null;
            }
        }.execute();
    }


    public Connection getConnection(){
        return con;
    }
    
    private void loadUserCredentials() throws Exception{
        //try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT * FROM users");
            if(result.first()){
                do{
                    clientNameV.addElement(result.getString("name"));
                    clientPassV.addElement(result.getString("password"));
                }
                while(result.next());
            }
        //}
        //catch(SQLException sE){
            //System.err.println("Error at loading user Credential from DB : " + sE.getMessage());
        //}
    }

    public void reloadUserCredentials() throws Exception{
        loadUserCredentials();
    }

    public Vector<String> getUserNames(){
        //loadUserCredentials();
        return clientNameV;
    }

    public Vector<String> getPasswords(){
        //loadUserCredentials();
        return clientPassV;
    }

    public String getCompanyName(){
        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                self.start();
                return null;
            }
        }.execute();
        return compName;
    }

    public String getAddress(){
        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                self.start();
                return null;
            }
        }.execute();
        return addRess;
    }

    public String getContactInfo(){
        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                self.start();
                return null;
            }
        }.execute();
        return contact;
    }

    public void setUserName(String u){
        userName = u;
    }

    public String getUserName(){
        return userName;
    }

}
