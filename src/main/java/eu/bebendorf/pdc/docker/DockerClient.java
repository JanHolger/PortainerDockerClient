package eu.bebendorf.pdc.docker;

import eu.bebendorf.pdc.docker.model.ContainerFilter;
import eu.bebendorf.pdc.docker.model.DockerContainer;
import eu.bebendorf.pdc.docker.model.DockerFilter;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.http.HttpClient;
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

}
