package ru.plantarum.core.uploading.excel;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ExcelEntity {
    private List<String> products = new ArrayList<>();
    private List<String> bareCodes = new ArrayList<>();
    private List<String> priceBuys = new ArrayList<>();
    private List<String> priceSales = new ArrayList<>();
}
