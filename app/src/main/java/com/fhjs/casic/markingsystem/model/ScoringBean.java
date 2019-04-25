package com.fhjs.casic.markingsystem.model;

import cn.bmob.v3.BmobObject;

public class ScoringBean extends BmobObject {
    private String evaluationID;//面试或许绩效考核对象的id
    private String evaluationName;//  name
    private String userID; // 打分人的id
    private String userName;// name
    private Integer markType;//打分类型   个人面试-1  or  绩效考核-2
    private String conclusion;//结论
    private Float score_A;//仪表形象得分
    private Float score_B;//表达能力得分
    private Float score_C;//工作态度得分
    private Float score_D;//岗位能力得分
    private Float score_E;//综合素质得分
    private String date;

    public String getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(String evaluationID) {
        this.evaluationID = evaluationID;
    }

    public String getEvaluationName() {
        return evaluationName;
    }

    public void setEvaluationName(String evaluationName) {
        this.evaluationName = evaluationName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getMarkType() {
        return markType;
    }

    public void setMarkType(Integer markType) {
        this.markType = markType;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public Float getScore_A() {
        return score_A;
    }

    public void setScore_A(Float score_A) {
        this.score_A = score_A;
    }

    public Float getScore_B() {
        return score_B;
    }

    public void setScore_B(Float score_B) {
        this.score_B = score_B;
    }

    public Float getScore_C() {
        return score_C;
    }

    public void setScore_C(Float score_C) {
        this.score_C = score_C;
    }

    public Float getScore_D() {
        return score_D;
    }

    public void setScore_D(Float score_D) {
        this.score_D = score_D;
    }

    public Float getScore_E() {
        return score_E;
    }

    public void setScore_E(Float score_E) {
        this.score_E = score_E;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ScoringBean{" +
                "evaluationID='" + evaluationID + '\'' +
                ", evaluationName='" + evaluationName + '\'' +
                ", userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", markType=" + markType +
                ", conclusion='" + conclusion + '\'' +
                ", score_A=" + score_A +
                ", score_B=" + score_B +
                ", score_C=" + score_C +
                ", score_D=" + score_D +
                ", score_E=" + score_E +
                ", date='" + date + '\'' +
                '}';
    }
}
