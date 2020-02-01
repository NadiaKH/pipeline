package ru.spbstu.rot13;

import ru.spbstu.pipeline.Fallible;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class EncoderParser  {
    Logger logger;
    public String mode;
    final int PARAM_TYPE_INDEX = 0;
    final int PARAM_VAL_INDEX = 1;
    final int VALID_LEX_LEN = 2;

    enum Grammar {
         MODE,
         DELIMITER,
         SPACES,
         EMPTY
    };

    private static final Map<String, Grammar > mapGrammar;
    private static final Map<Grammar, String > mapValues;
    static {
        mapGrammar = new HashMap<>();
        mapGrammar.put("MODE", Grammar.MODE);
        mapGrammar.put("=", Grammar.DELIMITER);
        mapGrammar.put("\\s+", Grammar.SPACES);
        mapGrammar.put("", Grammar.EMPTY);

        mapValues = new HashMap<>();
        mapValues.put(Grammar.MODE,  "MODE");
        mapValues.put(Grammar.DELIMITER, "=");
        mapValues.put( Grammar.SPACES, "\\s+");
        mapValues.put( Grammar.EMPTY, "");

    }

    public EncoderParser(Logger logger){
        this.logger = logger;
    }

    public Status parseConfig(String fname){


        try(BufferedReader br = new BufferedReader(new FileReader(fname))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                ln = ln.replaceAll(mapValues.get(Grammar.SPACES), mapValues.get(Grammar.EMPTY));

                String[] lex = ln.split(mapValues.get(Grammar.DELIMITER));
                if(lex.length != VALID_LEX_LEN){
                    logger.log(EncoderError.ERR_ENC_UNKNOWN_GRAMMAR.getMsg());
                    continue;
                }

                Grammar type = mapGrammar.get(lex[PARAM_TYPE_INDEX]);
                switch (type) {
                    case MODE:{
                        mode = lex[PARAM_VAL_INDEX];
                        break;
                    }
                    default: {
                        //Main.logger.log(Level.WARNING, "Config.Config(): Unknown grammar of Config");
                        return Status.EXECUTOR_ERROR;
                    }
                }
            }
        }catch(FileNotFoundException ex){
            //Main.logger.log(Level.SEVERE, "Config.Config(): " +ex.toString());
            return Status.EXECUTOR_ERROR;
        }
        catch(IOException ex){
            //Main.logger.log(Level.SEVERE, "Config.Config(): " +ex.toString());
            return Status.EXECUTOR_ERROR;
        }
        return Status.OK;
    }
}
