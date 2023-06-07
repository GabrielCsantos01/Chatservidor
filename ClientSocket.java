package com.mycompany.chat.blocking;
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
/**
 *
 * @author gabri
 */
// Essas variáveis representam os principais elementos necessários para a comunicação do cliente com o servidor através do socket. O objeto BufferedReader é usado para ler as mensagens recebidas do servidor, enquanto o objeto PrintWriter é usado para enviar mensagens para o servidor.
public class ClientSocket { //  ClientSocket para lidar com a comunicação, manipulação de mensagens e interação com o servidor
    private final Socket socket; // Esta linha declara uma variável socket do tipo Socket. Essa variável representa o socket associado a este cliente. 
    private final BufferedReader in; // Esta linha declara uma variável in do tipo BufferedReader. Essa variável é usada para ler dados recebidos do servidor através do socket. 
    private final PrintWriter out; // Esta linha declara uma variável out do tipo PrintWriter. Essa variável é usada para enviar dados para o servidor através do socket.
    private String login; //  Esta linha declara uma variável login do tipo String. Essa variável é usada para armazenar o nome de login do cliente.
   
    // Essas etapas realizadas no construtor permitem a inicialização dos objetos necessários para a comunicação entre o cliente e o servidor. O BufferedReader é usado para receber mensagens do servidor, enquanto o PrintWriter é usado para enviar mensagens para o servidor.
    public ClientSocket(Socket socket) throws IOException { // Esta é a assinatura do construtor, que recebe um objeto Socket como parâmetro. O construtor pode lançar uma exceção do tipo IOException.
        this.socket = socket; // Esta linha atribui o objeto socket recebido como parâmetro à variável socket da classe. Isso permite que o objeto ClientSocket tenha acesso ao socket associado a ele.
        System.out.println("cliente" + socket.getLocalSocketAddress()+ " se conectou"); //  Esta linha imprime uma mensagem no console indicando que um cliente se conectou. A mensagem inclui o endereço do socket obtido através do método getLocalSocketAddress().
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Esta linha cria um objeto BufferedReader que recebe como entrada o InputStream do socket. O InputStream é obtido através do método getInputStream() do objeto socket. A classe InputStreamReader é usada para converter o InputStream em caracteres legíveis, e o objeto resultante é passado para o construtor do BufferedReader. O BufferedReader é atribuído à variável in da classe.
        this.out = new PrintWriter(socket.getOutputStream(),true); //  Esta linha cria um objeto PrintWriter que recebe como saída o OutputStream do socket. O OutputStream é obtido através do método getOutputStream() do objeto socket. O segundo parâmetro true indica que o PrintWriter deve realizar a autoflush, ou seja, os dados serão enviados imediatamente ao servidor em vez de serem armazenados em buffer. O PrintWriter é atribuído à variável out da classe.
       
    }
    public SocketAddress getRemoteSocketAddress(){ // Este método retorna o endereço do socket remoto ao qual o cliente está conectado. Ele utiliza o método getRemoteSocketAddress() do objeto socket para obter o endereço remoto e retorna esse valor.
      return socket.getRemoteSocketAddress(); // 
    }
    
    public void close(){ // Este método é responsável por fechar os recursos relacionados à comunicação do cliente. Ele realiza as seguintes ações:
        try { //
            in.close(); // Fecha o BufferedReader in, encerrando a leitura de dados do socket.
            out.close(); //  Fecha o PrintWriter out, encerrando o envio de dados para o socket.
            socket.close(); // Fecha o Socket socket, encerrando a conexão com o servidor.
       }catch(IOException e){ // Caso ocorra uma exceção do tipo IOException durante o processo de fechamento, o bloco catch é executado. Ele exibe uma mensagem de erro no console, contendo a mensagem específica da exceção obtida através do método getMessage().
           System.out.println("erro ao fechar socket:" + e.getMessage()); // Esses métodos são úteis para gerenciar o fechamento adequado dos recursos e encerrar a comunicação do cliente com o servidor de forma adequada. O método getRemoteSocketAddress() permite obter informações sobre o endereço remoto do socket, enquanto o método close() fecha os recursos associados à comunicação.
       }  
    
    }
    //o método getMessage() lê uma linha de texto do BufferedReader associado ao socket do cliente. Se a leitura for bem-sucedida, a linha de texto lida é retornada como resultado do método. Se ocorrer uma exceção durante a leitura, null é retornado.
   public String getMessage(){ //  Esta é a assinatura do método, que retorna uma string. O método permite ler uma mensagem recebida do servidor.
       try{ // Este bloco try-catch envolve o código que pode lançar uma exceção do tipo IOException. O bloco try é responsável por executar o código que pode gerar uma exceção, enquanto o bloco catch captura a exceção e trata dela.
           return in .readLine(); // Dentro do bloco try, esta linha lê uma linha de texto do BufferedReader in. O método readLine() lê uma linha completa de texto do fluxo de entrada, até encontrar uma quebra de linha. Essa linha lida é retornada como resultado do método getMessage().
       
       } catch(IOException e){  
           return null; // Se ocorrer uma exceção do tipo IOException durante a execução do bloco try, o fluxo de execução será redirecionado para o bloco catch. Nesse caso, é retornado null, indicando que não foi possível ler a mensagem.
       }
   } 
   //o método sendMsg() envia uma mensagem para o servidor. Ele escreve a mensagem no PrintWriter associado ao socket do cliente e verifica se ocorreram erros durante o envio. Se não houver erros, o método retorna true, indicando que a mensagem foi enviada com sucesso. Caso contrário, retorna false.
   public boolean sendMsg(String msg){ // Esta é a assinatura do método, que recebe uma string como parâmetro e retorna um valor booleano. O método envia uma mensagem para o servidor.
       out.println(msg); //  Esta linha escreve a mensagem msg no PrintWriter out. O método println() adiciona automaticamente uma quebra de linha após a mensagem, permitindo que o servidor receba a mensagem em uma linha separada.
       return !out.checkError(); // Esta linha verifica se ocorreu algum erro ao enviar a mensagem, usando o método checkError() do PrintWriter out. O método checkError() retorna true se ocorrer algum erro durante a operação de envio, e false caso contrário. A expressão !out.checkError() retorna true se não houver erros, indicando que a mensagem foi enviada com sucesso.
   }  

}

