package org.havis.dbanonymizer;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class TestDataCreator {
    public static final String TEST_TABLE_NAME = "A";
    public static final String TEST_PRIMARY_KEY_NAME = "ID";
    public static final String TEST_COLUMN_NAME = "B";
    public static final String TEST_PARAM_NAME = "b";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void insertTestData(final String controlString) {
        final String sql = String.format("INSERT INTO %s (%s) VALUES (:%s)", TEST_TABLE_NAME, TEST_COLUMN_NAME, TEST_PARAM_NAME);
        jdbcTemplate.batchUpdate(sql, createTestDataParameters(controlString));
    }

    public void clearTestData() {
        jdbcTemplate.update("TRUNCATE TABLE " + TEST_TABLE_NAME, new HashMap<>());
    }

    private SqlParameterSource[] createTestDataParameters(final String controlString) {
        final int numberOfParameters = 10;
        List<SqlParameterSource> params = new ArrayList<>(numberOfParameters);

        IntStream.range(0, numberOfParameters).forEach(i -> params.add(createSqlParameters(createSecretRandomValue(controlString))));

        return params.toArray(new SqlParameterSource[0]);
    }

    private SqlParameterSource createSqlParameters(String value) {
        MapSqlParameterSource sqlParameters = new MapSqlParameterSource();

        sqlParameters.addValue(TEST_PARAM_NAME, value);

        return sqlParameters;
    }

    public String createSecretRandomValue(final String controlString) {
        return controlString + RandomStringUtils.randomAlphanumeric(8);
    }
}
