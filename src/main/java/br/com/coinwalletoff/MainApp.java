package br.com.coinwalletoff;

import br.com.coinwalletoff.Conexaodb.conn_db;
import br.com.coinwalletoff.Conexaodb.Criabasedados;
import br.com.coinwalletoff.Util.Runbackup;
import coinwalletoff.security_coin.Protegeapp;
import coinwalletoff.security_coin.Securitycoin;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
* Classe principal para inicialização da aplicação
* Controller da interface FXML palchave.fxml
* @author Cassio Dal Castel Lorenzett
* @version 1.0
* @since  2018-06-14
* @see Application
*/

public class MainApp extends Application {
    
    
    @FXML
    private PasswordField senhapri;

    @FXML
    private PasswordField senhaconf;

    @FXML
    private Button salva;
    
    
   /**
   * Evento no botão Salvar para criar senha para criptografar e descriptografar
   * as informações dentro da aplicação
   * @param event 
   * @return void
   */
    
    @FXML
    void salva(ActionEvent event) {
        String msg = "";

        if ((senhapri.getText().length() <= 16) || (senhaconf.getText().length() <= 16)) {
            if (senhapri.getText().equalsIgnoreCase(senhaconf.getText()) == false) {
                msg = "Senhas devem ser iguais !";
            } else {
                
                Securitycoin seguranca = new Protegeapp();
                
                conn_db conn = new conn_db();
                conn.conectar();
                PreparedStatement ps_;
                try {
                    
                    ps_ = conn.conn.prepareStatement("UPDATE acesso SET senha = ? where id = 1");
                    ps_.setString(1, seguranca.protegeapp(senhapri.getText()));
                    ps_.executeUpdate();
                    
                    msg = "";
                } catch (Exception ex) {
                   msg = ""+ex;
                }

            }
        } else {
            msg = "Campos devem ter tamanho menor ou igual a 16 caracteres !";
        }

        if ((senhapri.getText().trim().isEmpty()) || (senhaconf.getText().trim().isEmpty())) {
            msg = "Campos devem ser preenchidos !";
        }

        if (msg.trim().isEmpty() == false) {
            Alert msgalert = new Alert(Alert.AlertType.ERROR);
            msgalert.setHeaderText("Mensagem");
            msgalert.setContentText(msg);
            msgalert.show();
        }
        else
        {
            Stage stage2 = (Stage) salva.getScene().getWindow(); //Obtendo a janela atual
            stage2.close();
        }
    }

    /**
   * Função instanciada na inicialização da aplicação.
   * É responsavel por criar o arquivo da base de dados sqlite.
   * Se tudo tiver valido chama a tela para informar a senha desegurança
   * ou chamar a tela principal e caso nada estiver correto finalizar a aplicação.
   * @param stage
   * @see Exception
   * @return void
   */
    @Override
    public void start(Stage stage) throws Exception {
        String msg = "";

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();                
               
                 Task exit = new Task() {
                @Override
                protected Object call() throws Exception {
                     Runbackup runcop = new Runbackup();
                     runcop.Executa(1);
                     return null;
                    }    
                };
                
                Thread threxit = new Thread(exit);
                threxit.setDaemon(true);
                threxit.start();
            }
        });
                 
        if (new File(System.getProperty("user.home") + "/.credentials/sqlite").exists() == false) {//se o caminho especificado NAO EXISTIR

            if (new File(System.getProperty("user.home") + "/.credentials/sqlite").mkdirs()) {//criar a pasta de armazenamento para a base de dados

                //senão existir o arquivode base de dados, cria o mesmo e as tabelas necessarias.
                if (new File(System.getProperty("user.home") + "/.credentials/sqlite/coinwallet.db").exists() == false) {

                    //chama classe para criar base de dados
                    Criabasedados sqlite = new Criabasedados();
                    msg = sqlite.Criabase();

                    if (msg.isEmpty()) {
                        setSenha();
                    }

                }

            } else {
                msg = "Erro ao criar pasta para armazenar a base dedos da aplicação!"
                        + "Erro ao criar o seguinte caminho: " + System.getProperty("user.home") + "/.credentials/sqlite";
            }

        } else //seo caminho especificado EXISTIR
         //senão existir o arquivode base de dados, cria o mesmo e as tabelas necessarias.
            if (new File(System.getProperty("user.home") + "/.credentials/sqlite/coinwallet.db").exists() == false) {

                //chama classe para criar base de dados
                Criabasedados sqlite = new Criabasedados();
                msg = sqlite.Criabase();

                if (msg.isEmpty()) {
                    setSenha();
                }

            }

        if (msg.isEmpty()) {

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
            Scene scene = new Scene(root);
            // scene.getStylesheets().add("/styles/Styles.css");
            stage.setTitle("coinnwalletoff");
            stage.setScene(scene);
            stage.show();
        } else {
            Alert msgalert = new Alert(Alert.AlertType.ERROR);
            msgalert.setHeaderText("Aplicação não será inicializada, pois erros foram identificados.\n Erros encontrados:\n");
            msgalert.setContentText(msg);
            msgalert.showAndWait();
        }
    }
    
     /**
   * Exibe a interface FXML palchave.fxml, para informar a senha de segurança.
   * @see IOException
   * @return String
   */
    public String setSenha() throws IOException {
        String senha = "";
        Stage stage = new Stage();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/palchave.fxml"));

        Scene scene = new Scene(root);

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("coinnwalletoff");
        stage.setScene(scene);
        stage.showAndWait();

        return senha;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
