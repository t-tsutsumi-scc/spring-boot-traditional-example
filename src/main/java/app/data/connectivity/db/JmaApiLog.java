package app.data.connectivity.db;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JmaApiLog {

    public final long timestamp;
    public final String clientIpAddress;
    public final String areaCode;
    public final String userAgent;

    public JmaApiLog(long timestamp, String clientIpAddress, String areaCode, String userAgent) {
        this.timestamp = timestamp;
        this.clientIpAddress = clientIpAddress;
        this.areaCode = areaCode;
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
