package com.paril.mlaclientapp.model;
import java.io.Serializable;

public class MessageDetail implements Serializable
{

     public String msId;
    public String fromList;
    public String toList;
    public String msgSubject;
    public String msgBody;
    public String creationDate;
    public String sessionKey;
    public String isReceived;

    public void setMsgId(String msId){this.msId=msId;}
    public void setFromList(String fromList){this.fromList=fromList; }
    public void setToList(String toList){this.toList=toList;}
    public void setMsgSubject(String msgSubject){this.msgSubject=msgSubject;}
    public void setMsgBody(String msgBody){this.msgBody=msgBody;}
    public void setCreationDate(String creationDate){this.creationDate=creationDate;}
    public void setSessionKey(String sessionKey){this.sessionKey=sessionKey;}
    public void setIsReceived(String isReceived){this.isReceived=isReceived;}

    public String getMsgId(){return msId;}
    public String getFromList(){return fromList; }
    public String getToList(){return toList;}
    public String getMsgSubject(){return msgSubject;}
    public String getMsgBody(){return msgBody;}
    public String getCreationDate(){return creationDate;}
    public String getSessionKey(){return sessionKey;}
    public String  getIsReceived(){return isReceived;}


}
