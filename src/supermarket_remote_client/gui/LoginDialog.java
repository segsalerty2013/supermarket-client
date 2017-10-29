/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.gui;
import supermarket_remote_client.database.PingResources;
import supermarket_remote_client.gui.util.*;
import supermarket.util.InputsManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.sql.*;

/**
 *
 * @author MUSTAFA
 */
public class LoginDialog extends JDialog implements FocusListener{
    
    private JDialog self = this;
    
    private PopDialogParentHandler popH;
    
    private ClientFrame parent;
    private Dimension userDimension;
    private JPanel wrapperPanel;
    //private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel downPanel;
    //private JLabel compNameLbl;
    private JLabel userNameLbl;
    private JLabel passWordLbl;
    private JTextField userNameTxt;
    private JPasswordField passWordTxt;
    private JButton loginBtn;
    private JButton cancelBtn;
    
    ////////
    private PingResources resources;
    ///////
    private InputsManager input;
    private boolean validState = false;
    private String pass = "";
    private String userName = "";
    private Vector<String> userNamesV;
    private Vector<String> userPasswordV;

    private Connection connection;

    private boolean verify = false;

    private SwingWorker<Void, Void> creWork;
    private String addMsg = "";
    
    public LoginDialog(PingResources r){
        resources = r;
        dispose();
    }
    
