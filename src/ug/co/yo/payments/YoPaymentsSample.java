
package ug.co.yo.payments;


//import org.apache.commons.net.util.Base64;

/**
 * @author Munaawa Philip (swiftugandan@gmail.com) This class is a sample
 *         application of the Yo! Payments API (https://payments.yo.co.ug)
 */
public class YoPaymentsSample {

	public static final String SERVICE_URL = "http://41.220.12.206/services/yopaymentsdev/task.php";
    /**
     * @param args\
     */
    public final static void main(String[] args) throws Exception {

    	/*
    	 APIUsername - 
    	 APIPassword -
    	*/
    	
        YoPaymentsAPIClient yoPaymentsClient = new YoPaymentsAPIClient("90005142126", "1178700810");

        
        
        // String inputXML
        // =yoPaymentsClient.createWithdrawalXml(1000,"25677123456","Narrative 6");
       /* String inputXML = yoPaymentsClient.createWithdrawalXml(1500, "2567035004306", "",
                "DAKS - OIL FLITER", "", "", "4");*/
        
        String inputXML =  yoPaymentsClient.createDepositXml(2500,"2567035004306", "DAKS - OIL FLITER");
        
        // String inputXML = yoPaymentsClient.createBalanceCheckXml();

        

        
        
        YoPaymentsResponse yoPaymentsResponse = (YoPaymentsResponse)yoPaymentsClient
                .executeYoPaymentsRequest(inputXML, SERVICE_URL);

        System.out.println(">> "+yoPaymentsResponse.getStatusCode());
        System.out.println(yoPaymentsResponse.toString());

    }

}
