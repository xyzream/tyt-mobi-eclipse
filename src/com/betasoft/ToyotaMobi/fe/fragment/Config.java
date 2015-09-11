package com.betasoft.ToyotaMobi.fe.fragment;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

public class Config {

	// PayPal app configuration
	public static final String PAYPAL_CLIENT_ID = "AVxnBOZO5HW4fWrklbaTf5_MjWDULhA_yWh3WeoQk5fB3cLTea5B9ymLSY2qnLaxK197LDRazfCW1bPU";
	public static final String PAYPAL_CLIENT_SECRET = "EKzvlmvjTUmDhcBZqxo_Yiq9L2vaGvwsf8vmCQUDWP18fTI8eAfkwv1pvQZ8KxEUP39Nfhqg6BAx6OrT";

	public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
	public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
	public static final String DEFAULT_CURRENCY = "USD";

	// Our php+mysql server urls
	//public static final String URL_PRODUCTS = "http://10.11.11.166/PayPalServer/v1/products";
	public static final String URL_VERIFY_PAYMENT = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/paypal_payment_verify";

}