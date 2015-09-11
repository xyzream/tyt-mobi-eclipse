package com.betasoft.ToyotaMobi.JavaBeans;

import java.io.Serializable;

public class FAQsBean implements Serializable{
public String faqQuestion,faqAnswer;
public FAQsBean(String faqQuestion, String faqAnswer) 
{
this.faqQuestion = faqQuestion;
this.faqAnswer = faqAnswer;
}
}