    public LoginDialog(ClientFrame p, PingResources r){
        parent = p;
        resources = r;
        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////
        userDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(300, 100));
        setMinimumSize(new Dimension(300, 100));
        setLocation((userDimension.width - 300) / 2, (userDimension.height - 100) / 2);
        setAlwaysOnTop(true);
        //
        MyLookAndFeel.setLook();
        //
        input = new InputsManager();
        setUndecorated(true);
        initComponents();
        loadActions();
        setContentPane(wrapperPanel);
    }

    private void initComponents(){
        wrapperPanel = new JPanel(new BorderLayout(2, 2));
        centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 5));
        downPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));

        userNameLbl = new JLabel("User Name :");
        passWordLbl = new JLabel("Password :");
        userNameTxt = new JTextField();
        passWordTxt = new JPasswordField();
        loginBtn = new JButton("Login");
        cancelBtn = new JButton("Cancel");

        centerPanel.setPreferredSize(new Dimension(300, 80));
        downPanel.setPreferredSize(new Dimension(300, 30));

        userNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
        userNameLbl.setFont(new Font("Tahoma", 0, 12));
        userNameLbl.setPreferredSize(new Dimension(140, 30));

        passWordLbl.setHorizontalAlignment(SwingConstants.CENTER);
        passWordLbl.setFont(new Font("Tahoma", 0, 12));
        passWordLbl.setPreferredSize(new Dimension(140, 30));

        userNameTxt.setFont(new Font("Tahoma", 0, 12));
        userNameTxt.setPreferredSize(new Dimension(140, 30));
        userNameTxt.addFocusListener(this);

        passWordTxt.setFont(new Font("Tahoma", 0, 12));
        passWordTxt.setPreferredSize(new Dimension(140, 30));
        passWordTxt.addFocusListener(this);

        loginBtn.setPreferredSize(new Dimension(140, 30));
        loginBtn.setHorizontalAlignment(SwingConstants.CENTER);
        loginBtn.setBackground(new Color(65, 105, 225));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        cancelBtn.setPreferredSize(new Dimension(140, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));


        centerPanel.add(userNameLbl);
        centerPanel.add(userNameTxt);
        centerPanel.add(passWordLbl);
        centerPanel.add(passWordTxt);

        downPanel.add(loginBtn);
        downPanel.add(cancelBtn);

        wrapperPanel.setPreferredSize(new Dimension(300, 150));
        wrapperPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        
        wrapperPanel.add(centerPanel, BorderLayout.CENTER);
        wrapperPanel.add(downPanel, BorderLayout.SOUTH);
    }

    public void reShow(){
        passWordTxt.setText("");
        show();
    }

    private void loadActions(){
        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                popH.stopPopHandler();
                parent.stopLoginTimer();
                hide();
                //parent.closingManager();
            }
        });

        loginBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(validState){
                    try{
                        userCredentialValidator();
                        while(!creWork.isDone()){
                            if(verify){
                                parent.setLoginState(true);  //means it has been logged in
                                popH.stopPopHandler();
                                dispose();
                                break;
                            }
                            else if(creWork.isDone() && !verify){
                                JOptionPane.showMessageDialog(self, "- INVALID Credential Provided -" + "\n" + addMsg, "AUTHENTICATION ERROR", JOptionPane.PLAIN_MESSAGE);
                                break;
                            }
                        }
                    }
                    catch(Exception E){}
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATA -", "LOGIN ERROR", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
/*
        passWordTxt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(validState || !validState){
                    if(userCredentialValidator()){
                        parent.setLoginState(true);  //means it has been logged in
                        popH.stopPopHandler();
                        dispose();
                    }
                    else{
                        JOptionPane.showMessageDialog(self, "- INVALID Credential Provided -", "AUTHENTICATION ERROR", JOptionPane.PLAIN_MESSAGE);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATA -", "LOGIN ERROR", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
 * 
 */
    }

    private void userCredentialValidator(){
        userNamesV = resources.getUserNames();
        userPasswordV = resources.getPasswords();
        for(int i = 0; i < userNamesV.size(); i++){
            if(userNamesV.elementAt(i).equals(userNameTxt.getText()) && userPasswordV.elementAt(i).equals(pass)){
                //means the credential is valid
                creWork = new SwingWorker<Void, Void>(){
                    public Void doInBackground(){
                        //check if the user isnt online yet before adding it
                        connection = resources.getConnection();
                        try{
                            Statement stat = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            ResultSet re = stat.executeQuery("SELECT * FROM online_users WHERE name='" + userNameTxt.getText() + "'");
                            if(re.first()){
                                //means the user don login before
                                addMsg = " '" + userNameTxt.getText() + "' ALREADY LOGGED IN -";
                                verify = false;
                            }
                            else{
                                userName = userNameTxt.getText();
                                verify = true;
                            }
                        }
                        catch(SQLException sE){
                            System.err.println("Error occured at CLIENT LOGIN : " + sE.getMessage());
                        }
                        return null;
                    }

                    @Override
                    public void done(){
                        if(verify){
                            //if the user is verified... insert it inside user's online
                            try{
                                Statement stat = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                                stat.executeUpdate("INSERT INTO online_users (name, time_stamp) VALUES ('" + userName + "', CAST('" +
                                        parent.getStartUpInstance().getRemoteTime()+"' AS TIME) )");
                            }
                            catch(Exception sE){
                                System.err.println("Error occured at CLIENT INSERT INTO LOGIN TABLE : " + sE.getMessage());
                            }
                            finally{
                                //update the online_sys table by putting this system credentials there
                                //new SwingWorker<Void, Void>(){
                                    //public Void doInBackground(){
                                        if(verify){
                                            try{
                                                Statement stat = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                                                stat.executeUpdate("INSERT INTO online_sys (sys_name, user_logged) VALUES ('" + java.net.InetAddress.getLocalHost().getHostName()
                                                        + "', '" + userName + "')");
                                            }
                                            catch(Exception sE){
                                                System.err.println("Error occured at UPDATING online_sys TABLE : " + sE.getMessage());
                                            }
                                        }
                                        //return null;
                                    //}
                                //}.execute();
                            }
                        }
                    }
                };
                creWork.execute();
                break;
            }
            else{
                verify = false;
                //JOptionPane.showMessageDialog(self, "- INVALID Credential Provided -", "AUTHENTICATION ERROR", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    public String getUserName(){
        return userName;
    }

    public void focusGained(FocusEvent e){
        if(e.getSource() == userNameTxt){
            userNameTxt.setText("");
        }
        else if(e.getSource() == passWordTxt){
            passWordTxt.setText("");
        }
    }

    public void focusLost(FocusEvent e){
        if(e.getSource() == userNameTxt){
            //check it
            input.passAnotherInput(userNameTxt.getText());
            if(input.isGoodInput()){
                userNameTxt.setText(InputsManager.makeUppercase(userNameTxt.getText()));
                InputsManager.paint_unpaintTextFields(userNameTxt, true);
                validState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(userNameTxt, false);
                validState = false;
            }
        }
        else if(e.getSource() == passWordTxt){
            pass = ""; //reassign pass variable to empty back
            //get the password to string first
            char []p = passWordTxt.getPassword();
            for(int i = 0; i < p.length; i++){
                pass += p[i];
            }
            input.passAnotherInput(pass);
            if(input.isGoodInput()){
                //passWordTxt.setText(pass);
                InputsManager.paint_unpaintTextFields(passWordTxt, true);
                validState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(passWordTxt, false);
                validState = false;
            }
        }
    }
}
