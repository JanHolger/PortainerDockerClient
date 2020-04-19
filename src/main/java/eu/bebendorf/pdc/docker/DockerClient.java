package eu.bebendorf.pdc.docker;

import eu.bebendorf.pdc.docker.model.*;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.http.HttpClient;
import eu.bebendorf.pdc.request.DockerContainerCreateRequest;
import eu.bebendorf.pdc.request.DockerVolumeCreateRequest;
import eu.bebendorf.pdc.response.DockerContainerCreateResponse;
import eu.bebendorf.pdc.response.DockerVolumePruneResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockerClient {

    @Getter
    private HttpClient httpClient;

    public DockerClient(HttpClient httpClient){
        this.httpClient = httpClient;
    }

    public List<DockerContainer> getContainers(ContainerFilter... filters) throws RequestException {
        List<DockerContainer> containers = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("all", "true");
        params.put("filters", DockerFilter.json(filters));
        for(DockerContainer container : httpClient.request("GET", "/containers/json?"+HttpClient.queryParams(params), DockerContainer[].class)){
            container.setClient(this);
            containers.add(container);
        }
        return containers;
    }

    public DockerContainer getContainer(String id) throws RequestException {
        List<DockerContainer> containers = getContainers(ContainerFilter.id(id));
        if(containers.size() == 0)
            return null;
        return containers.get(0);
    }

    public void startContainer(String id) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/start", null);
    }

    public void killContainer(String id, String signal) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/kill?signal="+HttpClient.urlEncode(signal), null);
    }

    public void killContainer(String id) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/kill", null);
    }

    public void stopContainer(String id, int timeout) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/stop?t="+timeout, null);
    }

    public void stopContainer(String id) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/stop", null);
    }

    public void restartContainer(String id, int timeout) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/restart?t="+timeout, null);
    }

    public void restartContainer(String id) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/restart", null);
    }

    public void renameContainer(String id, String name) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/rename?name="+HttpClient.urlEncode(name), null);
    }

    public void removeContainer(String id) throws RequestException {
        removeContainer(id, false, false, false);
    }

    public void removeContainer(String id, boolean removeVolumes, boolean force, boolean removeLink) throws RequestException {
        Map<String, String> params = new HashMap<>();
        params.put("v", removeVolumes?"true":"false");
        params.put("force", force?"true":"false");
        params.put("link", removeLink?"true":"false");
        httpClient.request("DELETE", "/containers/"+id+"?"+HttpClient.queryParams(params), null);
    }

    public String createContainer(DockerContainerCreateRequest request) throws RequestException {
        return httpClient.request("POST", "/containers/create", request, DockerContainerCreateResponse.class).getId();
    }

    public String createContainer(String name, DockerContainerCreateRequest request) throws RequestException {
        return httpClient.request("POST", "/containers/create?name="+HttpClient.urlEncode(name), request, DockerContainerCreateResponse.class).getId();
    }

    public List<DockerVolume> getVolumes(VolumeFilter... filters) throws RequestException {
        List<DockerVolume> volumes = new ArrayList<>();
        for(DockerVolume volume : httpClient.request("GET", "/volumes?filters="+HttpClient.urlEncode(DockerFilter.json(filters)), DockerVolume[].class)){
            volume.setClient(this);
            volumes.add(volume);
        }
        return volumes;
    }

    public DockerVolume getVolume(String name) throws RequestException {
        return httpClient.request("GET", "/volumes/"+name, DockerVolume.class);
    }

    public DockerVolume createVolume(DockerVolumeCreateRequest request) throws RequestException {
        return httpClient.request("POST", "/volumes/create", request, DockerVolume.class);
    }

    public DockerVolumePruneResponse pruneVolumes(VolumePruneFilter... filters) throws RequestException {
        return httpClient.request("POST", "/volumes/prune?filters="+HttpClient.urlEncode(DockerFilter.json(filters)), DockerVolumePruneResponse.class);
    }

}
