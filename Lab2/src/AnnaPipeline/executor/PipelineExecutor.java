package src.AnnaPipeline.executor;
import src.AnnaPipeline.encoder.*;
import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PipelineExecutor implements Executor {
    //private char[] data;
    private String data;
    private Status status;
    private Encoder encoder;
    private Logger logger;
    private String configFileName;

    private Consumer consumer;
    private Producer producer;
    private List<Consumer> consumers;
    private List<Producer> producers;

    public PipelineExecutor( String configFileName, Logger logger){
        this.configFileName = configFileName;
        this.logger = logger;
        status = Status.OK;
        encoder = new Encoder(configFileName, logger);
    }

    @Override
    public void loadDataFrom(Producer producer) {
        try {
            if (producer.status() == Status.OK) {
                //data = new String((byte[]) producer.get(), "UTF_16BE").toCharArray();
                data = new String((byte[]) producer.get(), "UTF_16BE");
            };

                //data = (char[]) producer.get();
        }catch(UnsupportedEncodingException ex){
            logger.log("Error occured while encoding ");
            status = Status.EXECUTOR_ERROR;
        }

    }

    @Override
    public void run() {
        if (producer.status() == Status.OK){
            data = encoder.Codec(data);
            status = encoder.status();
            consumer.loadDataFrom(this);
            consumer.run();
        }
        else{
            status = Status.EXECUTOR_ERROR;
            consumer.run();
        }
    }

    @Override
    public void addProducer(Producer producer) {
        this.producer = producer;
    }

    @Override
    public void addProducers(List producers) {
        this.producers = producers;
    }

    @Override
    public Object get(){
        try {
            //return (new String(data)).getBytes("UTF_16BE");
            return data.getBytes("UTF_16BE");
        }catch(UnsupportedEncodingException ex){
            logger.log(EncoderError.ERR_ENC_ENCODING.getMsg());
            status = Status.EXECUTOR_ERROR;
            return null;
        }

    }

    @Override
    public void addConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addConsumers(List consumers) {
        this.consumers = consumers;
    }

    @Override
    public Status status() {
        return status;
    }
}
