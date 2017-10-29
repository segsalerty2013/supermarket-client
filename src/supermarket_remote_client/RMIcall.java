package supermarket_remote_client;
import supermarket.rmi.ServerRMI;
import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.registry.*;
import java.sql.Connection;

public class RMIcall{

  protected ServerRMI rem = null;
  private String remoteName;
  private Registry rmiRegistry = null;
      
  public RMIcall(String server){
    remoteName = "rmi://" + server + ":1099/Super_Market";
    
    try{
    rmiRegistry = LocateRegistry.getRegistry(1099);
    rem = (ServerRMI) Naming.lookup(remoteName);
    //here... therefore ... rem is ready to invoke remote methods
    }
    catch(MalformedURLException me){
      System.err.println("Error occured with the URL provided : " + me.getMessage());
    }
    catch(NotBoundException nb){
      System.err.println("Error trying to bind with server RMI : " + nb.getMessage());
    }
    catch(RemoteException re){
      System.err.println("Error at the remote : " + re.getMessage());
    }
  }
  
  public String getRemoteDate() throws RemoteException{
    return rem.getRemoteDate();
  }
  
  public String getRemoteTime() throws RemoteException{
    return rem.getRemoteTime();
  }

  public Connection getRemoteConnection() throws RemoteException{
    return rem.getSQL_Connection();
  }
  
  public void TestRmiCall(String in) throws RemoteException{
    rem.TestRmiCall(in);
  }
  
  public ServerRMI getServerMethods(){
    return rem;
  }

  /*
  public static void main(String []args){
    try{
      RMIcall rmi = new RMIcall("localhost");
      rmi.TestRmiCall("Testing me today : " + rmi.getRemoteDate());
      System.out.println("The time is " + rmi.getRemoteTime());
    }
    catch(RemoteException e){
      System.err.println("Remote Exception was thrown from main : " + e.getMessage());
      }
  }
   * 
   */

  
}