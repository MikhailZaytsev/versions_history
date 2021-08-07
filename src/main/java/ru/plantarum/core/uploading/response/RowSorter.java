package ru.plantarum.core.uploading.response;

import java.util.Comparator;

public class RowSorter implements Comparator<InvalidParse> {

    @Override
    public int compare(InvalidParse o1, InvalidParse o2) {
        return o2.getRow().compareTo(o1.getRow());
    }
}
