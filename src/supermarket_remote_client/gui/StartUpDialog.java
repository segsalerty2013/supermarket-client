/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui;
import supermarket_remote_client.gui.util.*;
import supermarket_remote_client.database.*;
import supermarket_remote_client.util.*;
import supermarket.rmi.ServerRMI;
import supermarket.network.HandleNetwork;
import supermarket_remote_client.update_manager.UpdateHandler;
import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.registry.*;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;


/**
 *
 * @author MUSTAFA
 * This start up works with the RMI start it and pass the reference all over
 * also connect to remote database as far as host is valid
 */
public class StartUpDialog extends JDialog{

    private StartUpDialog self = this;
    private Dimension userDimension;
    private JPanel inPanel;
    private JPanel barsPanel;
    private JProgressBar currentBar;
    private JProgressBar allBar;
    private JLabel statLbl;
    private Task task;
    private ClientFrame client;
    private String status = "";
    private String remoteDate = "";
    private String remoteTime = "";
    private int salesModule = 0;
    private boolean remoteCall = false;
    private int opt = 0;
    private Thread th;

    private SwingWorker<Void, Void> t;

    //////////////////////
    //////////////////////
    private String serverHost = "localhost"; //default host to be localhost
    protected ServerRMI rem = null;
    private String remoteName;
    private Registry rmiRegistry = null;

    private boolean done= false;
    private RemoteHostManager remoteM;
    private Connection con = null;
    private HandleNetwork net;
    private ConnectionHandler conHandler;
    private PingResources resource;
    //////////////////////
    /////////////////////
    
    private UpdateHandler update;
    private boolean canContinue = false;
    
    /////
    private Timer rmiTryTimer;
    private int rmiTimeOut = 0;
    
    private boolean connectionStatus;

    public StartUpDialog(){
        userDimension = Toolkit.getDefaultToolkit().getScreenSize();
        //client = new ClientFrame();
        setSize(300, 120);
        setPreferredSize(new Dimension(300, 120));
        setLocation((userDimension.width - 300) / 2, (userDimension.height - 120) / 2);
        //
        MyLookAndFeel.setLook();
        //
        setUndecorated(true);
        setAlwaysOnTop(true);
        
        /////connection try
        connectionStatus = isNetworkCon_detected();
        
        
        initComponents();
        
        
        /////////////////////////////
        ////////////////////////////
        
        
        rmiTryTimer = new Timer(4000, new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent e){
                //if time out don reach.. no try to connect again
                connect_To_RMI();
                if(rmiTimeOut >= 3){
                    status = "Connection Time-Out";
                    rmiTryTimer.stop();
                    statLbl.setText(status);
                    task.doChangeHostAddress();
                    /*
                    try{
                        Thread.sleep(4000);
                        status = "Application will shut down ....";
                        statLbl.setText(status);
                        Thread.sleep(2800);
                        status = "Plug to a network and relaunch ...";
                        statLbl.setText(status);
                        Thread.sleep(2900);
                    }
                    catch(InterruptedException iE){}
                    status = "Application is shutting down ...";
                    statLbl.setText(status);
                    rmiTryTimer.stop();
                    self = null;
                    System.exit(0);
                     * 
                     */
                }
                rmiTimeOut++; //increament the timeout
            }
        });
        
        ///////////////////////////////
        ///////////////////////////////
        
        add(inPanel);
        th = new Thread(new Runnable(){
            public void run(){
                remoteM = new RemoteHostManager();
                remoteName = remoteM.getRemoteHost();
            }
        });
        if(!th.isAlive()){
            th.start();
        }
    }

    private void RMIcaller() throws RemoteException{
        serverHost = "rmi://" + remoteName + ":1099/Super_Market";

        try{
            rmiRegistry = LocateRegistry.getRegistry(1099);
            rem = (ServerRMI) Naming.lookup(serverHost);
            //here... therefore ... rem is ready to invoke remote methods
            update = new UpdateHandler(rem, "Super_Market_Client.jar");
            canContinue = update.checkUpdate(self, canContinue);
        }
        catch(MalformedURLException me){
            System.err.println("Error occured with the URL provided : " + me.getMessage());
        }
        catch(NotBoundException nb){
            System.err.println("Error trying to bind with server RMI : " + nb.getMessage());
        }
                /*
        catch(RemoteException re){
            //re.printStackTrace();
            System.err.println("Error at the remote : " + re.getMessage());
        }
         * 
         */
    }


    public void reCallRMI(){
        connect_To_RMI();
    }

