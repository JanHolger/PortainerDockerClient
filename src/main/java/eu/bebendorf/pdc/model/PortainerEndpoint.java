package eu.bebendorf.pdc.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.http.HttpClient;
import eu.bebendorf.pdc.PortainerClient;
import eu.bebendorf.pdc.docker.DockerClient;
import eu.bebendorf.pdc.exception.RequestException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PortainerEndpoint {
    @Setter @Getter
    PortainerClient client;
    @SerializedName("Id") @Getter
    Integer id;
    @SerializedName("Name") @Getter
    String name;
    @SerializedName("Type") @Getter
    int typeId;
    @SerializedName("URL") @Getter
    String url;
    @SerializedName("PublicURL") @Getter
    String publicUrl;
    @SerializedName("GroupID") @Getter
    String groupId;
    @SerializedName("TLS") @Getter
    Boolean tls;
    @SerializedName("TLSSkipVerify") @Getter
    Boolean tlsSkipVerify;
    @SerializedName("TLSSkipClientVerify") @Getter
    Boolean tlsSkipClientVerify;
    @SerializedName("ApplicationID") @Getter
    String applicationId;
    @SerializedName("TenantID") @Getter
    String tenantId;
    @SerializedName("AuthenticationKey") @Getter
    String authenticationKey;
    @SerializedName("UserAccessPolicies")
    Map<String, JsonObject> userAccessPolicies;
    @SerializedName("TeamAccessPolicies")
    Map<String, JsonObject> teamAccessPolicies;
    public int getTeamAccess(int teamId){
        if(!teamAccessPolicies.containsKey(String.valueOf(teamId)))
            return 0;
        return teamAccessPolicies.get(String.valueOf(teamId)).get("RoleID").getAsInt();
    }
    public void setTeamAccess(int teamId, int roleId) throws RequestException {
        Map<String, JsonObject> teamAccessPolicies = new HashMap<>(this.teamAccessPolicies);
        if(roleId == 0){
            teamAccessPolicies.remove(String.valueOf(teamId));
        }else{
            JsonObject o = new JsonObject();
            o.addProperty("RoleID", roleId);
            teamAccessPolicies.put(String.valueOf(teamId), o);
        }
        PortainerEndpoint endpoint = new PortainerEndpoint();
        endpoint.teamAccessPolicies = teamAccessPolicies;
        client.getHttpClient().request("PUT", "/endpoints/"+id, endpoint, null);
        this.teamAccessPolicies = teamAccessPolicies;
    }
    public int getUserAccess(int userId){
        if(!userAccessPolicies.containsKey(String.valueOf(userId)))
            return 0;
        return userAccessPolicies.get(String.valueOf(userId)).get("RoleID").getAsInt();
    }
    public void setUserAccess(int userId, int roleId) throws RequestException {
        Map<String, JsonObject> userAccessPolicies = new HashMap<>(this.userAccessPolicies);
        if(roleId == 0){
            userAccessPolicies.remove(String.valueOf(userId));
        }else{
            JsonObject o = new JsonObject();
            o.addProperty("RoleID", roleId);
            userAccessPolicies.put(String.valueOf(userId), o);
        }
        PortainerEndpoint endpoint = new PortainerEndpoint();
        endpoint.userAccessPolicies = userAccessPolicies;
        client.getHttpClient().request("PUT", "/endpoints/"+id, endpoint, null);
        this.userAccessPolicies = userAccessPolicies;
    }
    public void delete() throws RequestException {
        client.getHttpClient().request("DELETE", "/endpoints/"+id, null);
    }
    public DockerClient getDocker(){
        HttpClient httpClient = new HttpClient(client.getHttpClient().getAddress()+"/endpoints/"+id+"/docker");
        httpClient.setToken(client.getHttpClient().getToken());
        DockerClient dockerClient = new DockerClient(httpClient);
        dockerClient.setPortainerEndpoint(this);
        return dockerClient;
    }
}
