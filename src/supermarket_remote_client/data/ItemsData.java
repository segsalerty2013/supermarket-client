/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.data;
import java.util.*;
import javax.swing.*;
import java.sql.*;

/**
 *
 * @author MUSTAFA
 */
public class ItemsData {

    private List<String> itemName; //unit name
    private List<Integer> itemPrice; //unit price
    private List<String> bulkName; //bulk name
    private List<Integer> bulkPrice; //bulk price

    private Connection connection;
    private Statement statement;
    private ResultSet result;

    private DataDb dataDb;

    private boolean done = false;
    
    public ItemsData(Connection c){
        connection = c;
        itemName = new ArrayList<String>();
        itemPrice = new ArrayList<Integer>();
        bulkName = new ArrayList<String>();
        bulkPrice = new ArrayList<Integer>();
        dataDb = new DataDb();
        
        loadData();
    }

    public void reloadData(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                dataDb = new DataDb();
                dataDb.execute();
            }
        });
    }

    private void loadData(){
        dataDb.execute();
    }

    public List<String> getItemName(){
        return itemName;
    }

    public List<Integer> getItemPrice(){
        return itemPrice;
    }

    public List<String> getBulkName(){
        return bulkName;
    }

    public List<Integer> getBulkPrice(){
        return bulkPrice;
    }

    public boolean isTaskDone(){
        return done;
    }

    public int getPriceOfItem(String it){
        int price = 0;
        if(!it.equals(null)){
            Iterator unit = itemName.iterator();
            Iterator unit_price = itemPrice.iterator();
            Iterator bulk = bulkName.iterator();
            Iterator bulk_price = bulkPrice.iterator();
            String t = "";
            int t2 = 0;
            while(unit.hasNext() && unit_price.hasNext()){
                t = (String)unit.next();
                t2 = Integer.parseInt(String.valueOf(unit_price.next()));
                if(t.equals(it)){
                    price = t2;
                    break;
                }
            } //look into unit items to get match
            if(price == 0){
                //then loop the next while
                while(bulk.hasNext() && bulk_price.hasNext()){
                    t = (String)bulk.next();
                    t2 = Integer.parseInt(String.valueOf(bulk_price.next()));
                    if(t.equals(it)){
                        price = t2;
                        break;
                    }
                }
            }
        }
        return price;
    }

    private class DataDb extends SwingWorker<Void, Void>{
        
        DataDb(){

        }

        public Void doInBackground(){
            try{
                itemName.clear();
                itemPrice.clear();
                bulkName.clear();
                bulkPrice.clear();
                //statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                //statement.executeUpdate("DELETE FROM sales");
                //statement.executeUpdate("DELETE FROM online_users");
                //statement.executeUpdate("DELETE FROM online_sys");
                load_units();
                load_bulks();
            }
            catch(Exception sE){
                System.err.println(sE.getMessage());
            }
            return null;
        }

        private void load_units() throws Exception{
            //try{
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                result = statement.executeQuery("SELECT * FROM comodities_unit");//unit commodity
                if(result.first()){
                    do{
                        int x = result.getInt("stock_qty");
                        if(x != 0){
                            //means this item that i want to add is not finished
                            itemName.add(result.getString("name"));
                            itemPrice.add(result.getInt("unit_price"));
                        }
                    }
                    while(result.next());
                }
            //}
            //catch(SQLException sE){
               // System.err.println("Load Unit " + sE.getMessage());
            //}
        }

        private void load_bulks() throws Exception{
            //try{
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                result = statement.executeQuery("SELECT * FROM comodities_bulk");//unit commodity
                while(result.next()){
                    //if there are rows in comoditues bulk
                    int x = result.getInt("stock_qty");
                    if(x != 0){
                        //means this item that i want to add is not finished
                        bulkName.add(result.getString("name"));
                        bulkPrice.add(result.getInt("unit_price"));
                    }
                }
            //}
            //catch(SQLException sE){
                //System.err.println(sE.getMessage());
            //}
        }

        @Override
        public void done(){
            done = true;
        }
    }

}
