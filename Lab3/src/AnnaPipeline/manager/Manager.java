package src.AnnaPipeline.manager;

import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Reader;
import ru.spbstu.pipeline.Writer;
import ru.spbstu.pipeline.logging.Logger;
import src.AnnaPipeline.AnnaPipelineErr;
import src.AnnaPipeline.executor.PipelineReader;
import src.AnnaPipeline.executor.PipelineWriter;
import src.AnnaPipeline.parser.ManagerParser;

import java.util.ArrayList;

public class Manager implements Runnable {
    public ManagerParser config;//private
    private Reader reader;
    private Writer writer;
    private ArrayList<Executor> executors;
    private Logger logger;


    public Manager(String configFileName, Logger logger) {
        executors = new ArrayList<Executor>();

        config = new ManagerParser(configFileName, logger);
        config.parseConfig();
        this.logger = logger;
    }


    public void buildPipeline() {
        reader = new PipelineReader(config.inputFileName, config.buff_size, logger);
        writer = new PipelineWriter(config.outputFileName, logger);

        /* build executors list */
        try {
            for (int i = 0; i < config.executorClasses.size(); i++) {
                Executor exec = (Executor) Class.forName(config.executorClasses.get(i))
                        .getConstructor(String.class, Logger.class)
                        .newInstance(config.executorConfigs.get(i), logger);
                executors.add(exec);
            }
        } catch (Exception ex) {
            logger.log(AnnaPipelineErr.ERR_MNG_UNKNOWN_CLASS.getMsg(), ex);
        }

        /* hand-shaking */
        if (executors.isEmpty()) {
            /* R просто сохраняет ссылку на W. */
            reader.addConsumer(writer);
            /* W сохраняет ссылку на R и инициализирует поле W.dataAccessor */
            writer.addProducer(reader);
        } else {
            /* reader просто сохраняет ссылку на C */
            reader.addConsumer(executors.get(0));

            /* C сохраняет ссылку на R и инициализирует своё поле C.dataAccessor */
            executors.get(0).addProducer(reader);

            for (int i = 0; i < executors.size(); i++) {
                /* P сохраняет ссылку на C */
                if (i < executors.size() - 1) executors.get(i).addConsumer(executors.get(i + 1));

                /* C сохраняет ссылку на P и инициализирует своё поле C.dataAccessor */
                if (i > 0) executors.get(i).addProducer(executors.get(i - 1));
            }

            /* тут P просто сохраняет ссылку на W */
            executors.get(executors.size() - 1).addConsumer(writer);

            /* W сохраняет ссылку на P и инициализирует своё поле W.dataAccessor */
            writer.addProducer(executors.get(executors.size() - 1));
        }
    }


    @Override
    public void run() {
        reader.run();
    }
}
