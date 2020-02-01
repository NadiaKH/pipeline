package src.AnnaPipeline;

import ru.spbstu.pipeline.logging.UtilLogger;
import src.AnnaPipeline.manager.Manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
                logger.log(AnnaPipelineErr.ERR_MAIN_ARGS.getMsg());
                return;
            } else {
                Manager manager = new Manager(args[0], logger);
                manager.buildPipeline();
                manager.run();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IO error", ex);
            System.out.println("Error occurred!!");
            System.exit(1);
        }
    }

}


