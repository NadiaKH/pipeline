package src.AnnaPipeline.parser;
import ru.spbstu.pipeline.logging.Logger;
import src.AnnaPipeline.AnnaPipelineErr;

import java.io.*;
import java.util.*;

public class ManagerParser{

    public int buff_size = 100;

    public String inputFileName;
    public String outputFileName;
    public static ArrayList<String> executorClasses;
    public static ArrayList<String> executorConfigs;

    final int PARAM_TYPE_INDEX = 0;
    final int PARAM_VAL1_INDEX = 1;
    final int PARAM_VAL2_INDEX = 2;
    final int VALID_LEX_LEN_MIN = 2;
    final int VALID_LEX_LEN_MAX = 3;

    static Logger m_logger;
    static String m_ConfigFileName;

    enum Grammar {
         DELIMITER,
         BUFF_SIZE,
         INPUT_FILE,
         OUTPUT_FILE,
         EXECUTOR,
         SPACES,
         EMPTY
    };

    private static final Map<String, Grammar > mapGrammar;
    private static final Map<Grammar, String > mapValues;
    static {
        mapGrammar = new HashMap<>();
        mapGrammar.put("=", Grammar.DELIMITER);
        mapGrammar.put("\\s+", Grammar.SPACES);
        mapGrammar.put("", Grammar.EMPTY);
        mapGrammar.put("BUFF_SIZE", Grammar.BUFF_SIZE);
        mapGrammar.put("INPUT_FILE", Grammar.INPUT_FILE);
        mapGrammar.put("OUTPUT_FILE", Grammar.OUTPUT_FILE);
        mapGrammar.put("EXECUTOR", Grammar.EXECUTOR);

        mapValues = new HashMap<>();
        mapValues.put(Grammar.DELIMITER, "=");
        mapValues.put(Grammar.SPACES, "\\s+");
        mapValues.put(Grammar.EMPTY, "");
        mapValues.put(Grammar.BUFF_SIZE, "BUFF_SIZE");
        mapValues.put(Grammar.INPUT_FILE, "INPUT_FILE");
        mapValues.put(Grammar.OUTPUT_FILE, "OUTPUT_FILE");
        mapValues.put(Grammar.EXECUTOR, "EXECUTOR");

        executorClasses = new ArrayList<String>();
        executorConfigs = new ArrayList<String>();

    }
    public ManagerParser(String configFileName, Logger logger){
        m_ConfigFileName = configFileName;
        m_logger = logger;

    }

    public void parseConfig(){

        try(BufferedReader br = new BufferedReader(new FileReader(m_ConfigFileName))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                ln = ln.replaceAll(mapValues.get(Grammar.SPACES), mapValues.get(Grammar.EMPTY));

                String[] lex = ln.split(mapValues.get(Grammar.DELIMITER));
                if(ln.equals(mapValues.get(Grammar.EMPTY))) continue;
                if(lex.length > VALID_LEX_LEN_MAX || lex.length < VALID_LEX_LEN_MIN ){
                    m_logger.log(AnnaPipelineErr.ERR_MNG_WRONG_FORMAT.getMsg() );
                    break;
                }

                Grammar type = mapGrammar.get(lex[PARAM_TYPE_INDEX]);
                switch (type) {
                    case BUFF_SIZE:{
                        buff_size = Integer.parseInt(lex[PARAM_VAL1_INDEX]);
                        // todo: verify
                        break;
                    }
                    case INPUT_FILE:{
                        inputFileName = lex[PARAM_VAL1_INDEX];
                        break;
                    }
                    case OUTPUT_FILE:{
                        outputFileName = lex[PARAM_VAL1_INDEX];
                        break;
                    }
                    case EXECUTOR:{
                        // !!!!!!!!todo: verify!!!!!
                        if (lex.length == VALID_LEX_LEN_MAX) {
                           executorClasses.add(lex[PARAM_VAL1_INDEX]);
                           executorConfigs.add(lex[PARAM_VAL2_INDEX]);
                       }
                       else{
                            m_logger.log(AnnaPipelineErr.ERR_MNG_EXECUTOR_WRONG_PARAMS.getMsg() );

                       }
                       break;
                    }
                    case EMPTY:{
                        break;
                    }
                    default: {
                        m_logger.log(AnnaPipelineErr.ERR_MNG_UNKNOWN_GRAMMAR.getMsg() );
                        break;
                    }
                }
            }
        }catch(FileNotFoundException ex){
            m_logger.log( ex.toString());
        }
        catch(IOException ex){
            m_logger.log( ex.toString());
        }
    }


}

    

