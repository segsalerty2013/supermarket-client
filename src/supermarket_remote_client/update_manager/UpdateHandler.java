/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supermarket_remote_client.update_manager;
import supermarket.rmi.ServerRMI;
import java.rmi.RemoteException;
import java.io.*;
import javax.swing.*;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class UpdateHandler implements Serializable{
    
    private File exec; //the name of the executable jar file
    private ServerRMI server;
    private String exec_name;
    private Vector<Vector<Object>> streamV;
    
    public UpdateHandler(ServerRMI s, String m){
        //m is the name of the file
        server = s;
        exec_name = m;
        exec = new File(new File("").getAbsolutePath() + "\\" + m);
        streamV = new Vector<Vector<Object>>();
    }
    
    public boolean checkUpdate(JDialog d, boolean c){
        //stop the timer passed in
        try{
            if(server.isNeedUpdate(exec_name, exec.length())){
                d.dispose();
                int r = JOptionPane.showConfirmDialog(null, "THE SERVER WAS UPDATED AND REQUIRES IMPORTANT CLIENTs UPDATE", 
                        "SERVER NOTIFICATION", JOptionPane.OK_CANCEL_OPTION);
                if(r == 0){
                    //then update am
                    new SwingWorker<Void, Void>(){
                        public Void doInBackground(){
                            try{
                                streamV = server.getFiles_toReplace();
                                String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();//get the runtime process id
                                String x[] = pid.split("@");
                                //then write the SteamVector Object in a file
                                FileOutputStream out = out = new FileOutputStream(new File(new File("").getAbsolutePath()) + "\\update.dat");
                                ObjectOutputStream outObj = new ObjectOutputStream(out);
                                outObj.writeObject(streamV);
                                outObj.flush();
                                Thread.sleep(1000);
                                outObj.close();
                                Thread.sleep(1000);
                                out.close();
                                Runtime.getRuntime().exec("java -jar ClientUpdater.jar");
                                Thread.sleep(1000);
                                Runtime.getRuntime().exec("cmd /c taskkill /F /IM " + x[0]);
                            }
                            catch(RemoteException rE){
                                System.err.println(rE.getMessage());
                                //means someting happened to the remote method... blah blah
                                JOptionPane.showMessageDialog(null, "Update was Interruped. Please check Networ", "UPDATE ERROR", JOptionPane.PLAIN_MESSAGE);
                            }
                            catch(IOException iO){
                                System.err.println("Problem with Remote IO : " + iO.getMessage());
                            }
                            catch(InterruptedException iE){}
                            return null;
                        }
                    }.execute();
                }
                else{
                    System.exit(0);
                }
            }
            else{
                c = true;
            }
        }
        catch(RemoteException rE){
            System.err.println("Error at calling ChckUpdate from remote : " + rE.getMessage());
        }
        return c;
    }
    
}
