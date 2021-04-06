package app.data.connectivity.web.jma;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class AreaTimeSeries {

    public List<TimeDefine> timeDefines;
    public List<String> weather;
    public List<Wind> wind;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
