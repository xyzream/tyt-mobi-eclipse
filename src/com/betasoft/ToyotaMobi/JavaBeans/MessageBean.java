package com.betasoft.ToyotaMobi.JavaBeans;

import java.io.Serializable;

public class MessageBean implements Serializable{
public String senderID,receiverID,messages,uploadImageName,imgPath,userLocation,downloadedImagePath,chatID;
public MessageBean(String senderID, String receiverID, String messages, String uploadImageName, String imgPath, String userLocation)
{
this.senderID = senderID;
this.receiverID = receiverID;
this.messages = messages;
this.uploadImageName = uploadImageName;
this.imgPath = imgPath;
this.userLocation = userLocation;
}
}
