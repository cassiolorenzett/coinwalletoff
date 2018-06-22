# coinwalletoff
Software para guardar o endereço publico ou privado de qualquer carteira.

> * ### Tela inicial
 

![alt text][logo1]

   [logo1]: https://github.com/cassiolorenzett/coinwalletoff/blob/master/screenshots/walletimg1.png 


![alt text][logo2]

   [logo2]: https://github.com/cassiolorenzett/coinwalletoff/blob/master/screenshots/walletimg2.png



# Funcionalidades Extras

> * ### Backup e Sincronização das carteiras cadastradas, através da conta do Google Drive Vinculada

<dl> 
  <dt>Configuração API google drive</dt>
  <dd>
   Necessário ativar API do google drive e criar as chaves <b>Client ID</b> e <b>Client Secret</b>.
   <br>
   Clique no link para configurar a API:  https://console.cloud.google.com/apis
  </dd>  
    
  <dt>Efetuando o backup para google drive</dt>
  <dd>
   Após criado as chaves da API é só informa-las dentro do programa e sincronizar. Dando tudo certo 
   <br>
   já é possivel fazer o backup das suas carteiras para sua conta, clicando no botão de backup presente 
   na tela inicial do mesmo.
   
   <img src="https://github.com/cassiolorenzett/coinwalletoff/blob/master/screenshots/walletimg3.png">
   </dd>
   
  <dt>Sincronizando as informações do google drive para dentro do sistema</dt>
  <dd>
   Depois de efetuado o backup para a sua conta no google drive, é possivel baixar as carteiras e sincronizar as mesmas localmente
   em sua maquina.
   Ex:
   <img src="https://github.com/cassiolorenzett/coinwalletoff/blob/master/screenshots/walletimg4.png">

   </dd>
</dl>

> * ### Comunicação com a **Blockchain** do Bitcoin e Ethereum
 <dl> 
   Depois de efetuado o cadastro de qualquer carteira Bitcoin ou Ethereum é possivel verificar as movimentação das mesmas em suas 
   Blockchains. 
   <br>
   Informações que podem ser visualizadas referente a carteira informada
   <br>
   <b>Total recebido, Total enviado, Balanço, Numero de Transações e as Movimentações efetuadas </b>
   <img src="https://github.com/cassiolorenzett/coinwalletoff/blob/master/screenshots/walletimg6.png">
  <dd>
 </dl>
 
 > * ### Comunicação com sua conta do [Mercado Bitcoin](https://www.mercadobitcoin.com.br/) através da API do mesmo.
 <dl> 
  <dd>
   É possivel efetuar a comunicação com sua conta no Mercado Bitcoin e poder visualizar algumas informações como:<br>
   <b>1. Saldo em Reais<br>
      2. Saldo dos Altcoins presentes na platarfoma<br>
      3. Movimentos efetuados para cada altcoin</b><br>
   Dentre outras.
   </dd>
  
   <dt>Ativando e criando API do Mercado Bitcoin</dt>
   <dd>
   Para utilizar a funcionalidade é necessário criar e ativar a <a href="https://www.mercadobitcoin.com.br/trade-api/configuracoes/">API</a> no site do Mercado Bitcoin.<br>
   Será gerado duas chaves. O <b>Identificador</b> e o <b>Segredo</b>.
   <br> Estas seram utilizadas para comunicação do software com sua conta. 
   </dd>
   
   <dt>Cadastrando API dentro do programa</dt>
   <dd>
    Depois de gerado as chaves é necessário informa-las dentro do programa. Para isto clique no botão de 'Configurações' na tela
   principal e vá até a aba chamada 'Exchenges'.<br>
   Posicionado na tela informe as chaves geradas nos respectivos campos selecionando a exchenge 'Mercado Bitcoin'. <br>
   Depois de informado clique no botão 'Incluir/Editar'. Pronto, exchenge cadastrada e pronta para ser consumida.
   <br>
  <b>OBS: Atualmente só o Mercado Bitcoin esta funcional. Futuramente será incluido mais Exchenges</b>
   </dd>

  </dl>




