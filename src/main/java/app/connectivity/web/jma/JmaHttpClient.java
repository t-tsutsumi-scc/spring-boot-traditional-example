package app.connectivity.web.jma;

import app.data.connectivity.web.jma.Area;
import app.data.connectivity.web.jma.WeatherInfo;
import app.exception.UncheckedException;
import app.util.JsonMapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Component
public class JmaHttpClient {

    private static final String AREA_JSON_URL_TEMPLATE = "https://www.jma.go.jp/bosai/jmatile/data/wdist/VPFD/%s.json";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";

    private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    private final JsonMapper jsonMapper;

    public JmaHttpClient(@Autowired JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper.copy(mapper -> {
            mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        });
    }

    public WeatherInfo getWeatherInfo(Area area) {
        try {
            HttpResponse<InputStream> response = httpClient.send(
                    HttpRequest.newBuilder(new URI(String.format(AREA_JSON_URL_TEMPLATE, area.areaCode)))
                            .header(HttpHeaders.USER_AGENT, USER_AGENT).build(),
                    HttpResponse.BodyHandlers.ofInputStream());

            try (InputStream input = response.body()) {
                WeatherInfo weatherInfo = jsonMapper.readValue(input, WeatherInfo.class);
                weatherInfo.area = area;
                return weatherInfo;
            }
        } catch (IOException | InterruptedException | URISyntaxException ex) {
            throw new UncheckedException(ex);
        }
    }

    public String getWeatherIconUrl(String weatherName, boolean isDaytime) {
        switch (weatherName) {
            case "晴れ":
                if (isDaytime) {
                    return "https://www.jma.go.jp/bosai/wdist/images/yoho_001_d.svg";
                } else {
                    return "https://www.jma.go.jp/bosai/wdist/images/yoho_001_n.svg";
                }
            case "くもり":
                return "https://www.jma.go.jp/bosai/wdist/images/yoho_012.svg";
            case "雨":
                return "https://www.jma.go.jp/bosai/wdist/images/yoho_023.svg";
            case "雪":
                return "https://www.jma.go.jp/bosai/wdist/images/yoho_032.svg";
            case "雨または雪":
                return "https://www.jma.go.jp/bosai/wdist/images/yoho_026.svg";
            default:
                throw new IllegalArgumentException("Unknown weather name: " + weatherName);
        }
    }

    public Document getWeatherIcon(String iconUrl) {
        try {
            HttpResponse<InputStream> response = httpClient.send(
                    HttpRequest.newBuilder(new URI(iconUrl)).header(HttpHeaders.USER_AGENT, USER_AGENT).build(),
                    HttpResponse.BodyHandlers.ofInputStream());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            try (InputStream input = response.body()) {
                return builder.parse(input);
            }
        } catch (IOException | InterruptedException | URISyntaxException | ParserConfigurationException
                | SAXException ex) {
            throw new UncheckedException(ex);
        }
    }

}
