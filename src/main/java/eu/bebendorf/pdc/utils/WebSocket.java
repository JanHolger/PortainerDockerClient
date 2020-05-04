package eu.bebendorf.pdc.utils;

import lombok.Getter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WebSocket {
    private WebSocketClient client;
    private String url;
    private List<OpenListener> openListeners = new ArrayList<>();
    private List<MessageListener> msgListeners = new ArrayList<>();
    private List<CloseListener> closeListeners = new ArrayList<>();
    private List<ErrorListener> errorListeners = new ArrayList<>();
    @Getter
    private boolean open = false;
    public WebSocket(String url){
        this.url = url;
    }
    private static URI makeURI(String string){
        URI uri = null;
        try {
            uri = new URI(string);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }
    public void send(String msg){
        client.send(msg);
    }
    public void send(byte[] msg){
        client.send(msg);
    }
    public void close(){
        client.close();
    }
    public void open(){
        client = new WebSocketClient(makeURI(url)) {
            public void onOpen(ServerHandshake serverHandshake) {
                open = true;
                openListeners.forEach(OpenListener::onOpen);
            }
            public void onMessage(String s) {
                msgListeners.forEach(l -> l.onMessage(s));
            }
            public void onClose(int i, String s, boolean b) {
                open = false;
                closeListeners.forEach(l -> l.onClose(i, s));
            }
            public void onError(Exception e) {
                errorListeners.forEach(l -> l.onError(e));
            }
        };
        client.connect();
    }
    public void close(int code){
        client.close(code);
    }
    public void close(int code, String reason){
        client.close(code, reason);
    }
    public WebSocket onOpen(OpenListener l){
        openListeners.add(l);
        return this;
    }
    public WebSocket onMessage(MessageListener l){
        msgListeners.add(l);
        return this;
    }
    public WebSocket onClose(CloseListener l){
        closeListeners.add(l);
        return this;
    }
    public WebSocket onError(ErrorListener l){
        errorListeners.add(l);
        return this;
    }
    public interface OpenListener {
        void onOpen();
    }
    public interface MessageListener {
        void onMessage(String message);
    }
    public interface ErrorListener {
        void onError(Exception ex);
    }
    public interface CloseListener {
        void onClose(int code, String reason);
    }
}
