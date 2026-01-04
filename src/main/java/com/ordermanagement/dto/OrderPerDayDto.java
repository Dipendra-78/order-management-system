package com.ordermanagement.dto;

import java.time.LocalDate;

public record OrderPerDayDto(LocalDate date, Long count) {

}
