package org.havis.dbanonymizer;

import org.havis.dbanonymizer.anonymize.AnonymizationService;
import org.havis.dbanonymizer.dataconfig.CsvReader;
import org.havis.dbanonymizer.dataconfig.TableColumnSpecification;
import org.havis.dbanonymizer.dataconfig.TableColumnSpecificationValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class DbAnonymizerApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(DbAnonymizerApplication.class);

    private final AnonymizationService anonymizationService;
    private final TableColumnSpecificationValidatorService tableColumnSpecificationValidatorService;

    public DbAnonymizerApplication(
        AnonymizationService anonymizationService,
        TableColumnSpecificationValidatorService tableColumnSpecificationValidatorService
    ) {
        this.anonymizationService = anonymizationService;
        this.tableColumnSpecificationValidatorService = tableColumnSpecificationValidatorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DbAnonymizerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        validateArgs(args);

        final List<TableColumnSpecification> configuration = readConfiguration(args[0]);

        tableColumnSpecificationValidatorService.validateConfiguration(configuration);
        anonymizationService.anonymize(configuration);
    }

    private void validateArgs(String... args) {
        LOG.info("Validating command line arguments");
        if (args.length < 1) {
            LOG.error("CSV file name is required as a command line argument. Cannot continue.");
            throw new IllegalArgumentException("CSV file name is required");
        }
    }

    private List<TableColumnSpecification> readConfiguration(final String csvFileName) {
        LOG.info("Validating anonymization specification: {}", csvFileName);

        try {
            return CsvReader.readCsvConfig(csvFileName);
        } catch (FileNotFoundException e) {
            LOG.error("CSV file '{}' does not exist", csvFileName);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOG.error("I/O error trying when trying access CSV file '{}': {}", csvFileName, e.getMessage());
            throw new RuntimeException(e.getCause());
        } catch (RuntimeException e) {
            LOG.error("Reading CSV file '{}' failed: {}", csvFileName, e.getMessage());
            throw e;
        }
    }
}
