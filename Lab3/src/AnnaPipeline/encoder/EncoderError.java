package src.AnnaPipeline.encoder;

public enum EncoderError {
    ERR_ENC_WRONG_FORMAT("Rot13 Encoder: Config: Wrong grammar of config file."),
    ERR_ENC_UNKNOWN_GRAMMAR("Rot13 Encoder: Unknown grammar of Config"),
    ERR_ENC_ENCODING("Cannot convert properly to bytes"),
    ERR_ENC_NO_SUCH_SYMBOL_IN_ALPHABET("Encoder.Codec(): Alphabet doesn't contain some of input file symbols, they will be left untouched.");

    private final String msg;

    private EncoderError(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

}
