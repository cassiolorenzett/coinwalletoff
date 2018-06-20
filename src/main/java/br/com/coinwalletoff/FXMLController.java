package br.com.coinwalletoff;

import br.com.coinwalletoff.Conexaodb.conn_db;
import br.com.coinwalletoff.Util.Chave;
import br.com.coinwalletoff.Util.Runbackup;
import coinwalletoff.security_coin.Descriptografa;
import coinwalletoff.security_coin.Protegeapp;
import coinwalletoff.security_coin.Securitycoin;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import sun.applet.Main;

/**
* Classe Controller da interface FXML Scene.fxml
* @author Cassio Dal Castel Lorenzett
* @version 1.0
* @since  2018-06-14
* @see Initializable
*/

public class FXMLController implements Initializable {

    private PreparedStatement ps_;
    private static boolean carrega = true;
    private static String API_BRAZILIEX = "https://braziliex.com/api/v1/public/ticker";
    private static boolean refresh = true;
    private static boolean erro = false;
    Timer tme = new Timer();
    @FXML
    private Accordion exib_preco;
    private int i = 0, j = 0;

    @FXML
    private Label btc_prc;

    @FXML
    private Label btc_vol;

    @FXML
    private Label bch_prc;

    @FXML
    private Label bch_vol;

    @FXML
    private Label eth_prc;

    @FXML
    private Label eth_vol;

    @FXML
    private Label das_prc;

    @FXML
    private Label das_vol;

    @FXML
    private Label sng_prc;

    @FXML
    private Label sng_vol;

    @FXML
    private Label ltc_prc;

    @FXML
    private Label ltc_vol;

    @FXML
    private Label xmr_prc;

    @FXML
    private Label xmr_vol;

    @FXML
    private Label mxt_prc;

    @FXML
    private Label mxt_vol;

    @FXML
    private Label btc_var;

    @FXML
    private Label bch_var;

    @FXML
    private Label eth_var;

    @FXML
    private Label das_var;

    @FXML
    private Label sng_var;

    @FXML
    private Label ltc_var;

    @FXML
    private Label xmr_var;

    @FXML
    private Label mxt_var;
    @FXML
    private Button add_cart;
    @FXML
    private Button bt_blockchain;
    @FXML
    private Button del_cart;
    @FXML
    private Button refresh_but;
    @FXML
    private Button alt_cart;
    @FXML
    private Button sair;
    @FXML
    private Button refreshcoin;
    @FXML
    private Button config;
    @FXML
    private Button security;
    @FXML
    private Button backup;
    @FXML
    private ImageView img_security;
    @FXML
    private TableView<Coin> table_cart;
    @FXML
    private TableColumn<Coin, String> table_public;
    @FXML
    private TableColumn<Coin, String> table_private;
    @FXML
    private TableColumn<Coin, String> table_exchenge;
    @FXML
    private TableColumn<Coin, String> table_moeda;
    @FXML
    private TableColumn<Coin, String> table_id;
    @FXML
    private ImageView img1;
    @FXML
    private ImageView img2;
    @FXML
    private ImageView img3;
    @FXML
    private ImageView img4;
    @FXML
    private ImageView img5;
    @FXML
    private ImageView img6;
    @FXML
    private ImageView img7;
    @FXML
    private ImageView img8;
    @FXML
    private ProgressBar progress;
    @FXML
    private Label status;
    @FXML
    private HBox buttom;
    
    
    /**
   * Evento no botão Blockchain exibir a interface FXML blockchain_info.fxml
   * @param event 
   * @return void
   * @see IOException
   */
    @FXML
    private void blockchain(ActionEvent event) throws IOException {
        Stage block = new Stage();
        Parent root_block = FXMLLoader.load(getClass().getResource("/fxml/blockchain_info.fxml"));

        Scene scene1 = new Scene(root_block);

        block.setTitle("Carteira Coin");
        block.setResizable(false);
        //block.setAlwaysOnTop(true);
        block.setScene(scene1);
        block.show();
    }
    
