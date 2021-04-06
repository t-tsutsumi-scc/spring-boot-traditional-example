package app.service;

import app.connectivity.web.jma.JmaHttpClient;
import app.data.connectivity.web.jma.Area;
import app.data.connectivity.web.jma.TimeDefine;
import app.data.connectivity.web.jma.WeatherInfo;
import app.data.connectivity.web.jma.Wind;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Component
public class WeatherForecastService {

    @Autowired
    JmaHttpClient jmaHttpClient;

    private Map<String, Document> weatherIconCache = Collections.synchronizedMap(new HashMap<>());

    public WeatherInfo getWeatherInfo(Area area) throws IOException {
        return jmaHttpClient.getWeatherInfo(area);
    }

    public Document createSvgDocument(WeatherInfo weatherInfo) throws IOException, ParserConfigurationException {
        Document document = createNewDocument();
        buildRootElement(document, weatherInfo);
        buildAreaInfo(document, weatherInfo);
        buildPublishInfo(document, weatherInfo);
        buildTimes(document, weatherInfo);
        buildWeather(document, weatherInfo);
        buildWind(document, weatherInfo);
        buildTemperature(document, weatherInfo);
        return document;
    }

    public void writeSvgDocument(Document svgDocument, Writer output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        // transformer.setOutputProperty("indent", "yes");
        DOMSource source = new DOMSource(svgDocument);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }

    Document createNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    void buildRootElement(Document document, WeatherInfo weatherInfo) {
        Element rootElemtent = document.createElement("svg");
        rootElemtent.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        rootElemtent.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        rootElemtent.setAttribute("width",
                Integer.toString(68 + 40 * weatherInfo.areaTimeSeries.timeDefines.size() + 20));
        rootElemtent.setAttribute("height", "300");
        document.appendChild(rootElemtent);
    }

    void buildAreaInfo(Document document, WeatherInfo weatherInfo) {
        Element areaInfo = document.createElement("text");
        areaInfo.setAttribute("font-size", "14px");
        areaInfo.setAttribute("y", "14");
        areaInfo.setTextContent(String.format("%s／%s【気温：%s】", weatherInfo.area.prefName, weatherInfo.area.areaName,
                weatherInfo.pointTimeSeries.pointNameJP));
        document.getDocumentElement().appendChild(areaInfo);
    }

    void buildPublishInfo(Document document, WeatherInfo weatherInfo) {
        Element publishInfo = document.createElement("text");
        publishInfo.setAttribute("font-size", "12px");
        publishInfo.setAttribute("fill", "gray");
        publishInfo.setAttribute("y", "30");
        publishInfo.setTextContent(String.format("%s %s%s", weatherInfo.publishingOffice,
                weatherInfo.reportDateTime.format(DateTimeFormatter.ofPattern("uuuu年M月d日H時")), weatherInfo.infoType));
        document.getDocumentElement().appendChild(publishInfo);
    }

    void buildTimes(Document document, WeatherInfo weatherInfo) {
        for (int i = 0; i < weatherInfo.pointTimeSeries.timeDefines.size(); i++) {
            TimeDefine timeDefine = weatherInfo.pointTimeSeries.timeDefines.get(i);
            Element day = document.createElement("text");
            day.setAttribute("class", "day");
            day.setAttribute("font-size", "12px");
            day.setAttribute("font-weight", timeDefine.dateTime.getHour() == 0 ? "bold" : "normal");
            day.setAttribute("fill", "blue");
            day.setAttribute("text-anchor", "middle");
            day.setAttribute("x", Integer.toString(68 + 40 * i));
            day.setAttribute("y", "50");
            day.setTextContent(i == 0 || timeDefine.dateTime.getHour() == 0
                    ? timeDefine.dateTime.format(DateTimeFormatter.ofPattern("M/d"))
                    : "");
            document.getDocumentElement().appendChild(day);
        }

        for (int i = 0; i < weatherInfo.pointTimeSeries.timeDefines.size(); i++) {
            TimeDefine timeDefine = weatherInfo.pointTimeSeries.timeDefines.get(i);
            Element time = document.createElement("text");
            time.setAttribute("class", "time");
            time.setAttribute("font-size", "12px");
            time.setAttribute("font-weight", timeDefine.dateTime.getHour() == 0 ? "bold" : "normal");
            time.setAttribute("fill", "blue");
            time.setAttribute("text-anchor", "middle");
            time.setAttribute("x", Integer.toString(68 + 40 * i));
            time.setAttribute("y", "64");
            time.setTextContent(Integer.toString(timeDefine.dateTime.getHour()));
            document.getDocumentElement().appendChild(time);
        }

        for (int i = 0; i < weatherInfo.pointTimeSeries.timeDefines.size(); i++) {
            TimeDefine timeDefine = weatherInfo.pointTimeSeries.timeDefines.get(i);
            Element timeLine = document.createElement("line");
            timeLine.setAttribute("class", "time-line");
            timeLine.setAttribute("stroke", "blue");
            timeLine.setAttribute("stroke-weight", timeDefine.dateTime.getHour() == 0 ? "2" : "1");
            timeLine.setAttribute("x1", Integer.toString(68 + 40 * i));
            timeLine.setAttribute("x2", Integer.toString(68 + 40 * i));
            timeLine.setAttribute("y1", "66");
            timeLine.setAttribute("y2", "70");
            document.getDocumentElement().appendChild(timeLine);
        }

        Element timeHeader = document.createElement("text");
        timeHeader.setAttribute("font-size", "12px");
        timeHeader.setAttribute("x", "6");
        timeHeader.setAttribute("y", "66");
        timeHeader.setTextContent("時刻");
        document.getDocumentElement().appendChild(timeHeader);
    }

