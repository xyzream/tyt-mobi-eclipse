package com.betasoft.ToyotaMobi.JavaBeans;

import java.io.Serializable;

public class UserInfoBean implements Serializable{
	public String fullName;
	public String phoneNum;
	public int userType;
	public String userId;
	public String authNumber;
	public String chesisNum;
	public String address;
	public String emailAdress;
	public String profilePath;
	
public UserInfoBean(String Name,String Contact,int Type,String userID)
{
	this.fullName = Name;
	this.phoneNum = Contact;
	this.userType = Type;
	this.userId = userID;
//	this.authNumber = authNumber;
}
}
