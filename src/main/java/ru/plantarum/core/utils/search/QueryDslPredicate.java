package ru.plantarum.core.utils.search;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryDslPredicate<T> {
    private SearchCriteria criteria;

    private PathBuilder<T> entityPath;

    private final Class<T> type;


    public QueryDslPredicate(SearchCriteria criteria, PathBuilder<T> entityPath, Class<T> type) {
        this.criteria = criteria;
        this.entityPath = entityPath;
        this.type = type;
    }

    public BooleanExpression getPredicate() {
        String key = criteria.getKey();
        Class<?> valueType = getFieldType(key, this.type);
        Path<?> path = getPath(key, valueType);

        if (path instanceof NumberPath) {

            String valueStr = criteria.getValue().toString();
            NumberPath numberPath = (NumberPath) path;
            return numberPath.eq(getSingleNumericValue(valueType, valueStr));

        } else if (path instanceof StringPath) {

            String value = criteria.getValue().toString();
            return ((StringPath) path).containsIgnoreCase(value);

        } else if (path instanceof DateTimePath) {
            String value = criteria.getValue().toString();
            if (value.length() < 10)
                return null;
            ZoneOffset offset = ZoneOffset.of("+03:00");
            String timeChar = "00:00:00";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (value.contains(".")) {
                dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            }
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDate date = LocalDate.parse(value, dateFormatter);
            LocalTime time = LocalTime.parse(timeChar, timeFormatter);
            OffsetDateTime before = OffsetDateTime.of(date, time, offset);
            timeChar = "23:59:59";
            time = LocalTime.parse(timeChar, timeFormatter);
            OffsetDateTime after = OffsetDateTime.of(date, time, offset);
            return (((DateTimePath) path).between(before, after));
        }
        return null;
    }

    /**
     * @param valueType
     * @param valueStr
     * @return
     */
    private List<Number> getMultipleNumericValues(Class<?> valueType, String valueStr) {
        return Arrays.stream(valueStr.split("_"))
                .map(s -> getNumericValue(valueType, s)).collect(Collectors.toList());
    }

    /**
     * @param valueType
     * @param valueStr
     * @return
     */
    private Number getSingleNumericValue(Class<?> valueType, String valueStr) {
        Number value;
        value = getNumericValue(valueType, valueStr);
        return value;
    }

    private Path<?> getPath(String key, Class<?> type) {
        if (Integer.class.equals(type)) {
            return entityPath.getNumber(key, Integer.class);
        } else if (Long.class.equals(type)) {
            return entityPath.getNumber(key, Long.class);
        } else if (Short.class.equals(type)) {
            return entityPath.getNumber(key, Short.class);
        } else if (Float.class.equals(type)) {
            return entityPath.getNumber(key, Float.class);
        } else if (Double.class.equals(type)) {
            return entityPath.getNumber(key, Double.class);
        } else if (String.class.equals(type)) {
            return entityPath.getString(key);
        } else if (OffsetDateTime.class.equals(type)) {
            return entityPath.getDateTime(key, OffsetDateTime.class);
        } else if (LocalDate.class.equals(type)) {
            return entityPath.getDate(key, LocalDate.class);
        } else if (Boolean.class.equals(type)) {
            return entityPath.getBoolean(key);
        } else if (BigDecimal.class.equals(type)) {
            return entityPath.getNumber(key, BigDecimal.class);
        }
        throw new IllegalArgumentException(
                "can't find path for " + type.getName() + " by field " + key);
    }

    private Class<?> getFieldType(String fieldName, Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field.getType();
            }
        }
        if (fieldName.contains(".")) {
            String[] complexField = fieldName.split("\\.");
            Class<?> classOfComplexType = getFieldType(complexField[0], type);
            return getFieldType(fieldName.replaceFirst(complexField[0].concat("."), ""),
                    classOfComplexType);
        } else {
            throw new IllegalArgumentException("can't find " + fieldName + " in class " + type.getName());
        }
    }

    private Number getNumericValue(Class<?> type, String value) {
        if (Integer.class.equals(type)) {
            return Integer.parseInt(value);
        } else if (Long.class.equals(type)) {
            return Long.parseLong(value);
        } else if (Short.class.equals(type)) {
            return Short.parseShort(value);
        } else if (Float.class.equals(type)) {
            return Float.parseFloat(value);
        } else if (Double.class.equals(type)) {
            return Double.parseDouble(value);
        } else if (BigDecimal.class.equals(type)) {
            return new BigDecimal(value);
        }
        throw new IllegalArgumentException("can't parse value " + value + " of type " + type.getName());
    }
}
