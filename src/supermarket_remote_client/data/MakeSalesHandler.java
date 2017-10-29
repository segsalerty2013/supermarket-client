/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.data;
import java.awt.Color;
import java.util.*;
import java.sql.*;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Segun
 */
public class MakeSalesHandler {
    private Vector<Vector> data;
    private Connection connection;
    private Statement statement;
    private ResultSet result;

    private JButton ctrlBtn;
    private JLabel salesIdLbl;

    private String userName = "";
    private String date = "";
    private String time = "";
    private String reciept = "";

    private int counter = 0;

    private boolean quit = false;

    private Vector<String> reverseSqlV;

    public MakeSalesHandler(Vector<Vector> d, Connection c, JButton b, JLabel re, String u, String da, String t){
        data = d;
        connection = c;
        userName = u;
        date = da;
        time = t;
        ctrlBtn = b;
        reverseSqlV = new Vector<String>();
        salesIdLbl = re;
        salesIdLbl.setForeground(Color.BLACK); //make sure its set to its default value
        performSales(); //perform sales
        //doSales();
    }

    private boolean genRecieptNo(){
        boolean ret = false;
        int n = showRandomInteger(12000, 24999, new Random());
        /*
        int n = new Random().nextInt(6000);
        String r = String.valueOf(n);
        if(r.length() < 4){
            String res = "";
            List<Character> ch = new ArrayList<Character>();
            switch(r.length()){
                case 1:
                    ch.add('0');
                    ch.add('0');
                    ch.add('0');
                    ch.add(r.charAt(0));
                    break;
                case 2:
                    ch.add('0');
                    ch.add('0');
                    ch.add(r.charAt(0));
                    ch.add(r.charAt(1));
                    break;
                case 3:
                    ch.add('0');
                    ch.add(r.charAt(0));
                    ch.add(r.charAt(1));
                    ch.add(r.charAt(2));
                    break;
                default:
                    break;
            }
            for(char c : ch){
                res += c;
            }
            reciept = res;
            n = Integer.valueOf(res);
        }
        else{
            n = Integer.parseInt(r);
        }
        */
        //then check the reciept if valid
        try{
             statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
             result = statement.executeQuery("SELECT reciept FROM sales WHERE reciept=" + n);
             if(result.first()){
                 genRecieptNo();
                 counter++;
             }
             else if(counter >= 24999){
                JOptionPane.showMessageDialog(ctrlBtn, "Trial Mode Expired, Contact us for full Version\nsegsalerty@yahoo.com, 08025481373",
                        "SOFTWARE TRIAL EXPIRED", JOptionPane.PLAIN_MESSAGE);
                quitSales();
             }
             else{
                 reciept = String.valueOf(n);
                 ret = true;
             }
        }
        catch(SQLException sE){
            System.err.println("Errror occured trying to genReciept : " + sE.getMessage());
        }
        return ret;
    }

