/*
 * Classe de conexão ao google drive
 */
package br.com.coinwalletoff.Util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

public class Googledrive {

    /**
     * Application Gooledrivre. Utilizado para sincronização com a conta do
     * gooledrive, para efetuar o backup das informações das Coins cadastradas.
     */
    private static final String APPLICATION_NAME
            = "coinwalletoff";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
            ".credentials/googledrive");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY
            = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    private static String CLIENT_SECRETS = "";
    private static boolean exec;

    /**
     * Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES
            = Arrays.asList(DriveScopes.DRIVE_FILE);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
    
    
    public void setClientsecrets(String clienteid , String clientsecret){
       CLIENT_SECRETS = "{\"installed\": {\"client_id\": \""
            + clienteid
            + "\",\"client_secret\": \""
            + clientsecret
            + "\"}}";
    }
    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.

        GoogleClientSecrets clientSecrets
                = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(CLIENT_SECRETS));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow
                = new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        /* System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());*/
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     *
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    //retorna o serviço do google drive (API)
    public Drive getDrive() throws IOException {
        return getDriveService();
    }

    public String ativaconta() throws IOException {
        Drive service = getDriveService();
        About about = service.about().get().execute();
        String  ret = "";
        
        ret += "\nCurrent user name: " + about.getName();
        ret += "\nRoot folder ID: " + about.getRootFolderId();
        ret += "\nTotal quota (bytes): " + about.getQuotaBytesTotal();
        ret += "\nUsed quota (bytes): " + about.getQuotaBytesUsed();
        ret += "\nCurrent user name: " + about.getName();
        
        return ret;
    }

    //retorna o caimnho das pastas criadas referente as credenciais.
    public String getcaminho() {
        return DATA_STORE_DIR.getAbsolutePath();
    }

    
}
