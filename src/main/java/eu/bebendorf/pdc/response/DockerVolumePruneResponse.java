package eu.bebendorf.pdc.response;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class DockerVolumePruneResponse {
    @SerializedName("VolumesDeleted")
    List<String> deleted;
    @SerializedName("SpaceReclaimed")
    long spaceReclaimed;
}
