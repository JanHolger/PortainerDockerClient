package eu.bebendorf.pdc;

import eu.bebendorf.pdc.exception.PortainerAuthException;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.http.HttpClient;
import eu.bebendorf.pdc.model.PortainerEndpoint;
import eu.bebendorf.pdc.model.PortainerTeam;
import eu.bebendorf.pdc.model.PortainerTeamMembership;
import eu.bebendorf.pdc.model.PortainerUser;
import eu.bebendorf.pdc.request.PortainerAdminInitRequest;
import eu.bebendorf.pdc.request.PortainerAuthRequest;
import eu.bebendorf.pdc.http.HttpResponse;
import eu.bebendorf.pdc.response.PortainerAdminInitResponse;
import eu.bebendorf.pdc.response.PortainerAuthResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PortainerDockerClient {

    @Getter
    private HttpClient httpClient;

    public PortainerDockerClient(String address, String username, String password) throws PortainerAuthException {
        this.httpClient = new HttpClient("http://"+(address.contains(":")?address:(address+":9000"))+"/api");
        try {
            PortainerAuthResponse response = httpClient.request("POST", "/auth", new PortainerAuthRequest(username, password), PortainerAuthResponse.class);
            httpClient.setToken(response.getToken());
        }catch (RequestException error){
            HttpResponse response = httpClient.requestRaw("GET", "/users/admin/check", null);
            if(response.getResponseCode() != 204){
                try {
                    PortainerAdminInitResponse response1 = httpClient.request("POST", "/users/admin/init", new PortainerAdminInitRequest(username, password), PortainerAdminInitResponse.class);
                    PortainerAuthResponse response2 = httpClient.request("POST", "/auth", new PortainerAuthRequest(username, password), PortainerAuthResponse.class);
                    this.httpClient.setToken(response2.getToken());
                }catch (RequestException err){
                    throw new PortainerAuthException(err.getMessage());
                }
            }else{
                throw new PortainerAuthException(error.getMessage());
            }
        }
    }

    public List<PortainerUser> getUsers() throws RequestException {
        List<PortainerUser> users = new ArrayList<>();
        for(PortainerUser user : httpClient.request("GET", "/users", PortainerUser[].class)){
            user.setClient(this);
            users.add(user);
        }
        return users;
    }

    public PortainerUser getUser(int id) throws RequestException {
        PortainerUser user = httpClient.request("GET", "/users/"+id, PortainerUser.class);
        user.setClient(this);
        return user;
    }

    public PortainerUser createUser(String name, String password, int roleId) throws RequestException {
        PortainerUser user = new PortainerUser(null, null, name, password, roleId);
        user = httpClient.request("POST", "/users", user, PortainerUser.class);
        user.setClient(this);
        return user;
    }

    public List<PortainerTeam> getTeams() throws RequestException {
        List<PortainerTeam> teams = new ArrayList<>();
        for(PortainerTeam team : httpClient.request("GET", "/teams", PortainerTeam[].class)){
            team.setClient(this);
            teams.add(team);
        }
        return teams;
    }

    public PortainerTeam getTeam(int id) throws RequestException {
        PortainerTeam team = httpClient.request("GET", "/teams/"+id, PortainerTeam.class);
        team.setClient(this);
        return team;
    }

    public PortainerTeam createTeam(String name) throws RequestException {
        PortainerTeam team = new PortainerTeam(null, null, name);
        team = httpClient.request("POST", "/teams", team, PortainerTeam.class);
        team.setClient(this);
        return team;
    }

    public List<PortainerTeamMembership> getTeamMemberships() throws RequestException {
        List<PortainerTeamMembership> memberships = new ArrayList<>();
        for(PortainerTeamMembership membership : httpClient.request("GET", "/team_memberships", PortainerTeamMembership[].class)){
            membership.setClient(this);
            memberships.add(membership);
        }
        return memberships;
    }

    public PortainerTeamMembership createTeamMembership(int teamId, int userId, int roleId) throws RequestException {
        PortainerTeamMembership membership = new PortainerTeamMembership(null, null, teamId, userId, roleId);
        membership = httpClient.request("POST", "/team_memberships", membership, PortainerTeamMembership.class);
        membership.setClient(this);
        return membership;
    }

    public List<PortainerEndpoint> getEndpoints() throws RequestException {
        List<PortainerEndpoint> endpoints = new ArrayList<>();
        for(PortainerEndpoint endpoint : httpClient.request("GET", "/endpoints", PortainerEndpoint[].class)){
            endpoint.setClient(this);
            endpoints.add(endpoint);
        }
        return endpoints;
    }

    public PortainerEndpoint getEndpoint(int id) throws RequestException {
        PortainerEndpoint endpoint = httpClient.request("GET", "/endpoints/"+id, PortainerEndpoint.class);
        endpoint.setClient(this);
        return endpoint;
    }

    public PortainerEndpoint createLocalDockerEndpoint(String name) throws RequestException {
        PortainerEndpoint endpoint = httpClient.requestRawMultipart("POST", "/endpoints", new HashMap<String, Object>(){{
            put("Name", name);
            put("EndpointType", 1);
        }}).getBodyOrError(PortainerEndpoint.class);
        endpoint.setClient(this);
        return endpoint;
    }

}
