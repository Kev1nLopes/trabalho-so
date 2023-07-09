package Main;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class ThreadCadeira extends Thread {

    private String showHtml = "";
    private Socket socket;

    private BufferedWriter logWritter;

    public ThreadCadeira(Socket socket) {
        this.socket = socket;
        String remoteAddress = String.valueOf(socket);
        System.out.println("Setting thread name to: " + remoteAddress);
        this.setName(remoteAddress);


    }

    @Override
    public void run() {


    }

    public synchronized String changeLogs() throws IOException {
        try {
            OutputStream logs = new FileOutputStream("./volume/logs.txt");
            synchronized (logs) {

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "teste";
    }
}

//        System.out.println("nome" + this.getName());
//        OutputStreamWriter osw = new OutputStreamWriter(logs);
//        logWritter = new BufferedWriter(osw);
//        String message = "teste" + this.getName();
//        logWritter.write(message);
//        logWritter.newLine();
//        logWritter.flush();
//        return "teste";
//    }

