/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.ExchengesCore;



import javafx.scene.control.TreeItem;

/**
 * Interface utilizada .para retornar dados da Blockchain de determinasdas carteiras
 * @author Cassio Dal Castel Lorenzett
 * @version 1.0
 * @since 2018-06-14
 */
interface Blockchain 
{   
   void RunBlockchain(String address);
   String GetBalanco();
   String GetTx();
   String GetEnviado();
   String GetRecebido();
   String GetErro();
   TreeItem GetMovimentos();
   
}



