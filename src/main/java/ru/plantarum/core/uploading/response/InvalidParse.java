package ru.plantarum.core.uploading.response;

import lombok.*;

//Is it necessary?
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@EqualsAndHashCode
//@Setter
//@Getter
//@ToString
public class InvalidParse {

    private String entityName;
    private String message;

    public InvalidParse(String entityName, String message) {
        this.entityName = entityName;
        this.message = message;
    }

}
