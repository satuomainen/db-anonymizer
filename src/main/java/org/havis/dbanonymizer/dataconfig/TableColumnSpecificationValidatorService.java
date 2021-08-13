package org.havis.dbanonymizer.dataconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TableColumnSpecificationValidatorService {
    private static final Logger LOG = LoggerFactory.getLogger(TableColumnSpecificationValidatorService.class);

    private static final String VALIDATION_QUERY_TEMPLATE = "SELECT %s, %s FROM %s LIMIT 1";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TableColumnSpecificationValidatorService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional(readOnly = true)
    public void validateConfiguration(final List<TableColumnSpecification> configuration) {
        configuration.forEach(this::validateConfiguration);
    }

    private void validateConfiguration(final TableColumnSpecification configuration) {
        try {
            namedParameterJdbcTemplate.query(createValidationQuery(configuration), resultSet -> {});
        } catch (DataAccessException e) {
            LOG.error("Failed to validate columns '{}', '{}' for table '{}': {}",
                configuration.getPrimaryKey(),
                configuration.getColumnName(),
                configuration.getTableName(),
                e.getMessage());

            throw e;
        }
    }

    private String createValidationQuery(TableColumnSpecification configuration) {
        // Vulnerable to SQL injection if the specification input is compromised
        return String.format(VALIDATION_QUERY_TEMPLATE,
            configuration.getPrimaryKey(),
            configuration.getColumnName(),
            configuration.getTableName());
    }
}
