package ru.plantarum.core.cut;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@ToString
/*
 * CounterAgent without unnecessary fields for easier parsing to JSON
 * */
public class CounterAgentCut {
    private Long id;
    private String name;
    private String phone;
    private String profile;
}
