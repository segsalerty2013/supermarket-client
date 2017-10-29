/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui.drag_n_drop;
import supermarket_remote_client.data.ItemsData;
import supermarket_remote_client.gui.util.RecieptPanel;
import supermarket.util.InputsManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.util.*;


/**
 *
 * @author MUSTAFA
 * NOTE !!!!!!!
 * in next version ... need to check items to be dropped if already exists in the panel before successful drop and ask for modification
 * on previous entry or replace previous entry
 */

public class MyDropTargetListener extends DropTargetAdapter {

    private DropTarget dropTarget;
    private JPanel resultComp; //the label
    private JScrollPane scroll;
    private JPanel genRowPanel;
    private int width;
    private Vector<Integer> alreadyVal;
    private String value = ""; //quantity value

    private Stack<JPanel> panelsStack;

    private Vector<Vector> itemsV;
    private Vector<Integer> sumV;

    private JPopupMenu menu;
    private Action removeAc;
    private Action clearAc;

    private Thread autoRefresh;

    private ItemsData data;

    ///
    private RecieptPanel reciept;

    public MyDropTargetListener(JScrollPane sc, JPanel c, int w, ItemsData id, RecieptPanel r) {
        resultComp = c;
        width = w;
        scroll = sc;
        data =  id;
        reciept = r;
        itemsV = new Vector<Vector>();
        sumV = new Vector<Integer>();
        alreadyVal = new Vector<Integer>();
        panelsStack = new Stack<JPanel>();
        dropTarget = new DropTarget(resultComp, DnDConstants.ACTION_COPY, this, true, null);

        handleActions();
        
        autoRefresh = new Thread(new Runnable(){
            public void run(){
                javax.swing.Timer t = new javax.swing.Timer(new Random().nextInt(999), new ActionListener(){
                    public void actionPerformed(ActionEvent ev){
                        if(!panelsStack.isEmpty()){
                            removeAc.setEnabled(true);
                            clearAc.setEnabled(true);
                            new SwingWorker<Void, Void>(){
                                public Void doInBackground(){
                                    if(reciept.isSales()){
                                        reciept.setSalesStatus(false); //since its cleared... set its status back to false
                                        clearAll();                                      
                                    }
                                    return null;
                                }
                            }.execute();
                        }
                        else{
                            removeAc.setEnabled(false);
                            clearAc.setEnabled(false);
                        }
                        resultComp.repaint();
                        resultComp.validate();
                        scroll.repaint();
                        scroll.validate();
                    }
                });
                t.start();
            }
        });
        if(!autoRefresh.isAlive()){
            autoRefresh.start();
        }
    }

    private void initRowPanel(JLabel in, String qty){
        
        Color rowColor = Color.WHITE;
        JLabel qtyLbl = new JLabel("X " + qty);
        JLabel unitPLbl = new JLabel(InputsManager.formatNairaTextField(String.valueOf(data.getPriceOfItem(in.getText()))));
        JLabel sumPLbl = new JLabel(InputsManager.formatNairaTextField(String.valueOf(data.getPriceOfItem(in.getText()) * Integer.parseInt(qty))));
        qtyLbl.setPreferredSize(new Dimension(((width - 20) / 4) - 20, 25));
        qtyLbl.setHorizontalAlignment(SwingConstants.CENTER);
        qtyLbl.setBorder(BorderFactory.createLineBorder(new Color(224, 226, 130), 1));
        unitPLbl.setPreferredSize(new Dimension((width - 20) / 5, 25));
        unitPLbl.setHorizontalAlignment(SwingConstants.CENTER);
        unitPLbl.setBorder(BorderFactory.createLineBorder(new Color(224, 226, 130), 1));
        sumPLbl.setPreferredSize(new Dimension(((width - 20) / 5) + 10, 25));
        sumPLbl.setHorizontalAlignment(SwingConstants.CENTER);
        sumPLbl.setBorder(BorderFactory.createLineBorder(new Color(224, 226, 130), 1));
        /*
        removeBtn.setPreferredSize(new Dimension(((width - 20) / 4) - 8, 20));
        removeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        removeBtn.setBackground(new Color(65, 105, 225));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
         *
         */
        genRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        genRowPanel.setPreferredSize(new Dimension(width - 20, 26));
        genRowPanel.setBackground(rowColor);
        genRowPanel.setBorder(BorderFactory.createLineBorder(new Color(224, 226, 130), 2));
        //in.setIcon(new ImageIcon(getClass().getResource("items_selected.jpg")));
        in.setPreferredSize(new Dimension((width) / 3, 21));
        in.setHorizontalAlignment(SwingConstants.CENTER);
        in.setBorder(BorderFactory.createLineBorder(new Color(224, 226, 130), 0));
        genRowPanel.add(in);
        genRowPanel.add(qtyLbl);
        genRowPanel.add(unitPLbl);
        genRowPanel.add(sumPLbl);
        //genRowPanel.add(removeBtn);
        panelsStack.push(genRowPanel);

        //then populate the itemV
        Vector<String> v = new Vector<String>();
        v.addElement(in.getText()); //the item desciption
        v.addElement(unitPLbl.getText());
        v.addElement(qty);
        v.addElement(sumPLbl.getText());

        itemsV.addElement(v);
        sumV.addElement(data.getPriceOfItem(in.getText()) * Integer.parseInt(qty));

        //then do something to the reciept panel by resetting its tables value
        reciept.setTableItems(itemsV, sumV);
        reciept.increaseTableLength();
        //return genRowPanel;
    }

