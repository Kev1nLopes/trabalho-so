package Main;

import javax.swing.filechooser.FileView;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static StringBuilder cadeirasHtml = new StringBuilder();
    private static int numAcessos = 0;

    private static String showHtml = "/index.html";

    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(3000);
        OutputStream logs = new FileOutputStream("logs.txt");


        while (true){
            Socket socket = ss.accept();
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            byte[] buffer = new byte[2048];
            int nBytes = in.read(buffer);
            String str = new String(buffer, 0, nBytes);
            String[] linhas = str.split("\n");
            String[] linha1 = linhas[0].split(" ");
            String recurso = linha1[1];
            System.out.println("[RECURSO]" + recurso);
            showHtml = showHtml.replace('/', File.separatorChar);
            File f = new File("view" + showHtml);

            Thread thread = new Thread(() -> {
                try {
                    OutputStreamWriter osw = new OutputStreamWriter(logs);
                    BufferedWriter logWritter = new BufferedWriter(osw);

                    String header = "HTTP/1.1 200 OK\n"+
                            "Content-Type: "+  "text/html" + "; charset=utf-8\n\n";
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
                        byte[] buffer2 = new byte[2048];
                        int nBytes2 = fileIn.read(buffer2);

                        do{
                            if(nBytes2 > 0){

                                bout.write(buffer2, 0, nBytes2);
                                nBytes2 = fileIn.read(buffer2);
                            }

                        } while(nBytes2 == 1024);

                        if(nBytes2 > 0){
                            bout.write(buffer2, 0, nBytes2);
                        }
                    }

                    String saida = processaVariaveis(bout);

                    synchronized (logs) {
                        if(verificaArquivo(recurso)){
                            System.out.println("Essa cadeira ja foi agendada");
                            socket.close();
                            return;
                        }
                        if(recurso.contains("/cadeira")){
                            String[] arr = recurso.split("\\?");
                            if(arr[1].contains("numero")){
                                int idCadeira = Integer.parseInt(arr[1].split("=")[1]);
                                logWritter.write("cadeira-" + idCadeira + "-Agendada\n");
                                logWritter.newLine();
                                logWritter.flush();
                                saida.replace("<div id='cadeira-" + idCadeira + "' class='cadeira'>" + idCadeira + "</div>", "<div id='cadeira-" + idCadeira + "' class='cadeira busy'>" + idCadeira + "</div>" );

                            }

                        }
                        out.write((saida.getBytes(StandardCharsets.UTF_8)));
                        out.write(bout.toByteArray());
                        out.flush();
                        Thread.sleep(20000);
                        socket.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();






        }
    }


    public static boolean verificaArquivo(String recurso) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream("logs.txt"), "UTF-8");
        String cadeiraCodigo = "";
        if (recurso.contains("cadeira")) {
            cadeiraCodigo = recurso.split("\\?")[1].split("=")[1];
        }

        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine();


            // Verifica se a linha contém o conteúdo desejado
            if (linha.contains("cadeira-" + cadeiraCodigo + "-Agendada")) {
                System.out.println("O arquivo de logs já contém o conteúdo desejado.");
                scanner.close();
                return true;
            }
        }
        return false;
    }





    private static String processaVariaveis(ByteArrayOutputStream bout) {
        String str = new String(bout.toByteArray());

        cadeirasHtml = new StringBuilder("");
        for (int i = 0; i < 42; i++) {
            String cadeira = "<div id='cadeira-" + i + "' class='cadeira'>" + i + "</div>";
            cadeirasHtml.append(cadeira);
        }


        str = str.replace("${Cadeiras}", cadeirasHtml.toString());
        return str;
    }
}