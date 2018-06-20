
package br.com.coinwalletoff.ExchengesCore;

import com.google.common.io.BaseEncoding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author cassio
 */
public class Exchange_MercadoBitcoin {

    private static final String TAPI_PATH = "/tapi/";
    private static final String ENCRYPT_ALGORITHM = "HmacSHA512";
    private static final String METHOD_PARAM = "method";
    private byte[] mbTapiCodeBytes;
    private String mbTapiKey;

    public ArrayList Listordens(String mbTapiCode, String mbTapiKey, String moeda) {

        try {
            this.mbTapiCodeBytes = mbTapiCode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex);
        }
        this.mbTapiKey = mbTapiKey;

        String retcriptos = "";
        String params = "tapi_method=list_orders&coin_pair=BRL" + moeda + "&tapi_nonce=" + generateTonce();
        ArrayList retornolista = null;
        String jsonResponse = invokeTapiMethod(params);

        JSONParser parser = new JSONParser();

        Object resultObject;
        try {
            resultObject = parser.parse(jsonResponse);
            JSONObject getobj = (JSONObject) resultObject;
            retornolista = getMovimentos(getobj);

        } catch (ParseException ex) {
            System.err.println("erro: " + ex);
        }
        return retornolista;
    }

    private ArrayList getMovimentos(JSONObject objmovi) {
        ArrayList retmov = new ArrayList();
        String status_code = objmovi.get("status_code").toString();
        
        String tipoordem = "";
        String status = "";
       
        if (status_code.trim().equalsIgnoreCase("100") == true)
        {
            
            JSONObject mov = (JSONObject) objmovi.get("response_data");
            JSONArray movarr = (JSONArray) mov.get("orders");
        
            if (movarr.size() > 0) {

                for (int i = 0; i < movarr.size(); i++) {
                    JSONObject getinfo = (JSONObject) movarr.get(i);

                    if (getinfo.get("order_type").toString().equalsIgnoreCase("1")) {
                        tipoordem = "Compra";
                    } else {
                        tipoordem = "Venda";
                    }

                    if (getinfo.get("status").toString().equalsIgnoreCase("2")) {
                        status = "Ordem Aberta";
                    } else if (getinfo.get("status").toString().equalsIgnoreCase("3")) {
                        status = "Ordem Cancelada";
                    } else {
                        status = "Ordem concluída";
                    }

                    long unixSeconds = Long.parseLong(getinfo.get("created_timestamp").toString());

                    Date date = new Date(unixSeconds * 1000L); // *1000 converte segundos para milisegundos
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss "); // formata sua data
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); //timezone
                    String formattedDate = sdf.format(date);

                    retmov.add(new arraymovi(getinfo.get("order_id").toString(),
                            getinfo.get("coin_pair").toString(),
                            tipoordem,
                            status,
                            getinfo.get("limit_price").toString(),
                            getinfo.get("executed_price_avg").toString(),
                            getinfo.get("quantity").toString(),
                            getinfo.get("executed_quantity").toString(),
                            getinfo.get("fee").toString(),
                            formattedDate));

                }
            }
        }
        else
        {                
             retmov.add(new arraymovi(objmovi.get("error_message").toString(),
                            "",
                            "",
                            "error",
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""));
        }
        
        return retmov;
    }

    public String Getcriptos(String mbTapiCode, String mbTapiKey) {

        try {
            this.mbTapiCodeBytes = mbTapiCode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.err.println(ex);
        }
        this.mbTapiKey = mbTapiKey;

        String retcriptos = "";
        String params = "tapi_method=get_account_info&tapi_nonce=" + generateTonce();
        String status = "";
        
        String jsonResponse = invokeTapiMethod(params);

        JSONParser parser = new JSONParser();

        Object resultObject;
        try {
            resultObject = parser.parse(jsonResponse);
            JSONObject getobj = (JSONObject) resultObject;
            status = getobj.get("status_code").toString();
            
            if (status.trim().equalsIgnoreCase("100") == true)
            {
                
                JSONObject getdata = (JSONObject) getobj.get("response_data");
                JSONObject getbalance = (JSONObject) getdata.get("balance");
                JSONObject balancebtc = (JSONObject) getbalance.get("btc");
                JSONObject balancebtg = (JSONObject) getbalance.get("btg");
                JSONObject balancebch = (JSONObject) getbalance.get("bch");
                JSONObject balanceltc = (JSONObject) getbalance.get("ltc");
                JSONObject balancebrl = (JSONObject) getbalance.get("brl");

                JSONObject getlimits = (JSONObject) getdata.get("withdrawal_limits");
                JSONObject limitebtc = (JSONObject) getlimits.get("btc");
                JSONObject limitebtg = (JSONObject) getlimits.get("btg");
                JSONObject limitebch = (JSONObject) getlimits.get("bch");
                JSONObject limiteltc = (JSONObject) getlimits.get("ltc");
                JSONObject limitebrl = (JSONObject) getlimits.get("brl");

                JSONObject retobj = new JSONObject();

                retcriptos += "{\"status\":\""+status+"\",\"coins\":[";

                //bitcoin
                retobj.put("coin", "btc");
                retobj.put("name", "Bitcoin");
                retobj.put("valor", balancebtc.get("total"));
                retobj.put("ordens_open", balancebtc.get("amount_open_orders"));
                retobj.put("retirada_acessivel", limitebtc.get("available"));
                retobj.put("retirada_total", limitebtc.get("total"));

                retcriptos += retobj.toJSONString() + ",";

                //litecoin
                retobj.put("coin", "ltc");
                retobj.put("name", "Litecoin");
                retobj.put("valor", balanceltc.get("total"));
                retobj.put("ordens_open", balanceltc.get("amount_open_orders"));
                retobj.put("retirada_acessivel", limiteltc.get("available"));
                retobj.put("retirada_total", limiteltc.get("total"));

                retcriptos += retobj.toJSONString() + ",";

                //Bitcoin Gold
                retobj.put("coin", "btg");
                retobj.put("name", "Bitcoin Gold");
                retobj.put("valor", balancebtg.get("total"));
                retobj.put("ordens_open", balancebtg.get("amount_open_orders"));
                retobj.put("retirada_acessivel", limitebtg.get("available"));
                retobj.put("retirada_total", limitebtg.get("total"));

                retcriptos += retobj.toJSONString() + ", ";

                //Bitcoin Cash
                retobj.put("coin", "bch");
                retobj.put("name", "Bitcoin Cash");
                retobj.put("valor", balancebch.get("total"));
                retobj.put("ordens_open", balancebch.get("amount_open_orders"));
                retobj.put("retirada_acessivel", limitebch.get("available"));
                retobj.put("retirada_total", limitebch.get("total"));

                retcriptos += retobj.toJSONString();

                retcriptos += "], \"Real\": ";

                //Real
                retobj.put("coin", "brl");
                retobj.put("name", "Real");
                retobj.put("valor", balancebrl.get("total"));
                retobj.put("retirada_total", limitebrl.get("total"));

                retcriptos += retobj.toJSONString() + "}";
                
            }
            else
            {
                retcriptos += "{\"status\":\""+status+"\",\"error_message\":\""+getobj.get("error_message").toString()+"\"}";
            }
           
        } catch (ParseException ex) {
            Logger.getLogger(Exchange_MercadoBitcoin.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        return retcriptos;

    }

    private String invokeTapiMethod(String params) {

        String signature;
        String ret = "";
        try {
            signature = generateSignature("/tapi/v3/?" + params);
            URL url = new URL("https://www.mercadobitcoin.net/tapi/v3/");
            HttpURLConnection conn = getHttpPostConnection(url, signature);
            postRequestToServer(conn, params);
            ret = getResponseFromServer(conn);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Exchange_MercadoBitcoin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Exchange_MercadoBitcoin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Exchange_MercadoBitcoin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Exchange_MercadoBitcoin.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;

    }

    private void postRequestToServer(HttpURLConnection conn, String params) throws IOException {
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(params);
        wr.flush();
        wr.close();
    }

    private String getResponseFromServer(HttpURLConnection conn) throws IOException {
        String responseStr = null;
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }

        responseStr = sb.toString();
        return responseStr;
    }

    private long generateTonce() {
        long unixTime = System.currentTimeMillis() / 1000L;
        return unixTime;
    }

    private String generateSignature(String parameters) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec key = null;
        Mac mac = null;

        key = new SecretKeySpec(mbTapiCodeBytes, ENCRYPT_ALGORITHM);
        mac = Mac.getInstance(ENCRYPT_ALGORITHM);
        mac.init(key);
        byte[] sign = null;
        String ret = null;
        try {
            sign = mac.doFinal(parameters.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Exchange_MercadoBitcoin.class.getName()).log(Level.SEVERE, null, ex);
        }
        ret = BaseEncoding.base16().lowerCase().encode(sign);

        return ret;
    }

    

    private HttpURLConnection getHttpPostConnection(URL url, String signature) throws IOException {
        HttpsURLConnection conn;

        conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("TAPI-ID", mbTapiKey);
        conn.setRequestProperty("TAPI-MAC", signature);
        conn.setDoOutput(true);

        return conn;
    }
}

class arraymovi {

    public String ordem;
    public String tipo_ordem;
    public String status;
    public String preco;
    public String precorun;
    public String qtd;
    public String qtdrun;
    public String comissao;
    public String dataexec;
    public String moeda;

    public arraymovi(String ordem, String moeda, String tipo_ordem, String status, String preco, String precorun, String qtd, String qtdrun, String comissao, String dataexec) {
        this.ordem = ordem;
        this.tipo_ordem = tipo_ordem;
        this.status = status;
        this.preco = preco;
        this.precorun = precorun;
        this.qtd = qtd;
        this.qtdrun = qtdrun;
        this.comissao = comissao;
        this.dataexec = dataexec;
        this.moeda = moeda;
    }

}
