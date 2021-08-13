package org.havis.dbanonymizer.anonymize;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.havis.dbanonymizer.dataconfig.ColumnType;

import java.util.Locale;
import java.util.Optional;

public class AnonymousValueFactory {
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("fi-FI");
    private static final int RANDOM_STRING_LENGTH = 16;

    // TODO: make local configurable via environment variable
    private static final Faker faker = Faker.instance(DEFAULT_LOCALE);
    
    public static String getValueOrRandomString(final ColumnType columnType) {
        return Optional
            .ofNullable(typeToValueOrNull(columnType))
            .orElseGet(() -> getRandomString(RANDOM_STRING_LENGTH));
    }

    private static String typeToValueOrNull(final ColumnType columnType) {
        switch (columnType) {
            case FIRST_NAME:
                return faker.name().firstName();
            case LAST_NAME:
                return faker.name().lastName();
            case USERNAME:
                return faker.name().username();
            case RANDOM_STRING:
                return getRandomString(RANDOM_STRING_LENGTH);
            default:
                return null;
        }
    }

    private static String getRandomString(final int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }
}
