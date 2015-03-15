package main.java.websockets;

public class WebSocketMessage {

    public static final String SEPARATOR = "#";
    public static final String LOGIN_REQ = "log_req";
    public static final String LOGOUT_REQ = "log_req";

    public WebSocketMessage(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    private Object data;
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private String type;
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + SEPARATOR + data.toString();
    }
}
