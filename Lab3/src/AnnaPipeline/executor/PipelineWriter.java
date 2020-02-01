package src.AnnaPipeline.executor;

import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.Writer;
import ru.spbstu.pipeline.logging.Logger;
import src.AnnaPipeline.AnnaPipelineErr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class PipelineWriter implements Writer {
    private String fname;
    private byte[] data;

    private Status status;
    private Producer producer;
    private Producer.DataAccessor dataAccessor;
    private List<Producer> producers;
    private Logger logger;


    public PipelineWriter(String fname, Logger logger) {
        this.logger = logger;
        this.fname = fname;
        this.status = Status.OK;
    }


    @Override
    public long loadDataFrom(Producer producer) {
        data = (byte[]) dataAccessor.get();
//         if (data.length != dataAccessor.size()) return 0;
        return dataAccessor.size();
    }


    @Override
    public void run() {
        try (FileOutputStream foStream = new FileOutputStream(fname, true)) {
            foStream.write(data, 0, data.length);
        } catch (IOException ex) {
            logger.log(AnnaPipelineErr.ERR_RW_IO_ALL.getMsg(), ex);
            status = Status.WRITER_ERROR;
        } catch (RuntimeException ex) {
            logger.log(AnnaPipelineErr.ERR_RW_RT_ALL.getMsg(), ex);
            status = Status.WRITER_ERROR;
        }
    }

    ;


    @Override
    public Status status() {
        return status;
    }


    @Override
    public void addProducer(Producer producer) {
        /*P.outputDataTypes() возвращает множество строк - имена типов, в виде которых P может отдавать данные.*/
        this.producer = producer;
        Set<String> types = producer.outputDataTypes();
        if (types.contains(byte[].class.getCanonicalName()))
            dataAccessor = producer.getAccessor(byte[].class.getCanonicalName());
        else
            status = Status.WRITER_ERROR;
    }


    @Override
    public void addProducers(List producers) {
        this.producers = producers;
    }
}
