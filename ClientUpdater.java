

import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientUpdater{
    
    private static Vector<Vector<Object>> streamV;
    private static FileInputStream input;
    private static ObjectInputStream inputObj;
    private static OutputStream out;
    
    public ClientUpdater(){
        
    }
    
    public static void main(String []args){
        //Vector<Vector<Object>> streamV;
        try{
            UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
            if(!new File(new File("").getAbsolutePath() + "\\update.dat").exists()){
                JOptionPane.showMessageDialog(null, "Update Was Started in CRITICAL MODE", "Update Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            else{
                //then read the file and do whats necessary               
                new SwingWorker<Void, Void>(){
                    public Void doInBackground(){
                        /*
                        new WorkLoader(){
                            @Override                                
                            public void whenDone() {
                                JOptionPane.showMessageDialog(null, "Please Restart Application", "UPDATE COMPLETED", JOptionPane.PLAIN_MESSAGE);
                                System.exit(0);
                            }
                        }.passJob(this, new JDialog(), "Performing Necessary Update and Setting up Recomemnded Changes");
                         * 
                         */
                            
                            
                        try{
                            input = new FileInputStream(new File("").getAbsolutePath() + "\\update.dat");
                            inputObj = new ObjectInputStream(input);
                            streamV = (Vector<Vector<Object>>) inputObj.readObject();
                            input.close();
                        }
                        catch(Exception e){
                            System.err.println("Something happened at update Input : " + e.getMessage());
                        }
                        finally{
                            /*
                             * //dont delete this file for now
                            try{                               
                                new File(new File("").getAbsolutePath() + "\\update.dat").delete();
                            }
                            catch(Exception ee){}
                             * 
                             */
                        }
                        
                        out = null;
                        for(Vector<Object> o : streamV){
                            //int c = 0;
                            String fN = o.elementAt(0).toString(); //the file name and extension
                            Vector<byte[]> v = (Vector<byte[]>) o.elementAt(1); //the vector of the bytes
                            Vector<Integer> lenV = (Vector<Integer>)o.elementAt(2);

                            if(fN.contains("\\")){
                                String[] split = {"lib", ""};
                                if(!new File(new File("").getAbsolutePath() + "\\" + split[0]).exists()){
                                    new File(new File("").getAbsolutePath() + "\\" + split[0]).mkdir();
                                }
                            }
                            try{
                                out = new FileOutputStream(new File(new File("").getAbsolutePath()) + "\\" + fN);

                                for(int w = 0; w < v.size(); w++){
                                    //System.out.println("Available Byte is : " + v.elementAt(w) + " With length : " + lenV.elementAt(w));
                                    out.write(v.elementAt(w), 0, lenV.elementAt(w));
                                    out.flush();
                                }
                                //out.flush();
                                out.close();
                            }
                            catch(IOException iO){
                                System.err.println("Error at IO : " + iO.getMessage());
                            }                      
                        }
                        return null;
                    }
                }.execute();
            }
        }
        catch(Exception e){
            System.err.println("Error at Execution Main : " + e.getMessage());
        }
        finally{
            JOptionPane.showMessageDialog(null, "SUCCESS ! Please Restart Application", "UPDATE COMPLETE", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }
    }

static abstract class WorkLoader{

    private JDialog dialog;
    private JPanel wrapper;
    private JLabel label;
    private String txt;

    private SwingWorker<Void, Void> workT;
    private Component component;//component to lock while waiting and get
    //released when done
    private Timer workTimer;

    public WorkLoader(){

    }
    
    public void passJob(SwingWorker<Void, Void> w, Component c, String t){
        workT = w;
        component = c;
        txt = "<html><div style='text-align:center'><span style='color:red'>Please wait ... </span><br /><span>" + t + "</span></div></html>";
        init();
        dialog.setVisible(true);
        dialog.repaint();
        workTimer = new Timer(500, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(workT.getState() != SwingWorker.StateValue.DONE){
                    //while this worker is not done. then continue to roll
                    //component.setVisible(false);
                    component.setVisible(false);
                    //component.getParent().getParent().getParent().setEnabled(false);
                }
                else{
                   //this worker is done... then perform when the user
                    //put in whenDone method
                   workTimer.stop();
                   dialog.dispose();
                   //component.setVisible(true);
                   //component.setVisible(true);
                   //component.getParent().getParent().getParent().setEnabled(true);
                   whenDone();//invoke the method whendone()
                }
            }
        });
        workTimer.start();
    }

    private void init(){
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        dialog = new JDialog();
        label = new JLabel();
        label.setIcon(new ImageIcon(getClass().getResource("loading.gif")));
        label.setText(txt);
        label.setIconTextGap(5);
        label.setBackground(Color.WHITE);
        label.setPreferredSize(new Dimension(250, 75));
        wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setPreferredSize(new Dimension(250, 75));
        wrapper.setBackground(Color.WHITE);

        dialog.setSize(250, 75);
        //dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setUndecorated(true);
        dialog.setLocation((d.width - 250) / 2, (d.height - 75) / 2);
        dialog.setAlwaysOnTop(true);

        wrapper.add(label, BorderLayout.CENTER);
        dialog.getContentPane().add(wrapper);
    }
    public void pauseLoader(){
        workTimer.setRepeats(false);
        dialog.hide();
    }

    public void resumeLoader(){
        workTimer.setRepeats(true);
        workTimer.restart();
        dialog.show();
    }

    public abstract void whenDone(); //override this method -- what to do
    //when the worker is done

}
}
