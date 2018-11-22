package com.ssl.commerz;

import com.ssl.commerz.Utility.ParameterBuilder;
import com.ssl.commerz.Utility.Util;
import com.ssl.commerz.parametermappings.SSLCommerzInitResponse;
import com.ssl.commerz.parametermappings.SSLCommerzValidatorResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is responsible for initiating Transaction request
 * Generating request URL with Session or JSON list to perform payment option selection
 * Validates returned response from payment pages and finalize transaction.
 */
public class SSLCommerz {

    private String storeId;
    private String storePass;
    private boolean storeTestMode;

    private String[] keyList;
    private String generateHash;
    private String error;

    private String sslczURL = "https://securepay.sslcommerz.com/";
    private String submitURL = "gwprocess/v3/api.php";
    private String validationURL = "validator/api/validationserverAPI.php";
    private String checkingURL = "validator/api/merchantTransIDvalidationAPI.php";

    /****
     *
     * @param Store_ID
     * Store_ID is that Id of user to connect SSLCOmmerz
     * @param Store_Pass
     * Store_Pass is that password of user to connect SSLCOmmerz
     * @param Store_Test_Mode
     *  Store_Test_Mode is to define whether user is testing this or not.
     * @throws Exception
     */
    public SSLCommerz(String Store_ID, String Store_Pass, boolean Store_Test_Mode) throws Exception {
        if (!Store_ID.isEmpty() && !Store_Pass.isEmpty()) {
            this.storeId = Store_ID;
            this.storePass = Store_Pass;
            this.setSSLCzTestMode(Store_Test_Mode);
        } else {
            throw new Exception("Please provide Store ID and Password to initialize SSLCommerz");
        }
    }

    /***
     *
     * @param storeTestMode
     * storeTestMode defines user Testing mode and sets test id, password and sandbox URL
     */
    private void setSSLCzTestMode(boolean storeTestMode) {
        this.storeTestMode = storeTestMode;

        if (storeTestMode) {
            this.storeId = "testbox";
            this.storePass = "qwerty";
            this.sslczURL = "https://sandbox.sslcommerz.com/";
        }

    }

