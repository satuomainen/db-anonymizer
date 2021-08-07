package org.havis.dbanonymizer;

import org.havis.dbanonymizer.dataconfig.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
public class DbAnonymizerApplication implements CommandLineRunner {
    private static Logger LOG = LoggerFactory.getLogger(DbAnonymizerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DbAnonymizerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        validateArgs(args);
        validateConfiguration(args[0]);
    }

    private void validateArgs(String... args) {
        LOG.info("Validating command line arguments");
        if (args.length < 1) {
            LOG.error("CSV file name is required as a command line argument. Cannot continue.");
            throw new IllegalArgumentException("CSV file name is required");
        }
    }

    private void validateConfiguration(final String csvFileName) {
        LOG.info("Validating anonymization specification: {}", csvFileName);

        try {
            CsvReader.readCsvConfig(csvFileName);
        } catch (FileNotFoundException e) {
            LOG.error("CSV file '{}' does not exist", csvFileName);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOG.error("I/O error trying when trying access CSV file '{}': {}", csvFileName, e.getMessage());
            throw new RuntimeException(e.getCause());
        } catch (RuntimeException e) {
            LOG.error("Reading CSV file '{}' failed: {}", csvFileName, e.getMessage());
            throw new RuntimeException(e.getCause());
        }
    }
}
