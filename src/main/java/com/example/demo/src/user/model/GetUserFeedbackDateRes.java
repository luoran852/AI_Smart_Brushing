package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedbackDateRes {
    private int idx;
    private String brushDate;
    private String exactTime;
    private int score;
    private int brushtime;
    private String feedbackMsg;
}