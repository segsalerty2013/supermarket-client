/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui.util;
import supermarket_remote_client.gui.StartUpDialog;
import supermarket_remote_client.database.PingResources;
import supermarket_remote_client.gui.drag_n_drop.MyDropTargetListener;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author MUSTAFA
 */
public class SalesTab extends JPanel{

    private JPanel salesTab;
    private int width;
    private int height;
    private int leftWidth;
    private int rightWidth;
    private int leftTopH;
    private int rightTopH;
    private int leftDownH;
    private int rightDownH;

    //private JScrollPane topLeftScroll;
    private JScrollPane topRightScroll;
    private JScrollPane downRightScroll;

    private JSplitPane splitBoth;
    //private JSplitPane splitLeft;
    private JSplitPane splitRight;

    private ItemsPanel leftT;
    private JPanel rightT;
    private RecieptPanel rightD; //the reciept panel that stands for the right down panel

    private JPanel leftPanel;
    private JPanel rightPanel;

    //private JPanel topLeftPanel;
    //private JPanel topRightPanel;
    //private JPanel downRightPanel;

    private JPanel topLeftOutPanel;
    private JPanel topRightOutPanel;
    private JPanel downLeftOutPanel;
    private JPanel downRightOutPanel;

    private JLabel topLeftLbl;
    private JLabel topLeftLbl_2;
    private JLabel topRightLbl;
    private JLabel topRightLbl_2;
    private JLabel downLeftLbl;
    private JLabel downLeftLbl_2;
    private JLabel downRightLbl;
    private JLabel downRightLbl_2;

    private GridBagLayout grid;
    private GridBagConstraints gbConst;

    //private JButton removeOneBtn;
    //private JButton clearAllBtn;

    private Connection connection;

    private StartUpDialog startUp;
    private PingResources resource;

    public SalesTab(int w, int h, Connection c, StartUpDialog s, PingResources r){
        super(new BorderLayout());
        connection = c;
        startUp = s;
        resource = r;
        
        width = w - 20;
        height = h - 20;
        leftWidth = (width / 2) - 20;
        rightWidth = (width / 2) - 20;
        leftTopH = height + 10;
        //leftDownH = (height / 2) - 150;
        rightTopH = (height / 2);
        rightDownH = (height / 2);
        grid = new GridBagLayout();
        gbConst = new GridBagConstraints();
        gbConst.fill = GridBagConstraints.CENTER;
        gbConst.ipady = 0;
        gbConst.weightx = 0.0;
        gbConst.gridwidth = 0; //912;
        gbConst.gridheight = 0; //430;
        //gbConst.anchor = GridBagConstraints.CENTER;
        gbConst.gridx = 0;
        gbConst.gridy = 0;
        //
        MyLookAndFeel.setLook();
        //

        setPreferredSize(new Dimension(width, height));
        initPanel();        
    }
    
