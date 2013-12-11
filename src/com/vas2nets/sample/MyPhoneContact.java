package com.vas2nets.sample;

public class MyPhoneContact {
	private String contactId;
	private String name;
	private String phoneNumber;
	private String photo;
	private String email;
	
	public MyPhoneContact(String contactId, String name, String phoneNumber, String photo, String email){
		this.contactId = contactId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.photo = photo;
	}
	public String getName(){
		return name;
	}
	public String getPhoneNumber(){
		return phoneNumber;
	}
	public String getPhoto(){
		return photo;
	}
	public String getContactId(){
		return contactId;
	}
	
	public String getEmail(){
		return email;
	}

}