  private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
    if ( aStart > aEnd ) {
      throw new IllegalArgumentException("Start cannot exceed End.");
    }
    //get the range, casting to long to avoid overflow problems
    long range = (long)aEnd - (long)aStart + 1;
    // compute a fraction of the range, 0 <= frac < range
    long fraction = (long)(range * aRandom.nextDouble());
    int randomNumber =  (int)(fraction + aStart);    
    return randomNumber;
  }
    
    private void doSales(String com, String qty, String total){
        //new SwingWorker<Void, Void>(){
            //public Void doInBackground(){
                try{
                    if(!quit){
                        try{
                            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            int x = statement.executeUpdate("INSERT INTO sales (com_name, qty_purchased, total, date, user, reciept) VALUES ('" +
                                com + "', " + qty + ", " + (Integer.parseInt(total)) + ", '" + (time + " # " + date) + "', '"
                                + userName + "', " + Integer.parseInt(reciept) + ")");
                            //System.out.println(x);
                        }
                        catch(SQLException sE){
                            System.err.println("Errror occured trying to doSales : " + sE.getMessage());
                        }
                    }
                }
                catch(Exception e){
                    System.err.println(e.getMessage());
                }
                //return null;
            //}
        //}.execute();
    }

    private synchronized void performStockReduction(String com, int qty){
        int q = 0;
        int reduced = 0;
        if(com.startsWith("BULK_")){
            //therefore look inside bulk item table
            //first select from the table to get the qty available
            try{
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                result = statement.executeQuery("SELECT * FROM comodities_bulk WHERE name='" + com + "'");
                if(result.absolute(1)){
                    q = result.getInt("stock_qty");
                }
                if(qty > q){
                    //means the item u want to purchase is more than stock
                    JOptionPane.showMessageDialog(ctrlBtn, "- " + com + " is less than whats in STOCK - \nJUST " + q + " AVAILABLE",
                            "STOCK UNSATISFIED", JOptionPane.PLAIN_MESSAGE);
                    quitSales();
                }
                else{
                    reduced = q - qty;
                    int u = statement.executeUpdate("UPDATE comodities_bulk SET stock_qty=" + reduced + " WHERE name='" + com + "'");
                    //after it has been updated. store it in reverse vector
                    reverseSqlV.addElement("UPDATE comodities_bulk SET stock_qty=" + q + " WHERE name='" + com + "'");
                }
            }
            catch(SQLException sE){
                System.err.println("Error at bulk in perform stock reduction : " + sE.getMessage());
            }
        }
        else{
            //then, that means its a unit comodity then
            //first select from the table to get the qty available
            try{
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                result = statement.executeQuery("SELECT stock_qty FROM comodities_unit WHERE name='" + com + "'");
                if(result.absolute(1)){
                    q = result.getInt("stock_qty");
                }
                if(qty > q){
                    //means the item u want to purchase is more than stock
                    JOptionPane.showMessageDialog(ctrlBtn, "- " + com + " is less than whats in STOCK - \nJUST " + q + " AVAILABLE",
                            "STOCK UNSATISFIED", JOptionPane.PLAIN_MESSAGE);
                    quitSales();
                }
                else{
                    reduced = q - qty;
                    int u = statement.executeUpdate("UPDATE comodities_unit SET stock_qty=" + reduced + " WHERE name='" + com + "'");
                    reverseSqlV.addElement("UPDATE comodities_unit SET stock_qty=" + q + " WHERE name='" + com + "'");
                }
            }
            catch(SQLException sE){
                System.err.println("Error at unit statement in perform stock reduction : " + sE.getMessage());
            }
        }
    }

    private void quitSales(){
        quit = true;
        reciept = "INVALID SALES";
        salesIdLbl.setForeground(Color.red);
    }

    private void performSales(){
        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                if(genRecieptNo()){
                    for(int i = 0; i < data.size(); i++){
                        performStockReduction(data.elementAt(i).elementAt(0).toString(), Integer.parseInt(data.elementAt(i).elementAt(1).toString()));
                    }
                    //after the stocks are being reduced successfully.. then do sales gangan
                    for(int i = 0; i < data.size(); i++){
                        if(!quit){
                            doSales(data.elementAt(i).elementAt(0).toString(), data.elementAt(i).elementAt(1).toString(),
                                    data.elementAt(i).elementAt(2).toString());
                        }
                        else{
                            for(String k : reverseSqlV){
                                try{
                                    int j = statement.executeUpdate(k);
                                }
                                catch(SQLException sE){
                                    System.err.println(k + " : " + sE.getMessage());
                                }
                            }
                        }
                    }
                }
                return null;
            }
            @Override
            public void done(){
                ctrlBtn.setEnabled(true);
                salesIdLbl.setText(reciept);
                ctrlBtn.revalidate();
                salesIdLbl.revalidate();
            }
        }.execute();
    }
}
