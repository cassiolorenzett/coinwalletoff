/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.Util;

import br.com.coinwalletoff.Conexaodb.conn_db;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

/**
 * Utilizada para fazer o backup das informações com o google drive.
 *
 * @author cassio
 */
public class Backupgdrive {

    private static String msgerr = "";
    private static String client_id = "";
    private static String cliente_secret = "";
    //busca as credenciais cadastradas na base de dados, caso estejam cadastradas.

    //executa o processo de backup
    public String execCopia() throws IOException {
        Getcredencialgdrive credenc = new Getcredencialgdrive();
        String msgexec = "";
        
        if (credenc.getCredencial().size() > 0) {
           
            Googledrive gdrive = new Googledrive();

            gdrive.setClientsecrets(credenc.getCredencial().get(0).toString(), credenc.getCredencial().get(1).toString());
            
            if (createFile(gdrive.getcaminho()) == true) {
                // Build a new authorized API client service.
                Drive service = gdrive.getDrive();
                String delet = deleteFile(service);

                if (delet.isEmpty()) {
                    msgexec = uploadFile(service, gdrive.getcaminho());
                } else {
                    msgexec = delet;
                }

            } else {

                msgexec = msgerr;

            }
        }

        return msgexec;

    }

    //deleta todos os arquivos coinwalletoff.xml de backup para fazer o upload do atual que se esta enviando
    private static String deleteFile(Drive service) throws IOException {
        String pageToken = null;
        String retdel = "";

        do {
            FileList result = service.files().list()
                    .setQ("title contains 'coinwalletoff' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, items(id, title)")
                    .setPageToken(pageToken)
                    .execute();

            for (File file : result.getItems()) {

                try {
                    service.files().delete(file.getId()).execute();
                    retdel = "";
                } catch (IOException e) {
                    retdel = "" + e;
                }

            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return retdel;

    }

    //efetua o upload das informações cadastradas no software para o google drive para backup.
    private static String uploadFile(Drive service, String cam) {
        File body = new File();
        body.setTitle("coinwalletoff");
        body.setDescription("Doc Coins");
        body.setMimeType("text/xml");

        // Set the parent folder.
        /*if (parentId != null && parentId.length() > 0) {
      body.setParents(
          Arrays.asList(new ParentReference().setId(parentId)));
    }*/
        // File's content.
        java.io.File fileContent = new java.io.File(cam + "/coinwalletoff.xml");
        FileContent mediaContent = new FileContent("text/xml", fileContent);
        try {
            File file = service.files().insert(body, mediaContent).execute();

            // Uncomment the following line to print the File ID.
            // System.out.println("File ID: " + file.getId());
            return "";
        } catch (IOException e) {

            return "" + e;

        }
    }

    //cria o arquivo que contem as moedas cadastradas no software para efetuar o uploaddo mesmo no google drive.
    private static boolean createFile(String cam) {
        boolean stats = true;
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(cam + "/coinwalletoff.xml"), "utf-8"));
            writer.write("<coins>\n");

            ResultSet rs = null;
            try {
                int id = 0;
                rs = conn_db.conn.createStatement().executeQuery("SELECT * FROM carteira where deletado = ''");
                while (rs.next()) {

                    writer.write("<coin id='" + id + "'>\n");

                    writer.write("<privatekey>" + Base64.getEncoder().encodeToString(rs.getBytes("private_key")) + "</privatekey>\n");
                    writer.write("<publickey>" + Base64.getEncoder().encodeToString(rs.getBytes("public_key")) + "</publickey>\n");
                    writer.write("<exchenge>" + Base64.getEncoder().encodeToString(rs.getBytes("exchenge")) + "</exchenge>\n");
                    writer.write("<moeda>" + Base64.getEncoder().encodeToString(rs.getBytes("moeda")) + "</moeda>\n");
                    writer.write("<obs>" + Base64.getEncoder().encodeToString(rs.getBytes("obs")) + "</obs>\n");
                    writer.write("<datacad>" + rs.getString("data_cad") + "</datacad>\n");
                    writer.write("<horacad>" + rs.getString("hora_cad") + "</horacad>\n");
                    writer.write("<dataalt>" + rs.getString("dataalt") + "</dataalt>\n");
                    writer.write("<horaalt>" + rs.getString("horaalt") + "</horaalt>\n");

                    writer.write("</coin>\n");
                    id++;
                }
                stats = true;
            } catch (SQLException ex) {
                stats = false;
                msgerr = "" + ex;
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    stats = false;
                    msgerr = "" + ex;
                }
            }

            writer.write("</coins>");
            stats = true;
        } catch (IOException ex) {
            stats = false;
            msgerr = "" + ex;
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                stats = false;
                msgerr = "" + ex;
            }
        }

        return stats;
    }

}
