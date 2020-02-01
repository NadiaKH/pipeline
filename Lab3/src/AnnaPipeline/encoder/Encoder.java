package src.AnnaPipeline.encoder;

import ru.spbstu.pipeline.Fallible;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

//import ru.spbstu.rot13.EncoderParser;


public class Encoder implements Fallible {
    private static int ROT = 13;
    private static String alphabet = "";
    private static int CONTINIOUS_SEQUENCE_FIRST = 1;
    private static int CONTINIOUS_SEQUENCE_LAST = (int) '~';
    private static int BYTESEQ_MARKER = 0xFEFF;

    static {
        for (int letterCode = CONTINIOUS_SEQUENCE_FIRST; letterCode <= CONTINIOUS_SEQUENCE_LAST; letterCode++)
            alphabet += (char) letterCode;
    }

    private final String MODE_CODE = "code";
    private final String MODE_DECODE = "decode";
    Status status;
    Logger logger;
    private EncoderParser config;


    /*
        public void Run(){
            try(BufferedReader br = new BufferedReader(new FileReader(config.inputFileName));
                BufferedWriter bw = new BufferedWriter(new FileWriter(config.outputFileName))) {
                int num_readed = 0;

                char[] ibuff = new char[Config.BUFF_SIZE];
                char[] obuff = new char[Config.BUFF_SIZE];

                while ((num_readed = br.read(ibuff, 0, Config.BUFF_SIZE)) != -1) {
                    Codec(ibuff, obuff, num_readed);
                    bw.write(obuff, 0, num_readed);
                }
            }catch(IOException ex){
                Main.logger.log(Level.SEVERE, "Encoder.Run(): " +ex.toString());
            }
        }
    */
    public Encoder(String fname, Logger logger) {
        this.logger = logger;
        config = new EncoderParser(logger);
        status = config.parseConfig(fname);
    }

    //public char[] Codec( char[]src){
    public String Codec(String src) {
        char letter;
        char codedLetter;
        int index;
        int offset;
        int rot = ROT;

        char[] dst = new char[src.length()];
        //String dst = new String(src);

        switch (config.mode) {
            case MODE_CODE:
                rot = ROT;
                break;
            case MODE_DECODE:
                rot = -ROT;
                break;
        }
        try {
            for (int i = 0; i < src.length(); i++) {
                // letter = src[i];
                letter = src.charAt(i);
                if (letter == BYTESEQ_MARKER) {
                    dst[i] = letter;
                    continue;
                }
                ;
                index = alphabet.indexOf((int) letter);
                if (index == -1) {
                    logger.log(EncoderError.ERR_ENC_NO_SUCH_SYMBOL_IN_ALPHABET.getMsg());
                    continue;
                }
                offset = (index + rot) % alphabet.length();
                if (offset < 0)
                    offset = alphabet.length() + offset;
                codedLetter = alphabet.charAt(offset);
                dst[i] = codedLetter;
            }
        } catch (IndexOutOfBoundsException ex) {
            logger.log(ex.toString());
            status = Status.EXECUTOR_ERROR;
        } catch (RuntimeException ex) {
            logger.log(ex.toString());
            status = Status.EXECUTOR_ERROR;
        }
        return String.copyValueOf(dst);
    }

    @Override
    public Status status() {
        return status;
    }
}
