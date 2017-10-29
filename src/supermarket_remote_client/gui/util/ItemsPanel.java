/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui.util;
import supermarket_remote_client.gui.drag_n_drop.TransferableJPanel;
import supermarket_remote_client.gui.StartUpDialog;
import supermarket_remote_client.data.ItemsData;
import supermarket.util.InputsManager;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.util.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.*;

/**
 *
 * @author MUSTAFA
 */
public class ItemsPanel extends JPanel implements DragGestureListener{

    private JTabbedPane tab;
    private java.util.List<String> itemName;
    private java.util.List<String> itemPrice;

    private JPanel whole_sales;
    private JPanel unit_items;
    private JScrollPane unit_scroll;
    private JScrollPane bulk_scroll;
    private JPopupMenu menu;
    private JPopupMenu unit_menu;
    private JTextField wholeSales_field;
    private JTextField unit_field;
    /*
     * for wholesales content
     */
    private java.util.List<String> itemName_w;
    private java.util.List<String> itemPrice_w;

    private int width = 0;
    private int height = 0;

    private DragSource ds;

    private ItemsData data;

    private java.sql.Connection connection;

    private SwingWorker<Void, Void> work;
    private javax.swing.Timer time;

    private StartUpDialog startUp;

    private int u_oldHPos = 0;
    private int b_oldHPos = 0;

    private boolean pauseState = false;


    public ItemsPanel(int w, int h, java.sql.Connection c, StartUpDialog s){
        super(new FlowLayout(FlowLayout.CENTER, 5, 5));
        connection = c;
        width = w;
        height = h;
        startUp = s;
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setBackground(new Color(51, 148, 199));

        data = new ItemsData(connection);
        tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        tab.setPreferredSize(new Dimension(width - 10, height - 3)); //but get the number of items to be displayed and multiply by 30
        tab.setBackground(new Color(0, 228, 153));
        tab.setBorder(BorderFactory.createLineBorder(new Color(224, 226, 130), 2));

        init_setComponent();

        loadActions();
        tab.setFocusable(false);
        add(tab);
        initRefresh(); //init the refreshing of the components
    }

