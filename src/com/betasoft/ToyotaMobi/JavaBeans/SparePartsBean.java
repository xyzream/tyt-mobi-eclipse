package com.betasoft.ToyotaMobi.JavaBeans;

import java.io.Serializable;
public class SparePartsBean implements Serializable{

	public String sparepart_name;
	public String  id;
	public String  sparepart_price;
	public String part_number;
	public String image_id;
	public String partQuantity;
	public String shilling_to_USD;
	public SparePartsBean()
	{
		
	}
	public SparePartsBean(String SparePartName,String SparePartId,String SparePartPrice,String SparePartNumber, String SparePartImage)
	{
		this.sparepart_name=SparePartName;
		this.id=SparePartId;
		this.sparepart_price=SparePartPrice;
		this.part_number=SparePartNumber;
		this.image_id=SparePartImage;
		
	}
}
