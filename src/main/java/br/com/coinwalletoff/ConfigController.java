package br.com.coinwalletoff;

import br.com.coinwalletoff.Conexaodb.conn_db;
import br.com.coinwalletoff.Util.Chave;
import br.com.coinwalletoff.Util.Getcredencialgdrive;
import br.com.coinwalletoff.Util.Googledrive;
import coinwalletoff.security_coin.Criptografa;
import coinwalletoff.security_coin.Descriptografa;
import coinwalletoff.security_coin.Securitycoin;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.File;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Base64;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
* Classe Controller da interface FXML config.fxml
* Responsavel por gravar algumas configurações para aplicação trabalhar
* 1 - habilita/desabilita bacup automatico ao sair da aplicação
* 2 - grava token da API do google drive para o backup
* 3 - baixa as carteiras presente no google drive caso existam para serem descompactadas e sincronizadas na aplicação
* 4 - Cadastro das chaves de API de exchenges para comunicação com as mesmas para buscar informaçõe da conta.atualmente 
* somente API do Mercado Bitcoin esta homologada.
* @author Cassio Dal Castel Lorenzett
* @version 1.0
* @since  2018-06-14
* @see Initializable
*/
public class ConfigController implements Initializable {

    private static String caminho = "";
    @FXML
    private Button sair_conf;
    @FXML
    private Button salva_conf;
    @FXML
    private Tab tab_backup;
    @FXML
    private TextField cliente_id;
    @FXML
    private TextArea _status;

    @FXML
    private TableView<Coinbackup> table_backup;

    @FXML
    private TableColumn<Coinbackup, String> table_moeda;

    @FXML
    private TableColumn<Coinbackup, String> table_exchenge;

    @FXML
    private TableColumn<Coinbackup, String> table_public;

    @FXML
    private TableColumn<Coinbackup, String> table_private;
    
    @FXML
    private TableColumn<Coinbackup, String> table_obs;

    @FXML
    private PasswordField cliente_secret;
    @FXML
    private Button ativa_sinc_conf;
    @FXML
    private Tab tab_exchenge;
    @FXML
    private Tab tab_sincr_dados;
    @FXML
    private Button baixa_gdrive;

    @FXML
    private Button sinc_dados_local;

    @FXML
    private CheckBox chec_gdrive;

    @FXML
    private CheckBox auto_backup_exit;

    @FXML
    private Label label_status;

    @FXML
    private TableView<Coinaddapi> table_api;

    @FXML
    private TableColumn<Coinaddapi, String> exchenge;

    @FXML
    private TableColumn<Coinaddapi, String> identificador;

    @FXML
    private TableColumn<Coinaddapi, String> segredo;

    @FXML
    private ChoiceBox<String> api_exchenge;

    @FXML
    private TextField api_identificador;

    @FXML
    private TextField api_segredo;

    @FXML
    private Button api_bt_salva;

    @FXML
    private Button api_deleta;

    @FXML
    private Button api_limpa;

