package eu.bebendorf.pdc.http;

import com.google.gson.Gson;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.response.PortainerErrorResponse;

public class HttpResponse {

    private static Gson GSON = new Gson();

    int responseCode;
    String body;

    public HttpResponse(int responseCode, String body){
        this.responseCode = responseCode;
        this.body = body;
    }

    public String getBody(){
        return body;
    }

    public <T> T getBody(Class<T> clazz){
        if(clazz.equals(String.class))
            return (T) getBody();
        return GSON.fromJson(body, clazz);
    }

    public <T> T getBodyOrError(Class<T> responseType) throws RequestException {
        if(!isSuccess()){
            throw new RequestException(getBody(PortainerErrorResponse.class));
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
