package src.AnnaPipeline;

public enum AnnaPipelineErr {
    ERR_MNG_WRONG_FORMAT ( "Manager: Wrong grammar of config file."),
    ERR_MNG_EXECUTOR_WRONG_PARAMS( "Manager: Expected format -  EXECUTOR = <ClassName> = <configFileName>"),
    ERR_MNG_UNKNOWN_GRAMMAR("Manager: Unknown grammar of Config"),
    ERR_MNG_UNKNOWN_CLASS("Manager: Unknown executor class"),
    ERR_MAIN_ARGS("Main: Invalid arguments of Main"),
    ERR_RW_IO_ALL("Some IO error"),
    ERR_RW_RT_ALL("Some RT error"),
    ERR_RD_FILE_NOT_FOUND("PipelineReader: File Not Found"),
    ERR_ENCODER_ENCODING("Encoder: Cannot convert properly to bytes");

    private final String msg;

    private AnnaPipelineErr(String msg) {this.msg = msg;}

    public String getMsg() { return this.msg; }
}