    /**
     *
     * @param postData
     * postData is a Map with String Key and String value.
     * Construct this Map with Necessary Key value pair to provide request parameters.
     * @param isGetGatewayList
     * isGetGatewayList is to define whether user wants to get response as Gateway list or not
     * @return
     * Returns a String which is an URL with Session Key to perform payment option selection,
     * and payment finalization
     * @throws Exception
     */
    public String initiateTransaction(Map<String, String> postData, boolean isGetGatewayList) throws Exception {

        postData.put("store_id", this.storeId);
        postData.put("store_passwd", this.storePass);
        String response = this.sendPost(postData);
        try {
            SSLCommerzInitResponse resp = Util.extractInitResponse(response);
            if (resp.status.equals("SUCCESS")) {
                if (isGetGatewayList) {
                    // We will work on it!
                } else {
                    return resp.getGatewayPageURL();
                }
            } else {
                throw new Exception("Unable to get data from SSLCommerz. Please contact your manager!");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return response;
    }

    private String sendPost(Map<String, String> postData) throws IOException {
        System.out.println(this.sslczURL + this.submitURL);
        String response = SSLCommerz.post(this.sslczURL + this.submitURL, postData);
        return response;
    }

    /**
     * @param uri
     * uri to hit SSL Commerz URI
     * @param postData
     * postData is the request parameter Map comprised of key value pair
     * @return
     * Returns a String which is an URL with Session Key to perform payment option selection,
     * and payment finalization
     * @throws IOException
     */
    private static String post(String uri, Map<String, String> postData) throws IOException {
        String output = "";
        URL url = new URL(uri);

        String urlParameters = ParameterBuilder.getParamsString(postData, true);
        byte[] postDataBytes = urlParameters.getBytes();
        int postDataLength = postDataBytes.length;

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        con.setInstanceFollowRedirects(false);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        con.setUseCaches(false);
        con.setDoOutput(true);
        con.getOutputStream().write(postDataBytes);


        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String outputLine;
        while ((outputLine = br.readLine()) != null) {
            output = output + outputLine;
        }
        br.close();
        return output;
    }

    /**
     *
     * @param merchantTrnxnId
     * merchantTrnxnId : merchants Transaction id.
     * @param merchantTrnxnAmount
     * merchantTrnxnAmount: merchants transaction amount.
     * @param merchantTrnxnCurrency
     * merchantTrnxnCurrency: Merchants transaction amount
     * @param requestParameters
     *  requestParameters is a Map of String as key and String as value
     *  which should be constructed from the success response of the payment page.
     * @return
     * Returns a boolean value indicating a valid success response or not.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    boolean orderValidate(String merchantTrnxnId, String merchantTrnxnAmount, String merchantTrnxnCurrency,
                          Map<String, String> requestParameters) throws IOException, NoSuchAlgorithmException {
        boolean hash_verified = this.ipnHashVerify(requestParameters);
        if (hash_verified) {

            String EncodedValID = URLEncoder.encode(requestParameters.get("val_id"), Charset.forName("UTF-8").displayName());
            String EncodedStoreID = URLEncoder.encode(this.storeId, Charset.forName("UTF-8").displayName());
            String EncodedStorePassword = URLEncoder.encode(this.storePass, Charset.forName("UTF-8").displayName());


            //GET Request
            String validUrl = this.sslczURL + this.validationURL + "?val_id=" + EncodedValID
                    + "&store_id=" + EncodedStoreID + "&store_passwd=" + EncodedStorePassword + "&v=1&format=json";
            String json = Util.getByOpeningJavaUrlConnection(validUrl);

            if (!json.isEmpty()) {
                SSLCommerzValidatorResponse resp = Util.extractValidatorResponse(json);//new JavaScriptSerializer().Deserialize < SSLCommerzValidatorResponse > (json);

                if (resp.status.equals("VALID") || resp.status.equals("VALIDATED")) {

                    if (merchantTrnxnId.equals(resp.tran_id)
                            && (Math.abs(Double.parseDouble(merchantTrnxnAmount) - Double.parseDouble(resp.currency_amount)) < 1)
                            && merchantTrnxnCurrency.equals(resp.currency_type)) {
                        return true;
                    } else {
                        this.error = "Currency Amount not matching";
                        return false;
                    }

                } else {
                    this.error = "This transaction is either expired or failed";
                    return false;
                }
            } else {
                this.error = "Unable to get Transaction JSON status";
                return false;

            }
        } else {
            this.error = "Unable to verify hash";
            return false;
        }
    }

    /**
     *
     * @param requestParameters
     * requestParameters is a Map of String which should be constructed from the RequestParameter returned from
     * the success response of payment page.
     * @return
     * Returns if the Hash generated during SSL Commerz payment request and the hash generated from the
     * redirected success response from that payment page
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    private Boolean ipnHashVerify(final Map<String, String> requestParameters) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        // Check For verify_sign and verify_key parameters
        if (!requestParameters.get("verify_sign").isEmpty() && !requestParameters.get("verify_key").isEmpty()) {
            // Get the verify key
            String verify_key = requestParameters.get("verify_key");
            if (!verify_key.isEmpty()) {

                // Split key String by comma to make a list array
                keyList = verify_key.split(",");
                TreeMap<String, String> sortedMap = new TreeMap<String,String>();

                // Store key and value of post in a sorted Map
                for (final String k : keyList) {
                    sortedMap.put(k, requestParameters.get(k));
                }

                // Store Hashed Password in list
                final String hashedPass = this.md5(this.storePass);
                sortedMap.put("store_passwd", hashedPass);
                // Concat and make String from array
                String hashString = "";
                hashString += ParameterBuilder.getParamsString(sortedMap, false) + "&";

                // Trim '&' from end of this String
                hashString = hashString.substring(0, hashString.length() - 1); // omitting last &

                // Make hash by hash_string and store
                generateHash = this.md5(hashString);

                // Check if generated hash and verify_sign match or not
                // Matched
                return generateHash.equals(requestParameters.get("verify_sign"));
            }

            return false;
        } else {
            return false;
        }
    }

    /// <summary>
    /// Make PHP like MD5 Hashing
    /// </summary>
    /// <param name="s"></param>
    /// <returns>md5 Hashed String</returns>
    private String md5(String s) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytesOfMessage = s.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] theDigest = md.digest(bytesOfMessage);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < theDigest.length; ++i) {
            sb.append(Integer.toHexString((theDigest[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();

    }
}
