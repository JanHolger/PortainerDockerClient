package eu.bebendorf.pdc.response;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class DockerContainerCreateResponse {
    @SerializedName("Id")
    String id;
}
