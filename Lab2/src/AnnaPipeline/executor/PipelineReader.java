package src.AnnaPipeline.executor;

import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Fallible;
import ru.spbstu.pipeline.Reader;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import src.AnnaPipeline.AnnaPipelineErr;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class PipelineReader implements Reader {
    private String fname;
    private byte[] data;
    private int bufsize;

    private Status status;
    Consumer consumer;
    List<Consumer> consumers;

    private Logger logger;


    public PipelineReader(String fname, int bufsize, Logger logger){
        this.fname = fname;
        this.logger = logger;
        this.bufsize = bufsize;
        status = Status.OK;
    }

    @Override
    public Status status() {
        return status;
    }

    /**
      * @return Byte array of even length.
    */

    @Override
    public Object get(){
        byte[] tmp = new byte[]{0};
        try {
            tmp = new String(data, "UTF_16BE").getBytes("UTF_16BE");
        }catch(UnsupportedEncodingException ex){
            logger.log(AnnaPipelineErr.ERR_ENCODER_ENCODING.getMsg());
            status = Status.READER_ERROR;
        }
        return tmp;
    };

    @Override
    public void addConsumer(Consumer consumer){
        this.consumer = consumer;
    };

    @Override
    public void addConsumers(List consumers){
        this.consumers = consumers;
    };

    @Override
    public void run(){
        int num_read = 0;
        byte[] buffer = new byte[bufsize];
        try(FileInputStream fiStream = new FileInputStream(fname)){
            while ((num_read = fiStream.read(buffer, 0, bufsize)) != -1) {
                data = Arrays.copyOfRange(buffer, 0, num_read);
                status = Status.OK;
                consumer.loadDataFrom(this);
                consumer.run();
            }
            status = Status.READER_ERROR;
            consumer.loadDataFrom(this);
            consumer.run();
        }catch(FileNotFoundException ex){
            logger.log(AnnaPipelineErr.ERR_RD_FILE_NOT_FOUND.getMsg(), ex);
            status = Status.READER_ERROR;
        }catch(IOException ex){
            logger.log(AnnaPipelineErr.ERR_RW_IO_ALL.getMsg(), ex);
            status = Status.READER_ERROR;
        }catch(RuntimeException ex){
            logger.log(AnnaPipelineErr.ERR_RW_RT_ALL.getMsg(), ex);
            status = Status.READER_ERROR;
        }
    };


}
