package org.havis.dbanonymizer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(args = {"classpath:test-table-column-specification.csv"})
class DbAnonymizerApplicationTests {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void contextLoads() {
    }

    @Test
    void testFilenameArgumentIsRequired() {
        CommandLineRunner runner = applicationContext.getBean(CommandLineRunner.class);

        assertThrows(IllegalArgumentException.class, () -> runner.run());
    }
}
