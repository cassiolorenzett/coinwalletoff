/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.ExchengesCore;

import br.com.coinwalletoff.Conexaodb.conn_db;
import br.com.coinwalletoff.Util.Chave;
import coinwalletoff.security_coin.Descriptografa;
import coinwalletoff.security_coin.Securitycoin;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.TilePane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author cassio
 */
public class BlockchainController implements Initializable {

    private PreparedStatement ps_;

    @FXML
    private ChoiceBox<String> combo_exchenge;

    @FXML
    private ChoiceBox<String> carteira;

    @FXML
    private ChoiceBox<String> moeda;

    @FXML
    private Button busca_block;

    @FXML
    private Button conecta;

    @FXML
    private Button desconeta;

    @FXML
    private TextField address;

    @FXML
    private TilePane pane;

    @FXML
    private TextField balanco;

    @FXML
    private TextField n_tx;

    @FXML
    private TextField total_enviado;

    @FXML
    private TextField total_recebido;

    @FXML
    private TreeView<String> mov;

    @FXML
    private ProgressBar progress;

    @FXML
    private ProgressBar cripto_progress;

    @FXML
    private Label status;

    @FXML
    private Label moedalabel;

    @FXML
    private Label cripto_label;

    @FXML
    private Label open_ordens;

    @FXML
    private Label criptototal;

    @FXML
    private Label retirada;

    @FXML
    private Label totalrs;

    @FXML
    private Label retiradacripto;

    @FXML
    private Label retiradaacessivel;

    @FXML
    private Label openordens;

    @FXML
    private TableView<Coinmovi> movimentacaocoin;

    @FXML
    private TableColumn<Coinmovi, String> order_id;

    @FXML
    private TableColumn<Coinmovi, String> par_coin;

    @FXML
    private TableColumn<Coinmovi, String> order_type;

    @FXML
    private TableColumn<Coinmovi, String> quantidade_coin;

    @FXML
    private TableColumn<Coinmovi, String> preco_coin;

    @FXML
    private TableColumn<Coinmovi, String> preco_coin_run;

    @FXML
    private TableColumn<Coinmovi, String> qtd_executada;

    @FXML
    private TableColumn<Coinmovi, String> comissao_coin;

    @FXML
    private TableColumn<Coinmovi, String> createdate_coin;

    @FXML
    private TableColumn<Coinmovi, String> status_coin;

    protected Blockchain blockchain = null;
    private String mbTapiCode = "", mbTapiKey = "";

    private void clear() {
        pane.getChildren().clear();
        pane.setHgap(10);
        pane.setVgap(10);

        movimentacaocoin.setItems(null);

        retiradacripto.setText("");
        retiradaacessivel.setText("");
        openordens.setText("");
        criptototal.setText("0");
        moedalabel.setText("");

        totalrs.setText("0,00");
        retirada.setText("");
        open_ordens.setText("");
    }