    private PreparedStatement ps_;
    private static String id = "";
    
    
    /**
   * Deixa o componente checkbox responsavel por habilitar/desabilitar backup automatico ao sair da aplicação
   * selecionado ou não com base no cadastro do banco de dados.
   */
    private void setAuto_backup()
    {
        conn_db conn = new conn_db();
        conn.conectar();
        ResultSet rs = null;

        try {
            Securitycoin seguranca = new Descriptografa();
            Chave chvapp = new Chave();

            rs = conn_db.conn.createStatement().executeQuery("select * from auto_backup where id = 1");
            while (rs.next()) {

                if (rs.getInt("auto_backup") == 1) {
                    auto_backup_exit.setSelected(true);
                }
                else
                {
                    auto_backup_exit.setSelected(false);
                }

            }

        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Seleção autobackup");
            alert.setContentText("Erro ao selecionar, Erro_1: " + ex);
            alert.showAndWait();
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Seleção autobackup");
                alert.setContentText("Erro ao selecionar, Erro_2: " + ex);
                alert.showAndWait();
            }
        }
    }
    
     /**
   * Evento do click no componente checkbox.
   * Gravar na base de dados se foi habilitado ou não o backup ao sair
   * @param  event
   */
    @FXML
    void auto_backup_exit(ActionEvent event) {
        int valor = 0;

        if (auto_backup_exit.isSelected()) {
            valor = 1;
        } else {
            valor = 0;
        }

        conn_db connect = new conn_db();
        connect.conectar();

        try {
            ps_ = connect.conn.prepareStatement("update auto_backup set auto_backup = ? ");
            ps_.setInt(1, valor);

            ps_.executeUpdate();
            

            ps_.close();
        } catch (Exception ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Atualização");
            alert.setContentText("Erro ao Atualizar, Erro_1: " + ex);
            alert.showAndWait();
        }
        try {
            ps_.close();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Atualização");
            alert.setContentText("Erro ao Atualizar, Erro_2: " + ex);
            alert.showAndWait();
        }

    }
    
     /**
   * Evento do click na tabela de cadastro de exchenges
   * Preenche os campos API KEY e SEGREDO com base na linha clicada na tabela;
   * @param  event
   */
    @FXML
    void get_click_tabapi(MouseEvent event) {

        Coinaddapi row = table_api.getSelectionModel().getSelectedItem();
        this.id = row.tb_apiexchenge.get().split("-")[0].trim();
        api_identificador.setText(row.tb_apiidentificador.get());
        api_segredo.setText(row.tb_apisegredo.get());
        this.api_deleta.setDisable(false);
    }

    /**
   * Função que limpa os campos ligados aocadastro de exchenge na aba echenge.
   */
    private void clear() {
        this.id = "";
        api_identificador.setText("");
        api_segredo.setText("");
        this.api_deleta.setDisable(true);
        api_exchenge.getSelectionModel().clearSelection();
    }
    
    /**
   * Evento do botão Incluir/Editar da aba exchenge.
   * Grava ou altera  APIs de exchenges na base de dados
   * OBS: Exchenge homologada até agora: Mercado Bitcoin
   * @param event
   */
    @FXML
    void api_bt_salva(ActionEvent event) {

        String[] getret = null;

        if (api_exchenge.getSelectionModel().getSelectedItem() != null
                && api_identificador.getText().trim().isEmpty() == false
                && api_segredo.getText().trim().isEmpty() == false) {
            conn_db connect = new conn_db();
            connect.conectar();

            Securitycoin seguranca = new Criptografa();
            Chave chvapp = new Chave();

            getret = api_exchenge.getSelectionModel().getSelectedItem().trim().split("=");

            //inclusão
            if (this.id == "") {
                if (ver_cad_api(getret[0].trim()) == true) {

                    try {
                        ps_ = connect.conn.prepareStatement("insert into apis (id_exchenge, exchenge,identificador,segredo) values (?,?,?,?)");
                        ps_.setString(1, getret[0].trim());
                        ps_.setString(2, getret[1].trim());
                        ps_.setBytes(3, seguranca.encripta(api_identificador.getText().trim(), chvapp.getChv()));
                        ps_.setBytes(4, seguranca.encripta(api_segredo.getText().trim(), chvapp.getChv()));

                        ps_.executeUpdate();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Sucesso");
                        alert.setHeaderText("Cadastro Carteiras");
                        alert.setContentText("Cadastro efetuado com Sucesso !");
                        alert.showAndWait();

                        ps_.close();
                    } catch (Exception ex) {

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Cadastro Carteiras");
                        alert.setContentText("Erro ao cadastrar, Erro_1: " + ex);
                        alert.showAndWait();
                    }
                    try {
                        ps_.close();
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Cadastro Carteiras");
                        alert.setContentText("Erro ao cadastrar, Erro_2: " + ex);
                        alert.showAndWait();
                    }

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Já existe uma API cadastrada para a Exchenge " + getret[1].trim());
                    alert.showAndWait();
                }

            } else if (this.id.length() > 0) {
                try {
                    ps_ = connect.conn.prepareStatement("update apis set identificador = ? ,segredo = ? where id_exchenge = ?");
                    ps_.setBytes(1, seguranca.encripta(api_identificador.getText().trim(), chvapp.getChv()));
                    ps_.setBytes(2, seguranca.encripta(api_segredo.getText().trim(), chvapp.getChv()));
                    ps_.setString(3, this.id);

                    ps_.executeUpdate();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Sucesso");
                    alert.setHeaderText("Atualização Carteiras");
                    alert.setContentText("Atualização efetuada com Sucesso !");
                    alert.showAndWait();

                    ps_.close();
                } catch (Exception ex) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Atualização Carteiras");
                    alert.setContentText("Erro ao Atualizar, Erro_1: " + ex);
                    alert.showAndWait();
                }
                try {
                    ps_.close();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Atualização Carteiras");
                    alert.setContentText("Erro ao Atualizar, Erro_2: " + ex);
                    alert.showAndWait();
                }
            }

            get_apis_table();
            clear();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Necessário Preencher todos os campos.");
            alert.showAndWait();
        }

    }

    /**
   * Evento do botão Excluir da aba exchenge.
   * Deleta APIs de exchenges na base de dados
   * @param event
   */
    @FXML
    void api_deleta(ActionEvent event) {

        conn_db connect = new conn_db();
        connect.conectar();
        try {
            ps_ = connect.conn.prepareStatement("DELETE FROM apis WHERE id_exchenge = ? ");
            ps_.setString(1, this.id.trim());

            ps_.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Exclusão Carteiras");
            alert.setContentText("Exclusão efetuada com Sucesso !");
            alert.showAndWait();

            get_apis_table();
            clear();

            ps_.close();
        } catch (Exception ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Exclusão Carteiras");
            alert.setContentText("Erro ao Excluir, Erro_1: " + ex);
            alert.showAndWait();
        }
        try {
            ps_.close();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Exclusão Carteiras");
            alert.setContentText("Erro ao Excluir, Erro_2: " + ex);
            alert.showAndWait();
        }

    }

    /**
   * Evento do botão Limpar da aba exchenge.
   * Limpa campos na tela atraves da função clear()
   * @param event
   */
    @FXML
    void api_limpa(ActionEvent event) {
        clear();
    }

     /**
   * Evento do botão Ativa Sincronização da aba Conta(s) Sincronização.
   * Ativa API do google drive para backup das informações
   */
    @FXML
    private void ativa_sinc_conf() {

        if ((cliente_id.getText().trim().isEmpty() == false) && (cliente_secret.getText().trim().isEmpty() == false)) {
            try {
                Googledrive gdrive = new Googledrive();
                gdrive.setClientsecrets(cliente_id.getText().trim(), cliente_secret.getText().trim());
                String retorno = gdrive.ativaconta();
                salva_conf.setDisable(false);
                _status.setText("Conta ativada com sucesso !" + retorno);
            } catch (IOException ex) {
                salva_conf.setDisable(true);
                _status.setText("" + ex);
            }

        } else {

            _status.setStyle("-fx-border-color:red");
            _status.setText("Informações devem ser preenchidas ");

        }
    }

     /**
   * Evento do botão Salva da aba Conta(s) Sincronização.
   * Salva as chaves CLIENTE ID e CLIENT SECRET da API do google drive na base de dados
   */
    @FXML
    private void salva_conf() {

        conn_db connect = new conn_db();
        connect.conectar();

        if ((cliente_id.getText().trim().isEmpty() == false) && (cliente_secret.getText().trim().isEmpty() == false)) {
            try {

                Securitycoin seguranca = new Criptografa();
                Chave chvapp = new Chave();

                String query = "UPDATE gdrive SET cliente_id = ?, cliente_secret = ?, cliente_add = 1 where id = 1";
                PreparedStatement preparedStmt = conn_db.conn.prepareStatement(query);
                preparedStmt.setBytes(1, seguranca.encripta(cliente_id.getText().trim(), chvapp.getChv()));
                preparedStmt.setBytes(2, seguranca.encripta(cliente_secret.getText().trim(), chvapp.getChv()));

                // execute the preparedstatement
                preparedStmt.executeUpdate();
                preparedStmt.close();

                _status.setStyle("-fx-border-color:green");
                _status.setText("Informações foram salvas !");

            } catch (SQLException ex) {

                _status.setStyle("-fx-border-color:red");
                _status.setText("Erro ao salvar os dados: " + ex);
            }
        } else {

            _status.setStyle("-fx-border-color:red");
            _status.setText("Informações devem ser preenchidas ");

        }
    }

    /**
   * Evento do botão Sair na tela de configuração.
   * Sai da tela de configuraçõe
   */
    @FXML
    private void sair_conf() {
        Stage stage = (Stage) sair_conf.getScene().getWindow(); //Obtendo a janela atual
        stage.close();
    }

    /**
   * Função de inicialização da tela/interface config.fxml
   * @param url
   * @param rb
   * @return void
   */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.api_deleta.setDisable(true);

        //inicializa objeto do grid de carteiras vindas do google drive 
        this.table_public.setCellValueFactory(new PropertyValueFactory<Coinbackup, String>("endpublic"));
        this.table_private.setCellValueFactory(new PropertyValueFactory<Coinbackup, String>("endprivate"));
        this.table_exchenge.setCellValueFactory(new PropertyValueFactory<Coinbackup, String>("exchenge"));
        this.table_moeda.setCellValueFactory(new PropertyValueFactory<Coinbackup, String>("moeda"));
        this.table_obs.setCellValueFactory(new PropertyValueFactory<Coinbackup, String>("obs"));

        this.exchenge.setCellValueFactory(new PropertyValueFactory<Coinaddapi, String>("tb_apiexchenge"));
        this.identificador.setCellValueFactory(new PropertyValueFactory<Coinaddapi, String>("tb_apiidentificador"));
        this.segredo.setCellValueFactory(new PropertyValueFactory<Coinaddapi, String>("tb_apisegredo"));
        
        api_exchenge.setItems(FXCollections.observableArrayList(
                "1=Mercado Bitcoin"));

        salva_conf.setDisable(true);
        table_backup.setDisable(true);
        sinc_dados_local.setDisable(true);
        chec_gdrive.setDisable(true);
        getCredencial();
        get_apis_table();
        setAuto_backup();
    }

    
    /**
   * Busca as credenciais cadastradas na base de dados, caso estejam cadastradas.
   * Utilizado na função initialize()
   * @return void
   */
    private void getCredencial() {
        conn_db conn = new conn_db();
        conn.conectar();
        ResultSet rs = null;

        try {
            Securitycoin seguranca = new Descriptografa();
            Chave chvapp = new Chave();

            rs = conn_db.conn.createStatement().executeQuery("SELECT * FROM gdrive where id = 1");
            while (rs.next()) {

                if (rs.getInt("cliente_add") == 1) {
                    cliente_id.setText(seguranca.decripta(rs.getBytes("cliente_id"), chvapp.getChv()));
                    cliente_secret.setText(seguranca.decripta(rs.getBytes("cliente_secret"), chvapp.getChv()));
                }

            }

        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Seleção credencais");
            alert.setContentText("Erro ao selecionar, Erro_1: " + ex);
            alert.showAndWait();
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Seleção credencais");
                alert.setContentText("Erro ao selecionar, Erro_1: " + ex);
                alert.showAndWait();
            }
        }

    }

    /**
   * Evento do botão Baixar Informações na aba Sincronizar Dados
   * Baixa e extrai o backup de carteiras presenta no google drive para o grid na tela
   * @param event
   * @return void
   */
    @FXML
    private void baixa_gdrive(ActionEvent event) {

        if (validSinc() == true) {

            label_status.setStyle("-fx-text-fill:blue");
            label_status.setText("Baixando e extraindo dados, aguarde ... ");
            table_backup.setDisable(true);
            sinc_dados_local.setDisable(true);
            chec_gdrive.setDisable(true);
            baixa_gdrive.setDisable(true);
            Task getfile = new Task() {
                @Override
                protected Object call() throws Exception {
                    String passa = "";

                    try {
                        downloadFile();
                        PreencheTabela();
                        passa = "";
                    } catch (IOException ex) {
                        passa = "" + ex;
                    }

                    return passa;
                }

                @Override
                protected void succeeded() {
                    baixa_gdrive.setDisable(false);
                    if (getValue().toString().trim().isEmpty()) {

                        label_status.setStyle("-fx-text-fill:blue");
                        label_status.setText("Extração efetuada com sucesso !");

                        table_backup.setDisable(false);
                        sinc_dados_local.setDisable(false);
                        chec_gdrive.setDisable(false);

                    } else {
                        label_status.setStyle("-fx-text-fill:red");
                        label_status.setText("Erro na operação: " + getValue().toString());
                    }

                }

            };
            Thread thrback = new Thread(getfile);
            thrback.setDaemon(true);
            thrback.start();

        } else {
            Alert alertsinc = new Alert(Alert.AlertType.ERROR);
            alertsinc.setContentText("Verifique o cadastro da sua conta do Goole Drive, na tela de configuração de conta(s)."
                    + " Para validar o cadastro clique no botão de Configuração e entre na primeira aba.");
            alertsinc.showAndWait();
        }
    }
    
     /**
   * Evento do botão Sincronizar na aba Sincronizar Dados
   * Sincroniza os dados extraidos do backup com os dados locais cadastrados
   * @param event
   * @return void
   */
    @FXML
    private void sinc_dados_local(ActionEvent event) {

        label_status.setStyle("-fx-text-fill:blue");
        label_status.setText("Sincronizando dados, aguarde ... ");
        table_backup.setDisable(true);
        sinc_dados_local.setDisable(true);
        chec_gdrive.setDisable(true);
        baixa_gdrive.setDisable(true);
        Task sinc = new Task() {
            @Override
            protected Object call() throws Exception {

                String passa = "";

                conn_db conect = new conn_db();
                conect.conectar();

                XStream xstream = new XStream(new DomDriver());

                Securitycoin seg = new Descriptografa();
                Chave chapp = new Chave();
                xstream.alias("coins", ArrayList.class);
                xstream.processAnnotations(Coin.class);
                FileReader input;
                PreparedStatement ps_;

                String query = "UPDATE carteira SET deletado = '*' where id = ?";
                PreparedStatement preparedStmt = conn_db.conn.prepareStatement(query);

                try {
                    input = new FileReader(caminho + "/coinwalletoff.xml");
                    StringBuilder xml;
                    ArrayList<Coin> coinsret = (ArrayList) xstream.fromXML(input);

                    if (chec_gdrive.isSelected() == false) {
                        ps_ = conect.conn.prepareStatement("UPDATE carteira SET deletado = '*'");
                        ps_.executeUpdate();
                        ps_.clearParameters();

                    }

                    ps_ = conect.conn.prepareStatement("insert into carteira (private_key,public_key,exchenge,moeda, data_cad, hora_cad, dataalt, horaalt,deletado,obs) values (?,?,?,?,?,?,?,?,?,?)");

                    for (Coin coin : coinsret) {

                        ps_.setBytes(1, Base64.getDecoder().decode(coin.getPrivatekey()));
                        ps_.setBytes(2, Base64.getDecoder().decode(coin.getPublickey()));
                        ps_.setBytes(3, Base64.getDecoder().decode(coin.getExchenge()));
                        ps_.setBytes(4, Base64.getDecoder().decode(coin.getMoeda()));
                        ps_.setString(5, coin.getDatacad());
                        ps_.setString(6, coin.getHoracad());
                        ps_.setString(7, coin.getDataalt());
                        ps_.setString(8, coin.getHoraalt());
                        ps_.setString(9, "");
                        ps_.setBytes(10, Base64.getDecoder().decode(coin.getObs()));
                        ps_.addBatch();
                    }

                    ps_.executeBatch();
                    ps_.close();

                } catch (FileNotFoundException ex) {
                    System.err.println("Erro: " + ex);
                }

                return passa;
            }

            @Override
            protected void succeeded() {
                baixa_gdrive.setDisable(false);
                if (getValue().toString().trim().isEmpty()) {

                    label_status.setStyle("-fx-text-fill:blue");
                    label_status.setText("Sincronização efetuada com sucesso !");

                    table_backup.setDisable(true);
                    table_backup.setItems(null);
                    sinc_dados_local.setDisable(true);
                    chec_gdrive.setDisable(true);

                } else {
                    label_status.setStyle("-fx-text-fill:red");
                    label_status.setText("Erro na operação: " + getValue().toString());
                }

            }

        };
        Thread thrback = new Thread(sinc);
        thrback.setDaemon(true);
        thrback.start();
    }

    
    /**
   * Valida se o cadastro dos dados do google drive estão incluidosna base de dados.
   * @return boolean
   */
    private boolean validSinc() {
        conn_db connect = new conn_db();
        connect.conectar();
        ResultSet rs = null;
        boolean passa = true;

        try {
            rs = conn_db.conn.createStatement().executeQuery("SELECT * FROM gdrive where id=1");
            while (rs.next()) {

                if (rs.getInt("cliente_add") == 1) {
                    if ((rs.getString("cliente_id").trim().isEmpty()) || (rs.getString("cliente_secret").trim().isEmpty())) {
                        passa = false;
                    } else {
                        passa = true;
                    }

                } else {
                    passa = false;
                }
            }

        } catch (SQLException ex) {
            //System.err.println("erro: " + ex);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                //System.err.println("erro: " + ex);
            }
        }

        return passa;
    }
    
    /**
   * Baixa o arquivo de backup de carteira do google drive.
   * Presenta na função baixa_gdrive()
   * @return void
   * @see IOException
   */
    private void downloadFile() throws IOException {

        Getcredencialgdrive credenc = new Getcredencialgdrive();

        if (credenc.getCredencial().size() > 0) {

            Googledrive gdrive = new Googledrive();
            gdrive.setClientsecrets(credenc.getCredencial().get(0).toString(), credenc.getCredencial().get(1).toString());
            caminho = gdrive.getcaminho();
            Drive service = gdrive.getDrive();
            String pageToken = null;

            do {
                FileList result = service.files().list()
                        .setQ("title contains 'coinwalletoff' and trashed = false")
                        .setSpaces("drive")
                        .setFields("nextPageToken, items(id, title)")
                        .setPageToken(pageToken)
                        .execute();

                for (File file : result.getItems()) {

                    try {
                        File fileToDownload = service.files().get(file.getId()).execute();
                        GenericUrl u = new GenericUrl(fileToDownload.getDownloadUrl());
                        Get request = service.files().get(fileToDownload.getId());
                        //String name = fileToDownload.getTitle(); 
                        OutputStream bos = new FileOutputStream(gdrive.getcaminho() + "/coinwalletoff.xml");
                        MediaHttpDownloader mhd = request.getMediaHttpDownloader();

                        mhd.setDirectDownloadEnabled(true);
                        mhd.download(u, bos);
                        bos.close();

                    } catch (IOException e) {
                        Alert erro = new Alert(Alert.AlertType.ERROR);
                        erro.setContentText("erro ao efetuar o download do arquivo: " + e);
                        erro.showAndWait();
                    }

                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        }
    }

     /**
   * Valida se ja existe uma API cadastrada na base de dados.
   * Presenta na função api_bt_salva()
   * @param  id
   * @return boolean
   */
    private boolean ver_cad_api(String id) {
        boolean passa = false;
        try {
            PreparedStatement pstmt = conn_db.conn.prepareStatement("SELECT count(*) TOTAL FROM apis where id_exchenge = ?");
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {

                if (rs.getInt("TOTAL") > 0) {
                    passa = false;
                } else {
                    passa = true;
                }
            }
            pstmt.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erro na conexão com o banco. erro: " + e.getMessage());
            alert.showAndWait();
        }

        return passa;
    }

  
     /**
   * Busca as APIs cadatradas na base de dados, e seta as mesmas no grid para serem exibidas
   * Presente na função api_bt_salva()
   * @return void
   */
    private void get_apis_table() {
        ObservableList<Coinaddapi> dataNotesapi = FXCollections.observableArrayList();

        Securitycoin seg = new Descriptografa();
        Chave chapp = new Chave();

        try {
            PreparedStatement pstmt = conn_db.conn.prepareStatement("SELECT * FROM apis");
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {

                dataNotesapi.add(new Coinaddapi(rs.getString("id_exchenge").trim() + " - " + rs.getString("exchenge").trim(),
                        seg.decripta(rs.getBytes("identificador"), chapp.getChv()),
                        seg.decripta(rs.getBytes("segredo"), chapp.getChv())));

            }

            table_api.setItems(null);
            table_api.setItems(dataNotesapi);

            pstmt.close();
        } catch (SQLException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erro na conexão com o banco. erro: " + e.getMessage());
            alert.showAndWait();
        }

    }

     /**
   * Extrai os dados do backup baixados da conta do google drive para a tabela de cartiras da aba Sincronizar Dados
   * Presente na função baixa_gdrive()
   * @return void
   */
    private void PreencheTabela() {

        ObservableList<Coinbackup> dataNotes = FXCollections.observableArrayList();

        Securitycoin seg = new Descriptografa();
        Chave chapp = new Chave();

        XStream xstream = new XStream(new DomDriver());
        xstream.alias("coins", ArrayList.class);
        xstream.processAnnotations(Coin.class);

        try {
            FileReader input = new FileReader(caminho + "/coinwalletoff.xml");
            StringBuilder xml;
            ArrayList<Coin> coinsret = (ArrayList) xstream.fromXML(input);

            for (Coin coin : coinsret) {

                dataNotes.add(new Coinbackup(seg.decripta(Base64.getDecoder().decode(coin.getMoeda()), chapp.getChv()),
                        seg.decripta(Base64.getDecoder().decode(coin.getExchenge()), chapp.getChv()),
                        seg.decripta(Base64.getDecoder().decode(coin.getPublickey()), chapp.getChv()),
                        seg.decripta(Base64.getDecoder().decode(coin.getPrivatekey()), chapp.getChv()),
                        seg.decripta(Base64.getDecoder().decode(coin.getObs()), chapp.getChv())
                ));
            }
            table_backup.setItems(null);
            table_backup.setItems(dataNotes);
        } catch (FileNotFoundException ex) {
            Alert errotable = new Alert(Alert.AlertType.ERROR);
            errotable.setContentText("Erro ao buscar informações: " + ex);
            errotable.show();
        }
    }

    /**
   * Classe responsavel por possibilitar ler as informações do XML do backup do googledrive em forma de objeto. 
   */
    @XStreamAlias("coin")
    public class Coin {

        private String moeda;
        private String exchenge;
        private String publickey;
        private String privatekey;
        private String datacad;
        private String horacad;
        private String dataalt;
        private String horaalt;
        private String obs;

        public String getMoeda() {
            return moeda;
        }

        public String getExchenge() {
            return exchenge;
        }

        public String getPublickey() {
            return publickey;
        }

        public String getPrivatekey() {
            return privatekey;
        }

        public String getDatacad() {
            return datacad;
        }

        public String getHoracad() {
            return horacad;
        }

        public String getDataalt() {
            return dataalt;
        }

        public String getHoraalt() {
            return horaalt;
        }
        
        public String getObs() {
            return obs;
        }

    }

    /**
   * Classe responsavel por setar e pegar os valores da tabelas de APIs e colocar no grid de cadastro de APIS.
   */
    public class Coinaddapi {

        private final SimpleStringProperty tb_apiexchenge;
        private final SimpleStringProperty tb_apiidentificador;
        private final SimpleStringProperty tb_apisegredo;

        public Coinaddapi(String tb_apiexchenge, String tb_apiidentificador, String tb_apisegredo) {
            this.tb_apiexchenge = new SimpleStringProperty(tb_apiexchenge);
            this.tb_apiidentificador = new SimpleStringProperty(tb_apiidentificador);
            this.tb_apisegredo = new SimpleStringProperty(tb_apisegredo);

        }

        public String getTb_apiexchenge() {
            return tb_apiexchenge.get();
        }

        public void setTb_apiexchenge(String Tb_apiexchenge) {
            this.tb_apiexchenge.set(Tb_apiexchenge);
        }

        public String getTb_apiidentificador() {
            return tb_apiidentificador.get();
        }

        public void setTb_apiidentificador(String Tb_apiidentificador) {
            this.tb_apiidentificador.set(Tb_apiidentificador);
        }

        public String getTb_apisegredo() {
            return tb_apisegredo.get();
        }

        public void setTb_apisegredo(String Tb_apisegredo) {
            this.tb_apisegredo.set(Tb_apisegredo);
        }
    }

     /**
   * Classe responsavel por setar e pegar os valores da tabelas de backup e colocar no grid.
   */
    public class Coinbackup {

        private final SimpleStringProperty endpublic;
        private final SimpleStringProperty endprivate;
        private final SimpleStringProperty moeda;
        private final SimpleStringProperty exchenge;
        private final SimpleStringProperty obs;

        private Coinbackup(String endpublic, String endprivate, String moeda, String exchen, String obs) {

            this.endpublic = new SimpleStringProperty(endpublic);
            this.endprivate = new SimpleStringProperty(endprivate);
            this.moeda = new SimpleStringProperty(moeda);
            this.exchenge = new SimpleStringProperty(exchen);
            this.obs = new SimpleStringProperty(obs);
        }

        public String getEndpublic() {
            return endpublic.get();
        }

        public SimpleStringProperty end_publicProperty() {
            return endpublic;
        }

        public void setEndpublic(String Endpublic) {
            this.endpublic.set(Endpublic);
        }

        public String getEndprivate() {
            return endprivate.get();
        }

        public SimpleStringProperty end_privateProperty() {
            return endprivate;
        }

        public void setEndprivate(String End_private) {
            this.endprivate.set(End_private);
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
        
        
        public String getObs() {
            return moeda.get();
        }

        public SimpleStringProperty moedaObs() {
            return moeda;
        }

        public void setObs(String Obs) {
            this.moeda.set(Obs);
        }

    }

}
