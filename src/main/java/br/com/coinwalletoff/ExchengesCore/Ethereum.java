/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.ExchengesCore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javafx.scene.control.TreeItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Classe que faz a comunicação com a blockchain do Ethereum.
 * Retorna informações dacarteira Ethereum passada,como qtd recebida,movimentaçõe, balanço, etc
 * as carteiras na base de dados.
 * @author Cassio Dal Castel Lorenzett
 * @version 1.0
 * @since 2018-06-14
 * @see HttpCliente,Blockchain
 */
public class Ethereum extends HttpCliente implements Blockchain {

    private String tx = "";
    private String recebido = "";
    private String enviado = "0";
    private String balanco = "";
    private TreeItem movimentos = null;
    private String error = "";

    /**
    * Conecta-se com a API da blockchain do Ethereum e retorna o objeto JSON com as informações,
    * sobre a carteira.
    * Utilizado no fonte BlockchainController na função busca_block
    */
    @Override
    public void RunBlockchain(String address) {
        String urlsaldo = "https://api.etherscan.io/api?module=account&action=balance&address=" + address + "&tag=latest";
        String urlhisto = "http://api.etherscan.io/api?module=account&action=txlist&address=" + address + "&sort=desc";

        JSONObject retornosaldo = super.http(urlsaldo, "");

        if (super.getErros().isEmpty()) {
            ExecBalance(retornosaldo);

            JSONObject retornohisto = super.http(urlhisto, "");

            if (super.getErros().isEmpty()) {
                ExecHistorico(retornohisto, address);
            }
        } else {
            this.error = super.getErros();
            this.tx = "";
            this.balanco = "";
            this.enviado = "0";
            this.recebido = "0";
            this.movimentos = null;
        }
    }

    /**
    * Retorna o balanço da carteira .
    * Utilizado no fonte BlockchainController na função busca_block
    */
    @Override
    public String GetBalanco() {
        return this.balanco;
    }

    /**
    * Retorna  algum erro na consulta da API sobre a carteira.
    * Utilizado no fonte BlockchainController na função busca_block
    */
    @Override
    public String GetErro() {
        return this.error;
    }

    /**
    * Retorna os movimentos da carteira.
    * Utilizado no fonte BlockchainController na função busca_block
    */
    @Override
    public TreeItem GetMovimentos() {
        return this.movimentos;
    }

     /**
    * Retorna o numero de Transações
    * Utilizado no fonte BlockchainController na função busca_block
    */
    @Override
    public String GetTx() {
        return this.tx;
    }

    /**
    * Retorna as moedas enviadas pela carteira.
    * Utilizado no fonte BlockchainController na função busca_block
    */
    @Override
    public String GetEnviado() {
        return format(Integer.valueOf(this.enviado));
    }

    /**
    * Retorna as moedas recebidas pela carteira.
    * Utilizado no fonte BlockchainController na função busca_block
    */
    @Override
    public String GetRecebido() {
        return format(Integer.valueOf(this.recebido));
    }

     /**
    * Retorna balanço da carteira
    */
    private void ExecBalance(JSONObject obj) {
        JSONObject valores = (JSONObject) obj;

        if (valores.size() > 0) {

            this.balanco = valores.get("result").toString();

        }

    }

     /**
    * Recebe objeto JSON com as informações da carteira na Blockchain 
    * e formata as informações para serem retornadas para o usuario.
    */
    private void ExecHistorico(JSONObject obj, String endereco) {
        JSONObject valores = (JSONObject) obj;
        
        if (valores.size() > 0) {
           
            TreeItem<String> _rootItem = new TreeItem<>("Movimentos");
            
            JSONArray eth = (JSONArray) valores.get("result");

            String acao = "";
          
            this.tx = Integer.toString(eth.size());
            this.error = "";
            for (int i = 0; i < eth.size(); i++) {

                JSONObject _txeth = (JSONObject) eth.get(i);
                
                long unixSeconds = Long.parseLong(_txeth.get("timeStamp").toString());
                Date date = new Date(unixSeconds * 1000L); // *1000 converte segundos para milisegundos
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss "); // formata sua data
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); //timezone
                String formattedDate = sdf.format(date);
            
                if (_txeth.get("to").toString().equalsIgnoreCase(endereco)) {
                    acao = "Deposito";
                    
                } else {
                    acao = "Retirada";
                    
                }
               
                TreeItem<String> rootItem = new TreeItem<>("Hash ->  "+_txeth.get("hash").toString() + " Ação -> "+acao);
                 rootItem.setExpanded(true);
                
                 rootItem.getChildren().addAll(
                        new TreeItem<String>("Total -> "+_txeth.get("value").toString()),
                        new TreeItem<String>("Altura do bloco -> "+_txeth.get("blockNumber").toString()),
                        new TreeItem<String>("Dados de Entrada -> "+_txeth.get("input").toString()),
                        new TreeItem<String>("Data -> "+formattedDate)
                                              
                );
                
                _rootItem.getChildren().add(rootItem);
                _rootItem.setExpanded(true);
                
                
            }
            
            this.movimentos = _rootItem;
            this.enviado = "0";
            this.recebido = "0";
        }

    }

    private String format(Integer valuebtc)
    {   String btcformat = "";
    
        String fotmat_btc1 = String.format("%024d", valuebtc).substring(0, 2);
        String fotmat_btc2 = String.format("%024d", valuebtc).substring(2);
        
       
        btcformat = fotmat_btc1+"."+fotmat_btc2;
                
        return btcformat;
    }
}