    private void initPanel(){
        salesTab = new JPanel(grid); //wrap the whole component into this panel
        salesTab.setSize(width, height);
        salesTab.setPreferredSize(new Dimension(width, height));
        //salesTab.setMaximumSize(new Dimension(width, height));

        /*
        removeOneBtn = new JButton("Remove Previous");
        clearAllBtn = new JButton("Clear All");

        removeOneBtn.setPreferredSize(new Dimension(((rightWidth - 40) / 2), 25));
        removeOneBtn.setHorizontalAlignment(SwingConstants.CENTER);
        removeOneBtn.setBackground(new Color(65, 105, 225));
        removeOneBtn.setForeground(Color.WHITE);
        removeOneBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 11));

        clearAllBtn.setPreferredSize(new Dimension(((rightWidth - 40) / 2), 25));
        clearAllBtn.setHorizontalAlignment(SwingConstants.CENTER);
        clearAllBtn.setBackground(new Color(65, 105, 225));
        clearAllBtn.setForeground(Color.WHITE);
        clearAllBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 11));
         *
         */

        //topLeftScroll = new JScrollPane();
        topRightScroll = new JScrollPane();
        downRightScroll = new JScrollPane();
        
        leftT = new ItemsPanel(leftWidth - 20, leftTopH - 70, connection, startUp); //init with the items panel here
        rightT = new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 2));
        rightD = new RecieptPanel(downRightScroll, rightWidth, rightDownH, startUp, resource);
        rightD.setBackground(Color.WHITE);

        leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        //downRightPanel = new JPanel();
        
        topLeftOutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        topRightOutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        downLeftOutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        downRightOutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        splitBoth = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        //splitLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitRight  = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        topLeftLbl = new JLabel("AVAILABLE ITEMS IN STOCK FOR SALES");
        topLeftLbl_2 = new JLabel("Just Drag Items here and Drop to Sales Panel by the Right Side");
        topRightLbl = new JLabel("SALES CARTALOG SUMMARY");
        topRightLbl_2 = new JLabel("Drop Items Here");
        downLeftLbl = new JLabel("AVAILABLE ITEMS SEARCH");
        downLeftLbl_2 = new JLabel("Search Items by Keyword");
        downRightLbl = new JLabel("RECIEPTS");
        downRightLbl_2 = new JLabel("Reciept Preview");

        rightT.setPreferredSize(new Dimension(rightWidth - 35, rightTopH - 50));
        rightT.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 2));
        rightT.setBackground(new Color(109, 139, 232));

        rightD.setPreferredSize(new Dimension(rightWidth - 35, rightDownH - 50));
        //rightT.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 2));
        //rightT.setBackground(new Color(172, 169, 212));

        leftT.setPreferredSize(new Dimension(leftWidth - 5, leftTopH - 45));
        leftT.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 2));
        leftT.setBackground(new Color(224, 226, 130));

        topLeftOutPanel.setPreferredSize(new Dimension(leftWidth, leftTopH));
        //topLeftOutPanel.setMaximumSize(new Dimension(leftWidth, leftTopH));
        //topLeftOutPanel.setBorder(BorderFactory.createLineBorder(new Color(234, 174, 60), 3));
        //topLeftOutPanel.setBackground(new Color(172, 169, 212));

        topRightOutPanel.setPreferredSize(new Dimension(rightWidth, rightTopH));
        topRightOutPanel.setMaximumSize(new Dimension(rightWidth, rightTopH));
        //topRightOutPanel.setBackground(Color.WHITE);

        downLeftOutPanel.setPreferredSize(new Dimension(leftWidth, leftDownH));
        downLeftOutPanel.setMaximumSize(new Dimension(leftWidth, leftDownH));
        downLeftOutPanel.setMinimumSize(new Dimension(leftWidth, leftDownH));

        downRightOutPanel.setPreferredSize(new Dimension(rightWidth, rightDownH));
        downRightOutPanel.setMaximumSize(new Dimension(rightWidth, rightDownH));
        downRightOutPanel.setMinimumSize(new Dimension(rightWidth, rightDownH));
        //downRightOutPanel.setBackground(new Color(216, 216, 216));

        topLeftLbl.setPreferredSize(new Dimension(leftWidth, 25));
        topLeftLbl.setFont(new Font("Poor Richard", 1, 15));
        topLeftLbl.setHorizontalAlignment(SwingConstants.CENTER);

        topLeftLbl_2.setPreferredSize(new Dimension(leftWidth, 15));
        topLeftLbl_2.setFont(new Font("Poor Richard", Font.PLAIN, 14));
        topLeftLbl_2.setHorizontalAlignment(SwingConstants.CENTER);

        topRightLbl.setPreferredSize(new Dimension(rightWidth, 25));
        topRightLbl.setFont(new Font("Poor Richard", 1, 14));
        topRightLbl.setHorizontalAlignment(SwingConstants.CENTER);

        topRightLbl_2.setPreferredSize(new Dimension(rightWidth, 15));
        topRightLbl_2.setFont(new Font("Poor Richard", Font.PLAIN, 14));
        topRightLbl_2.setHorizontalAlignment(SwingConstants.CENTER);

        downLeftLbl.setPreferredSize(new Dimension(leftWidth, 25));
        //downLeftLbl.setMinimumSize(new Dimension(leftWidth, 25));
        downLeftLbl.setFont(new Font("Poor Richard", 1, 14));
        downLeftLbl.setHorizontalAlignment(SwingConstants.CENTER);

        downLeftLbl_2.setPreferredSize(new Dimension(leftWidth, 15));
        //downLeftLbl_2.setMinimumSize(new Dimension(leftWidth, 15));
        downLeftLbl_2.setFont(new Font("Poor Richard", Font.PLAIN, 14));
        downLeftLbl_2.setHorizontalAlignment(SwingConstants.CENTER);

        downRightLbl.setPreferredSize(new Dimension(rightWidth, 25));
        //downRightLbl.setMinimumSize(new Dimension(rightWidth, 25));
        downRightLbl.setFont(new Font("Poor Richard", 1, 14));
        downRightLbl.setHorizontalAlignment(SwingConstants.CENTER);

        downRightLbl_2.setPreferredSize(new Dimension(rightWidth, 15));
        //downRightLbl_2.setMinimumSize(new Dimension(rightWidth, 15));
        downRightLbl_2.setFont(new Font("Poor Richard", Font.PLAIN, 14));
        downRightLbl_2.setHorizontalAlignment(SwingConstants.CENTER);

        /*
        topLeftScroll.setPreferredSize(new Dimension(leftWidth, leftTopH - 42));
        topLeftScroll.setMinimumSize(new Dimension(leftWidth, leftTopH - 42));
        topLeftScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        topLeftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        topLeftScroll.setViewportView(leftT);
         *
         */
        
        topRightScroll.setPreferredSize(new Dimension(rightWidth, rightTopH - 42));
        topRightScroll.setMinimumSize(new Dimension(rightWidth, rightTopH - 42));
        topRightScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        topRightScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        topRightScroll.setViewportView(rightT);
        
        downRightScroll.setPreferredSize(new Dimension(rightWidth, rightDownH - 42));
        downRightScroll.setMinimumSize(new Dimension(rightWidth, rightDownH - 42));
        downRightScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        downRightScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        downRightScroll.setViewportView(rightD);

        topLeftOutPanel.add(topLeftLbl);
        topLeftOutPanel.add(topLeftLbl_2);
        topLeftOutPanel.add(leftT);
        //topLeftOutPanel.add(topLeftScroll);

        downLeftOutPanel.add(downLeftLbl);
        downLeftOutPanel.add(downLeftLbl_2);
        //downLeftOutPanel.add(downLeftLbl);

        topRightOutPanel.add(topRightLbl);
        topRightOutPanel.add(topRightLbl_2);
        //topRightOutPanel.add(topRightPanel);
        topRightOutPanel.add(topRightScroll);
        //topRightOutPanel.add(removeOneBtn);
        //topRightOutPanel.add(clearAllBtn);

        downRightOutPanel.add(downRightLbl);
        downRightOutPanel.add(downRightLbl_2);
        //downRightOutPanel.add(downRightPanel);
        downRightOutPanel.add(downRightScroll);
/*
        splitLeft.setDividerSize(10);
        splitLeft.setOneTouchExpandable(true);
        splitLeft.setLeftComponent(topLeftOutPanel);
        splitLeft.setRightComponent(downLeftOutPanel);
 *
 */

        splitRight.setDividerSize(10);
        //splitRight.setOneTouchExpandable(true);
        splitRight.setLeftComponent(topRightOutPanel);
        splitRight.setRightComponent(downRightOutPanel);

        leftPanel.add(topLeftOutPanel);
        rightPanel.add(splitRight);


        splitBoth.setDividerSize(15);
        splitBoth.setLeftComponent(leftPanel);
        splitBoth.setRightComponent(rightPanel);
        salesTab.add(splitBoth);

        new MyDropTargetListener(topRightScroll, rightT, rightWidth - 10, leftT.getData(), rightD);

    }

    public JPanel getSalesTab(){
        return salesTab;
    }

}