    void buildWeather(Document document, WeatherInfo weatherInfo) {
        for (int i = 0; i < weatherInfo.areaTimeSeries.weather.size(); i++) {
            int hour = weatherInfo.areaTimeSeries.timeDefines.get(i).dateTime.getHour();
            Element weatherSvg = document.createElement("svg");
            weatherSvg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
            weatherSvg.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
            weatherSvg.setAttribute("width", "40");
            weatherSvg.setAttribute("height", "32");
            weatherSvg.setAttribute("x", Integer.toString(68 + 40 * i));
            weatherSvg.setAttribute("y", "74");

            String weather = weatherInfo.areaTimeSeries.weather.get(i);
            switch (weather) {
                case "晴れ":
                    // fall through
                case "くもり":
                    // fall through
                case "雨":
                    // fall through
                case "雪":
                    // fall through
                case "雨または雪":
                    weatherSvg.setAttribute("viewBox", "0 0 80 64");
                    Document weatherIcon = getWeatherIcon(weather, hour >= 6 && hour < 18);
                    synchronized (weatherIcon) {
                        NodeList fragmentNodes = weatherIcon.getDocumentElement().getChildNodes();
                        for (int j = 0; j < fragmentNodes.getLength(); j++) {
                            weatherSvg.appendChild(document.importNode(fragmentNodes.item(j), true));
                        }
                    }
                    break;
                default:
                    weatherSvg.setAttribute("viewBox", "0 0 0 0");
                    break;
            }
            document.getDocumentElement().appendChild(weatherSvg);
        }

        for (int i = 0; i < weatherInfo.pointTimeSeries.timeDefines.size(); i++) {
            TimeDefine timeDefine = weatherInfo.pointTimeSeries.timeDefines.get(i);
            Element weatherLine = document.createElement("line");
            weatherLine.setAttribute("class", "weather-line");
            weatherLine.setAttribute("stroke", "gray");
            weatherLine.setAttribute("stroke-width", timeDefine.dateTime.getHour() == 0 ? "2" : "1");
            weatherLine.setAttribute("x1", Integer.toString(68 + 40 * i));
            weatherLine.setAttribute("x2", Integer.toString(68 + 40 * i));
            weatherLine.setAttribute("y1", "74");
            weatherLine.setAttribute("y2", "106");
            document.getDocumentElement().appendChild(weatherLine);
        }

        Element weatherHeader = document.createElement("text");
        weatherHeader.setAttribute("font-size", "12px");
        weatherHeader.setAttribute("x", "6");
        weatherHeader.setAttribute("y", "96");
        weatherHeader.setTextContent("天気");
        document.getDocumentElement().appendChild(weatherHeader);
    }

