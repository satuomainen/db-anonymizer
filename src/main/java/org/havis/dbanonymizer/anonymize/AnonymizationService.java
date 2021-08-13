package org.havis.dbanonymizer.anonymize;

import org.havis.dbanonymizer.dataconfig.TableColumnSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AnonymizationService {
    private static final Logger LOG = LoggerFactory.getLogger(AnonymizationService.class);

    private static final String ID_QUERY_TEMPLATE = "SELECT `%s` FROM `%s`";
    private static final String ANONYMIZATION_STATEMENT_TEMPLATE = "UPDATE `%s` SET `%s` = :anonymizedValue WHERE `%s` = :id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AnonymizationService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void anonymize(final List<TableColumnSpecification> configuration) {
        // TODO: To avoid transaction log getting too big and slowing things down,
        //  run the anonymization in smaller batches each in their own transaction.

        LOG.info("Starting anonymization");

        final Map<String, Set<Object>> tableNameToAllPrimaryKeys = createPrimaryKeyMap(configuration);

        configuration.forEach(specification -> {
            anonymize(tableNameToAllPrimaryKeys.get(specification.getTableName()), specification);
        });
    }

    private Map<String, Set<Object>> createPrimaryKeyMap(final List<TableColumnSpecification> configuration) {
        final Map<String, Set<Object>> primaryKeyMap = new HashMap<>();

        configuration.forEach(specification -> primaryKeyMap.put(
            specification.getTableName(),
            findAllPrimaryKeyValues(specification)));

        return primaryKeyMap;
    }

    private void anonymize(final Set<Object> allPrimaryKeys, final TableColumnSpecification specification) {
        LOG.info("Anonymizing '{}.{}' as {}",
            specification.getTableName(),
            specification.getColumnName(),
            specification.getColumnType().name());

        allPrimaryKeys.forEach(id -> anonymizeForId(id, specification));
    }

    private void anonymizeForId(Object id, TableColumnSpecification specification) {
        final String anonymizationStatement = String.format(
            ANONYMIZATION_STATEMENT_TEMPLATE,
            specification.getTableName(),
            specification.getColumnName(),
            specification.getPrimaryKey());

        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("anonymizedValue", AnonymousValueFactory.getValueOrRandomString(specification.getColumnType()));
        parameters.addValue("id", id);

        jdbcTemplate.update(anonymizationStatement, parameters);
    }

    private Set<Object> findAllPrimaryKeyValues(final TableColumnSpecification specification) {
        final Set<Object> ids = new HashSet<>();

        jdbcTemplate.query(createIdQuery(specification), new MapSqlParameterSource(), row -> {
            ids.add(row.getObject(1));
        });

        return ids;
    }

    private String createIdQuery(final TableColumnSpecification specification) {
        // Vulnerable to SQL injection if the specification input is compromised
        return String.format(ID_QUERY_TEMPLATE, specification.getPrimaryKey(), specification.getTableName());
    }
}
