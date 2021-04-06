package app.data.connectivity.web.jma;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class PointTimeSeries {

    public String pointNameJP;
    public List<TimeDefine> timeDefines;
    public List<Integer> temperature;
    public List<Integer> maxTemperature;
    public List<Integer> minTemperature;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