    void buildWind(Document document, WeatherInfo weatherInfo) {
        for (int i = 0; i < weatherInfo.areaTimeSeries.wind.size(); i++) {
            Wind wind = weatherInfo.areaTimeSeries.wind.get(i);
            Element windCircle = document.createElement("circle");
            windCircle.setAttribute("cx",
                    Integer.toString(68 + (int) BigDecimal.valueOf(40).multiply(new BigDecimal(i + ".5")).longValue()));
            windCircle.setAttribute("cy", "126");
            windCircle.setAttribute("r", wind.speed == 1 ? "6" : "0");
            windCircle.setAttribute("stroke", wind.speed == 1 ? "darkgray" : "none");
            windCircle.setAttribute("fill", wind.speed == 1 ? "rgb(242,242,255)" : "none");
            document.getDocumentElement().appendChild(windCircle);
        }

        for (int i = 0; i < weatherInfo.areaTimeSeries.wind.size(); i++) {
            Wind wind = weatherInfo.areaTimeSeries.wind.get(i);
            Element windPolygon = document.createElement("polygon");

            int x = 68 + (int) BigDecimal.valueOf(40).multiply(new BigDecimal(i + ".5")).longValue();
            windPolygon.setAttribute("points", wind.speed == 1 || StringUtils.isEmpty(wind.direction) ? ""
                    : String.format("%d,116 %f,136 %d,131 %f,136", x, x - 10 / 1.732, x, x + 10 / 1.732));

            Function<String, Integer> directionToRorate = direction -> {
                switch (direction) {
                    case "北":
                        return 180;
                    case "北東":
                        return 225;
                    case "東":
                        return 270;
                    case "南東":
                        return 315;
                    case "南":
                        return 0;
                    case "南西":
                        return 45;
                    case "西":
                        return 90;
                    case "北西":
                        return 135;
                    default:
                        return 0;
                }
            };
            windPolygon.setAttribute("transform",
                    String.format("rotate(%d %d %d)", directionToRorate.apply(wind.direction), x, 126));

            windPolygon.setAttribute("stroke", wind.speed == 1 ? "none" : "darkgray");

            Function<Integer, String> speedToFill = speed -> {
                switch (speed) {
                    case 2:
                        return "rgb(160,210,255)";
                    case 3:
                        return "rgb(0,65,255)";
                    case 4:
                        return "rgb(250,245,0)";
                    case 5:
                        return "rgb(255,153,0)";
                    case 6:
                        return "rgb(255,40,0)";
                    default:
                        return "none";
                }
            };
            windPolygon.setAttribute("fill", speedToFill.apply(wind.speed));
            document.getDocumentElement().appendChild(windPolygon);
        }

        for (int i = 0; i < weatherInfo.pointTimeSeries.timeDefines.size(); i++) {
            TimeDefine timeDefine = weatherInfo.pointTimeSeries.timeDefines.get(i);
            Element windLine = document.createElement("line");
            windLine.setAttribute("class", "wind-line");
            windLine.setAttribute("stroke", "gray");
            windLine.setAttribute("stroke-width", timeDefine.dateTime.getHour() == 0 ? "2" : "1");
            windLine.setAttribute("x1", Integer.toString(68 + 40 * i));
            windLine.setAttribute("x2", Integer.toString(68 + 40 * i));
            windLine.setAttribute("y1", "110");
            windLine.setAttribute("y2", "142");
            document.getDocumentElement().appendChild(windLine);
        }

        for (int i = 0; i < weatherInfo.areaTimeSeries.wind.size(); i++) {
            Wind wind = weatherInfo.areaTimeSeries.wind.get(i);
            Element windText = document.createElement("text");
            windText.setAttribute("font-size", "8px");
            windText.setAttribute("text-anchor", "middle");
            windText.setAttribute("x",
                    Integer.toString(68 + (int) BigDecimal.valueOf(40).multiply(new BigDecimal(i + ".5")).longValue()));
            windText.setAttribute("y", "146");
            windText.setTextContent(wind.speed != 1 && wind.direction.equals("") ? ""
                    : String.format("（%s）", wind.range.replace(' ', '～')));
            document.getDocumentElement().appendChild(windText);
        }

        Element windHeader = document.createElement("text");
        windHeader.setAttribute("font-size", "12px");
        windHeader.setAttribute("x", "6");
        windHeader.setAttribute("y", "132");
        windHeader.setTextContent("風（m/s）");
        document.getDocumentElement().appendChild(windHeader);
    }