    private void manage_setComponent(){        
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                switch(startUp.getMarketModule()){
                    case 0:
                        //wholesales
                        tab.setComponentAt(0, initItems_Wholesales());
                        break;
                    case 1:
                        //retails
                        tab.setComponentAt(0, initItems());
                        break;
                    case 2:
                        //generic
                        tab.setComponentAt(0, initItems());
                        tab.setComponentAt(1, initItems_Wholesales());
                        break;
                    default:
                }
                tab.revalidate();
            }
        });
    }

    private void init_setComponent(){
        //bulk_scroll.getVerticalScrollBar().addAdjustmentListener(scrollListen);
        switch(startUp.getMarketModule()){
            case 0:
                //wholesales
                tab.add("WholeSales  -- @ BULK Sales --", initItems_Wholesales());
                break;
            case 1:
                //retails
                tab.add("Retails  -- @ UNIT Sales --", initItems());
                break;
            case 2:
                //generic
                tab.add("Retails  -- @ UNIT Sales --", initItems());
                tab.add("WholeSales  -- @ BULK Sales --", initItems_Wholesales());
                break;
            default:
        }
        tab.revalidate();
    }

    public ItemsData getData(){
        return data;
    }
    
    private void initRefresh(){
        work = new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                time = new javax.swing.Timer(19000, new java.awt.event.ActionListener(){
                    public void actionPerformed(java.awt.event.ActionEvent e){  
                        if(data.isTaskDone()){
                            new SwingWorker<Void, Void>(){
                                public Void doInBackground(){
                                    data.reloadData(); //reload the data
                                    return null;
                                }
                                @Override
                                public void done(){
                                    manage_setComponent();
                                }
                            }.execute();
                        }
                    }
                });
                if(!time.isRunning()){
                    time.start();
                }
                return null;
            }

            @Override
            public void done(){
                continue_pause_handler(); //contains the thread and timer to continue and pause refresh events
            }
        };
        work.execute();
    }

    private void pauseRefresh(){
        try{
            Thread.sleep(90);
            if(time.isRunning()){
                time.stop();
                //pauseState = true;
                System.out.println("I Pasused refrsh");
            }
            else{
                //pauseState = false;
            }
        }
        catch(InterruptedException iE){}
    }

    private void continueRefresh(){
        try{
            Thread.sleep(90);
            if(!time.isRunning()){
                time.restart();                
                System.out.println("I continue refrsh");
            }
            else{
                //pauseState = true;
            }
        }
        catch(InterruptedException iE){}
    }

    //havent used this function for now
    private void scrollHandler(){
        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                try{
                    if(!pauseState){
                        pauseRefresh();
                        Thread.sleep(20000);
                        continueRefresh();
                        pauseState = false;
                    }
                    //else{
                        //continueRefresh();
                    //}
                }
                catch(InterruptedException iE){}
                    return null;
                }
            }.execute();
    }

    private void continue_pause_handler(){
        new Thread(){
            @Override
            public void run(){
                new SwingWorker<Void, Void>(){
                    public Void doInBackground(){
                        new javax.swing.Timer(1500, new java.awt.event.ActionListener(){
                            public void actionPerformed(java.awt.event.ActionEvent e){
                                if(unit_menu.isVisible() || menu.isVisible()){
                                    pauseRefresh();
                                }
                                else{
                                    continueRefresh();
                                }
                            }
                        }).start();
                        return null;
                    }
                }.execute();
            }
        }.start();
    }

    private void loadActions(){
        unit_scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            public void adjustmentValueChanged(AdjustmentEvent ee){
                if(unit_scroll.getVerticalScrollBar().getValue() != u_oldHPos){
                    //scrollHandler();
                    //pauseState = true;
                    //then re-add the action listeners
                    //loadActions(); //call load actions again
                }
            }
        });

        bulk_scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            public void adjustmentValueChanged(AdjustmentEvent ee){
                if(bulk_scroll.getVerticalScrollBar().getValue() != b_oldHPos){
                    //scrollHandler();
                    //pauseState = true;
                    //then re-add the action listeners
                    //loadActions(); //call load actions again
                }
            }
        });
    }

    private JScrollPane initItems(){
        //max, 16 items
        unit_scroll = new JScrollPane();
        unit_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        unit_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        unit_scroll.setPreferredSize(new Dimension(((width - 100) / 2) - 1, 400));
        itemName = new ArrayList<String>();
        itemPrice = new ArrayList<String>();
        unit_items = new JPanel();
        ds = new DragSource();
        
        //
        unit_menu = new JPopupMenu();
        JLabel top = new JLabel("Search Items");
        top.setIcon(new ImageIcon(getClass().getResource("search.jpg")));
        top.setIconTextGap(7);
        top.setForeground(new Color(109, 139, 232));
        top.setPreferredSize(new Dimension(110, 25));
        top.setFont(new Font("Geogia", Font.BOLD, 11));
        unit_field = new JTextField();
        unit_field.setPreferredSize(new Dimension(110, 30));
        unit_field.setFont(new Font("Tahoma", Font.PLAIN, 14));
        unit_field.setForeground(Color.BLUE);
        unit_menu.add(top);
        unit_menu.addSeparator();
        unit_menu.add(unit_field);
        unit_field.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent e){
                //populateUnitPanel(unit_field.getText());
            }
            public void removeUpdate(DocumentEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        populateUnitPanel(unit_field.getText().toUpperCase());                        
                    }
                });
            }
            public void insertUpdate(DocumentEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        populateUnitPanel(unit_field.getText().toUpperCase());
                    }
                });  
            }
        });

        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
               unit_items.setComponentPopupMenu(unit_menu);
            }
        });
        
        //

        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                populateUnitPanel(""); //populate the items
            }
        });
        unit_items.setPreferredSize(new Dimension(((width - 100) / 2) - 18, 380));
        unit_scroll.setViewportView(unit_items);
        return unit_scroll;
    }

    private JScrollPane initItems_Wholesales(){
        bulk_scroll = new JScrollPane();
        bulk_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        bulk_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bulk_scroll.setPreferredSize(new Dimension(((width - 100) / 2) - 1, 400));
        itemName_w = new ArrayList<String>();
        itemPrice_w = new ArrayList<String>();
        whole_sales = new JPanel();
        ds = new DragSource();
        
        //
        menu = new JPopupMenu();
        JLabel top = new JLabel("Search Items");
        top.setIcon(new ImageIcon(getClass().getResource("search.jpg")));
        top.setIconTextGap(7);
        top.setForeground(new Color(109, 139, 232));
        top.setPreferredSize(new Dimension(110, 25));
        top.setFont(new Font("Geogia", Font.BOLD, 11));
        wholeSales_field = new JTextField();
        wholeSales_field.setPreferredSize(new Dimension(110, 30));
        wholeSales_field.setFont(new Font("Tahoma", Font.PLAIN, 14));
        wholeSales_field.setForeground(Color.BLUE);
        menu.add(top);
        menu.addSeparator();
        menu.add(wholeSales_field);
        wholeSales_field.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent e){
                //populateWholesalesPanel(wholeSales_field.getText());
            }
            public void removeUpdate(DocumentEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        populateWholesalesPanel(wholeSales_field.getText().toUpperCase());
                    }
                });
            }
            public void insertUpdate(DocumentEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        populateWholesalesPanel(wholeSales_field.getText().toUpperCase());
                    }
                });  
            }
        });
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
               whole_sales.setComponentPopupMenu(menu);
            }
        });
        
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                populateWholesalesPanel(""); //populate the items
            }
        });
        //
        whole_sales.setPreferredSize(new Dimension(((width - 100) / 2) - 18, 380));
        bulk_scroll.setViewportView(whole_sales);
        return bulk_scroll;
    }

    public void dragGestureRecognized(DragGestureEvent event) {
        Cursor cursor = null;
        JPanel panel = (JPanel) event.getComponent();

        Component []comp = panel.getComponents(); //get the components from dragged panel and get the first Jlabel
        JLabel item = (JLabel) comp[0];

        if (event.getDragAction() == DnDConstants.ACTION_COPY) {
            cursor = DragSource.DefaultCopyDrop;
        }

        event.startDrag(cursor, new TransferableJPanel(item));
    }

    private void populateWholesalesPanel(String query){

        itemName_w.clear();//clear the queue
        itemPrice_w.clear();//clear the queue

        whole_sales.removeAll();
        
        //populate the collections below here
        if(!query.equals("")){
            try{
                Iterator a = data.getBulkName().iterator();
                Iterator b = data.getBulkPrice().iterator();
                String t = "";
                String t2 = "";
                while(a.hasNext() && b.hasNext()){
                    t = (String)a.next();
                    t2 = String.valueOf(b.next());
                    if(query.equals(t)){
                        //first add the ones that has equal value
                        itemName_w.add(t);
                        itemPrice_w.add(InputsManager.formatNairaTextField(t2));
                    }
                }
            }
            finally{
                Iterator a = data.getBulkName().iterator();
                Iterator b = data.getBulkPrice().iterator();
                String t = "";
                String t2 = "";
                while(a.hasNext() && b.hasNext()){
                    t = (String)a.next();
                    t2 = String.valueOf(b.next());
                    if(!t.equals(query) && t.startsWith(query)){
                        //then add other matches
                        itemName_w.add(t);
                        whole_sales.setPreferredSize(new Dimension(((width - 100) / 2) - 18, 30 * itemName_w.size()));
                        itemPrice_w.add(InputsManager.formatNairaTextField(t2));
                    }
                }
            }
        }
        else{
            for(String s : data.getBulkName()){
                itemName_w.add(s);
                whole_sales.setPreferredSize(new Dimension(((width - 100) / 2) - 18, 30 * itemName_w.size()));
            }

            for(Integer i : data.getBulkPrice()){
                itemPrice_w.add(InputsManager.formatNairaTextField(String.valueOf(i)));
            }
        }

        Iterator itName = itemName_w.iterator();
        Iterator itPrice = itemPrice_w.iterator();

        while(itName.hasNext() && itPrice.hasNext()){
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
            JLabel nameL = new JLabel((String)itName.next());
            JLabel priceL = new JLabel((String)itPrice.next());
            nameL.setPreferredSize(new Dimension(((width - 100) / 2) - 1, 23));
            nameL.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 1));
            nameL.setHorizontalAlignment(SwingConstants.CENTER);
            nameL.setFont(new Font("Tahoma", Font.PLAIN, 13));

            priceL.setPreferredSize(new Dimension(((width - 101) / 2), 23));
            priceL.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 1));
            priceL.setHorizontalAlignment(SwingConstants.CENTER);
            priceL.setFont(new Font("Tahoma", Font.PLAIN, 13));

            panel.setPreferredSize(new Dimension(width - 100, 25));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 1));
            panel.setToolTipText("Click me and then drag");
            panel.setTransferHandler(new TransferHandler(panel.getName()));//
            panel.add(nameL);
            panel.add(priceL);
            whole_sales.add(panel);
            ds.createDefaultDragGestureRecognizer(panel, DnDConstants.ACTION_COPY, this);
        }
        whole_sales.repaint();
        whole_sales.validate();
    }

    private void populateUnitPanel(String query){

        itemName.clear();//clear the queue
        itemPrice.clear();//clear the queue

        unit_items.removeAll();

        //populate the collections below here

        if(!query.equals("")){
            try{
                Iterator a = data.getItemName().iterator();
                Iterator b = data.getItemPrice().iterator();
                String t = "";
                String t2 = "";
                while(a.hasNext() && b.hasNext()){
                    t = (String)a.next();
                    t2 = String.valueOf(b.next());
                    if(query.equals(t)){
                        //first add the ones that has equal value
                        itemName.add(t);
                        unit_items.setPreferredSize(new Dimension(((width - 100) / 2) - 18, 30 * itemName.size()));
                        itemPrice.add(InputsManager.formatNairaTextField(t2));
                    }
                }
                
            }
            finally{
                Iterator a = data.getItemName().iterator();
                Iterator b = data.getItemPrice().iterator();
                String t = "";
                String t2 = "";
                while(a.hasNext() && b.hasNext()){
                    t = (String)a.next();
                    t2 = String.valueOf(b.next());
                    if(!t.equals(query) && t.startsWith(query)){
                        //then add other matches
                        itemName.add(t);
                        itemPrice.add(InputsManager.formatNairaTextField(t2));
                    }
                }
            }
        }
        else{
            for(String s : data.getItemName()){
                itemName.add(s);
                unit_items.setPreferredSize(new Dimension(((width - 100) / 2) - 18, 30 * itemName.size()));
            }

            for(Integer i : data.getItemPrice()){
                itemPrice.add(InputsManager.formatNairaTextField(String.valueOf(i)));
            }
        }



        Iterator itName = itemName.iterator();
        Iterator itPrice = itemPrice.iterator();

        while(itName.hasNext() && itPrice.hasNext()){
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
            JLabel nameL = new JLabel((String)itName.next());
            JLabel priceL = new JLabel((String)itPrice.next());
            nameL.setPreferredSize(new Dimension(((width - 100) / 2) - 1, 23));
            nameL.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 1));
            nameL.setHorizontalAlignment(SwingConstants.CENTER);
            nameL.setFont(new Font("Tahoma", Font.PLAIN, 13));

            priceL.setPreferredSize(new Dimension(((width - 101) / 2), 23));
            priceL.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 1));
            priceL.setHorizontalAlignment(SwingConstants.CENTER);
            priceL.setFont(new Font("Tahoma", Font.PLAIN, 13));

            panel.setPreferredSize(new Dimension(width - 100, 25));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 1));
            panel.setToolTipText("Click me and then drag");
            panel.setTransferHandler(new TransferHandler(panel.getName()));//
            panel.add(nameL);
            panel.add(priceL);
            unit_items.add(panel);
            ds.createDefaultDragGestureRecognizer(panel, DnDConstants.ACTION_COPY, this);
        }
        unit_items.repaint();
        unit_items.validate();
    }

}

