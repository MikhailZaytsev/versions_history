package ru.plantarum.core.uploading.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@ToString
public class InvalidParse {
    private Integer row;
    private String entityName;
    private String message;

}