    private void getSecretsKeys(String id) {
        Securitycoin seg = new Descriptografa();
        Chave chapp = new Chave();
        conn_db connect = new conn_db();

        try {
            PreparedStatement pstmt = connect.conn.prepareStatement("SELECT * FROM apis where id_exchenge = ?");
            pstmt.setString(1, id);
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                this.mbTapiCode = seg.decripta(rs.getBytes("segredo"), chapp.getChv());
                this.mbTapiKey = seg.decripta(rs.getBytes("identificador"), chapp.getChv());
                        
            }

            pstmt.close();
        } catch (SQLException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erro na conexão com o banco. erro: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void desconecta(ActionEvent event) {
        clear();
        combo_exchenge.getSelectionModel().clearSelection();
        this.desconeta.setDisable(true);
        this.mbTapiCode = "";
        this.mbTapiKey = "";
    }

    @FXML
    void conecta(ActionEvent event) {
       
        if (!combo_exchenge.getSelectionModel().isEmpty()) {
           
            cripto_progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            cripto_label.setStyle("-fx-text-fill:black");
            cripto_label.setText("Buscando informações da Exchenge. Aguarde ... ");
            conecta.setDisable(true);

            clear();

            Task cript = new Task() {
                @Override
                protected Object call() throws Exception {

                    String Criptos = "";
                    String id = combo_exchenge.getSelectionModel().getSelectedItem().split("-")[0].trim();
                    getSecretsKeys(id);

                    Exchange_MercadoBitcoin ex = new Exchange_MercadoBitcoin();
                    Criptos = ex.Getcriptos(mbTapiCode, mbTapiKey);

                    return Criptos;

                }

                @Override
                protected void succeeded() {
                    JSONParser parser = new JSONParser();
                    JSONObject ret = null;
                    String msgget = "";
                    try {
                        ret = (JSONObject) parser.parse((String) getValue());

                        if (ret.get("status").toString().trim().equalsIgnoreCase("100")) {

                            getReais((JSONObject) ret.get("Real"));
                            getMoedas((JSONArray) ret.get("coins"));
                            desconeta.setDisable(false);
                            msgget = "";
                        } else {
                            cripto_label.setStyle("-fx-text-fill:red");
                            msgget = ret.get("error_message").toString();
                        }

                        cripto_label.setText(msgget);
                        conecta.setDisable(false);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                cripto_progress.setProgress(0);
                            }
                        });

                    } catch (ParseException ex) {
                        System.err.println(ex);
                    }
                }
            };

