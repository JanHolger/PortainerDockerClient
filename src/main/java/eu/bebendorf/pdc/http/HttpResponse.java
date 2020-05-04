package eu.bebendorf.pdc.http;

import com.google.gson.Gson;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.response.PortainerErrorResponse;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static Gson GSON = new Gson();

    int responseCode;
    byte[] body;

    public HttpResponse(int responseCode, byte[] body){
        this.responseCode = responseCode;
        this.body = body;
    }

    public HttpResponse(int responseCode, String body){
        this.responseCode = responseCode;
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    public String getBody(){
        return new String(body, StandardCharsets.UTF_8);
    }

    public byte[] getBodyBytes(){
        return body;
    }

    public <T> T getBody(Class<T> clazz){
        if(clazz.equals(String.class))
            return (T) getBody();
        if(clazz.equals(byte[].class))
            return (T) getBodyBytes();
        return GSON.fromJson(getBody(), clazz);
    }

    public <T> T getBodyOrError(Class<T> responseType) throws RequestException {
        if(!isSuccess()){
            throw new RequestException(getBody());
        }
        if(responseType != null){
            return getBody(responseType);
        }
        return null;
    }

    public int getResponseCode(){
        return responseCode;
    }

    public boolean isSuccess(){
        return responseCode >= 200 && responseCode < 300;
    }

}
