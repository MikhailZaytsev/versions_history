package ru.plantarum.core.utils.search;

import lombok.Data;

@Data
public class SearchCriteria {
    public final static String OPERATION_EQUALS = ":";

    private final String key;
    private final String operation;
    private final Object value;


}
