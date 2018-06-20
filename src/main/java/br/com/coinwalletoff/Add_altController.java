package br.com.coinwalletoff;

import br.com.coinwalletoff.Conexaodb.conn_db;
import br.com.coinwalletoff.Util.Chave;
import coinwalletoff.security_coin.Criptografa;
import coinwalletoff.security_coin.Descriptografa;
import coinwalletoff.security_coin.Securitycoin;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Classe Controller da interface FXML add_alt.fxml Responsavel por Gravar e atuaizar 
 * as carteiras na base de dados.
 * @author Cassio Dal Castel Lorenzett
 * @version 1.0
 * @since 2018-06-14
 * @see Initializable
 */
public class Add_altController implements Initializable {

    private PreparedStatement ps_;
    public static String updateval = "";
    conn_db connect = new conn_db();

    @FXML
    private Button canc;
    @FXML
    private TextField end_pub;
    @FXML
    private TextField end_pri;
    @FXML
    private TextField exch;
    @FXML
    private TextField moeda;
    @FXML
    private Label idbanco;
    @FXML
    private TextArea obs;

   /**
   * Evento do click do botão Cadastrar
   * Gravar ou Atualiza na base de dados as informações da carteira informadas na tela
   * @param  event
   */
    @FXML
    private void cadastrar(ActionEvent event) {

        if (vaidaCampos() == 0) {//se tudo tiver ok com os valores da tela passa
            Date hj = new Date();

            DateFormat data = new SimpleDateFormat("yyyyMMdd");
            DateFormat hora = new SimpleDateFormat("HH:mm");
            connect.conectar();

            Securitycoin seguranca = new Criptografa();
            Chave chvapp = new Chave();

            if (idbanco.getText().isEmpty()) {

                try {

                    ps_ = connect.conn.prepareStatement("insert into carteira (private_key,public_key,exchenge,moeda, data_cad, hora_cad,deletado,obs) values (?,?,?,?,?,?,?,?)");
                    ps_.setBytes(1, seguranca.encripta(end_pri.getText().trim(), chvapp.getChv()));
                    ps_.setBytes(2, seguranca.encripta(end_pub.getText().trim(), chvapp.getChv()));
                    ps_.setBytes(3, seguranca.encripta(exch.getText().trim(), chvapp.getChv()));
                    ps_.setBytes(4, seguranca.encripta(moeda.getText().trim(), chvapp.getChv()));
                    ps_.setString(5, data.format(hj).toString());
                    ps_.setString(6, hora.format(hj).toString());
                    ps_.setString(7, "");
                    ps_.setBytes(8, seguranca.encripta(obs.getText().trim(), chvapp.getChv()));

                    ps_.executeUpdate();
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Sucesso");
                    alert.setHeaderText("Cadastro Carteiras");
                    alert.setContentText("Cadastro efetuado com Sucesso !");
                    alert.showAndWait();

                    end_pub.setText("");
                    end_pri.setText("");
                    exch.setText("");
                    moeda.setText("");
                    obs.setText("");

                    ps_.close();
                } catch (Exception ex) {

                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Cadastro Carteiras");
                    alert.setContentText("Erro ao cadastrar, Erro_1: " + ex);
                    alert.showAndWait();
                }
                try {
                    ps_.close();
                } catch (Exception ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Cadastro Carteiras");
                    alert.setContentText("Erro ao cadastrar, Erro_2: " + ex);
                    alert.showAndWait();
                }

            } else {

                ResultSet rs = null;

                try {
                    String query = "UPDATE carteira SET public_key = ?, "
                            + "private_key = ?, "
                            + "exchenge = ?, "
                            + "moeda = ?, "
                            + "dataalt = ?, "
                            + "horaalt = ?, "
                            + "obs = ? "
                            + "where id = ?";

                    PreparedStatement preparedStmt = conn_db.conn.prepareStatement(query);
                    preparedStmt.setBytes(1, seguranca.encripta(end_pub.getText().trim(), chvapp.getChv()));
                    preparedStmt.setBytes(2, seguranca.encripta(end_pri.getText().trim(), chvapp.getChv()));
                    preparedStmt.setBytes(3, seguranca.encripta(exch.getText().trim(), chvapp.getChv()));
                    preparedStmt.setBytes(4, seguranca.encripta(moeda.getText().trim(), chvapp.getChv()));
                    preparedStmt.setString(5, data.format(hj).toString());
                    preparedStmt.setString(6, hora.format(hj).toString());
                    preparedStmt.setBytes(7, seguranca.encripta(obs.getText().trim(), chvapp.getChv()));
                    preparedStmt.setString(8, idbanco.getText().trim());

                    // execute the preparedstatement
                    preparedStmt.executeUpdate();
                    preparedStmt.close();

                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Sucesso");
                    alert.setHeaderText("Atualização Carteiras");
                    alert.setContentText("Atualização efetuada com Sucesso !");
                    alert.showAndWait();

                    Stage stage2 = (Stage) canc.getScene().getWindow(); //Obtendo a janela atual
                    stage2.close();

                } catch (SQLException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Atualização Carteiras");
                    alert.setContentText("Erro ao Atualizar, Erro: " + ex);
                    alert.showAndWait();
                }
            }
        }

    }

