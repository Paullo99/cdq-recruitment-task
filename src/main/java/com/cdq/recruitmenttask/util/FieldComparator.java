package com.cdq.recruitmenttask.util;

import com.cdq.recruitmenttask.dto.FieldChangeResult;
import com.cdq.recruitmenttask.model.FieldChangeClassification;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class FieldComparator {

    private static final double THRESHOLD_LOW = 0.4;
    private static final double THRESHOLD_HIGH = 0.9;

    public static FieldChangeResult compare(String field, String oldVal, String newVal) {
        if (isAdded(oldVal, newVal)) {
            return new FieldChangeResult(field, null, newVal, FieldChangeClassification.ADDED);
        }

        if (isDeleted(oldVal, newVal)) {
            return new FieldChangeResult(field, oldVal, null, FieldChangeClassification.DELETED);
        }

        if (isUnchangedEmpty(oldVal, newVal)) {
            return new FieldChangeResult(field, oldVal, newVal, FieldChangeClassification.HIGH);
        }

        double similarity = calculateSimilarity(oldVal, newVal);
        FieldChangeClassification classification = classifyBySimilarity(similarity);

        return new FieldChangeResult(field, oldVal, newVal, classification);
    }

    private static boolean isAdded(String oldVal, String newVal) {
        return StringUtils.isEmpty(oldVal) && !StringUtils.isEmpty(newVal);
    }

    private static boolean isDeleted(String oldVal, String newVal) {
        return !StringUtils.isEmpty(oldVal) && StringUtils.isEmpty(newVal);
    }

    private static boolean isUnchangedEmpty(String oldVal, String newVal) {
        return StringUtils.isEmpty(oldVal) && StringUtils.isEmpty(newVal);
    }

    private static FieldChangeClassification classifyBySimilarity(double similarity) {
        if (similarity < THRESHOLD_LOW) {
            return FieldChangeClassification.LOW;
        } else if (similarity < THRESHOLD_HIGH) {
            return FieldChangeClassification.MEDIUM;
        } else {
            return FieldChangeClassification.HIGH;
        }
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
}