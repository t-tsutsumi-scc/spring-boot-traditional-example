package app.data.connectivity.web.jma;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Area {

    public final String areaCode;
    public final String prefName;
    public final String areaName;

    public Area(String areaCode, String prefName, String areaName) {
        this.areaCode = areaCode;
        this.prefName = prefName;
        this.areaName = areaName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
