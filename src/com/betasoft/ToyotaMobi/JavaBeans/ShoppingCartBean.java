package com.betasoft.ToyotaMobi.JavaBeans;

public class ShoppingCartBean {
	public String name;
	public String partNumber;
	public String qty;
	public String price;


	ShoppingCartBean(String partNumber, String name,  String qty, String price) {
		this.partNumber=partNumber;
		this.name=name;

		this.qty=qty;
		this.price=price;



	}
}