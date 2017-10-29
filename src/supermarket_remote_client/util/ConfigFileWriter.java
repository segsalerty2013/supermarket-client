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
public class ConfigFileWriter {

    private String write;
    private File file;
    private Writer out;

    private boolean stat = false;

    public ConfigFileWriter(){

    }

    public ConfigFileWriter(String f, String t){

        file = new File(f);
        write = t;
        if(!file.exists()){
            try{
                file.createNewFile();
                t = "localhost";
            }
            catch(IOException e){
                System.err.println("Cannot createa file here in : " + e.getMessage());
            }
        }
        try{
            init();
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        catch(IllegalArgumentException e2){
            System.err.println(e2.getMessage());
        }

    }

    public void writeAnother(String f, String t){
        file = new File(f);
        write = t;
        try{
            init();
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        catch(IllegalArgumentException e2){
            System.err.println(e2.getMessage());
        }
    }

    private void init() throws IOException, FileNotFoundException, IllegalArgumentException{
        if(file == null){
            throw new IllegalArgumentException("File should not be empty");
        }
        if(!file.exists()){
            throw new FileNotFoundException("Cannot find file to write into : " + file);
        }
        if(!file.isFile()){
            throw new IllegalArgumentException("The specified isnt a file : " + file);
        }
        if(!file.canWrite()){
            throw new IllegalArgumentException("File type cannot to written to : " + file);
        }

        out = new BufferedWriter(new FileWriter(file));
        try{
            out.write(write);
            stat = true;
        }
        finally{
            out.close();
        }
    }

    public boolean getWriteStatus(){
        return stat;
    }
}
