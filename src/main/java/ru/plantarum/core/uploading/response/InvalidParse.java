package ru.plantarum.core.uploading.response;

import lombok.*;

@Builder
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@ToString
public class InvalidParse {

    private String entityName;
    private String message;
    private int cellIndex;

    public InvalidParse(String entityName, String message) {
        this.entityName = entityName;
        this.message = message;
    }

    public InvalidParse(String entityName, String message, int cellIndex) {
        this.entityName = entityName;
        this.message = message;
        this.cellIndex = cellIndex;
    }
}
