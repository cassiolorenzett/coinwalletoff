/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.ExchengesCore;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Classe abstrata que faz comunicação com API passada via GET.
 * Retorna objeto JSON com o retorno da API passada.
 * @author Cassio Dal Castel Lorenzett
 * @version 1.0
 * @since 2018-06-14
 * @see HttpCliente,Blockchain
 */
abstract class HttpCliente {
   private static String erro; 
  
    /**
    * Retorna erros durante a cominicação com a API
    */
   public String getErros()
   {
       return this.erro;
   }
   
   /**
    * Faz a comunicação com a API via GET, retornando o resultado da mesma.
    */
    public JSONObject http(String url, String body) {
        
        JSONObject obj = null;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(url);
            StringEntity params = new StringEntity(body);
            request.addHeader("content-type", "application/json");
            //request.setEntity(params);
            HttpResponse result = httpClient.execute(request);

            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
                JSONParser parser = new JSONParser();
                Object resultObject = parser.parse(json);
                
                obj = (JSONObject) resultObject;
               
                if (obj.containsKey("status"))
                {   
                    if (obj.get("status").toString().trim().equalsIgnoreCase("0"))
                    {
                        this.erro = obj.get("result").toString();
                    }
                    else
                    {
                        this.erro = "";
                    }
                    
                }
                else
                {
                    this.erro = "";
                }
                
            } catch (Exception e) {

                //status.setText("Erro na leitura do json " + e);
                this.erro =  "Erro na leitura do json: "+e;
                
                //JOptionPane.showMessageDialog(null, "Erro na leitura do json " + e);
            }

        } catch (IOException ex) {
         
            //status.setText("Erro ao iniciar servidor http: " + ex);
            this.erro = "Erro ao iniciar servidor http: " + ex;
           
            //JOptionPane.showMessageDialog(null, "Erro ao iniciar servidor http: " + ex);
        }
       

        return obj;
    }
    
}
