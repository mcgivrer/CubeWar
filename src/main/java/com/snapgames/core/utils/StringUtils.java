package com.snapgames.core.utils;

import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {

    /**
     * Create a String from all the {@link java.util.Map.Entry} of a {@link Map}.
     * <p>
     * the String is composed on the format "[ entry1:value1 | entry2:value2 ]"
     * where, in e the map :
     *
     * <pre>
     * Maps.of("1_entry1","value1","2_entry2","value2",...);
     * </pre>
     * <p>
     * this will sort the Entry on the `[9]` from the `[9]_[keyname]` key name.
     *
     * @param attributes the {@link Map} of value to be displayed.
     * @param start      the character to start the string with.
     * @param end        the character to end the string with.
     * @param delimiter  the character to seperate each entry.
     * @return a concatenated {@link String} based on the {@link Map}
     * {@link java.util.Map.Entry}.
     */
    public static String prepareStatsString(Map<String, Object> attributes, String start, String delimiter,
                                            String end) {
        return start + attributes.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> {
            String value = "";
            switch (entry.getValue().getClass().getSimpleName()) {
                case "Double", "double", "Float", "float" -> {
                    value = String.format("%04.2f", entry.getValue());
                }
                case "Integer", "int" -> {
                    value = String.format("%5d", entry.getValue());
                }
                default -> {
                    value = entry.getValue().toString();
                }
            }
            return entry.getKey().substring(((String) entry.getKey().toString()).indexOf('_') + 1)
                    + ":"
                    + value;
        }).collect(Collectors.joining(delimiter)) + end;
    }
}
