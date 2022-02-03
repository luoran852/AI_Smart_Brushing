package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedbackRes {
    private int idx;
    private int userIdx;
    private String feedbackMsg;
    private String mappingVideoUrl;
    private String date;
}