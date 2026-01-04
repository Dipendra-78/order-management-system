package com.ordermanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PageResponseDto<T> {

    private List<T> data;

    private int page;

    private int size;

    private long totalElement;

    private int totalPages;
    

}
