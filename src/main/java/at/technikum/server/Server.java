package at.technikum.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    // opens port

    private final Application application;

    private ServerSocket serverSocket;

    public Server(Application application) {
        this.application = application;
    }

    public void start() {
        try {
            this.serverSocket = new ServerSocket(10001);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        while (true) {
            try {
                Socket socket = this.serverSocket.accept();

                RequestHandler requestHandler = new RequestHandler(
                        socket,
                        this.application
                );

                threadPool.submit(requestHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}