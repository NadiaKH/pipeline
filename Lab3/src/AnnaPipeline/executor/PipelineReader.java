package src.AnnaPipeline.executor;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Reader;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import src.AnnaPipeline.AnnaPipelineErr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import ru.spbstu.pipeline.Fallible;

public class PipelineReader implements Reader {
    Consumer consumer;
    List<Consumer> consumers;
    private String fname;
    private byte[] data;
    private int bufsize;
    private String outputTypeName;
    private Status status;
    private Logger logger;


    public PipelineReader(String fname, int bufsize, Logger logger) {
        this.fname = fname;
        this.logger = logger;
        this.bufsize = bufsize;
        status = Status.OK;
    }

    //@Override
    public Status status() {
        return status;
    }

    @Override
    public void addConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    ;

    @Override
    public void addConsumers(List consumers) {
        this.consumers = consumers;
    }

    ;

    @Override
    public void run() {
        int num_read = 0;
        byte[] buffer = new byte[bufsize];
        try (FileInputStream fiStream = new FileInputStream(fname)) {
            while ((num_read = fiStream.read(buffer, 0, bufsize)) != -1) {
                data = Arrays.copyOfRange(buffer, 0, num_read);
                status = Status.OK;
                if (consumer.loadDataFrom(this) == 0) {
                    // вернулся ноль => данные не загрузились
                    status = Status.EXECUTOR_ERROR;
                    return;
                }
                consumer.run();
                //таким образом статус ошибки, если появится, дойдет до R и тот завершит цикл.
                status = consumer.status();
            }
        } catch (FileNotFoundException ex) {
            logger.log(AnnaPipelineErr.ERR_RD_FILE_NOT_FOUND.getMsg(), ex);
            status = Status.READER_ERROR;
        } catch (IOException ex) {
            logger.log(AnnaPipelineErr.ERR_RW_IO_ALL.getMsg(), ex);
            status = Status.READER_ERROR;
        } catch (RuntimeException ex) {
            logger.log(AnnaPipelineErr.ERR_RW_RT_ALL.getMsg(), ex);
            status = Status.READER_ERROR;
        }
    }

    ;

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
            try {
                if (outputTypeName.equals(byte[].class.getCanonicalName()))
                    tmp = data;
                else if (outputTypeName.equals(String.class.getCanonicalName()))
                    tmp = new String(data, "UTF_16BE");
                else if (outputTypeName.equals(char[].class.getCanonicalName()))
                    tmp = new String(data, "UTF_16BE").toCharArray();

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
            // Objects.requireNonNull(data);
            long sz = 0;
            try {
                if (outputTypeName.equals(byte[].class.getCanonicalName()))
                    // тут мы знаем, что output это массив,
                    sz = data.length;
                else if (outputTypeName.equals(String.class.getCanonicalName()))
                    // если строка, вернули бы ((String) output).length()
                    sz = (new String(data, "UTF_16BE")).length();
                else if (outputTypeName.equals(char[].class.getCanonicalName()))
                    sz = (new String(data, "UTF_16BE")).toCharArray().length;

            } catch (UnsupportedEncodingException ex) {
                logger.log(AnnaPipelineErr.ERR_ENCODER_ENCODING.getMsg());
                status = Status.READER_ERROR;
            }
            return sz;
        }
    }


}
