/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client;
import supermarket_remote_client.gui.StartUpDialog;
import javax.swing.SwingUtilities;

/**
 *
 * @author MUSTAFA
 */
public class Main{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                StartUpDialog start = new StartUpDialog();
                start.initializeOption();
                start.setVisible(true);
            }
        });
    }

}
