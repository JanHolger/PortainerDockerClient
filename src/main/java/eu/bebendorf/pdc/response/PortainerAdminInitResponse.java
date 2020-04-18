package eu.bebendorf.pdc.response;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class PortainerAdminInitResponse {
    @SerializedName("Id")
    int id;
    @SerializedName("Username")
    String username;
    @SerializedName("Password")
    String password;
    @SerializedName("Role")
    int roleId;
}
