package com.jonyshev.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PagingInfo {
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
}