package src.AnnaPipeline.executor;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import src.AnnaPipeline.AnnaPipelineErr;
import src.AnnaPipeline.encoder.Encoder;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PipelineExecutor implements Executor {
    //private char[] data;
    private String data;
    private Status status;
    private Encoder encoder;
    private Logger logger;
    private String configFileName;
    private String outputTypeName;
    private Producer.DataAccessor dataAccessor;


    private Consumer consumer;
    private Producer producer;
    private List<Consumer> consumers;
    private List<Producer> producers;

    public PipelineExecutor(String configFileName, Logger logger) {
        this.configFileName = configFileName;
        this.logger = logger;
        status = Status.OK;
        encoder = new Encoder(configFileName, logger);
    }

    @Override
    public long loadDataFrom(Producer producer) {
        data = (String) dataAccessor.get();
        if (data.length() != dataAccessor.size()) return 0;
        return dataAccessor.size();
    }

    @Override
    public void run() {
        data = encoder.Codec(data);
        status = encoder.status();
        if (consumer.loadDataFrom(this) == 0) {
            // вернулся ноль => данные не загрузились
            status = Status.EXECUTOR_ERROR;
            return;
        }
        consumer.run();
        //таким образом статус ошибки, если появится, дойдет до R и тот завершит цикл.
        status = consumer.status();
    }

    @Override
    public void addProducer(Producer producer) {
        /*P.outputDataTypes() возвращает множество строк - имена типов, в виде которых P может отдавать данные.*/
        this.producer = producer;
        Set<String> types = producer.outputDataTypes();
        if (types.contains(String.class.getCanonicalName()))//TODO hardcode
            dataAccessor = producer.getAccessor(String.class.getCanonicalName());
        else
            status = Status.EXECUTOR_ERROR;

    }

    @Override
    public void addProducers(List producers) {
        this.producers = producers;
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

    /**
     * @param typeName Canonical type name.
     * @return Data accessor for specified type.
     */
    @NotNull
    @Override
    public Producer.DataAccessor getAccessor(@NotNull final String typeName) {
        // сохраняем себе имя типа, в который будем конвертировать перед отдачей.
        this.outputTypeName = typeName;
        return new DataAccessor();
    }

    /**
     * <p>Types of output data producer can produce.</p>
     * <p>Class canonical name is for example String.class.getCanonicalName().</p>
     *
     * @return Set of canonical names of producer's possible output types.
     */
    @Override
    public @NotNull Set<String> outputDataTypes() {
        Set<String> supportedDataTypes = new HashSet<>();
        supportedDataTypes.add(byte[].class.getCanonicalName());
        supportedDataTypes.add(char[].class.getCanonicalName());
        supportedDataTypes.add(String.class.getCanonicalName());
        return supportedDataTypes;
    }

    private final class DataAccessor implements Producer.DataAccessor {

        /**
         * Также, DataAccessor может в get() содержать логику конвертации в выбранный С тип данных, зависит от реализации.
         */
        @NotNull
        @Override
        public Object get() {
            Object tmp = new byte[]{0};
            try {//hardcode
                if (outputTypeName.equals(byte[].class.getCanonicalName()))
                    tmp = (byte[]) data.getBytes("UTF_16BE");
                else if (outputTypeName.equals(String.class.getCanonicalName()))
                    tmp = (String) data;
                else if (outputTypeName.equals(char[].class.getCanonicalName()))
                    tmp = (char[]) data.toCharArray();
            } catch (UnsupportedEncodingException ex) {
                logger.log(AnnaPipelineErr.ERR_ENCODER_ENCODING.getMsg());
                status = Status.READER_ERROR;
            }
            return tmp;
        }

        /**
         * @return Size of data. byteArray.length or string.length() if data has type String or ...
         */
        @Override
        public long size() {
            long sz = 0;
            if (outputTypeName.equals(byte[].class.getCanonicalName()))
                // тут мы знаем, что output это массив,
                sz = data.getBytes().length;
            else if (outputTypeName.equals(String.class.getCanonicalName()))
                // если строка, вернули бы ((String) output).length()
                sz = data.length();
            else if (outputTypeName.equals(char[].class.getCanonicalName()))
                sz = data.toCharArray().length;
            return sz;
        }
    }


}
