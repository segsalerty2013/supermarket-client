/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui.util;
import supermarket_remote_client.gui.StartUpDialog;
import supermarket_remote_client.table.RecieptItemsTable;
import supermarket_remote_client.database.PingResources;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class RecieptPanel extends JPanel{

    private RecieptPanel self = this;
    
    private int width;
    private int height;

    private JScrollPane scroll;

    private JPanel wrapper;

    private JPanel onePanel;
    private JPanel twoPanel;
    private JPanel threePanel;
    private JPanel footPanel;

    private JPanel emptyPanel;

    private JLabel compNameLbl;
    private JLabel compAddLbl;
    private JLabel compTelLbl;
    private JPanel timePanel; //time panel
    private JPanel datePanel; //the date panel
    private JLabel dateLbl;
    private JLabel timeLbl;
    private JLabel dateValLbl;
    private JLabel timeValLbl;
    private JLabel recieptLbl;

    private JLabel totalLbl;
    private JLabel totalAmmLbl;

    private JPanel postedPanel;
    private JLabel postedLbl;
    private JLabel postedNameLbl;
    private JPanel customerPanel;
    private JLabel customerLbl;
    private JLabel salesIdLbl;

    private JScrollPane scroller;
    private JTable itemsTable;
    private RecieptItemsTable recieptTableModel;

    private JPopupMenu print_menu;
    private JButton print_cancelBtn;
    private JButton completeSalesBtn;

    private StartUpDialog startUp;
    private PingResources resource;
    private DataManager data;
    
    private String compName = "";
    private String compAdd = "";
    private String compTel = "";
    private String date = "";
    private String time = "";
    private String totalAmm = "";
    private String userName = "";
    private String custName = "";

    //my actions
    private Action print;
    private Action complete;
    private Action cancelSales;

    private boolean salesStat = false;
    private boolean salesDone = false;

    public RecieptPanel(JScrollPane sc, int w, int h, StartUpDialog s, PingResources r){
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        scroll = sc;
        startUp = s;
        resource = r;
        width = w;
        height = h - 20;
        setPreferredSize(new Dimension(width - 33, height));
        setBackground(Color.WHITE);
        setFont(new Font("Tahoma", Font.PLAIN, 16));

        data = new DataManager();
        data.execute();
        
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                initComponents();
                loadActions();
            }
        });
    }

    public boolean isSales(){
        return salesStat;
    }

    private void pauseRefresh(){
        data.t.stop();
    }

    private void continueRefresh(){
        data.t.restart();
    }

    public void setSalesStatus(boolean s){
        salesStat = true; //true
        salesDone = s; //false
    }

    private void loadActions(){

        print = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                print.setEnabled(false);
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        printReciept();
                    }
                });
            }
        };
        print.putValue(Action.NAME, "Print Reciept");

        complete = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                print_cancelBtn.setEnabled(false);
                complete.setEnabled(false);
                completeSalesBtn.setEnabled(false);
                pauseRefresh();
                //then do the background work to register this sales and give us its REF
                //SwingUtilities.invokeLater(new Runnable(){
                    //public void run(){
                        Vector<Vector> v = new Vector<Vector>();
                        for(int i = 0; i < recieptTableModel.getRowCount(); i++){
                            Vector<Object>s = new Vector<Object>();
                            s.addElement(recieptTableModel.getValueAt(i, 0));
                            s.addElement(recieptTableModel.getValueAt(i, 2));
                            String []a = recieptTableModel.getValueAt(i, 3).toString().replaceAll(", ", "").split(" ");
                            s.addElement(a[1]);
                            v.addElement(s);
                        }
                        new supermarket_remote_client.data.MakeSalesHandler(v, resource.getConnection(), print_cancelBtn, salesIdLbl, userName, date, time);
                    //}
                //});
                print_cancelBtn.setAction(print);
                salesDone = true; //set this variable to true since sales done
                continueRefresh();
            }
        };
        complete.putValue(Action.NAME, "Complete Sales");

        cancelSales = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                //cancel and clear this reciept
                recieptTableModel.clearTable();
                salesIdLbl.setText(" ");
                salesStat = true; //it was true before
                salesDone = false;
            }
        };
        cancelSales.putValue(Action.NAME, "Cancel Sales");
    }

    private void printReciept(){
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName(" Print Reciept ");

        pj.setPrintable(new Printable(){
            public int print(Graphics pg, PageFormat pf, int pageNum){
                if (pageNum > 0){
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                self.paint(g2);
                return Printable.PAGE_EXISTS;
            }
        });

        if(pj.printDialog() == false){
            print.setEnabled(true);
            return;
        }

        try{
            pj.print();
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    recieptTableModel.clearTable();
                    salesIdLbl.setText(" ");
                    salesStat = true;
                    salesDone = false;
                }
            });

        }catch (PrinterException ex){
            // handle exception
        }
    }

    public void setTableItems(java.util.Vector<java.util.Vector> v, java.util.Vector<Integer> i){
        recieptTableModel.setDataVector(v);
        salesIdLbl.setText(" ");
        //then calculate the total
        SwingUtilities.invokeLater(new SalesCalculatorAndManager(i));
    }

    public void increaseTableLength(){
        itemsTable.setPreferredSize(new Dimension(itemsTable.getWidth(), itemsTable.getHeight() + 18));
        scroller.setPreferredSize(new Dimension(scroller.getWidth(), scroller.getHeight() + 18));
        twoPanel.setPreferredSize(new Dimension(twoPanel.getWidth(), twoPanel.getHeight() + 18));
        self.setPreferredSize(new Dimension(self.getWidth(), self.getHeight() + 18));
        wrapper.setPreferredSize(new Dimension(self.getWidth(), self.getHeight() + 18));
        self.revalidate();
    }

    public void reduceTableLength(){
        itemsTable.setPreferredSize(new Dimension(itemsTable.getWidth(), itemsTable.getHeight() - 18));
        scroller.setPreferredSize(new Dimension(scroller.getWidth(), scroller.getHeight() - 18));
        twoPanel.setPreferredSize(new Dimension(twoPanel.getWidth(), twoPanel.getHeight() - 18));
        self.setPreferredSize(new Dimension(self.getWidth(), self.getHeight() - 18));
        wrapper.setPreferredSize(new Dimension(self.getWidth(), self.getHeight() - 18));
        self.revalidate();
    }

    public void reduceTableLengthToDefault(){
        itemsTable.setPreferredSize(new Dimension(itemsTable.getWidth(), 30));
        scroller.setPreferredSize(new Dimension(scroller.getWidth(), 30));
        twoPanel.setPreferredSize(new Dimension(twoPanel.getWidth(), 30));
        self.setPreferredSize(new Dimension(width - 33, height));
        wrapper.setPreferredSize(new Dimension(width - 35, height));
        self.revalidate();
    }

    private void initComponents(){

        print_cancelBtn = new JButton("Cancel");
        print_cancelBtn.setPreferredSize(new Dimension(200, 29));
        print_cancelBtn.setToolTipText("Click to print a reciept after sales");
        print_cancelBtn.setEnabled(false);
        completeSalesBtn = new JButton("Complete Sales");
        completeSalesBtn.setPreferredSize(new Dimension(200, 29));
        completeSalesBtn.setToolTipText("Click to MAKE sales");
        completeSalesBtn.setEnabled(false);
        print_menu = new JPopupMenu();
        print_menu.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
        print_menu.setPreferredSize(new Dimension(430, 45));

        print_menu.add(completeSalesBtn);
        print_menu.add(print_cancelBtn);
        

        scroller = new JScrollPane();
        scroller.setMinimumSize(new Dimension(width - 35, 30));
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBackground(Color.WHITE);
        recieptTableModel = new RecieptItemsTable();
        itemsTable = new JTable();
        itemsTable.setMinimumSize(new Dimension(width - 35, 30));
        itemsTable.setModel(recieptTableModel);
        itemsTable.setBackground(Color.WHITE);
        itemsTable.setRowSelectionAllowed(false);
        scroller.setViewportView(itemsTable);

        emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(width - 35, 40));
        
        wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setPreferredSize(new Dimension(width - 35, height));
        wrapper.setBackground(Color.WHITE);
        onePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        onePanel.setPreferredSize(new Dimension(width - 35, 120));
        twoPanel = new JPanel(new BorderLayout(0, 0));
        twoPanel.setPreferredSize(new Dimension(width - 35, 30));
        threePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        compNameLbl = new JLabel(compName);
        compNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
        compNameLbl.setPreferredSize(new Dimension(width - 35, 20));
        compNameLbl.setFont(new Font("Tahoma", 1, 15));
        compAddLbl = new JLabel(compAdd);
        compAddLbl.setHorizontalAlignment(SwingConstants.CENTER);
        compAddLbl.setPreferredSize(new Dimension(width - 35, 20));
        compTelLbl = new JLabel(compTel);
        compTelLbl.setHorizontalAlignment(SwingConstants.CENTER);
        compTelLbl.setPreferredSize(new Dimension(width - 35, 20));
        timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        timePanel.setPreferredSize(new Dimension((width - 35) / 2, 20));
        timePanel.setBackground(Color.WHITE);
        datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        datePanel.setPreferredSize(new Dimension((width - 35) / 2, 20));
        datePanel.setBackground(Color.WHITE);
        dateLbl = new JLabel("DATE :");
        dateLbl.setPreferredSize(new Dimension(60, 20));
        dateLbl.setFont(new Font("Tahoma", 1, 13));
        timeLbl = new JLabel("TIME :");
        timeLbl.setPreferredSize(new Dimension(60, 20));
        timeLbl.setFont(new Font("Tahoma", 1, 13));
        dateValLbl = new JLabel(date);
        timeValLbl = new JLabel(time);
        recieptLbl = new JLabel("RECIEPT");
        recieptLbl.setHorizontalAlignment(SwingConstants.CENTER);
        recieptLbl.setPreferredSize(new Dimension(width - 35, 20));
        recieptLbl.setFont(new Font("Cursive", 1, 17));
        totalLbl = new JLabel("NET TOTAL :");
        totalLbl.setPreferredSize(new Dimension(((width - 35) /2) + 100, 20));
        totalLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLbl.setFont(new Font("Tahoma", 1, 13));
        totalAmmLbl = new JLabel(totalAmm);
        totalAmmLbl.setPreferredSize(new Dimension(((width - 35) / 2) - 100, 20));
        totalAmmLbl.setHorizontalAlignment(SwingConstants.CENTER);
        postedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        postedPanel.setPreferredSize(new Dimension((width - 35)/ 2, 20));
        postedPanel.setBackground(Color.WHITE);
        postedLbl = new JLabel("ISSUED BY :");
        postedLbl.setPreferredSize(new Dimension((width - 35) / 4, 20));
        postedLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        postedLbl.setFont(new Font("Tahoma", 1, 13));
        postedNameLbl = new JLabel(userName);
        postedNameLbl.setPreferredSize(new Dimension((width - 35) /4, 20));
        postedNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
        customerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        customerPanel.setPreferredSize(new Dimension((width - 35)/ 2, 20));
        customerPanel.setBackground(Color.WHITE);
        customerLbl = new JLabel("REF :");
        customerLbl.setPreferredSize(new Dimension(60, 20));
        customerLbl.setFont(new Font("Tahoma", 1, 13));
        salesIdLbl = new JLabel(custName);
        salesIdLbl.setPreferredSize(new Dimension(((width - 35) / 2) - 140, 20));
        salesIdLbl.setFont(new Font("san-serif", 1, 13));

        ///
        datePanel.add(dateLbl);
        datePanel.add(dateValLbl);
        timePanel.add(timeLbl);
        timePanel.add(timeValLbl);

        onePanel.add(compNameLbl);
        onePanel.add(compAddLbl);
        onePanel.add(compTelLbl);
        onePanel.add(datePanel);
        onePanel.add(timePanel);
        onePanel.add(recieptLbl);

        twoPanel.add(scroller, BorderLayout.CENTER);

        threePanel.add(totalLbl);
        threePanel.add(totalAmmLbl);

        postedPanel.add(postedLbl);
        postedPanel.add(postedNameLbl);
        
        customerPanel.add(customerLbl);
        customerPanel.add(salesIdLbl);
        
        footPanel.add(postedPanel);
        footPanel.add(customerPanel);
        ///
        wrapper.add(onePanel);
        wrapper.add(twoPanel);
        wrapper.add(threePanel);
        wrapper.add(emptyPanel);
        wrapper.add(footPanel);
        //
        Component c[] = wrapper.getComponents();
        for(Component i: c){
            i.setBackground(Color.WHITE);
        }
        self.setComponentPopupMenu(print_menu);
        
        add(wrapper);
        scroll.setViewportView(self);
    }

    private class DataManager extends SwingWorker<Void, Void>{
        private Timer t;

        DataManager(){
            t = new Timer(4000, new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    new SwingWorker<Void, Void>(){
                        public Void doInBackground(){
                            try{
                                resource.reloadUserCredentials(); //reload the user credentials underground
                                //System.out.println("This thing dey reload resources");
                            }
                            catch(Exception E){
                                System.err.println("Hey... see me error at Client Data Manager in RecieptPanel :" + E.getMessage());
                            }
                            return null;
                        }
                        @Override
                        public void done(){
                            if(!compName.equals(resource.getCompanyName()) || !compAdd.equals(resource.getAddress()) ||
                                    !compTel.equals(resource.getContactInfo()) || !userName.equals(resource.getUserName())){
                                compName = resource.getCompanyName();
                                compAdd = resource.getAddress();
                                compTel = resource.getContactInfo();
                                userName = resource.getUserName();

                                compNameLbl.setText(compName);
                                compAddLbl.setText(compAdd);
                                compTelLbl.setText(compTel);
                                postedNameLbl.setText(userName);

                                compNameLbl.revalidate();
                                compAddLbl.revalidate();
                                compTelLbl.revalidate();
                                postedNameLbl.revalidate();
                            }
                        }
                    }.execute(); //exec this swing working to manage reload of resources
                    date = startUp.getRemoteDate();
                    time = startUp.getRemoteTime();

                    dateValLbl.setText(date);
                    timeValLbl.setText(time);

                    dateValLbl.revalidate();
                    timeValLbl.revalidate();
                    //then manage the actions of the popup menu as well
                    if(recieptTableModel.isItemRowEmpty() && !salesStat){
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                print_cancelBtn.setEnabled(false);
                                completeSalesBtn.setEnabled(false);
                            }
                        });
                    }

                    else if(!recieptTableModel.isItemRowEmpty() && !salesDone && !salesStat){
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                print_cancelBtn.setAction(cancelSales);
                                completeSalesBtn.setAction(complete);
                                print_cancelBtn.setEnabled(true);
                                completeSalesBtn.setEnabled(true);
                            }
                        });
                    }
                    
                    else if(salesStat && !recieptTableModel.isItemRowEmpty()){
                        print_cancelBtn.setAction(print);
                        completeSalesBtn.setEnabled(false);
                    }
                    else if(recieptTableModel.isItemRowEmpty() && salesStat && !salesDone){
                        salesStat = false;
                        salesDone = false;
                    }
                    /*
                    else{
                        print_cancelBtn.setAction(cancelSales);
                        completeSalesBtn.setEnabled(true);
                    }
                     * 
                     */
                }
            });
        }

        public Void doInBackground(){
            t.start();
            return null;
        }
    }

    private class SalesCalculatorAndManager implements Runnable{

        private java.util.Vector<Integer> sums;
        private int netTotal = 0;

        SalesCalculatorAndManager(java.util.Vector<Integer> i){
            sums = i;
        }

        public void run(){
            for(Integer s : sums){
                netTotal += s;
            }
            //then set the net total label
            totalAmm = supermarket.util.InputsManager.formatNairaTextField(String.valueOf(netTotal));
            totalAmmLbl.setText(totalAmm);
            totalAmmLbl.revalidate();
        }
    }
}