/*
    private void unBindRMI(){
        try{
           rmiRegistry.unbind("Super_Market");
        }
        catch(Exception e){
            System.err.println("Error occured at unbinding RMI: " + e.getMessage());
        }
    }
 * 
 */

    private boolean isNetworkCon_detected(){
        boolean conStat = false;
        net = new HandleNetwork();
        if(net.getConnectionStatus()){
            //means there is network detected
            conStat = true;
        }
        else{
            conStat = false; //means no network con detected
        }
        return conStat;
    }

    private void connect_To_RMI(){
        try{
            RMIcaller(); //call the RMI in background.. connect to it and make available to tamper remotely
        }
        catch(RemoteException rE){
            //System.err.println("Error at the remote : " + rE.getMessage());
            if(!rmiTryTimer.isRunning()){
                rmiTryTimer.start();
            }
        }
        //loadRemoteTime_Date_SalesModule();
    }

    private void loadRemoteTime_Date_SalesModule(){
        try{
            try{
                remoteDate = rem.getRemoteDate();
                remoteTime = rem.getRemoteTime();
                salesModule = rem.getMarketModule();
            }
            catch(NullPointerException nE){
                System.err.println("Remote Server Returns NULL : " + nE.getMessage());
            }
            if(!remoteDate.equals("") && !remoteTime.equals("")){
                remoteCall = true;
            }
            else{
                remoteCall = false;
            }
        }
        catch(RemoteException rE){
            remoteCall = false;
            System.err.println("Error at getting remote Name date and time .. means connection is not valid : " + rE.getMessage());
        }
    }

    public String getRemoteTime(){
        return remoteTime;
    }

    public String getRemoteDate(){
        return remoteDate;
    }

    public int getMarketModule(){
        try{
            salesModule = rem.getMarketModule();
        }
        catch(RemoteException rE){
            System.err.println("Error at getMarketModule : " + rE.getMessage());
        }
        return salesModule;
    }

    public boolean isConnectionValid(){
        loadRemoteTime_Date_SalesModule(); //test if time and date can be gotted from the server to see if connection still valid
        return remoteCall;
    }

    private void initComponents(){
        inPanel = new JPanel(new BorderLayout());
        barsPanel = new JPanel(new BorderLayout(2, 8));
        statLbl = new JLabel("please wait ............");
        currentBar = new JProgressBar(0, 100);
        allBar = new JProgressBar(0, 100);

        currentBar.setStringPainted(true);
        currentBar.setPreferredSize(new Dimension(290,40));
        allBar.setPreferredSize(new Dimension(290,50));
        allBar.setStringPainted(true);

        statLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statLbl.setFont(new Font("Verdana", 0, 10));

        barsPanel.setPreferredSize(new Dimension(300, 90));

        inPanel.setPreferredSize(new Dimension(300, 120));
        inPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 185, 80), 1));

        barsPanel.add(currentBar, BorderLayout.CENTER);
        barsPanel.add(allBar, BorderLayout.SOUTH);

        inPanel.add(barsPanel, BorderLayout.CENTER);
        inPanel.add(statLbl, BorderLayout.SOUTH);
    }

    public void initializeOption(){
        opt = 1;
        //status = "Initializing necessary Components";
        //statLbl.setText(status);
        task = new Task();
        task.execute();
    }

    public void deActivator(){
        opt = 2;
        ///status = "Deactivating running components in Use";
        //statLbl.setText(status);
        task = new Task();
        task.execute();
    }

    class Task extends SwingWorker<Void, Void>{
        private int prog = 0;
        private int allBarValue = 0;
        
        Task(){
            
            t = new SwingWorker<Void, Void>(){
                public Void doInBackground(){
                    if(opt == 1){
                        //start the rmi and launch application
                        if(!connectionStatus){ //true for testing on localhost... must change to false if deploying
                            //then, ping RMI
                            try{
                                status = "Trying to initiate connection to remote .....";
                                statLbl.setText(status);
                                Thread.sleep(2800);
                                connect_To_RMI();
                                Thread.sleep(2800);
                                //loadRemoteTime_Date_SalesModule(); //load remote time and date to determine remote connection successful
                                if(isConnectionValid() && canContinue){
                                    //if connection is valid
                                    status = "Connected to Remote .....";
                                    Thread.sleep(1800);
                                    //then check if update is avaialble
                                    status = "Checking for UPDATE .......";
                                    statLbl.setText(status);
                                    Thread.sleep(2000);
//                                  //then init remote db connection
                                    status = "Trying to preload necessary components .....";
                                    statLbl.setText(status);
                                    conHandler = new ConnectionHandler(remoteName);
                                    Thread.sleep(4800);
                                    con = conHandler.getConnectionInstance(); // initialize the con variabe with the valid conection got
                                    status = "necessary components loaded ......";
                                    statLbl.setText(status);
                                    Thread.sleep(4800);
                                    status = "DONE";
                                    statLbl.setText(status);
                                    // when done ... initialize the pingResources and pass it the valid connection
                                    resource = new PingResources(con);
                                    resource.start();
                                    Thread.sleep(2800);
                                    client = new ClientFrame(resource);
                                    client.loadStartUpDialog(self);
                                    //System.err.println("I don ready to show Main Menu now ");
                                    dispose();
                                    client.setVisible(true);

                                    //when thread is ready to show main menu... pass the resource reference
                                    //then show the main frame that w
                                }
                                else if(canContinue){
                                    doChangeHostAddress(); //do the change in address
                                }
                                statLbl.setText(status);
                            }
                            catch(InterruptedException e){}
                        }
                        else{
                            //no connection detected
                            try{
                                status = "No Network Connection Detected .....";
                                statLbl.setText(status);
                                Thread.sleep(4000);
                                int op = JOptionPane.showConfirmDialog(self, "This Computer is on a network, "
                                        + "It doesnt just appear to this APP that it is", "SURE YOU ARE CONNECTED TO A NETWORK ?", JOptionPane.YES_NO_OPTION);
                                if(op == JOptionPane.YES_OPTION){
                                    //means this person is proving this comp is connected
                                    connectionStatus = false;
                                    opt = 1;
                                    task = new Task();
                                    task.execute();
                                }
                                else{
                                    status = "Application will shut down ....";
                                    statLbl.setText(status);
                                    Thread.sleep(2800);
                                    status = "Plug to a network and relaunch ...";
                                    statLbl.setText(status);
                                    Thread.sleep(2900);
                                    status = "Application is shutting down ...";
                                    statLbl.setText(status);
                                    self = null;
                                    System.exit(0);                                    
                                }                            
                            }
                            catch(InterruptedException e){}

                        }
                    }
                    else{
                        //disconnect
                        prog = 0;
                        allBarValue = 0;
                        self.setVisible(true);
                        try{
                            status = "Logging out appropriately ...";
                            statLbl.setText(status);
                            Thread.sleep(3000);
                            status = "Successfully Logged out ...";
                            statLbl.setText(status);
                            Thread.sleep(2800); //sleep again before relaunching shit
                            status = "Closing all connections to remote ...";
                            statLbl.setText(status);
                            //unBindRMI();
                            Thread.sleep(3000);
                            status = "Completed ...";
                            statLbl.setText(status);
                            Thread.sleep(3000);
                            status = "Shutting Application down ...";
                            statLbl.setText(status);
                            Thread.sleep(2870);
                            self = null;
                            dispose();
                            System.exit(0);
                        }
                        catch(InterruptedException e){}
                    }
                    return null;
                }
            };
        }

        public Void doInBackground(){
            Random random = new Random();
            t.execute();
            while(!t.isDone() || done == false){
                try{
                    Thread.sleep(random.nextInt(1000));
                }
                catch(InterruptedException e){}
                prog += random.nextInt(10);
                setProgress(Math.min(prog, 100));
                prog = getProgress();
                currentBar.setValue(getProgress());
                if(currentBar.getValue() == 100){
                    allBarValue += 50;
                    prog = 0;
                    allBar.setValue(allBarValue);
                    if(allBarValue == 100){
                        //that means all task was copleted successfully
                        try{
                            Thread.sleep(2000);
                        }
                        catch(InterruptedException e){}
                        done = true;
                    }
                }
                if(done){
                    
                }
                repaint();
                validate();
            }

            return null;
        }

        @Override
        public void done(){

        }
        
        private void doChangeHostAddress(){
            Object newVal = null;
            try{
                //then connection to RMI is not valid show message that its either the server is unavailable or reset servername
                newVal = JOptionPane.showInputDialog(self, "Cannot connect to REMOTE SYSTEM - Provide a valid HOST -",
                    "Connection Manager | Remote Access", JOptionPane.ERROR_MESSAGE, null, null, remoteName);
                if(newVal.equals("") || newVal.equals(remoteName)){
                    //self.dispose();
                    int k = JOptionPane.showConfirmDialog(self, "No changes made to the Server Address", "Sure Server is Available", 
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(k == JOptionPane.OK_OPTION){
                        try{
                            Thread.sleep(1000);
                            dispose();
                            Thread.sleep(1000); //sleep again before relaunching shit
                            System.gc();//gabage collect
                            StartUpDialog start = new StartUpDialog();
                            start.initializeOption();
                            start.setVisible(true);
                        }
                        catch(InterruptedException e){}                        
                    }
                    else{
                        self.dispose();
                    }
                }
                else{
                    remoteM.writeNewHost(newVal.toString()); //write new input
                    //System.out.println("OK was clicked");
                    //then re-execute this
                    try{
                        Thread.sleep(3000);
                        dispose();
                        Thread.sleep(1000); //sleep again before relaunching shit
                        System.gc();//gabage collect
                        StartUpDialog start = new StartUpDialog();
                        start.initializeOption();
                        start.setVisible(true);
                    }
                    catch(InterruptedException e){}
                }
            }
            catch(NullPointerException nE){
                //System.out.println("cancel was clicked");
                //System.err.println("New value is passed as Null : " + nE.getMessage());
                self = null;
                System.gc();
                dispose();
            }            
        }
    }


}