    void buildTemperature(Document document, WeatherInfo weatherInfo) {
        Element temperatureHeader = document.createElement("text");
        temperatureHeader.setAttribute("font-size", "12px");
        temperatureHeader.setAttribute("x", "6");
        temperatureHeader.setAttribute("y", "164");
        temperatureHeader.setTextContent("気温（℃）");
        document.getDocumentElement().appendChild(temperatureHeader);

        int highTemperature = Integer.MIN_VALUE;
        int lowTemperature = Integer.MAX_VALUE;
        for (int i = 0; i < weatherInfo.pointTimeSeries.timeDefines.size(); i++) {
            Integer temperature = weatherInfo.pointTimeSeries.temperature.get(i);
            if (temperature != null) {
                if (highTemperature < temperature) {
                    highTemperature = temperature;
                }
                if (lowTemperature > temperature) {
                    lowTemperature = temperature;
                }
            }

            Integer maxTemperature = weatherInfo.pointTimeSeries.maxTemperature.get(i);
            if (maxTemperature != null && highTemperature < maxTemperature) {
                highTemperature = maxTemperature;
            }

            Integer minTemperature = weatherInfo.pointTimeSeries.minTemperature.get(i);
            if (minTemperature != null && lowTemperature > minTemperature) {
                lowTemperature = minTemperature;
            }
        }

        for (int i = 0; i < weatherInfo.pointTimeSeries.timeDefines.size(); i++) {
            TimeDefine timeDefine = weatherInfo.pointTimeSeries.timeDefines.get(i);
            Element tempLine = document.createElement("line");
            tempLine.setAttribute("class", "temp-line");
            tempLine.setAttribute("stroke", "gray");
            tempLine.setAttribute("stroke-width", timeDefine.dateTime.getHour() == 0 ? "2" : "1");
            tempLine.setAttribute("x1", Integer.toString(68 + 40 * i));
            tempLine.setAttribute("x2", Integer.toString(68 + 40 * i));
            tempLine.setAttribute("y1", "168");
            tempLine.setAttribute("y2", "280");
            document.getDocumentElement().appendChild(tempLine);
        }

        int yHigh = (int) (highTemperature % 5 == 0 ? highTemperature
                : 5 * (Math.floor((double) highTemperature / 5) + 1));
        int yLow = (int) (lowTemperature % 5 == 0 ? lowTemperature : 5 * (Math.ceil((double) lowTemperature / 5) - 1));
        int numberOfLines = (yHigh - yLow) / 5 + 1;
        int lineInterval = 112 / numberOfLines;
        int margin = lineInterval / 2;
        for (int i = 0; i < numberOfLines; i++) {
            Element tempLine = document.createElement("line");
            tempLine.setAttribute("class", "temp-line");
            tempLine.setAttribute("stroke", "gray");
            tempLine.setAttribute("stroke-dasharray", "5");
            tempLine.setAttribute("x1", "60");
            tempLine.setAttribute("x2",
                    Integer.toString(68 + 40 * (weatherInfo.pointTimeSeries.temperature.size() - 1) + 8));
            tempLine.setAttribute("y1", Integer.toString(168 + margin + i * lineInterval));
            tempLine.setAttribute("y2", Integer.toString(168 + margin + i * lineInterval));
            document.getDocumentElement().appendChild(tempLine);

            Element tempText = document.createElement("text");
            tempText.setAttribute("font-size", "12px");
            tempText.setAttribute("text-anchor", "end");
            tempText.setAttribute("x", "58");
            tempText.setAttribute("y", Integer.toString(168 + margin + i * lineInterval + 4));
            tempText.setTextContent(Integer.toString(yHigh - i * 5));
            document.getDocumentElement().appendChild(tempText);
        }

        Element tempPolyline = document.createElement("polyline");
        {
            AtomicInteger i = new AtomicInteger();
            String points = weatherInfo.pointTimeSeries.temperature.stream()
                    .map(temp -> String.format("%d,%f", 68 + 40 * i.getAndIncrement(),
                            168 + margin + (112 - margin - margin) * (1 - (double) (temp - yLow) / (yHigh - yLow))))
                    .collect(Collectors.joining(" "));
            tempPolyline.setAttribute("points", points);
        }
        tempPolyline.setAttribute("stroke", "orange");
        tempPolyline.setAttribute("stroke-width", "2");
        tempPolyline.setAttribute("fill", "none");
        document.getDocumentElement().appendChild(tempPolyline);

        for (int[] lowTempLine : getTempLines(weatherInfo.pointTimeSeries.minTemperature)) {
            Element lowTempline = document.createElement("line");
            lowTempline.setAttribute("stroke", "blue");
            lowTempline.setAttribute("stroke-width", "2");
            lowTempline.setAttribute("x1", Integer.toString(68 + 40 * lowTempLine[1]));
            lowTempline.setAttribute("x2", Integer.toString(68 + 40 * lowTempLine[2]));
            double y = 168 + margin + (112 - margin - margin) * (1 - (double) (lowTempLine[0] - yLow) / (yHigh - yLow));
            lowTempline.setAttribute("y1", Double.toString(y));
            lowTempline.setAttribute("y2", Double.toString(y));
            document.getDocumentElement().appendChild(lowTempline);

            Element lowTempText = document.createElement("text");
            lowTempText.setAttribute("font-size", "12px");
            lowTempText.setAttribute("font-weight", "bold");
            lowTempText.setAttribute("fill", "blue");
            lowTempText.setAttribute("text-anchor", "middle");
            lowTempText.setAttribute("x",
                    Double.toString((double) ((68 + 40 * lowTempLine[1]) + (68 + 40 * lowTempLine[2])) / 2));
            lowTempText.setAttribute("y", Double.toString(y + 14));
            lowTempText.setTextContent(Integer.toString(lowTempLine[0]));
            document.getDocumentElement().appendChild(lowTempText);
        }

        for (int[] highTempLine : getTempLines(weatherInfo.pointTimeSeries.maxTemperature)) {
            Element highTempline = document.createElement("line");
            highTempline.setAttribute("stroke", "red");
            highTempline.setAttribute("stroke-width", "2");
            highTempline.setAttribute("x1", Integer.toString(68 + 40 * highTempLine[1]));
            highTempline.setAttribute("x2", Integer.toString(68 + 40 * highTempLine[2]));
            double y = 168 + margin
                    + (112 - margin - margin) * (1 - (double) (highTempLine[0] - yLow) / (yHigh - yLow));
            highTempline.setAttribute("y1", Double.toString(y));
            highTempline.setAttribute("y2", Double.toString(y));
            document.getDocumentElement().appendChild(highTempline);

            Element highTempText = document.createElement("text");
            highTempText.setAttribute("font-size", "12px");
            highTempText.setAttribute("font-weight", "bold");
            highTempText.setAttribute("fill", "red");
            highTempText.setAttribute("text-anchor", "middle");
            highTempText.setAttribute("x",
                    Double.toString((double) ((68 + 40 * highTempLine[1]) + (68 + 40 * highTempLine[2])) / 2));
            highTempText.setAttribute("y", Double.toString(y - 4));
            highTempText.setTextContent(Integer.toString(highTempLine[0]));
            document.getDocumentElement().appendChild(highTempText);
        }

        int centerX = (136 + 40 * (weatherInfo.pointTimeSeries.temperature.size() - 1)) / 2;

        Element earlyMorningLowTemperatureText = document.createElement("text");
        earlyMorningLowTemperatureText.setAttribute("font-size", "11px");
        earlyMorningLowTemperatureText.setAttribute("fill", "blue");
        earlyMorningLowTemperatureText.setAttribute("text-anchor", "end");
        earlyMorningLowTemperatureText.setAttribute("x", Integer.toString(centerX - 10));
        earlyMorningLowTemperatureText.setAttribute("y", "294");
        earlyMorningLowTemperatureText.setTextContent("―：朝の最低気温");
        document.getDocumentElement().appendChild(earlyMorningLowTemperatureText);

        Element daytimeHighTemperatureText = document.createElement("text");
        daytimeHighTemperatureText.setAttribute("font-size", "11px");
        daytimeHighTemperatureText.setAttribute("fill", "red");
        daytimeHighTemperatureText.setAttribute("text-anchor", "start");
        daytimeHighTemperatureText.setAttribute("x", Integer.toString(centerX + 10));
        daytimeHighTemperatureText.setAttribute("y", "294");
        daytimeHighTemperatureText.setTextContent("―：日中の最高気温");
        document.getDocumentElement().appendChild(daytimeHighTemperatureText);
    }

