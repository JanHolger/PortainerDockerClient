package eu.bebendorf.pdc.request;

import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.docker.model.DockerEndpointConfig;

public class DockerNetworkConnectRequest {
    @SerializedName("Container")
    public String container;
    @SerializedName("EndpointConfig")
    public DockerEndpointConfig endpointConfig;
}
