package src.AnnaPipeline.manager;

import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Reader;
import ru.spbstu.pipeline.Writer;
import ru.spbstu.pipeline.logging.Logger;
import src.AnnaPipeline.AnnaPipelineErr;
import src.AnnaPipeline.executor.PipelineExecutor;
import src.AnnaPipeline.executor.PipelineReader;
import src.AnnaPipeline.executor.PipelineWriter;
import src.AnnaPipeline.parser.ManagerParser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Manager implements Runnable{
    private Reader reader;
    private Writer writer;
    private ArrayList<Executor> executors;
    private Logger logger;
    public ManagerParser config;

    public Manager(String configFileName, Logger logger){
         executors = new ArrayList<Executor>();

        config = new ManagerParser(configFileName, logger);
        config.parseConfig();
        this.logger = logger;
    }

    public void buildPipeline(){
        reader = new PipelineReader(config.inputFileName, config.buff_size, logger);
        writer = new PipelineWriter(config.outputFileName, logger);

        try {
            for (int i = 0; i < config.executorClasses.size(); i++) {
                Executor exec = (Executor) Class.forName(config.executorClasses.get(i))
                        .getConstructor(String.class, Logger.class)
                        .newInstance(config.executorConfigs.get(i), logger);
                executors.add(exec);
            }
        }catch(Exception ex){
            logger.log(AnnaPipelineErr.ERR_MNG_UNKNOWN_CLASS.getMsg(), ex);
        }

        if (executors.isEmpty()) {
            reader.addConsumer(writer);
            writer.addProducer(reader);
        }else{
            reader.addConsumer(executors.get(0));
            executors.get(0).addProducer(reader);
            for (int i = 0; i < executors.size(); i++) {
                if (i < executors.size() - 1) executors.get(i).addConsumer(executors.get(i + 1));
                if (i > 0) executors.get(i).addProducer(executors.get(i - 1));
            }
            executors.get(executors.size() - 1).addConsumer(writer);
            writer.addProducer(executors.get(executors.size() - 1));
        }
    }


    @Override
    public void run() {
        reader.run();
    }
}
