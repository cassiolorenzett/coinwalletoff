/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.Conexaodb;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.scene.control.Alert;



/**
 * Classe utilizada para conexão com o banco dados
 * @author Cassio Dal Castel Lorenzett
 * @version 1.0
 * @since 2018-06-14
 */
public class conn_db {
    public static Connection conn = null;
   
    /**
    * Função que executa a conexão com a base de dados
    * Utilizada em toda a aplicação
    */
    public void conectar() {
       
        try {
         Class.forName("org.sqlite.JDBC");
         conn = DriverManager.getConnection("jdbc:sqlite:"+System.getProperty("user.home") + "/.credentials/sqlite/coinwallet.db");
         conn.setAutoCommit(true);
        
      } catch ( Exception e ) {
            Alert msgalert = new Alert(Alert.AlertType.ERROR);
            msgalert.setHeaderText("Erros encontrados:");
            msgalert.setContentText(""+e);
            msgalert.showAndWait();
          
      }
      
   }
}
