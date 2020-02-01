package encoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main  {
    static Logger logger = Logger.getLogger("Logging Main");

    public static void main(String[] args) {

        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("log.config.txt"));
            if(args.length < 1){
                logger.log(Level.SEVERE, "No config file!");
                return;
            }
        }catch(FileNotFoundException ex){
            logger.log(Level.INFO, "Main(): Cannot find logger configuration file. Logging messages will be written to console only!" +ex.toString());
        }
        catch(IOException ex){
            logger.log(Level.SEVERE, "Main(): " +ex.toString());
        }

        Encoder enc = new Encoder(args[0]);
        enc.Run();
    }
}
