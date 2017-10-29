/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui;
import supermarket_remote_client.gui.util.*;
import supermarket_remote_client.database.PingResources;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author Segun
 */
public class TaskDialog extends JDialog{

    private TaskDialog self = this;
    private String userName = "";
    private Connection connection;

    private static final Dimension userDimension = Toolkit.getDefaultToolkit().getScreenSize();

    private JTabbedPane tabTab;

    private JPanel wrapper;
    private SalesTab salesTab = null;

    ///for sales tab sizing
    private int width = userDimension.width - 150;
    private int height = userDimension.height - 150;

    private ClientFrame parent;

    private PingResources resource;
    
    private TaskDialog.BackgroundWork t;
    

    public TaskDialog(PingResources p, String u, Connection c, ClientFrame f){ //initialize constructor with user nae
        resource = p;
        userName = u;
        connection = c;
        parent = f;
        
        //salesTab = new SalesTab(width - 7, height - 7, connection, parent.getStartUpInstance(), resource);
        t = new TaskDialog.BackgroundWork();
        
        setTitle(resource.getCompanyName() + " : USER LOGGED IN AS ---> " + userName);//the JFrame title
        setSize(width + 100, height + 100);
        setPreferredSize(new Dimension(width + 100, height + 100));
        setMinimumSize(new Dimension(width + 50, height + 100));
        setLocation((userDimension.width - (width + 100)) /2, (userDimension.height - (height + 100)) /2);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //setResizable(false);
        //setIconImage(new ImageIcon(getClass().getResource("images/logo.jpg")).getImage());
        
        //
        MyLookAndFeel.setLook();
        //
        
        self.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                hideMe();
            }
        });
        t.execute(); //start the swing worker
    }

    public void showMe(int t){
        tabTab.setSelectedIndex(t);
        self.setVisible(true);
    }

    public void hideMe(){
        self.hide();
    }

    private void initComponents(){
        //
        //salesTab = new SalesTab(width - 7, height - 7);
        wrapper = new JPanel(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(new Dimension(userDimension.width - 67, userDimension.height - 67)));
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(134, 206, 134), 2));

        tabTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        tabTab.setPreferredSize(new Dimension((userDimension.width - 67), (userDimension.height - 67)));
        //tabTab.setBorder(BorderFactory.createLineBorder(new Color(152, 213, 152), 1));
        tabTab.setBackground(new Color(0, 228, 153));
        tabTab.add("Make Sales", salesTab.getSalesTab());
        tabTab.add("Today's Sales Summary", new JPanel());
        tabTab.add("Today's Stock Summary", new JPanel());
        tabTab.add("Reciepts", new JPanel());
        tabTab.setFocusable(false);
        wrapper.add(tabTab);
        pack();

    }

    class BackgroundWork extends SwingWorker<Void, Void>{

        public BackgroundWork(){
        }

        public Void doInBackground(){
            //salesPanel = salesTab.getSalesTab();
            salesTab = new SalesTab(width - 7, height - 7, connection, parent.getStartUpInstance(), resource);
            repaint();
            validate();
            return null;
        }

        @Override
        public void done(){
            new SwingWorker<Void, Void>(){
                public Void doInBackground(){
                    initComponents(); //then initialize the component
                    setContentPane(wrapper);
                    repaint();
                    validate();
                    return null;
                }
            }.execute();
        }
    }

}
