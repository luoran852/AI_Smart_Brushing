package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedbackDetailRes {
    private String brushDate;
    private String brushTime;
    private int score;
    private String feedbackMsg;
}
