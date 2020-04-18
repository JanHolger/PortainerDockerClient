package eu.bebendorf.pdc.exception;

import eu.bebendorf.pdc.response.PortainerErrorResponse;

public class RequestException extends Exception {
    private PortainerErrorResponse response;
    public RequestException(PortainerErrorResponse response){
        this.response = response;
    }
    public PortainerErrorResponse getResponse(){
        return response;
    }
    public String getMessage(){
        if(response == null)
            return "";
        return response.getError();
    }
}
