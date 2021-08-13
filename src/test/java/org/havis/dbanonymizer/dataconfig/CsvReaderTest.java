package org.havis.dbanonymizer.dataconfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvReaderTest {

    @Test
    public void testReadCsvConfig_validConfiguration_shouldPass() {
        final List<TableColumnSpecification> specifications = CsvReader
            .readCsvConfig(getReaderWithTitleForContent("a,id,b,FIRST_NAME"));

        assertThat(specifications, hasSize(1));

        final TableColumnSpecification spec = specifications
            .stream()
            .findFirst()
            .orElseThrow(IllegalStateException::new);

        assertEquals("a", spec.getTableName());
        assertEquals("b", spec.getColumnName());
        assertEquals(ColumnType.FIRST_NAME, spec.getColumnType());
    }

    @Test
    public void testReadCsvConfig_fileNotFound_shouldFail() {
        assertThrows(FileNotFoundException.class, () -> {
            CsvReader.readCsvConfig("notexistingfile.xyz");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "a,id,b,UNKNOWN_ENUM",
        ",id,b,FIRST_NAME",
        "a,id,,FIRST_NAME",
        "a,b,,"
    })
    public void testReadCsvConfig_invalidConfig_shouldFail(String contentLine) {
        assertThrows(RuntimeException.class, () -> CsvReader.readCsvConfig(getReaderWithTitleForContent(contentLine)));
    }

    @Test
    public void testReadCsvConfig_missingHeaderLine_shouldFail() {
        assertThrows(RuntimeException.class, () -> CsvReader.readCsvConfig(getInvalidConfigContentsMissingHeaderRow()));
    }

    private Reader getReaderWithTitleForContent(final String contentLine) {
        final String csvContent = Stream.of(
                "Table,PrimaryKey,Column,Type",
                contentLine)
            .collect(Collectors.joining(System.lineSeparator()));

        return new StringReader(csvContent);
    }

    private Reader getInvalidConfigContentsMissingHeaderRow() {
        final String csvContent = Stream.of("a,id,b,FIRST_NAME").collect(Collectors.joining(System.lineSeparator()));

        return new StringReader(csvContent);
    }
}