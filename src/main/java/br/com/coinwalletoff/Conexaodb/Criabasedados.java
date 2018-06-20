package br.com.coinwalletoff.Conexaodb;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Classe que cria a base de dados e as tabelas necessarias para a aplicação funcionar
 * @author Cassio Dal Castel Lorenzett
 * @version 1.0
 * @since 2018-06-14
 */
public class Criabasedados {

    
   /**
   * Função que cria a base de dados eas tabelas
   * Utilizada no fonte MainApp na função start
   */
    public String Criabase() {
        String passa = "";

        String sql = "CREATE TABLE acesso (\n"
                + "	id	INTEGER,\n"
                + "	senha	TEXT,\n"
                + "	PRIMARY KEY(id)\n"
                + ");";

        String sql1 = "CREATE TABLE carteira (\n"
                + "	id	INTEGER,\n"
                + "	private_key	BLOB,\n"
                + "	public_key	BLOB,\n"
                + "	exchenge	BLOB,\n"
                + "	moeda	BLOB,\n"
                 + "	obs	BLOB,\n"
                + "	data_cad	TEXT,\n"
                + "	hora_cad	TEXT,\n"
                + "	deletado	TEXT,\n"
                + "	dataalt	TEXT,\n"
                + "	horaalt	TEXT,\n"
                + "	PRIMARY KEY(id)\n"
                + ");";

        String sql2 = "CREATE TABLE gdrive (\n"
                + "	id	INTEGER,\n"
                + "	cliente_id	BLOB,\n"
                + "	cliente_secret	BLOB,\n"
                  + "	cliente_add	INTEGER,\n"
                + "	PRIMARY KEY(id)\n"
                + ");";
               
        String sql3 = "CREATE TABLE apis (\n"
                + "	id	INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	exchenge	TEXT,\n"
                + "	identificador	TEXT,\n"
                + "	segredo	        TEXT,\n"
                + "	id_exchenge	TEXT"                
                + ");";
       
         String sql4 = "CREATE TABLE auto_backup (\n"
                + "	id	    INTEGER ,\n"
                + "	auto_backup INTEGER,\n"
                + "	PRIMARY KEY(`id`)\n"             
                + ");";
         
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.home") + "/.credentials/sqlite/coinwallet.db");
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
            stmt.execute(sql4);
            stmt.execute("insert into acesso values (1,'')");
            stmt.execute("insert into gdrive values (1,'','',0)");
            stmt.execute("insert into auto_backup values (1,0)");
            
            passa = "";
        } catch (SQLException e) {
            passa = "" + e;
        } 
        
        return passa;
    }
    
   

   

    

    
    
}
