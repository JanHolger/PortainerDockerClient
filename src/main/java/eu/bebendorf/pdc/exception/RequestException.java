package eu.bebendorf.pdc.exception;

import com.google.gson.Gson;
import eu.bebendorf.pdc.response.DockerErrorResponse;
import eu.bebendorf.pdc.response.PortainerErrorResponse;

public class RequestException extends Exception {
    private static final Gson GSON = new Gson();
    public RequestException(String body){
        super(body);
    }
    public PortainerErrorResponse portainer(){
        if(getMessage() == null)
            return null;
        return GSON.fromJson(super.getMessage(), PortainerErrorResponse.class);
    }
    public DockerErrorResponse docker(){
        if(getMessage() == null)
            return null;
        return GSON.fromJson(super.getMessage(), DockerErrorResponse.class);
    }
}
