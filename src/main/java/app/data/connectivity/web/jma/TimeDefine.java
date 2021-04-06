package app.data.connectivity.web.jma;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.OffsetDateTime;

public class TimeDefine {

    public OffsetDateTime dateTime;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