    private static List<int[]> getTempLines(List<Integer> temps) {
        List<int[]> tempLines = new ArrayList<>();
        for (int loopStart = 0; loopStart < temps.size();) {
            int currentTemp = 0;
            int tempStart = -1;
            int tempEnd = -1;

            for (int i = loopStart;; i++) {
                if (i >= temps.size()) {
                    loopStart = i;
                    break;
                }

                Integer temp = temps.get(i);
                if (temp != null) {
                    if (tempStart == -1) {
                        currentTemp = temp;
                        tempStart = i;
                        continue;
                    }
                    if (currentTemp != temp) {
                        tempEnd = i - 1;
                        loopStart = i;
                        break;
                    }
                } else {
                    if (tempStart != -1) {
                        tempEnd = i - 1;
                        loopStart = i + 1;
                        break;
                    }
                }
            }

            if (tempStart != -1 && tempEnd != -1) {
                tempLines.add(new int[] {currentTemp, tempStart, tempEnd});
            }
        }
        return tempLines;
    }

    /**
     * The icon document is not thread-safe. Should be used with synchronized.
     */
    private Document getWeatherIcon(String weatherName, boolean isDaytime) {
        return weatherIconCache.computeIfAbsent(jmaHttpClient.getWeatherIconUrl(weatherName, isDaytime),
                iconUrl -> jmaHttpClient.getWeatherIcon(iconUrl));
    }

}
