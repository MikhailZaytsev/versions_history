package ru.plantarum.core.web.paging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Page<T> {

    public Page(List<T> data) {
        this.data = data;
    }

    public Page(org.springframework.data.domain.Page<T> page) {
        this.data = page.getContent();
        this.recordsFiltered= page.getTotalElements();
        this.recordsTotal=page.getTotalElements();
    }

    private List<T> data;
    private long recordsFiltered;
    private long recordsTotal;
    private int draw;

}
