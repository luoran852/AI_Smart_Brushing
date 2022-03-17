package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedbackDateReq {
    private int year;
    private int month;
    private int day;
    private int hour;
}
