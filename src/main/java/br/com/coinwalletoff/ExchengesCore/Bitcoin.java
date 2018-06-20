package br.com.coinwalletoff.ExchengesCore;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TreeItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Classe que faz a comunicação com a blockchain do Bitcoin.
 * Retorna informações dacarteira bitcoin passada,como qtd recebida,movimentaçõe, balanço, etc
 * as carteiras na base de dados.
 * @author Cassio Dal Castel Lorenzett
 * @version 1.0
 * @since 2018-06-14
 * @see HttpCliente,Blockchain
 */
public class Bitcoin extends HttpCliente implements Blockchain {

    
    private String tx= "";
    private String recebido= "";
    private String enviado= "0";
    private String balanco= "";
    private TreeItem movimentos= null;
    private String error= "";
    
    
    /**
    * Conecta-se com a API da blockchain do bitcoin e retorna o objeto JSON com as informações,
    * sobre a carteira.
    * Utilizado no fonte BlockchainController na função busca_block
    */
    @Override
    public void RunBlockchain(String address) {
        String url = "https://blockchain.info/pt/multiaddr?active=" + address;
        
        JSONObject retorno = super.http(url, "");
        
        if (super.getErros().isEmpty())
        {   
            ExecBitcoin(retorno);
        }
        else
        {
            this.error = super.getErros();
            this.tx =  "";
            this.balanco =  "";
            this.enviado =  "0";
            this.recebido =  "0";
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
    * Recebe objeto JSON com as informações da carteira na Blockchain 
    * e formata as informações para serem retornadas para o usuario.
    */
    private void ExecBitcoin(JSONObject obj) {
        JSONObject valores = (JSONObject) obj;
        
       
        if (valores.size() > 0) {
            
            TreeItem<String> _rootItem = new TreeItem<>("Movimentos");
             
            JSONArray btc = (JSONArray) valores.get("addresses");
            JSONObject _btc = (JSONObject) btc.get(0);
            
            this.error = "";
            this.tx = _btc.get("n_tx").toString();
            this.balanco = _btc.get("final_balance").toString();
            this.enviado = _btc.get("total_sent").toString();
            this.recebido = _btc.get("total_received").toString();

            JSONArray tx_btc = (JSONArray) valores.get("txs");
            String acao = "";
            int btc_value = 0;
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            
               
            for (int i = 0; i < tx_btc.size(); i++) {
                
                JSONObject _txbtc = (JSONObject) tx_btc.get(i);
                
                if (Integer.valueOf(_txbtc.get("result").toString()) > 0) {
                    acao = "Deposito";
                    btc_value = Integer.valueOf(_txbtc.get("result").toString());
                } else {
                    acao = "Retirada";
                    btc_value = Integer.valueOf(_txbtc.get("result").toString())*-1;
                }
                
                
                final String data = Instant.ofEpochSecond((long) _txbtc.get("time"))
                        .atZone(ZoneId.of("GMT-4"))
                        .format(formatter);
                
                 TreeItem<String> rootItem = new TreeItem<>("Hash ->  "+_txbtc.get("hash").toString() + " Ação -> "+acao);
                 rootItem.setExpanded(true);
                 rootItem.getChildren().addAll(
                        new TreeItem<String>("Total -> "+format(btc_value)),
                        new TreeItem<String>("Tamanho -> "+_txbtc.get("size").toString()),
                        new TreeItem<String>("Taxa -> "+format(Integer.valueOf(_txbtc.get("fee").toString()))),
                        new TreeItem<String>("Data -> "+data)
                                              
                );
                
                _rootItem.getChildren().add(rootItem);
                _rootItem.setExpanded(true);
            }
            
            this.movimentos = _rootItem;
            
        }
       
    }
    
     /**
    * Formata o numero passado em bitcoin para formatar para ficar com a mascara correta.
    */
    private String format(Integer valuebtc)
    {   String btcformat = "";
    
        String fotmat_btc1 = String.format("%09d", valuebtc).substring(0, 1);
        String fotmat_btc2 = String.format("%09d", valuebtc).substring(1);
        
       
        btcformat = fotmat_btc1+"."+fotmat_btc2;
                
        return btcformat;
    }

}
