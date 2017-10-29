/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui.drag_n_drop;
import java.awt.datatransfer.*;
import javax.swing.JLabel;

/**
 *
 * @author MUSTAFA
 */

public class TransferableJPanel implements Transferable {

    protected static DataFlavor panelFlavor = new DataFlavor(JLabel.class, "A JLabel Item Object");
    protected static DataFlavor[] supportedFlavors = {panelFlavor, DataFlavor.stringFlavor, };
    private JLabel item;

    public TransferableJPanel(JLabel l) {
        item = l;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(panelFlavor) || flavor.equals(DataFlavor.stringFlavor)){
            return true;
        }
        else{
            return false;
        }
    }


    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException{
        if (flavor.equals(panelFlavor))
            return item; //get the components been dragged
        else if (flavor.equals(DataFlavor.stringFlavor))
             return item.toString();
         else
             throw new UnsupportedFlavorException(flavor);
    }
}
