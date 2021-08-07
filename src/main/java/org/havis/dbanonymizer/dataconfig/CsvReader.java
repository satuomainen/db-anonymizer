package org.havis.dbanonymizer.dataconfig;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class CsvReader {

    public static List<TableColumnSpecification> readCsvConfig(final String fileName) throws IOException {
        return readCsvConfig(new FileReader(ResourceUtils.getFile(fileName)));
    }

    public static List<TableColumnSpecification> readCsvConfig(final Reader reader) {
        return new CsvToBeanBuilder<TableColumnSpecification>(reader)
            .withType(TableColumnSpecification.class)
            .build()
            .parse();
    }
}
