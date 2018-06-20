/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.Util;

import br.com.coinwalletoff.Conexaodb.conn_db;
import coinwalletoff.security_coin.Descriptografa;
import coinwalletoff.security_coin.Securitycoin;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.scene.control.Alert;

/**
 *
 * @author cassio
 */
public class Getcredencialgdrive {
    
    public ArrayList getCredencial() {
        conn_db conn = new conn_db();
        conn.conectar();
        ResultSet rs = null;
        ArrayList<String> ret = new ArrayList<String>();
        try {
            Securitycoin seguranca = new Descriptografa();
            Chave chvapp = new Chave();

            rs = conn_db.conn.createStatement().executeQuery("SELECT * FROM gdrive where id = 1");
            while (rs.next()) {

                if (rs.getInt("cliente_add") == 1) {
                    ret.add(seguranca.decripta(rs.getBytes("cliente_id"), chvapp.getChv()));
                    ret.add(seguranca.decripta(rs.getBytes("cliente_secret"), chvapp.getChv()));
                }

            }

        } catch (SQLException ex) {
            
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setContentText("erro ao buscar credenciais: "+ex);
            erro.showAndWait();
            ret.add(null);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setContentText("erro ao buscar credenciais: "+ex);
                erro.showAndWait();
                ret.add(null);
            }
        }
        return ret;
    }
}
