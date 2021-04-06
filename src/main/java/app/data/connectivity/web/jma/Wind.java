package app.data.connectivity.web.jma;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Wind {

    public String direction;
    public int speed;
    public String range;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
