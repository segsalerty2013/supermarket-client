/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.util;

/**
 *
 * @author MUSTAFA
 */
public class RemoteHostManager {

    private static final String DIRECTORY = new java.io.File("").getAbsolutePath() + "\\";
    private ConfigFileWriter writer;
    private ConfigFileReader reader;

    public RemoteHostManager(){
        writer = new ConfigFileWriter();
        reader = new ConfigFileReader(DIRECTORY + "super_market_config.ini");
    }

    public String getRemoteHost(){
        return reader.getString();
    }

    public void writeNewHost(String txt){
        writer = new ConfigFileWriter(DIRECTORY + "super_market_config.ini", txt);
    }

    public boolean getWrittenStatus(){
        return writer.getWriteStatus();
    }
}
