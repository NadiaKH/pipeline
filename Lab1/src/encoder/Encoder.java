package encoder;

import java.io.*;
import java.util.logging.*;


public class Encoder {
    private Config config;
    private static int ROT = 13;
    private static String alphabet = "";
    private static int CONTINIOUS_SEQUENCE_FIRST = 1;
    private static int CONTINIOUS_SEQUENCE_LAST = (int) '~';
    private final String MODE_CODE = "code";
    private final String MODE_DECODE = "decode";

    static {
        for (int letterCode = CONTINIOUS_SEQUENCE_FIRST; letterCode <= CONTINIOUS_SEQUENCE_LAST; letterCode++)
            alphabet += (char) letterCode;
    }

    private void Codec(char[] src, char[]dst, int len){
        char letter;
        char codedLetter;
        int index;
        int offset;
        int rot = ROT;
        switch(config.mode){
            case MODE_CODE:
                rot = ROT;
                break;
            case MODE_DECODE:
                rot = -ROT;
                break;
        }
        try {
            for (int i = 0; i < len; i++) {
                letter = src[i];

                index = alphabet.indexOf((int) letter);
                if (index == -1) {
                    Main.logger.log(Level.WARNING, "Encoder.Codec(): Alphabet doesn't contain some of input file symbols, they will be left untouched.");
                    continue;
                }
                offset = (index + rot) % alphabet.length();
                if (offset < 0)
                    offset = alphabet.length() + offset;
                codedLetter = alphabet.charAt(offset);
                dst[i] = codedLetter;
            }
        }
        catch(IndexOutOfBoundsException ex){
            Main.logger.log(Level.SEVERE, "Encoder.Codec(): array index out of range." + ex.toString());
        }
        catch(RuntimeException ex){
            Main.logger.log(Level.SEVERE, "Encoder.Codec(): " + ex.toString());
        }
    }


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

    public Encoder(String fname){
        config = new Config(fname);
    }

}
