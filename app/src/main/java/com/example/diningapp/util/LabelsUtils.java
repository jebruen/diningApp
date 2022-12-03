package com.example.diningapp.util;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LabelsUtils {
    public static final String CONFLICT_LABEL_MESSAGE    = "The label conflicts with existing existingLabels!";
    public static final String EXIST_LABEL_MESSAGE       = "The label exist!";
    public static final String UPDATE_SUCCESSFUL_MESSAGE = "Update Successfully!";

    public static final Map<String, List<String>> labelMap
            = ImmutableMap.of(
                    "vegan", Collections.singletonList("meat"), 
            "vegetarian", Collections.singletonList("meat"),
            "meat", Arrays.asList("vegan", "vegetarian"),
            "hot", Collections.singletonList("cold"),
            "cold", Collections.singletonList("hot")
    );
    
    public static boolean ifConflict(List<String> existingLabels, String labelToAdd) {
        existingLabels = existingLabels.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<String> conflictingLabels = labelMap.get(labelToAdd.toLowerCase());

        if (Objects.nonNull(conflictingLabels)) {
            for (String conflictLabel: conflictingLabels) {
                if (existingLabels.contains(conflictLabel.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }
}
