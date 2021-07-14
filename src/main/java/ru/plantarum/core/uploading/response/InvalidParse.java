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
    private String error;
    private int row;

    public InvalidParse(String entityName, String error, int row) {
        this.entityName = entityName;
        this.error = error;
        this.row = row;
    }
}
