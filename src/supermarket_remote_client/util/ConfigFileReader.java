/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.util;
import java.io.*;

/**
 *
 * @author MUSTAFA
 */
public class ConfigFileReader {

    private File file;
    private StringBuffer content;
    private BufferedReader reader;
    private String text = null;

    public ConfigFileReader(){

    }

    public ConfigFileReader(String f){

        //"C:/Users/MUSTAFA/Documents/NetBeansProjects/BDF CyberTech/src/bdfcybertech/util/"
        file = new File(f);
        if(!file.exists()){
            try{
                file.createNewFile();
            }
            catch(IOException e){
                System.err.println("Cannot createa file here in : " + e.getMessage());
            }
        }
        content = new StringBuffer();
        try{
            init();
        }
        catch(IOException e){
            System.err.print(e.getMessage());
        }
    }

    public void reFileReader(String f){
        file = new File(f);
        content = new StringBuffer();
        try{
            init();
        }
        catch(IOException e){
            System.err.print(e.getMessage());
        }
    }

    private void init() throws IOException{
        try{
            reader = new BufferedReader(new FileReader(file));
            while((text = reader.readLine()) != null){
                content.append(text);
                //content.append(System.getProperty("line.separator"));
            }
        }
        catch(FileNotFoundException e){
            System.err.println("File is not found : " + e.getMessage());
        }
        finally{
            try{
                reader.close();
            }
            catch(NullPointerException e){
                System.err.println(e.getMessage());
            }
        }
    }

    public String getString(){
        return content.toString();
    }
}
