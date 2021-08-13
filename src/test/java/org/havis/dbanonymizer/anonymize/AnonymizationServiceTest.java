package org.havis.dbanonymizer.anonymize;

import org.havis.dbanonymizer.TestDataCreator;
import org.havis.dbanonymizer.dataconfig.ColumnType;
import org.havis.dbanonymizer.dataconfig.TableColumnSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(args = {"classpath:test-table-column-specification.csv"})
@Transactional
class AnonymizationServiceTest {
    private static final String CONTROL_STRING = "secret";

    @Autowired
    private AnonymizationService anonymizationService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataCreator testDataCreator;

    @BeforeEach
    void setUp() {
        testDataCreator.insertTestData(CONTROL_STRING);
    }

    @Test
    public void testAnonymizationChangesData() {
        anonymizationService.anonymize(createTestConfiguration());

        assertControlStringDoesNotOccurInTestTable();
    }

    private void assertControlStringDoesNotOccurInTestTable() {
        final String sql = String.format("SELECT %s FROM %s", TestDataCreator.TEST_COLUMN_NAME, TestDataCreator.TEST_TABLE_NAME);
        final List<String> values = jdbcTemplate.queryForList(sql, new HashMap<>(), String.class);

        assertTrue(values.stream().noneMatch(v -> v.contains(CONTROL_STRING)));
    }

    private List<TableColumnSpecification> createTestConfiguration() {
        final List<TableColumnSpecification> configuration = new ArrayList<>();

        configuration.add(createTestTableColumnConfiguration());

        return configuration;
    }

    private TableColumnSpecification createTestTableColumnConfiguration() {
        final TableColumnSpecification spec = new TableColumnSpecification();

        spec.setTableName(TestDataCreator.TEST_TABLE_NAME);
        spec.setPrimaryKey(TestDataCreator.TEST_PRIMARY_KEY_NAME);
        spec.setColumnName(TestDataCreator.TEST_COLUMN_NAME);
        spec.setColumnType(ColumnType.FIRST_NAME);

        return spec;
    }
}