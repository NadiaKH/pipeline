package encoder;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class Config {

    private String delim = " = ";
    public static final int BUFF_SIZE = 100;

    public String inputFileName;
    public String outputFileName;
    public String mode;
    final int PARAM_TYPE_INDEX = 0;
    final int PARAM_VAL_INDEX = 1;
    final int VALID_LEX_LEN = 2;

    enum paramTypes {INPUT_FILE, OUTPUT_FILE, MODE};
    private static final Map<String, paramTypes> mapTypes;
    static {
        mapTypes = new HashMap<>();
        mapTypes.put("INPUT_FILE", paramTypes.INPUT_FILE);
        mapTypes.put("OUTPUT_FILE", paramTypes.OUTPUT_FILE);
        mapTypes.put("MODE", paramTypes.MODE);
    }

    public Config(String fname){

        try(BufferedReader br = new BufferedReader(new FileReader(fname))) {
            String ln;

            while ((ln = br.readLine()) != null) {
                String[] lex = ln.split(delim);
                if(lex.length != VALID_LEX_LEN){
                    Main.logger.log(Level.INFO, "Wrong grammar of config file.");
                    continue;
                }
                paramTypes type = mapTypes.get(lex[PARAM_TYPE_INDEX]);
                switch (type) {
                    case INPUT_FILE: {
                        inputFileName = lex[PARAM_VAL_INDEX];
                        break;
                    }
                    case OUTPUT_FILE: {
                        outputFileName = lex[PARAM_VAL_INDEX];
                        break;
                    }
                    case MODE: {
                        mode = lex[PARAM_VAL_INDEX];
                        break;
                    }
                    default: {
                        Main.logger.log(Level.WARNING, "Config.Config(): Unknown grammar of Config");
                        break;
                    }
                }
            }
        }catch(FileNotFoundException ex){
            Main.logger.log(Level.SEVERE, "Config.Config(): " +ex.toString());
        }
        catch(IOException ex){
            Main.logger.log(Level.SEVERE, "Config.Config(): " +ex.toString());
        }
    }
}
