/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.coinwalletoff.Util;

/**
 *
 * @author cassio
 */
public class Chave {
    
       static  String var = "";
      
        public void setChv(String chv)
        {
            this.var = ajustcvh(chv);
        }
         
        public String getChv()
        {
            return this.var;
        }
        
        //ajusta o teste para deixar o mesmo com tamanho de 16,pois a criptografia AES é baseada em uma chave de 16 bytes
        private static String ajustcvh(String texto)
        {   
           
            boolean parada = true;
            if (texto.trim().length() < 16)
            {
                texto = texto.trim();
                while(parada)
                {
                    if (texto.length() <= 16)
                    {
                        texto += " ";
                        
                    }
                    
                    if (texto.length() == 16)
                    {
                        parada = false;
                    }
                    
                }
            }
            
            
            return texto;
        }
    
}
