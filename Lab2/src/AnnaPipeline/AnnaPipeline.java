package src.AnnaPipeline;

import ru.spbstu.pipeline.logging.UtilLogger;
import src.AnnaPipeline.manager.Manager;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.io.IOException;

import java.io.FileInputStream;

public class AnnaPipeline {
    private static final Logger LOGGER = Logger.getLogger(AnnaPipeline.class.getName());

    public static void main(String[] args) {
            try {
                LogManager.getLogManager().readConfiguration(new FileInputStream("log.config.txt"));

                ru.spbstu.pipeline.logging.Logger logger = UtilLogger.of(LOGGER);

                if (args.length < 1) {
                    logger.log(AnnaPipelineErr.ERR_MAIN_ARGS.getMsg());
                    return;
                }

                if (args.length != 1) {

                } else {
                    Manager manager = new Manager(args[0], logger);
                    manager.buildPipeline();
                    manager.run();
                }
            }catch(IOException ex){

            }
        }

}


