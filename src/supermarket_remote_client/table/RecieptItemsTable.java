/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.table;
import supermarket_remote_client.table.util.MyTablesTemplate;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class RecieptItemsTable extends MyTablesTemplate{

    private Vector<String> headV;

    public RecieptItemsTable(){
        headV = new Vector<String>();
        headV.addElement("Item Description");
        headV.addElement("Unit Price");
        headV.addElement("Quantity");
        headV.addElement("Total");

        init();
    }

    private void init(){
        setHeaderVector(headV);
    }

    public void setData(Vector<Vector> d){
        setDataVector(d);
        fireTableDataChanged();
    }

    public boolean isItemRowEmpty(){
        if(this.getDataVector().isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }
}