     /**
   * Evento no botão Bloquear/Desbloquear 
   * Solicita senha de segurança para Desbloquear as informações da aplicação
   * ou pode ser usado para Bloquear a mesma.
   * @param event 
   * @return void
   */
    @FXML
    private void actionseg(ActionEvent event) {
        Chave chvapp = new Chave();
        if (security.getText().equalsIgnoreCase("desbloquear")) {
            
            PasswordField password = new PasswordField();
            password.setPromptText("Senha");
            
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Acesso restrito !");
            dialog.setHeaderText("Acesso restrito, entre com a senha para liberação !");
           
           
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
           
            grid.add(new Label("Senha:"), 0, 1);
            grid.add(password, 1, 1);
          
            ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
            
            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>("", password.getText());
                }
                return null;
            });

            dialog.getDialogPane().setContent(grid);
            
            Platform.runLater(() -> password.requestFocus());
            
            Optional<Pair<String, String>> result = dialog.showAndWait();
            
           result.ifPresent(valPassword -> {
                
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("Carregando informações, Aguarde ....");
                alert1.show();
               
                Securitycoin seccoin = new Protegeapp();
                String hexPassword = seccoin.protegeapp(valPassword.getValue());

                if (hexPassword.toString().toLowerCase().trim().equalsIgnoreCase(getsenha())) {
                    add_cart.setDisable(false);
                    del_cart.setDisable(false);
                    alt_cart.setDisable(false);
                    backup.setDisable(false);
                    refresh_but.setDisable(false);
                    config.setDisable(false);
                    bt_blockchain.setDisable(false);
                    
                    
                    Image image = new Image(Main.class.getResourceAsStream("/img/bloqueia.png"));
                    img_security.setImage(image);
                    security.setText("Bloquear");

                    chvapp.setChv(valPassword.getValue());

                    listaDeCoins();

                    alert1.close();

                } else {
                    alert1.close();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Erro na autenticação !");
                    alert.setTitle("Erro ");
                    alert.showAndWait();
                }
            });
        }
        else {

            add_cart.setDisable(true);
            del_cart.setDisable(true);
            alt_cart.setDisable(true);
            refresh_but.setDisable(true);
            backup.setDisable(true);
            config.setDisable(true);
            bt_blockchain.setDisable(true);

            Image image = new Image(Main.class.getResourceAsStream("/img/desbloqueia.png"));
            img_security.setImage(image);
            security.setText("Desbloquear");
            table_cart.setItems(null);
            chvapp.setChv("");

        }

    }
    
     /**
   * Função que retorna a senha de segurança cadastrada na base de dados.
   * ou pode ser usado para Bloquear a mesma.
   * @return String
   */
    private String getsenha() {
        conn_db conect = new conn_db();
        conect.conectar();

        String senha = "";
        ResultSet rs = null;
        try {
            rs = conn_db.conn.createStatement().executeQuery("SELECT * FROM acesso where id = 1");
            while (rs.next()) {

                senha = rs.getString("senha").trim();
            }

        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro na autenticação !");
            alert.setTitle("Erro ");
            alert.setContentText(""+ex);
            alert.showAndWait();
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro na autenticação !");
                alert.setTitle("Erro ");
                alert.setContentText(""+ex);
                alert.showAndWait();
            }
        }

        return senha;
    }
    
     /**
   * Evento dobotão Add Carteira(adicionar novo registro na base de dados) que exibe a interface FXML add_alt.fxml
   * @param event
   * @see IOException
   * @return String
   */
    @FXML
    private void add_cart_action(ActionEvent event) throws IOException {

        Stage stage1 = new Stage();
        Parent root1 = FXMLLoader.load(getClass().getResource("/fxml/add_alt.fxml"));

        Scene scene1 = new Scene(root1);

        stage1.setTitle("Carteira Coin");
        stage1.setResizable(false);
        stage1.setScene(scene1);
        stage1.show();

    }
    
    /**
   * Evento do botão Del Carteira(deleta registro na base de dados)
   * @param event
   * @return void
   */
    @FXML
    private void del_cart_action(ActionEvent event) {

        if (table_cart.getSelectionModel().getSelectedItem() != null) {
            TablePosition pos = table_cart.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();

            String id = table_cart.getItems().get(row).getId();

            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "",
                    ButtonType.YES, ButtonType.NO);
            alert.setHeaderText("Deseja excluir o registro ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES) {

                conn_db connect = new conn_db();
                connect.conectar();
                ResultSet rs = null;

                try {
                    String query = "UPDATE carteira SET deletado = '*' where id = ?";
                    PreparedStatement preparedStmt = conn_db.conn.prepareStatement(query);
                    preparedStmt.setInt(1, Integer.parseInt(id));

                    // execute the preparedstatement
                    preparedStmt.executeUpdate();
                    preparedStmt.close();

                    listaDeCoins();

                } catch (SQLException ex) {
                    Alert alertdel = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Exclusão registro");
                    alert.setContentText("Erro ao Excluir, Erro: " + ex);
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION,
                    "",
                    ButtonType.YES);
            alert1.setHeaderText("Selecione um registro !");
            alert1.showAndWait();
        }
    }

    /**
   * Evento do botão Refresh Tab(atualiza os itens da tabela de carteiras na tela)
   * @param event
   * @return void
   */
    @FXML
    private void atualizatab(ActionEvent event) {

        listaDeCoins();
    }

     /**
   * Evento do botão Configurações onde exibe a interface FXML config.fxml
   * @param event
   * @return void
   */
    @FXML
    private void config(ActionEvent event) {

        Stage stage2 = new Stage();
        FXMLLoader loader1 = new FXMLLoader();
        loader1.setLocation(Main.class.getResource("/fxml/config.fxml"));
        Parent root = null;
        try {
            root = loader1.load();
        } catch (IOException ex) {
             Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Execução tela configurações");
                alert.setContentText("Erro ao abrir tela , Erro: " + ex);
                alert.showAndWait();
        }

        stage2.setTitle("Configurações");
        stage2.setScene(new Scene(root));
        stage2.setResizable(false);
        //stage2.setAlwaysOnTop(true);
        stage2.show();

    }

    /**
   * Evento do botão Alt Carteira(altera registro na base de dados) que exibe a interface FXML add_alt.fxml
   * @param event
   * @return void
   */
    @FXML
    private void alt_cart_action(ActionEvent event) {
        if (table_cart.getSelectionModel().getSelectedItem() != null) {
            TablePosition pos = table_cart.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();

            String id = table_cart.getItems().get(row).getId();
            Stage stage1 = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/add_alt.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Execução tela manutenção");
                alert.setContentText("Erro ao abrir tela , Erro: " + ex);
                alert.showAndWait();
            }

            stage1.setTitle("Manutenção Coin");
            stage1.setScene(new Scene(root));
            stage1.setResizable(false);
            stage1.show();

            Add_altController controller = loader.getController();
            try {
                controller.update(id);
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Seleção update");
                alert.setContentText("Erro ao atualizar, Erro: " + ex);
                alert.showAndWait();
            }
        }
    }

    /**
   * Evento do botão Sair onde finaliza a aplicação com possibilidade de backup ao sair
   * @param event
   * @return void
   */
    @FXML
    private void sair_action(ActionEvent event) {
     
     //1 - faz backup e finaliza
     //0 = faz backup
     backup(1);
        
    }
    
    /**
   * Evento do botão Refresh Preço onde atualiza o preço das altcois na tela
   * @param event
   * @return void
   */
    @FXML
    private void atualizapreco(ActionEvent event) {

        status.setStyle("-fx-text-fill:blue");
        status.setText("Aguarde, buscando preços Coins");
        //função que atualiza preço altcois
        carregapreco();
    }

    /**
   * Evento do botão backup onde efetua a copia das carteiras para a conta do google drive cadastrada
   * @param event
   * @return void
   */
    @FXML
    private void backup(ActionEvent event) {
        
        //1 - faz backup e finaliza
        //0 = faz backup
        backup(0);
    }

    /**
   * Função de inicialização da tela/interface Scene.fxml
   * @param url
   * @param rb
   * @return void
   */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
             
        //inicializa objeto do grid de carteiras
        this.table_id.setCellValueFactory(new PropertyValueFactory<Coin, String>("id"));
        this.table_public.setCellValueFactory(new PropertyValueFactory<Coin, String>("end_public"));
        this.table_private.setCellValueFactory(new PropertyValueFactory<Coin, String>("end_private"));
        this.table_exchenge.setCellValueFactory(new PropertyValueFactory<Coin, String>("exchenge"));
        this.table_moeda.setCellValueFactory(new PropertyValueFactory<Coin, String>("moeda"));

        //desabilita botões 
        add_cart.setDisable(true);
        del_cart.setDisable(true);
        alt_cart.setDisable(true);
        backup.setDisable(true);
        refresh_but.setDisable(true);
        config.setDisable(true);
        bt_blockchain.setDisable(true);
        
        //seta imagem e texto do botão de desbloqueio/bloqueio
        Image image = new Image(Main.class.getResourceAsStream("/img/desbloqueia.png"));
        img_security.setImage(image);
        security.setText("Desbloquear");
        
        //carrega função para buscar preço das altcoins. Seta intervalo de execução automatico de 3 minutos.
        status.setStyle("-fx-text-fill:blue");
        status.setText("Aguarde, buscando preços Coins");

        tme.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                carregapreco();
            }
        }, 0, 300000);

    }

     /**
   * Busca as carteiras cadatradas na base de dados, e seta as mesmas no grid para serem exibidas
   * @return void
   */
    private void listaDeCoins() {
        ObservableList<Coin> dataNotes = FXCollections.observableArrayList();
        conn_db connect = new conn_db();
        connect.conectar();
        ResultSet rs = null;

        Chave chvapp = new Chave();
        Securitycoin seguranca = new Descriptografa();

        Integer i = 0;
        try {
            rs = conn_db.conn.createStatement().executeQuery("SELECT * FROM carteira where deletado = ''");
            while (rs.next()) {
                
                dataNotes.add(new Coin(rs.getString("id"),
                        seguranca.decripta(rs.getBytes("public_key"), chvapp.getChv()),
                        seguranca.decripta(rs.getBytes("private_key"), chvapp.getChv()),
                        seguranca.decripta(rs.getBytes("moeda"), chvapp.getChv()),
                        seguranca.decripta(rs.getBytes("exchenge"), chvapp.getChv())));
            }
            table_cart.setItems(null);
            table_cart.setItems(dataNotes);
        } catch (SQLException ex) {
             Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Seleção Coins");
            alert.setContentText("Erro ao selecionar, Erro_1: " + ex);
            alert.showAndWait();
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                 Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Seleção Coins");
                alert.setContentText("Erro ao selecionar, Erro_1: " + ex);
                alert.showAndWait();
            }
        }

    }

    /**
   * Classe responsavel em setar e pegar os valores no grid na tela atraves de objetos
   * É utilizada dentro dafunção listaDeCoins()
   */
    public class Coin {

        private final SimpleStringProperty id;
        private final SimpleStringProperty end_public;
        private final SimpleStringProperty end_private;
        private final SimpleStringProperty moeda;
        private final SimpleStringProperty exchenge;

        private Coin(String id, String endpublic, String endprivate, String moeda, String exchen) {
            this.id = new SimpleStringProperty(id);
            this.end_public = new SimpleStringProperty(endpublic);
            this.end_private = new SimpleStringProperty(endprivate);
            this.moeda = new SimpleStringProperty(moeda);
            this.exchenge = new SimpleStringProperty(exchen);
        }

        public String getId() {
            return id.get();
        }

        public SimpleStringProperty idProperty() {
            return id;
        }

        public void setId(String Id) {
            this.id.set(Id);
        }

        public String getEndpublic() {
            return end_public.get();
        }

        public SimpleStringProperty end_publicProperty() {
            return end_public;
        }

        public void setEndpublic(String Endpublic) {
            this.end_public.set(Endpublic);
        }

        public String getEnd_private() {
            return end_private.get();
        }

        public SimpleStringProperty end_privateProperty() {
            return end_private;
        }

        public void setEnd_private(String End_private) {
            this.end_private.set(End_private);
        }

        public String getExchenge() {
            return exchenge.get();
        }

        public SimpleStringProperty exchengeProperty() {
            return exchenge;
        }

        public void setExchenge(String Exchenge) {
            this.exchenge.set(Exchenge);
        }

        public String getMoeda() {
            return moeda.get();
        }

        public SimpleStringProperty moedaProperty() {
            return moeda;
        }

        public void setMoeda(String Moeda) {
            this.moeda.set(Moeda);
        }

    }
    
    /**
   * Utilizado para se conectar a API da braziliex e retornar as moedas e suas informações.
   * É utilizada dentro dafunção carregapreco()
   * @param  url
   * @param  body
   * @return JSONObject
   */
    private JSONObject http(String url, String body) {
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

                refresh = false;


                /* if (resultObject instanceof JSONArray) {
                JSONArray array=(JSONArray)resultObject;
                for (Object object : array) {
                JSONObject obj =(JSONObject)object;
                }
                }else if (resultObject instanceof JSONObject) {
                JSONObject obj =(JSONObject)resultObject;
                String nome = (String) obj.get("active");
                System.err.println(nome);
                }
                 */
            } catch (Exception e) {

                //i++;
                //status.setText("Erro na leitura do json " + e);
                //System.err.println("Erro na leitura do json " + i);
                //JOptionPane.showMessageDialog(null, "Erro na leitura do json " + e);
            }

        } catch (IOException ex) {
            //j++;
            //status.setText("Erro ao iniciar servidor http: " + ex);
            //System.err.println("Erro ao iniciar servidor http: " + j);
            //JOptionPane.showMessageDialog(null, "Erro ao iniciar servidor http: " + ex);
        }

        return obj;
    }

     /**
   * Carrega os preços das altcoins na tela.
   * Utiliza a comunicação com a API da braziliex para pegar as moedas e seus dados e tratalos para 
   * exibir na tela.
   * @return void
   */
    private void carregapreco() {
        refreshcoin.setDisable(true);

        Task tarefaCargaPg = new Task() {
            JSONObject retorno = null;

            @Override
            protected JSONObject call() throws Exception {

                Double o = 0.37;
                Platform.runLater(new Runnable() {
                    public void run() {
                        progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }
                });

                refresh = true;
                while (carrega) {

                    retorno = http(API_BRAZILIEX, "");

                    if (i >= 160 || j >= 160) {
                        erro = true;
                        break;

                    }

                    if (o.intValue() == 1) {
                        o = 0.37;
                    } else {
                        o += 0.1;
                        img1.setOpacity(o);
                        img2.setOpacity(o);
                        img3.setOpacity(o);
                        img4.setOpacity(o);
                        img5.setOpacity(o);
                        img6.setOpacity(o);
                        img7.setOpacity(o);
                        img8.setOpacity(o);

                        Thread.sleep(150);

                    }

                    if (refresh == false) {
                        img1.setOpacity(1);
                        img2.setOpacity(1);
                        img3.setOpacity(1);
                        img4.setOpacity(1);
                        img5.setOpacity(1);
                        img6.setOpacity(1);
                        img7.setOpacity(1);
                        img8.setOpacity(1);

                        break;

                    }

                }

                return retorno;

            }

            @Override
            protected void failed() {

                progress.setProgress(0);
                status.setStyle("-fx-text-fill:red");
                status.setText("Erro ao buscar preços");
                refreshcoin.setDisable(false);

            }

            @Override
            protected void succeeded() {

                if (erro == false) {
                    DecimalFormat format = new DecimalFormat("###,###.#");
                    
                    //joga os valores da API na tela.
                    
                    JSONObject valores = (JSONObject) getValue();
                    //bitcoin
                    JSONObject btc = (JSONObject) valores.get("btc_brl");
                    btc_prc.setText(format.format(Double.parseDouble((String) btc.get("last"))));
                    btc_vol.setText(format.format(Double.parseDouble((String) btc.get("quoteVolume24"))));
                    btc_var.setText(format.format(Double.parseDouble((String) btc.get("percentChange"))));

                    //bicoin cash
                    JSONObject bch = (JSONObject) valores.get("bch_brl");
                    bch_prc.setText(format.format(Double.parseDouble((String) bch.get("last"))));
                    bch_vol.setText(format.format(Double.parseDouble((String) bch.get("quoteVolume24"))));
                    bch_var.setText(format.format(Double.parseDouble((String) bch.get("percentChange"))));

                    //ethereum
                    JSONObject eth = (JSONObject) valores.get("eth_brl");
                    eth_prc.setText(format.format(Double.parseDouble((String) eth.get("last"))));
                    eth_vol.setText(format.format(Double.parseDouble((String) eth.get("quoteVolume24"))));
                    eth_var.setText(format.format(Double.parseDouble((String) eth.get("percentChange"))));

                    //dash
                    JSONObject dash = (JSONObject) valores.get("dash_brl");
                    das_prc.setText(format.format(Double.parseDouble((String) dash.get("last"))));
                    das_vol.setText(format.format(Double.parseDouble((String) dash.get("quoteVolume24"))));
                    das_var.setText(format.format(Double.parseDouble((String) dash.get("percentChange"))));

                    //sngls
                    JSONObject sngls = (JSONObject) valores.get("sngls_brl");
                    sng_prc.setText(format.format(Double.parseDouble((String) sngls.get("last"))));
                    sng_vol.setText(format.format(Double.parseDouble((String) sngls.get("quoteVolume24"))));
                    sng_var.setText(format.format(Double.parseDouble((String) sngls.get("percentChange"))));

                    //ltc
                    JSONObject ltc = (JSONObject) valores.get("ltc_brl");
                    ltc_prc.setText(format.format(Double.parseDouble((String) ltc.get("last"))));
                    ltc_vol.setText(format.format(Double.parseDouble((String) ltc.get("quoteVolume24"))));
                    ltc_var.setText(format.format(Double.parseDouble((String) ltc.get("percentChange"))));

                    //xmr
                    JSONObject xmr = (JSONObject) valores.get("xmr_brl");
                    xmr_prc.setText(format.format(Double.parseDouble((String) xmr.get("last"))));
                    xmr_vol.setText(format.format(Double.parseDouble((String) xmr.get("quoteVolume24"))));
                    xmr_var.setText(format.format(Double.parseDouble((String) xmr.get("percentChange"))));

                    //mxt
                    JSONObject mxt = (JSONObject) valores.get("mxt_brl");
                    mxt_prc.setText(format.format(Double.parseDouble((String) mxt.get("last"))));
                    mxt_vol.setText(format.format(Double.parseDouble((String) mxt.get("quoteVolume24"))));
                    mxt_var.setText(format.format(Double.parseDouble((String) mxt.get("percentChange"))));

                    progress.setProgress(0);
                    status.setStyle("-fx-text-fill:blue");
                    status.setText("Preços coins OK");
                    refreshcoin.setDisable(false);

                } else if (erro == true) {
                    progress.setProgress(0);
                    status.setStyle("-fx-text-fill:red");
                    status.setText("Erro ao buscar preços das moedas.");
                    refreshcoin.setDisable(false);
                    i = 0;
                    j = 0;
                    erro = false;
                }

            }
        };
        Thread t = new Thread(tarefaCargaPg);
        t.setDaemon(true);
        t.start();

    }

   
    
   /**
   * Efetua o backup das carteiras cadastradas paraa conta do google drive cadastrada
   * @param opc  
   * 0 - Efetua somente backup
   * 1 - Efetua  backup e finaliza aplicação
   * @return void
   */
    private void backup(int opc)
    {
            status.setStyle("-fx-text-fill:blue");
            status.setText("Efetuando Backup.Aguarde ...");
            backup.setDisable(true);
            progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            
            Task sinc = new Task() {
                @Override
                protected Object call() throws Exception {
                    String ret = "";
                    Runbackup runcop = new Runbackup();
                    ret =  runcop.Executa(opc);
                    return ret;
                }
                
            @Override
            protected void succeeded() {
                
                if (getValue().toString().isEmpty()) {
                           
                            status.setStyle("-fx-text-fill:blue");
                            status.setText("Backup efetuado com sucesso !");
                            if (opc != 1){
                                backup.setDisable(false);
                            }
                           
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    progress.setProgress(0);
                                }
                            });
                        } else {
                           
                            status.setStyle("-fx-text-fill:red");
                            status.setText("Falha ao efetuar Backup !");
                            if (opc != 1){
                                backup.setDisable(false);
                            }
                            
                            
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    progress.setProgress(0);
                                }
                            });
                            
                            if (!getValue().toString().trim().equalsIgnoreCase("IGNORA")){
                                Alert errosinc = new Alert(Alert.AlertType.ERROR);
                                errosinc.setHeaderText("ATENÇÃO");
                                errosinc.setContentText(getValue().toString());
                                errosinc.showAndWait();
                            }
                        }
                
            }
                
           
            };
            Thread thrsinc = new Thread(sinc);
            thrsinc.setDaemon(true);
            thrsinc.start();

        
    }
}
