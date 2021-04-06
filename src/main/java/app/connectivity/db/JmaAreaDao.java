package app.connectivity.db;

import app.data.connectivity.web.jma.Area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JmaAreaDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Optional<Area> getArea(String areaCode) {
        return jdbcTemplate.query("SELECT * FROM jma_area WHERE area_code=?", rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(new Area(rs.getString(1), rs.getString(2), rs.getString(3)));
        }, areaCode);
    }

    public List<Area> getAllArea() {
        return jdbcTemplate.query("SELECT area_code, pref_name, area_name FROM jma_area ORDER BY 1", (rs, rowNum) -> {
            return new Area(rs.getString(1), rs.getString(2), rs.getString(3));
        });
    }

}
