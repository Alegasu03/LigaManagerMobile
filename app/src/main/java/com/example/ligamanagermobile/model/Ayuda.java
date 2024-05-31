package com.example.ligamanagermobile.model;

public class Ayuda {
    private String question;
    private String answer;

    public Ayuda(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
