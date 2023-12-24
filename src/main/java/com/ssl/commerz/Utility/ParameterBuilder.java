package com.ssl.commerz.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ParameterBuilder {
    public static String getParamsString(Map<String, String> params, boolean urlEncode) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (urlEncode)
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            else
                result.append(entry.getKey());

            result.append("=");
            if (urlEncode)
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            else
                result.append(entry.getValue());
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static Map<String, String> constructRequestParameters() {
        // CREATING LIST OF POST DATA
        String baseUrl = "http://localhost:9000/";//Request.Url.Scheme + "://" + Request.Url.Authority + Request.ApplicationPath.TrimEnd('/') + "/";
        Map<String, String> postData = new HashMap<String, String>();
        postData.put("total_amount", "150.00");
        postData.put("tran_id", "TESTJAVA1234"); // use unique tran_id for each API call
        postData.put("success_url", baseUrl + "ssl-success-page");
        postData.put("fail_url", "https://sandbox.sslcommerz.com/developer/fail.php");
        postData.put("cancel_url", "https://sandbox.sslcommerz.com/developer/cancel.php");
        postData.put("cus_name", "ABC XY");
        postData.put("cus_email", "abc.xyz@example.com");
        postData.put("cus_add1", "Address Line One");
        postData.put("cus_city", "Dhaka");
        postData.put("cus_postcode", "1000");
        postData.put("cus_country", "Bangladesh");
        postData.put("cus_phone", "0111111111");
        postData.put("shipping_method", "NO");
        postData.put("product_name", "Test Product");
        postData.put("product_category", "General");
        postData.put("product_profile", "General");
//        postData.put("ship_name", "ABC XY");
//        postData.put("ship_add1", "Address Line One");
//        postData.put("ship_add2", "Address Line Two");
//        postData.put("ship_city", "City Name");
//        postData.put("ship_state", "State Name");
//        postData.put("ship_postcode", "Post Code");
//        postData.put("ship_country", "Country");
        return postData;
    }
}
