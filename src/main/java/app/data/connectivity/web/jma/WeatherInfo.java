package app.data.connectivity.web.jma;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.OffsetDateTime;

public class WeatherInfo {

    public Area area;
    public String publishingOffice;
    public OffsetDateTime reportDateTime;
    public String infoType;
    public AreaTimeSeries areaTimeSeries;
    public PointTimeSeries pointTimeSeries;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
