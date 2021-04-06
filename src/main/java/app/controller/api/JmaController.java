package app.controller.api;

import app.connectivity.db.JmaApiLogDao;
import app.connectivity.db.JmaAreaDao;
import app.data.connectivity.db.JmaApiLog;
import app.data.connectivity.web.jma.Area;
import app.data.connectivity.web.jma.WeatherInfo;
import app.exception.CustomResponseException;
import app.service.WeatherForecastService;
import app.util.Moments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

@RestController
@RequestMapping("/api/jma")
public class JmaController {

    @Autowired
    WeatherForecastService weatherForecastLogic;

    @Autowired
    JmaAreaDao jmaAreaDao;

    @Autowired
    JmaApiLogDao jmaApiLogDao;

    @Autowired
    Moments moments;

    @GetMapping(value = "/areas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Area> areas() {
        return jmaAreaDao.getAllArea();
    }

    @GetMapping(value = "/weather-forecast", produces = "image/svg+xml")
    @Validated
    public ResponseEntity<String> weatherForecast(@RequestParam("areaCode") @NotEmpty String areaCode,
            HttpServletRequest request, @RequestHeader("User-Agent") String userAgent)
            throws IOException, ParserConfigurationException, TransformerException {
        Area area = jmaAreaDao.getArea(areaCode)
                .orElseThrow(() -> new CustomResponseException("Invalid areaCode", HttpStatus.BAD_REQUEST));

        WeatherInfo weatherInfo = weatherForecastLogic.getWeatherInfo(area);
        Document svgDocument = weatherForecastLogic.createSvgDocument(weatherInfo);

        StringWriter svgDocumentWriter = new StringWriter();
        weatherForecastLogic.writeSvgDocument(svgDocument, svgDocumentWriter);

        long timestamp = moments.epochMilli();
        String clientIpAddress = request.getRemoteAddr();
        jmaApiLogDao.insertLog(timestamp, clientIpAddress, areaCode, userAgent);

        return ResponseEntity.ok(svgDocumentWriter.toString());
    }

    @GetMapping(value = "/api-log", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JmaApiLog> apiLog() {
        return jmaApiLogDao.getLatestLogs();
    }

}
