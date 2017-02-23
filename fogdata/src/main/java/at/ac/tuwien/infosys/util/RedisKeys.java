package at.ac.tuwien.infosys.util;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 */
public enum RedisKeys {
    UTILIZATION("utilization"), IP("ip"), PARENT("parent"), CHILDREN("children"), DEVICE_TYPE("device_type"),
    DEVICE_ID("id"), PORT("port"), LOCATION("location"), CLOUD_IP("cloud_ip"), CLOUD_PORT("cloud_port"),
    PARENTS("parents"), SERVICE_IMAGE("service_image"), SERVICE_TYPES("service_types"), SERVICE_DATA("service_data");


    private String key;
    RedisKeys(final String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
    @Override
    public String toString() {
        return this.getKey();
    }
}