    /**
   * Evento do click do botão Cancelar
   * Sai da tela de manutenção de carteiras
   * @param  event
   */
    @FXML
    private void cancelar(ActionEvent event) {
        Stage stage2 = (Stage) canc.getScene().getWindow(); //Obtendo a janela atual
        stage2.close();
    }

    /**
   * Busca dados de detarmina carteira passada por referencia e seta os valores na tela
   * função chama no fonte FXMLController na função alt_cart_action
   * @param  valor
   * @throws SQLException
   */
    public void update(String valor) throws SQLException {
        Securitycoin seguranca = new Descriptografa();
        Chave chvapp = new Chave();

        String sql = "SELECT * FROM carteira WHERE id =  ?";
        connect.conectar();
        try {
            PreparedStatement pstmt = conn_db.conn.prepareStatement(sql);
            pstmt.setString(1, valor);
            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                idbanco.setText(valor);
                end_pri.setText(seguranca.decripta(rs.getBytes("private_key"), chvapp.getChv()));
                end_pub.setText(seguranca.decripta(rs.getBytes("public_key"), chvapp.getChv()));
                exch.setText(seguranca.decripta(rs.getBytes("exchenge"), chvapp.getChv()));
                moeda.setText(seguranca.decripta(rs.getBytes("moeda"), chvapp.getChv()));
                obs.setText(seguranca.decripta(rs.getBytes("obs"), chvapp.getChv()));

            }
            pstmt.close();
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Seleção Carteira");
            alert.setContentText("Erro ao Selecionar Carteira, Erro: " + e);
            alert.showAndWait();
        }

    }

    /**
   * Valida se determinados campos obrigatorios estãocom valores ou não.
   * Caso os campos estiverm vazios aborda do mesmo fica vermelho 
   * Se tudo tiver OK retorna valor 0(zero), senão retorna valor maior que zero.
   * Utilizado na função cadastrar()
   * @return interger
   */
    private int vaidaCampos() {
        int num = 0;

        num = 0;
        if (end_pub.getText().trim().isEmpty()) {
            end_pub.setStyle("-fx-border-color:red");
            num++;
        }
        else
        {
            end_pub.setStyle("");
        }

        if (exch.getText().trim().isEmpty()) {
            exch.setStyle("-fx-border-color:red");
            num++;
        }
        else
        {
            exch.setStyle("");
        }

        if (moeda.getText().trim().isEmpty()) {
            moeda.setStyle("-fx-border-color:red");
            num++;
        } 
        else
        {
            moeda.setStyle("");
        }

        return num;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
