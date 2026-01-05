package exception;
//引数が不正（null/空など）の例外。
public class InvalidParameterException extends RuntimeException{
    //不正だったパラメータ名
    private final String parameterName;
    //不正だったパラメータ値
    private final Object parameterValue;
    public InvalidParameterException(String message) {
        super("InvalidParameter message=" + message);
        this.parameterName = null;
        this.parameterValue = null;
    }

    public InvalidParameterException(String message, String parameterName) {
        super("InvalidParameter message=" + message + ", parameterName=" + parameterName);
        this.parameterName = parameterName;
        this.parameterValue = null;
    }

    public InvalidParameterException(String message, String parameterName, Object parameterValue) {
        super("InvalidParameter message=" + message
                + ", parameterName=" + parameterName
                + ", parameterValue=" + parameterValue);
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public String getParameterName() {
        return parameterName;
    }

    public Object getParameterValue() {
        return parameterValue;
    }


}
