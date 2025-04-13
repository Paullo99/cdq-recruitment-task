package com.cdq.recruitmenttask.util;

import com.cdq.recruitmenttask.model.FieldChangeClassification;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FieldComparatorTest {

    @ParameterizedTest(name = "[{index}] {0} -> {1} = {2}")
    @MethodSource("provideFieldComparisonExamples")
     void testCompare_shouldClassifyCorrectly(String oldVal, String newVal, FieldChangeClassification expected) throws InterruptedException {
        var result = FieldComparator.compare("field", oldVal, newVal);
        assertEquals(expected, result.classification());
    }

    static Stream<Arguments> provideFieldComparisonExamples() {
        return Stream.of(
                Arguments.of("ABCD", "BCD", FieldChangeClassification.MEDIUM),
                Arguments.of("ABCD", "BWD", FieldChangeClassification.MEDIUM),
                Arguments.of("ABCDEFG", "CFG", FieldChangeClassification.MEDIUM),
                Arguments.of("ABCABC", "ABC", FieldChangeClassification.MEDIUM),
                Arguments.of("ABCDEFGH", "TDD", FieldChangeClassification.LOW),

                Arguments.of(null, "new", FieldChangeClassification.ADDED),
                Arguments.of("old", null, FieldChangeClassification.DELETED),
                Arguments.of("old", "", FieldChangeClassification.DELETED),
                Arguments.of("", "new", FieldChangeClassification.ADDED),
                Arguments.of("", "", FieldChangeClassification.HIGH),
                Arguments.of(null, null, FieldChangeClassification.HIGH),
                Arguments.of("same", "same", FieldChangeClassification.HIGH),
                Arguments.of("ABCDEFGHIJK", "ABCDEFGHIJ", FieldChangeClassification.HIGH),
                Arguments.of("Text", "text", FieldChangeClassification.MEDIUM)
        );
    }
}
