/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.Util;

import br.com.coinwalletoff.Conexaodb.conn_db;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author cassio
 */
public class Runbackup  {

    private String ret = "", aux = "";
    private Stage stage;
    private boolean fecha = false;
    public String Executa(int opc) {

        //somente executa backup
        if (opc == 0) {

            if (validSinc() == true) {//se cliente id e cliente secret estiverem cadastrados passa.

                try {
                    Backupgdrive gdrive = new Backupgdrive();
                    ret = gdrive.execCopia();

                } catch (IOException ex) {
                    ret = "" + ex;
                }

            } else {
                ret = "Verifique o cadastro da sua conta do Goole Drive, na tela de configuração de conta(s)."
                        + " Para validar o cadastro clique no botão de Configuração e entre na primeira aba.";
            }

        }

        //valida se é para fazer backup antes de sair da aplicação
        if (opc == 1) {
            
            Chave ch = new Chave();

            if (!ch.getChv().trim().isEmpty()) {

                if (autoBackup() == 1) {
                   
                    if (validSinc() == true) {//se cliente id e cliente secret estiverem cadastrados passa.
                        
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                ShowFormCopia();
                            }
                        });
                        
                        try {
                            Backupgdrive gdrive = new Backupgdrive();
                            ret = aux = gdrive.execCopia();

                        } catch (IOException ex) {
                            aux = ret = "" + ex;
                        }

                    } else {
                        aux = ret = "Verifique o cadastro da sua conta do Goole Drive, na tela de configuração de conta(s)."
                                + " Para validar o cadastro clique no botão de Configuração e entre na primeira aba.";
                    }
                }
            } else {
                aux = ret = "Você deve estar logado no sistema para o mesmo poder aplicar a chave de segurança informada "
                        + " e com isto efetuar o backup das informações. Clique no botão DESBLOQUEAR e prossiga"
                        + " com a cópia de segurança !";
            }

            if (!ret.trim().isEmpty()) {
               
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                      
                        if (fecha)
                        {
                            stage.close();
                        }    
                         
                        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.NO);
                        alert.setHeaderText("Não foi possivel efetuar o Backup. Deseja finalizar a aplicação mesmo assim ?");
                        alert.setContentText("Erro(s) encontrado(s): \n" + aux);
                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.get() == ButtonType.YES) {
                            System.exit(0);
                        }
                    }
                });
                ret = "IGNORA";
            } else {              
                
                System.exit(0);
            }

        }

        return ret;
    }
    
   
    private void ShowFormCopia() {
        Parent root;
        this.stage = new Stage();
       
        try {
            this.fecha = true;
            root = FXMLLoader.load(getClass().getResource("/fxml/Msgexit.fxml"));

            Scene scene = new Scene(root);
            stage.setAlwaysOnTop(true);
            this.stage.setScene(scene);
            this.stage.initStyle(StageStyle.UNDECORATED);
            this.stage.showAndWait();
                     
        } catch (IOException ex) {
            System.err.println(ex);
        }
       
        
     
    }

    //valida se o cadastro dos dados do google drive estão incluidos
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
            System.err.println("erro: " + ex);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                System.err.println("erro: " + ex);
            }
        }

        return passa;
    }

    public int autoBackup() {
        conn_db connect = new conn_db();
        connect.conectar();
        int auto = 0;
        ResultSet rs = null;
        try {
            rs = conn_db.conn.createStatement().executeQuery("select * from auto_backup where id = 1");
            while (rs.next()) {
                auto = rs.getInt("auto_backup");
            }

        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro na seleção !");
            alert.setTitle("Erro ");
            alert.setContentText("" + ex);
            alert.showAndWait();
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro na seleção !");
                alert.setTitle("Erro ");
                alert.setContentText("" + ex);
                alert.showAndWait();
            }
        }

        return auto;
    }
}