            Thread thrcrp = new Thread(cript);
            thrcrp.setDaemon(true);
            thrcrp.start();

        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Necessário selecionar uma EXCHENGE.");
            alert.showAndWait();
        }

    }

    @FXML
    void busca_block(ActionEvent event) {

        if (carteira.getSelectionModel().getSelectedItem() != null
                && moeda.getSelectionModel().getSelectedItem() != null) {

            status.setText("Buscando informações na Blockchain. Aguarde ... ");
            busca_block.setDisable(true);

            final String[] cart = carteira.getSelectionModel().getSelectedItem().split("-");
            progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

            Task bc = new Task() {
                @Override
                protected Object call() throws Exception {

                    if (moeda.getSelectionModel().getSelectedItem().toLowerCase().equalsIgnoreCase("BITCOIN")) {

                        blockchain = new Bitcoin();
                        blockchain.RunBlockchain(cart[1].trim());

                    }

                    if (moeda.getSelectionModel().getSelectedItem().toLowerCase().trim().equalsIgnoreCase("ETHEREUM")) {

                        blockchain = new Ethereum();
                        blockchain.RunBlockchain(cart[1].trim());

                    }
                    ArrayList<Object> list = new ArrayList();

                    list.add(blockchain.GetBalanco());
                    list.add(cart[1].trim());
                    list.add(blockchain.GetTx());
                    list.add(blockchain.GetEnviado());
                    list.add(blockchain.GetRecebido());
                    list.add(blockchain.GetMovimentos());
                    list.add(blockchain.GetErro());

                    return list;

                }

                @Override
                protected void succeeded() {
                    ArrayList<Object> ret = new ArrayList();
                    ret = (ArrayList<Object>) getValue();

                    balanco.setText((String) ret.get(0));
                    address.setText((String) ret.get(1));
                    n_tx.setText((String) ret.get(2));
                    total_enviado.setText((String) ret.get(3));
                    total_recebido.setText((String) ret.get(4));
                    mov.setRoot((TreeItem<String>) ret.get(5));
                    status.setText((String) ret.get(6));

                    busca_block.setDisable(false);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            progress.setProgress(0);
                        }
                    });

                }
            };

            Thread thrbc = new Thread(bc);
            thrbc.setDaemon(true);
            thrbc.start();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Necessário selecionar uma CARTEIRA E MOEDA.");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.order_id.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("ordem"));
        this.par_coin.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("moeda"));
        this.order_type.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("tipoordem"));
        this.quantidade_coin.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("qtd"));
        this.qtd_executada.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("qtdrun"));
        this.preco_coin.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("preco"));
        this.preco_coin_run.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("precorun"));
        this.comissao_coin.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("comissao"));
        this.status_coin.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("status"));
        this.createdate_coin.setCellValueFactory(new PropertyValueFactory<Coinmovi, String>("dataexec"));

        this.desconeta.setDisable(true);

        moeda.setItems(FXCollections.observableArrayList(
                "Bitcoin", "Ethereum"));
        carteiras();
        getExchenges();

    }

    private void getExchenges() {
        conn_db connect = new conn_db();

        ResultSet rs = null;
        ObservableList<String> retexchenge = FXCollections.observableArrayList();
        try {
            int id = 0;
            rs = connect.conn.createStatement().executeQuery("SELECT * FROM apis");
            while (rs.next()) {

                retexchenge.add(rs.getString("id_exchenge") + " - " + rs.getString("exchenge"));

            }

        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erro na conexão com o banco. erro: " + ex);
            alert.showAndWait();
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Erro ao finalizar conexão com o banco. erro: " + ex);
                alert.showAndWait();
            }
        }

        combo_exchenge.setItems(retexchenge);
    }

    private void getReais(JSONObject objcripto) {

        totalrs.setText((String) objcripto.get("valor"));
        retirada.setText("Retirada Acessivel: " + objcripto.get("retirada_acessivel"));
        open_ordens.setText("Ordens Abertas: " + objcripto.get("ordens_open"));
    }

    private void getMoedas(JSONArray objmoedas) {

        final JSONArray moedas = objmoedas;

        //Insets margins = new Insets(15, 15, 15, 15);
        //pane.setPadding(margins);
        for (int j = 0; j < objmoedas.size(); j++) {

            JSONObject getresult = (JSONObject) objmoedas.get(j);

            final Button button = new Button(getresult.get("name") + " - " + getresult.get("coin"));
            button.setPrefSize(126, 63);

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    GetInfocoins(button.getText(), moedas);
                }
            });

            pane.getChildren().add(button);

        }
    }

    private void GetInfocoins(String text, JSONArray moedas) {

        final String[] moeda = text.split("-");
        cripto_label.setStyle("-fx-text-fill:black");

        for (int j = 0; j < moedas.size(); j++) {

            JSONObject getmodresult = (JSONObject) moedas.get(j);

            if (getmodresult.get("coin").toString().trim().equalsIgnoreCase(moeda[1].trim())) {
                retiradacripto.setText("Retirada Total: " + getmodresult.get("retirada_total"));
                retiradaacessivel.setText("Retirada Acessivel: " + getmodresult.get("retirada_acessivel"));
                openordens.setText("Ordens Abertas: " + getmodresult.get("ordens_open"));
                criptototal.setText("" + getmodresult.get("valor"));
                moedalabel.setText(text);

                movimentacaocoin.setItems(null);

                cripto_progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                cripto_label.setText("Buscando Movimentos Moeda " + moeda[1].toUpperCase() + ". Aguarde ... ");
                conecta.setDisable(true);

                Task coinsmov = new Task() {
                    @Override
                    protected ArrayList call() throws Exception {

                        Exchange_MercadoBitcoin ex = new Exchange_MercadoBitcoin();

                        return ex.Listordens(mbTapiCode, mbTapiKey, moeda[1].trim().toUpperCase());

                    }

                    @Override
                    protected void succeeded() {
                        ObservableList<Coinmovi> dataCoins = FXCollections.observableArrayList();
                        String msg = "";

                        try {
                            ArrayList<arraymovi> lista = (ArrayList) get();

                            for (arraymovi row : lista) {

                                if (row.status.trim().equalsIgnoreCase("error") == false) {

                                    dataCoins.add(new Coinmovi(row.ordem,
                                            row.moeda,
                                            row.tipo_ordem,
                                            row.status,
                                            row.preco,
                                            row.precorun,
                                            row.qtd,
                                            row.qtdrun,
                                            row.comissao,
                                            row.dataexec
                                    ));
                                } else {
                                    msg = row.ordem;
                                    cripto_label.setStyle("-fx-text-fill:red");

                                }

                            }

                            movimentacaocoin.setItems(null);
                            movimentacaocoin.setItems(dataCoins);

                        } catch (InterruptedException ex) {
                            System.err.println(ex);
                        } catch (ExecutionException ex) {
                            System.err.println(ex);
                        }

                        conecta.setDisable(false);
                        cripto_label.setText(msg);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                cripto_progress.setProgress(0);
                            }
                        });

                    }
                };

                Thread movicoin = new Thread(coinsmov);
                movicoin.setDaemon(true);
                movicoin.start();
            }

        }

    }

    //busca as carteiras cadastradas
    private void carteiras() {
        conn_db connect = new conn_db();
        Chave chvapp = new Chave();
        Securitycoin seguranca = new Descriptografa();

        ResultSet rs = null;
        ObservableList<String> retorno = FXCollections.observableArrayList();
        try {
            int id = 0;
            rs = connect.conn.createStatement().executeQuery("SELECT * FROM carteira where deletado = ''");
            while (rs.next()) {

                retorno.add(seguranca.decripta(rs.getBytes("moeda"), chvapp.getChv()) + " - " + seguranca.decripta(rs.getBytes("public_key"), chvapp.getChv())
                );
            }

        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erro na conexão com o banco. erro: " + ex);
            alert.showAndWait();
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Erro ao finalizar conexão com o banco. erro: " + ex);
                alert.showAndWait();
            }
        }

        carteira.setItems(retorno);

    }

    public class Coinmovi {

        private final SimpleStringProperty ordem;
        private final SimpleStringProperty moeda;
        private final SimpleStringProperty tipoordem;
        private final SimpleStringProperty status;
        private final SimpleStringProperty qtd;
        private final SimpleStringProperty qtdrun;
        private final SimpleStringProperty preco;
        private final SimpleStringProperty precorun;
        private final SimpleStringProperty comissao;
        private final SimpleStringProperty dataexec;

        private Coinmovi(String ordem, String moeda, String tipoordem, String status, String preco, String precorun, String qtd, String qtdrun, String comissao, String dataexec) {
            this.ordem = new SimpleStringProperty(ordem);
            this.moeda = new SimpleStringProperty(moeda);
            this.tipoordem = new SimpleStringProperty(tipoordem);
            this.status = new SimpleStringProperty(status);
            this.qtd = new SimpleStringProperty(qtd);
            this.qtdrun = new SimpleStringProperty(qtdrun);
            this.preco = new SimpleStringProperty(preco);
            this.precorun = new SimpleStringProperty(precorun);
            this.comissao = new SimpleStringProperty(comissao);
            this.dataexec = new SimpleStringProperty(dataexec);
        }

        public String getPreco() {
            return preco.get();
        }

        public void setPreco(String Preco) {
            this.preco.set(Preco);
        }

        public String getPrecorun() {
            return precorun.get();
        }

        public void setPrecorun(String Precorun) {
            this.precorun.set(Precorun);
        }

        public String getComissao() {
            return comissao.get();
        }

        public void setComissao(String Comissao) {
            this.comissao.set(Comissao);
        }

        public String getDataexec() {
            return dataexec.get();
        }

        public void setDataexec(String Dataexec) {
            this.dataexec.set(Dataexec);
        }

        public String getQtd() {
            return qtd.get();
        }

        public void setQtd(String Qtd) {
            this.qtd.set(Qtd);
        }

        public String getQtdrun() {
            return qtdrun.get();
        }

        public void setQtdrun(String Qtdrun) {
            this.qtdrun.set(Qtdrun);
        }

        public String getOrdem() {
            return ordem.get();
        }

        public void setOrdem(String Ordem) {
            this.ordem.set(Ordem);
        }

        public String getMoeda() {
            return moeda.get();
        }

        public void setMoeda(String Moeda) {
            this.moeda.set(Moeda);
        }

        public String getTipoordem() {
            return tipoordem.get();
        }

        public void setTipoordem(String Tipoordem) {
            this.tipoordem.set(Tipoordem);
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String Status) {
            this.status.set(Status);
        }

    }
}
