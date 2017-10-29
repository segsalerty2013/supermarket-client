/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui;
import supermarket_remote_client.gui.util.MyLookAndFeel;
import supermarket_remote_client.database.PingResources;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.sql.*;

/**
 *
 * @author MUSTAFA
 */
public class ClientFrame extends JFrame implements WindowListener{

    private ClientFrame me = this;
    /////////
    private Connection con = null;
    /////////
    //private HandleNetwork network;
    private Timer t;

    private Dimension userDimension;
    private JPanel wrapperPanel;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JLabel topLbl;
    private JButton makeSalesBtn;
    private JButton salesSumBtn;
    private JButton stockSumBtn;
    private JButton recieptBtn;
    private JButton signOutBtn;
    private JScrollPane centerScroll;

    ////
    private Vector<JButton> disabledBtnV = new Vector<JButton>();
    private PingResources resource;
    private boolean loginStat = false;
    private StartUpDialog startUp = null; //initialize the start up to null for the load Status Dialog method to initialize
    private BackgroundWork backWork;
    private BackgroundWork_2 backWork_2;
    private boolean isCancel = false;
    ////
    private JLabel connectivityLbl;

    private TaskDialog taskFrame;

    public ClientFrame(PingResources r){
        //super("E-SUPER MARKET UNIVERSAL SOLUTION V 1.0.0.1  - www.technoglobalprogrammers.net");
        userDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(400, 200);
        setPreferredSize(new Dimension(400, 200));
        setMinimumSize(new Dimension(400, 200));
        setResizable(false);
        setLocation((userDimension.width - 400) / 2, (userDimension.height - 200) / 2);
        //setAlwaysOnTop(true)
        //manage the resource
        resource = r;
        setTitle(resource.getCompanyName());
        con = resource.getConnection(); //init the connection here
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //
        MyLookAndFeel.setLook();
        //
        initComponents();
        //after initing component .. handle the state of the JButtons
        handleButtonState(false); //after login .. set it true
        loadActions();
        setContentPane(wrapperPanel);
        addWindowListener(this); //add the mouse listener to this frame

        /*
         * start background work
         */
        //taskFrame = new TaskDialog(resource, backWork.loginD.getUserName(), con, me); //initialize this taskFrame here and wait for user to click
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                backWork = new BackgroundWork();
                backWork_2 = new BackgroundWork_2();
                backWork.execute();
                backWork_2.execute();
            }
        });
    }

    public void loadStartUpDialog(StartUpDialog st){
        startUp = st;
    }

    public StartUpDialog getStartUpInstance(){
        return startUp;
    }

    private void initComponents(){
        wrapperPanel = new JPanel(new BorderLayout(2, 1));
        topPanel = new JPanel(new BorderLayout(2, 0));
        centerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 1));

        wrapperPanel.setPreferredSize(new Dimension(400, 200));
        wrapperPanel.setBorder(BorderFactory.createLineBorder(new Color(134, 206, 134), 3));
        
        topPanel.setPreferredSize(new Dimension(400, 40));
        //topPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        centerPanel.setPreferredSize(new Dimension(400, 130));
        
        topLbl = new JLabel("NOBODY IS CURRENTLY LOGGED IN");
        makeSalesBtn = new JButton("Make Sales");
        salesSumBtn = new JButton("Sales Summary");
        stockSumBtn = new JButton("Stock Summary");
        recieptBtn = new JButton("Track Reciept");
        signOutBtn = new JButton("LOGIN");
        connectivityLbl = new JLabel("SERVER IS ONLINE");
     

        disabledBtnV.addElement(makeSalesBtn);
        disabledBtnV.addElement(salesSumBtn);
        disabledBtnV.addElement(stockSumBtn);
        disabledBtnV.addElement(recieptBtn);
        //disabledBtnV.addElement(signOutBtn);

        makeSalesBtn.setPreferredSize(new Dimension(170, 30));
        makeSalesBtn.setHorizontalAlignment(SwingConstants.CENTER);
        makeSalesBtn.setBackground(new Color(65, 105, 225));
        makeSalesBtn.setForeground(Color.WHITE);
        makeSalesBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        salesSumBtn.setPreferredSize(new Dimension(170, 30));
        salesSumBtn.setHorizontalAlignment(SwingConstants.CENTER);
        salesSumBtn.setBackground(new Color(65, 105, 225));
        salesSumBtn.setForeground(Color.WHITE);
        salesSumBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        stockSumBtn.setPreferredSize(new Dimension(170, 30));
        stockSumBtn.setHorizontalAlignment(SwingConstants.CENTER);
        stockSumBtn.setBackground(new Color(65, 105, 225));
        stockSumBtn.setForeground(Color.WHITE);
        stockSumBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        recieptBtn.setPreferredSize(new Dimension(170, 30));
        recieptBtn.setHorizontalAlignment(SwingConstants.CENTER);
        recieptBtn.setBackground(new Color(65, 105, 225));
        recieptBtn.setForeground(Color.WHITE);
        recieptBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        signOutBtn.setPreferredSize(new Dimension(348, 30));
        signOutBtn.setHorizontalAlignment(SwingConstants.CENTER);
        signOutBtn.setBackground(new Color(65, 105, 225));
        signOutBtn.setForeground(Color.WHITE);
        signOutBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        signOutBtn.setEnabled(false);

        connectivityLbl.setHorizontalAlignment(SwingConstants.CENTER);
        connectivityLbl.setFont(new Font("Georgia", 0, 10));
        connectivityLbl.setPreferredSize(new Dimension(348, 20));
        connectivityLbl.setForeground(Color.WHITE);

        centerPanel.add(connectivityLbl);
        centerPanel.add(makeSalesBtn);
        centerPanel.add(salesSumBtn);
        centerPanel.add(stockSumBtn);
        centerPanel.add(recieptBtn);
        centerPanel.add(signOutBtn);

        centerScroll = new JScrollPane();
        centerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        centerScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        centerScroll.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        centerScroll.setViewportView(centerPanel);

        topLbl.setIcon(new ImageIcon(getClass().getResource("images/icn_profile.gif")));
        topLbl.setHorizontalAlignment(SwingConstants.CENTER);
        topLbl.setFont(new Font("Tahoma", 1, 14));
        topLbl.setPreferredSize(new Dimension(400, 40));
        topLbl.setForeground(Color.RED);
        topLbl.setIconTextGap(8);

        topPanel.add(topLbl);
        
        wrapperPanel.add(topPanel, BorderLayout.NORTH);
        wrapperPanel.add(centerScroll, BorderLayout.CENTER);

    }

    public void setLoginState(boolean s){
        loginStat = s;
        if(loginStat){
            //if login status is true, means the user already logged in ... enable the buttons
            signOutBtn.setText("SIGN-OUT");
            signOutBtn.removeActionListener(backWork.login);
            signOutBtn.addActionListener(backWork.signOut);
            topLbl.setText("LOGGED IN AS - " + backWork.loginD.getUserName() + " -");
            topLbl.setForeground(Color.BLACK);
            handleButtonState(true); //then dispose the login dialog if visible
            taskFrame = new TaskDialog(resource, backWork.loginD.getUserName(), con, me); //initialize this taskFrame here and wait for user to click
            resource.setUserName(backWork.loginD.getUserName());
        }
        else{
            signOutBtn.setText("LOGIN");
            signOutBtn.removeActionListener(backWork.signOut);
            signOutBtn.addActionListener(backWork.login);
            handleButtonState(false);
        }
        if(!t.isRunning() && !isCancel){
            //t.setDelay(timeOut);
            t.setRepeats(true);
            t.restart();
        }
    }

    private void loadActions(){
        makeSalesBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                taskFrame.showMe(0);
            }
        });
        salesSumBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                taskFrame.showMe(1);
            }
        });
        stockSumBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                taskFrame.showMe(2);
            }
        });
        recieptBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                taskFrame.showMe(3);
            }
        });
    }

    public void windowActivated(WindowEvent e){

    }
    
    public void windowClosing(WindowEvent e){
        //if window is closing .. shut the database down
        int confirm = JOptionPane.showConfirmDialog(me, "Are you sure you want to exit?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(confirm == 0){
            //mean say he said yes
            //System.out.println("My value is 0");
            dispose();
            new SwingWorker<Void, Void>(){
                public Void doInBackground(){
                    try{
                        Statement stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        stat.executeUpdate("DELETE FROM online_users WHERE name='" +  backWork.loginD.getUserName() + "'");
                        Thread.sleep(1200);
                        stat.executeUpdate("DELETE FROM online_sys WHERE user_logged='" + backWork.loginD.getUserName() + "'");
                    }
                    catch(Exception sE){
                        //System.err.println("SQL , OnlineUsersMonitor : " + sE.getMessage());
                    }
                    finally{
                        resource.setUserName("");
                    }
                    return null;
                }
            }.execute();
            startUp.deActivator();
        }
        else{
            //System.out.println("My value is not 0 but " + confirm);
            //just dont close it
        }
    }
    public void windowClosed(WindowEvent e){

    }
    public void windowDeactivated(WindowEvent e){

    }
    public void windowDeiconified(WindowEvent e){

    }
    public void windowIconified(WindowEvent e){

    }
    public void windowOpened(WindowEvent e){

    }

    public void stopLoginTimer(){
        isCancel = true;
        handleButtonState(false);
        setLoginState(false);
    }

    private void handleButtonState(boolean s){
        for(int i = 0; i < disabledBtnV.size(); i++){
            disabledBtnV.elementAt(i).setEnabled(s);
        }
        repaint();
        validate();
    }

    //check for user if logged in or logged out at background with swing worker check every seconds
    class BackgroundWork extends SwingWorker<Void, Void>{

        LoginDialog loginD = new LoginDialog(resource);
        
        ActionListener signOut = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //sign out; --- reset topLbl
                taskFrame.hideMe();
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        if(t.isRunning()){
                            t.stop();
                        }
                        setLoginState(false);
                        topLbl.setText("- " + loginD.getUserName() + " - JUST SIGNED OUT");
                        topLbl.setForeground(Color.RED);
                        handleButtonState(false);
                        //loginD.reShow();
                        signOutBtn.setText("LOGIN");
                    }
                });
                new SwingWorker<Void, Void>(){
                    public Void doInBackground(){
                        try{
                            con = resource.getConnection();
                            Statement stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            stat.executeUpdate("DELETE FROM online_users WHERE name='" +  loginD.getUserName() + "'");
                            Thread.sleep(1900);
                            stat.executeUpdate("DELETE FROM online_sys WHERE user_logged='" + loginD.getUserName() + "'");
                        }
                        catch(Exception sE){
                            System.err.println("SQL , OnlineUsersMonitor : " + sE.getMessage());
                        }
                        finally{
                            resource.setUserName("");
                        }
                        return null;
                    }
                }.execute();
            }
        };

        ActionListener login = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //login;
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        if(t.isRunning()){
                            t.stop();
                        }
                        handleButtonState(false);
                        loginD.reShow();
                        //signOutBtn.setText("SIGN-OUT");
                    }
                });
            }
        };

        BackgroundWork(){
            signOutBtn.addActionListener(signOut);
            t = new Timer(900, new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            signOutBtn.setEnabled(true);
                            //resource.start();
                            if(loginStat){
                                //if login status is true, means the user already logged in ... enable the buttons
                                signOutBtn.setText("SIGN-OUT");
                                signOutBtn.removeActionListener(login);
                                signOutBtn.addActionListener(signOut);
                                topLbl.setText("LOGGED IN AS - " + loginD.getUserName() + " -");
                                topLbl.setForeground(Color.BLACK);
                                if(loginStat){
                                    handleButtonState(true); //then dispose the login dialog if visible
                                }
                            }
                            else{
                                t.stop();
                                loginD = new LoginDialog(me, resource);
                                signOutBtn.setText("LOGIN");
                                signOutBtn.removeActionListener(signOut);
                                signOutBtn.addActionListener(login);
                                handleButtonState(false);
                            }
                        }
                    });
                }
            });//initialize the timer

        }
        public Void doInBackground(){
            if(!t.isRunning()){
                t.start();
            }
            return null;
        }

        @Override
        public void done(){

        }
    } //end of first background work

    class BackgroundWork_2 extends SwingWorker<Void, Void>{
        /*
         * This class manages the auto-logout and network failure
         * a timer that repeats every 8 seconds
         */
        private Timer networkTime; //check  the network connectivity
        private boolean conStatus = false;
        BackgroundWork_2(){
            networkTime = new Timer(12000, new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    conStatus = startUp.isConnectionValid(); //get the new status of the start up
                    if(conStatus){
                        //dont do anything
                        //t.start();
                        connectivityLbl.setText("SERVER IS ACTIVE");
                        connectivityLbl.setForeground(Color.WHITE);
                        /*
                        if(loginStat && !isCancel){
                            handleButtonState(true);//disable all the buttons
                        }
                         */
                        signOutBtn.setEnabled(true); //disable login/sign out btn
                    }
                    else{
                        t.stop(); //stop the timer that pops login up
                        connectivityLbl.setText("CONNECTION TO SERVER LOST");
                        connectivityLbl.setForeground(Color.red);
                        try{
                            Thread.sleep(1000);
                            handleButtonState(false);//disable all the buttons
                            Thread.sleep(1000);
                            signOutBtn.setEnabled(false); //disable login/sign out btn
                            Thread.sleep(1100);
                            repaint();
                            validate();
                            Thread.sleep(1500);
                            SwingUtilities.invokeLater(new Runnable(){
                                public void run(){
                                    startUp.reCallRMI();
                                }
                            });
                        }
                        catch(InterruptedException ex){}
                        //signOutBtn.repaint();
                        //signOutBtn.validate();
                    }
                    repaint();
                    validate();

                }
            });

        }

        public Void doInBackground(){
            if(!networkTime.isRunning()){
                networkTime.start();
            }
            return null;
        }

        @Override
        public void done(){

        }
    }
}
