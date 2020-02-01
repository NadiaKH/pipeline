package src.AnnaPipeline.executor;

import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.Writer;
import ru.spbstu.pipeline.logging.Logger;
import src.AnnaPipeline.AnnaPipelineErr;

import java.io.*;
import java.util.List;
import java.util.logging.Level;

public class PipelineWriter implements Writer {
    private String fname;
    private byte[] data;

    private Status status;
    private Producer producer;
    private List<Producer> producers;
    private Logger logger;


    public PipelineWriter(String fname, Logger logger){
        this.logger = logger;
        this.fname = fname;
        this.status = Status.OK;
    }


    @Override
    public void loadDataFrom(Producer producer) {
        try {
            if (producer.status() == Status.OK) {
                //data = new String((byte[]) producer.get(), "UTF_16BE").toCharArray();
                String str = new String((byte[]) producer.get(), "UTF_16BE");
                data = str.getBytes("UTF_16BE");
            };

            //data = (char[]) producer.get();
        }catch(UnsupportedEncodingException ex){
            logger.log(AnnaPipelineErr.ERR_ENCODER_ENCODING.getMsg());
            status = Status.EXECUTOR_ERROR;
        }

    }


    @Override
    public void run(){
        try(FileOutputStream foStream = new FileOutputStream(fname, true)) {
            if (producer.status() == Status.OK)
                foStream.write(data, 0, data.length);
        }catch(IOException ex){
            logger.log(AnnaPipelineErr.ERR_RW_IO_ALL.getMsg(), ex);
            status = Status.WRITER_ERROR;
        }catch(RuntimeException ex){
            logger.log(AnnaPipelineErr.ERR_RW_RT_ALL.getMsg(), ex);
            status = Status.WRITER_ERROR;
        }
    };


    @Override
    public Status status() {
        return status;
    }

    @Override
    public void addProducer(Producer producer) {
        this.producer = producer;
    }

    @Override
    public void addProducers(List producers) {
        this.producers = producers;
    }
}
