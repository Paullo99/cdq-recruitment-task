package com.cdq.recruitmenttask.util;

import com.cdq.recruitmenttask.dto.FieldChangeResult;
import com.cdq.recruitmenttask.model.FieldChangeClassification;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class FieldComparator {

    private static final double THRESHOLD_LOW = 0.4;
    private static final double THRESHOLD_HIGH = 0.9;

    public static FieldChangeResult compare(String field, String oldVal, String newVal) {
        if (isEmpty(oldVal) && !isEmpty(newVal)) {
            return new FieldChangeResult(field, null, newVal, FieldChangeClassification.ADDED);
        }

        if (!isEmpty(oldVal) && isEmpty(newVal)) {
            return new FieldChangeResult(field, oldVal, null, FieldChangeClassification.DELETED);
        }

        if ((oldVal == null && newVal == null) || (oldVal != null && oldVal.equals(newVal))) {
            return new FieldChangeResult(field, oldVal, newVal, FieldChangeClassification.HIGH);
        }

        double similarity = calculateSimilarity(oldVal, newVal);
        FieldChangeClassification classification;

        if (similarity < THRESHOLD_LOW) {
            classification = FieldChangeClassification.LOW;
        } else if (similarity < THRESHOLD_HIGH) {
            classification = FieldChangeClassification.MEDIUM;
        } else {
            classification = FieldChangeClassification.HIGH;
        }

        return new FieldChangeResult(field, oldVal, newVal, classification);
    }

    private static double calculateSimilarity(String oldVal, String newVal) {
        if (oldVal == null || newVal == null) {
            return 0.0;
        }

        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int distance = levenshtein.apply(oldVal, newVal);
        int maxLength = Math.max(oldVal.length(), newVal.length());

        double dissimilarity = (double) distance / maxLength;
        return 1.0 - dissimilarity;
    }

    private static boolean isEmpty(String val) {
        return val == null || val.isBlank();
    }
}