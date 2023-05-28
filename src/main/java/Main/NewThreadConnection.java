package Main;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NewThreadConnection extends Thread{
    private Socket socket;

    private String showHtml = "";
    private BufferedWriter logWritter;

    private OutputStream logs;

    public NewThreadConnection(Socket socket, OutputStream logs){
        this.socket = socket;
        this.logs = logs;

    }

    @Override
    public void run(){
//        String header = "HTTP/1.1 200 OK\n"+
//                "Content-Type: "+  getContentType(recurso)+ "; charset=utf-8\n\n";
        try {
            this.setLogWritter();
            //Ele permite que o programa receba dados de entrada de um cliente conectado ao servidor através do objeto Socket.
            InputStream in = socket.getInputStream();
            //Ele permite que o programa envie dados de saída para um cliente conectado ao servidor através do objeto Socket.
            OutputStream out = socket.getOutputStream();
            //2. Recebe a requisição
            byte[] buffer = new byte[1024];
            //vai ler oq tem na entrada, pra dentro de buffer e vai retornar a quantidade de bytes que ele leu
            //O buffer é usado para armazenar temporariamente dados que serão lidos ou escritos em um stream de entrada ou saída.
            int nBytes = in.read(buffer);
            System.out.println("testando"+ nBytes);

            String str = new String(buffer, 0, nBytes);

            String[] linhas = str.split("\n");
            String[] linha1 = linhas[0].split(" ");
            String recurso = linha1[1];
            System.out.println("[RECURSO]" + recurso);
            this.NovaConexao(recurso, nBytes);



        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private void setLogWritter(){
        OutputStreamWriter osw = new OutputStreamWriter(logs);
        logWritter = new BufferedWriter(osw);
    }


    public synchronized void NovaConexao(String recurso, int nBytes) throws IOException, InterruptedException {
        String header = "HTTP/1.1 200 OK\n"+
                "Content-Type: "+  getContentType(recurso)+ "; charset=utf-8\n\n";

        byte[] buffer = new byte[1024];

        showHtml = showHtml.replace('/', File.separatorChar);
        File f = new File("view" + showHtml);
        //fornece uma maneira de escrever dados em uma matriz de bytes
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if(!f.exists()){
            bout.write("404 NOT FOUND \n\n".getBytes(StandardCharsets.UTF_8));
        }else{
            //Ele é usado para abrir um fluxo de entrada de arquivo e ler os dados do arquivo em forma de bytes
            InputStream fileIn = new FileInputStream(f);
            //Escrevendo o cabeçalho
            bout.write(header.getBytes(StandardCharsets.UTF_8));
            //Escrevendo o arquivo
            nBytes = fileIn.read(buffer);
            String str2 = "";
            do{
                if(nBytes > 0){

                    bout.write(buffer, 0, nBytes);
                    nBytes = fileIn.read(buffer);
                }

            } while(nBytes == 1024);

            if(nBytes > 0){
                bout.write(buffer, 0, nBytes);
            }
            if(recurso.equals("/")){

                showHtml = "/index.html";
            }else if(recurso.contains("/cadeira")){

                showHtml = "/index.html";
                String[] arr = recurso.split("\\?");
                if(arr[1].contains("numero")){
                    int qt = Integer.parseInt(arr[1].split("=")[1]);
                    logWritter.write("Cadeira agendada" + qt + Thread.currentThread().getId());
                    logWritter.newLine();
                    logWritter.flush();
                    }

                }
            }






    }


    private static String getContentType(String nomeRecurso){
        if(nomeRecurso.endsWith(".css")){
            return "text/css";
        }else if(nomeRecurso.endsWith(".jpg") || nomeRecurso.endsWith(".jpg")){
            return "image/jpeg";
        }else if(nomeRecurso.endsWith(".npg")){
            return "image/png";
        }else if(nomeRecurso.endsWith(".js")) {
            System.out.println("tem js");
            return "text/javascript";
        }else{
            return "text/html";
        }
    }



}
