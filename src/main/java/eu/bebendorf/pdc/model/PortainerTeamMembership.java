package eu.bebendorf.pdc.model;

import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.PortainerClient;
import eu.bebendorf.pdc.exception.RequestException;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class PortainerTeamMembership {
    @Setter
    PortainerClient client;
    @SerializedName("Id")
    Integer id;
    @SerializedName("UserID")
    Integer userId;
    @SerializedName("TeamID")
    Integer teamId;
    @SerializedName("Role")
    Integer roleId;
    public void delete() throws RequestException {
        client.getHttpClient().request("DELETE", "/team_memberships/"+id, null);
    }
    public void changeRole(int roleId) throws RequestException {
        PortainerTeamMembership membership = new PortainerTeamMembership();
        membership.roleId = roleId;
        client.getHttpClient().request("PUT", "/team_memberships/"+id, membership, null);
        this.roleId = roleId;
    }
    public PortainerUser getUser() throws RequestException {
        return client.getUser(userId);
    }

    public PortainerTeam getTeam() throws RequestException {
        return client.getTeam(teamId);
    }
}