    private void managePopUp(){
        menu = new JPopupMenu();
        //menu.setPopupSize(new Dimension(125, 50));
        JMenuItem removeOne = new JMenuItem("Remove Previous");
        //removeOne.setPreferredSize(new Dimension(125, 23));
        //removeOne.setHorizontalAlignment(SwingConstants.CENTER);
        JMenuItem clearAll = new JMenuItem("Clear All");
        //clearAll.setPreferredSize(new Dimension(125, 23));
        //clearAll.setHorizontalAlignment(SwingConstants.CENTER);
        removeOne.setAction(removeAc);
        clearAll.setAction(clearAc);
        menu.add(removeOne);
        menu.addSeparator();
        menu.add(clearAll);
        resultComp.setComponentPopupMenu(menu);
    }

    private void handleActions(){
        removeAc = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                panelsStack.pop();
                itemsV.removeElementAt(itemsV.size() - 1); //remove the last element added
                sumV.removeElementAt(sumV.size() - 1); //remove the last element added
                resultComp.remove(panelsStack.size());
                reciept.setTableItems(itemsV, sumV);
                reciept.reduceTableLength();
            }
        };
        removeAc.putValue(Action.NAME, "Remove Previous");

        clearAc = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                clearAll();
            }
        };
        clearAc.putValue(Action.NAME, "Clear All");
    }

    private void clearAll(){
        itemsV.clear(); //clear the items vector
        sumV.clear();
        panelsStack.clear();
        resultComp.removeAll();
        reciept.setTableItems(itemsV, sumV);
        reciept.reduceTableLengthToDefault();
    }
/*
    private void manageScroll(){
        //manage the scroll downs of the items displayed here according to dropping

        //System.out.println(compHeight);
    }
 * 
 */

    private int manageOptions(JLabel l){
        int opt = 0;
        if(alreadyVal.isEmpty()){
            //that means user hasnt input a value here before ..so, show a fresh JOPtionPane
            try{
                value = JOptionPane.showInputDialog(resultComp, "How Many || " + l.getText() + " || ?", "Enter Quantity", JOptionPane.OK_CANCEL_OPTION);
                if(value.equals("") || value.equals("0")){
                    //user did not provide a value
                    value = "1";
                }
                //check the value for valid input and see if it can be added to recent items Vector
                try{
                    int v = Integer.parseInt(value);
                    processVector(v);
                    opt = 1;
                }
                catch(NumberFormatException nE){
                    //show error that the value provided is not an Integer
                    JOptionPane.showMessageDialog(resultComp, "- Invalid 'INTEGER' Value Provided -", "INPUT ERROR", JOptionPane.PLAIN_MESSAGE);
                    return 0;
                }
            }
            catch(NullPointerException e){
                //meaning the user canceled the process
                value = e.toString();
                return 0;
            }
        }
        else{
            Object val = "";
            val = JOptionPane.showInputDialog(resultComp, "How Many || " + l.getText() + " || ?", "Enter Quantity", JOptionPane.OK_CANCEL_OPTION,
                    null, null, alreadyVal.elementAt((alreadyVal.size() - 1)));
            

            
            try{
                value = val.toString();
                if(value.equals("") || value.equals("0")){
                    //user did not provide a value
                    value = "1";
                }
                //check the value for valid input and see if it can be added to recent items Vector
                int v = Integer.parseInt(value);
                processVector(v);
                opt = 1;
            }
            catch(NumberFormatException nE){
                //show error that the value provided is not an Integer
                JOptionPane.showMessageDialog(resultComp, "- Invalid 'INTEGER' Value Provided -", "INPUT ERROR", JOptionPane.PLAIN_MESSAGE);
                return 0;
            }
            catch(NullPointerException e){
                //meaning the user canceled the process
                value = e.toString();
                return 0;
            }
        }
        return opt;
    }

    private void processVector(int i){
        if(!alreadyVal.contains(i)){
            //if the integer doesnt exist in this vector.. add it to the vector's element
            alreadyVal.addElement(i);
        }
    }

    public void drop(DropTargetDropEvent event) {
        try {

          Transferable tr = event.getTransferable();
          JLabel lbl = (JLabel) tr.getTransferData(TransferableJPanel.panelFlavor);

            if (event.isDataFlavorSupported(TransferableJPanel.panelFlavor)){
                event.acceptDrop(DnDConstants.ACTION_COPY);
                //if item was dragged... get the qty that u wanna self from user
                int stat = manageOptions(lbl);
                if(stat != 0){
                    initRowPanel(lbl, value); //pass the new value of the label and value of quantity
                    //generate the Row Jpanel and pass the new Label intance here to be modified by the method initRowPanel
                    //resultComp = new JPanel();
                    int compHeight = 30;
                    Iterator it = panelsStack.iterator();
                    while(it.hasNext()){
                        resultComp.add((JPanel)it.next());
                    }
                    compHeight = 30 * panelsStack.size();
                    /*
                    for(JPanel p : panelsStack){
                        resultComp.add(p);//add the rPanel to to the result Component
                        compHeight += p.getHeight();
                    }
                     * 
                     */
                    if(compHeight >= 208){
                        resultComp.setPreferredSize(new Dimension(width, compHeight));
                        //resultComp.setCaretPosition(resultComp.getHeight());
                    }
                }
                /*
                else{

                }
                 *
                 */
                event.dropComplete(true);
                //after adding the rowPanel... manage scrll to determine if the JPAnel needed to be expanded
                //manageScroll();
                managePopUp();
                //
                return;
            }
          event.rejectDrop();
        } catch (Exception e) {
          e.printStackTrace();
          event.rejectDrop();
        }
      }
  }
