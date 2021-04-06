package app.connectivity.db;

import app.data.connectivity.db.JmaApiLog;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JmaApiLogDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<JmaApiLog> getLatestLogs() {
        return jdbcTemplate.query("SELECT * FROM jma_api_log ORDER BY 1 DESC LIMIT 10", (rs, rowNum) -> {
            return new JmaApiLog(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4));
        });
    }

    public void insertLog(long timestamp, String areaCode, String clientIpAddress, String userAgent) {
        jdbcTemplate.update("INSERT INTO jma_api_log VALUES (?, ?, ?, ?)", timestamp, areaCode, clientIpAddress,
                StringUtils.left(userAgent, 255));
    }

}
