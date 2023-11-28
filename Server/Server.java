/* Modified By: Tyler Justin H. Tan and Lanz Kendall Y. Lim, CSNETWK (S12) */

import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

class Connection extends Thread {

    private Socket socket;
    private Server server;
    private String handle = null;
    private DataInputStream reader;
    private DataOutputStream writer;
    private boolean isRegistered = false;

    public Connection(Socket socket, Server server, String tempHandle) {
        this.socket = socket;
        this.server = server;
        this.handle = tempHandle;
    }

    @Override
    public void run() {
        try {
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
            System.out.println("Server: Client " + socket.getRemoteSocketAddress() + " has connected!");
            writer.writeUTF("Connection to File Exchange Server is successful!");

            while (true) {
                String command = reader.readUTF();
                StringTokenizer st = new StringTokenizer(command, " ");
                String cmd = st.nextToken();
                boolean close = false;

                switch (cmd) {
                    case "/leave":
                        try {
                            System.out.println("Here");
                            writer.writeUTF("Connection closed. Thank you!");
                            socket.close();
                            writer.close();
                            reader.close();
                            System.out.println("Here");
                            server.removeUser(this);
                            System.out.println("Here");
                            System.out.println("Server: Connection closed. Thank you!");
                        } catch (Exception e) {
                            System.out.println("Error: Disconnection failed.");
                        }
                        close = true;
                        break;
                    case "/register":
                        String handle = st.nextToken();
                        if (server.isUser(handle)) {
                            writer.writeUTF("Error: Registration failed. Handle or alias already exists.");
                            break;
                        }
                        this.handle = handle;
                        writer.writeUTF("Welcome " + handle + "!");
                        System.out.println("Server: " + handle + " has registered.");
                        this.isRegistered = true;
                        break;
                    case "/store":
                        if(!this.isRegistered)
                        {
                            writer.writeUTF("Error: You must register first before uploading files.");
                            break;
                        } else {
                            writer.writeUTF("Ready to receive file.");
                        }
                        String filename = st.nextToken();
                        int filesize = Integer.parseInt(st.nextToken());

                        byte[] file = new byte[filesize];
                        reader.readFully(file, 0, filesize);
                        FileOutputStream fileWriter = new FileOutputStream(filename);
                        fileWriter.write(file);
                        fileWriter.close();
                        Date time = new java.util.Date(System.currentTimeMillis());

                        writer.writeUTF(this.handle + "<" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time)
                                + ">: Uploaded " + filename + ".");
                        System.out.println("Server: File " + filename + " has been stored.");
                        break;
                    case "/dir":
                        if(!this.isRegistered)
                        {
                            writer.writeUTF("Error: You must register first before accessing the server directory.");
                            break;
                        }
                        File dir = new File(".");
                        File[] files = dir.listFiles();
                        String fileList = "Server Directory:";
                        for (File f : files) {
                            if (f.isFile() && !f.getName().equals("Server.java")
                                    && !f.getName().equals("Connection.java") && !f.getName().equals("Client.java")
                                    && !f.getName().equals("Client.class") && !f.getName().equals("Connection.class")
                                    && !f.getName().equals("Server.class")) {
                                fileList += "\n" + f.getName();
                            }
                        }
                        System.out.println(fileList);
                        writer.writeUTF(fileList);
                        break;
                    case "/get":
                        if(!this.isRegistered)
                        {
                            writer.writeUTF("Error: You must register first before downloading files.");
                            break;
                        }
                        String filename2 = st.nextToken();
                        File file2 = new File(filename2);
                        if (!file2.exists()) {
                            writer.writeUTF("Error: File not found in the server.");
                            break;
                        }
                        byte[] fileContent = new byte[(int) file2.length()];
                        FileInputStream fileReader = new FileInputStream(file2);
                        fileReader.read(fileContent);
                        fileReader.close();
                        writer.writeUTF(fileContent.length + "");
                        writer.write(fileContent);
                        System.out.println("Server: File " + filename2 + " has been sent.");
                        break;
                    case "/msg":
                        if(!this.isRegistered)
                        {
                            writer.writeUTF("Error: You must register first before sending messages.");
                            break;
                        }
                        String recipient = st.nextToken();
                        if(recipient.equals(this.handle)) {
                            writer.writeUTF("Error: Cannot send message to yourself.");
                            break;
                        }
                        if(!server.isUser(recipient)) {
                            writer.writeUTF("Error: Recipient does not exist.");
                            break;
                        }
                        String message = "";
                        while (st.hasMoreTokens()) {
                            message += st.nextToken() + " ";
                        }
                        server.sendTo(recipient, this.handle + ": " + message);
                        writer.writeUTF("Message sent to " + recipient + " successfully.");
                        System.out.println("Server: Message sent to " + recipient + ".");
                        break;
                    case "/all":
                        if(!this.isRegistered)
                        {
                            writer.writeUTF("Error: You must register first before sending messages.");
                            break;
                        }
                        String message2 = "";
                        while (st.hasMoreTokens()) {
                            message2 += st.nextToken() + " ";
                        }
                        server.broadcast(this.handle, this.handle + ": " + message2);
                        writer.writeUTF("Message broadcasted to all users successfully.");
                        System.out.println("Server: Message sent to all users.");
                        break;
                }

                if (close)
                    break;
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server: Client " + socket.getRemoteSocketAddress() + " has disconnected.");
        }
    }

    public String getHandle() {
        return handle;
    }

    public void send(String message) {
        try {
            writer.writeUTF(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Server {

    private ArrayList<Connection> connections = new ArrayList<Connection>();

    public Server(int nPort) {
        ServerSocket serverSocket;
        try {
            System.out.println("Server: Listening on port " + nPort + "...");
            serverSocket = new ServerSocket(nPort);
        } catch (Exception e) {
            System.out.println("Error: Invalid port number. Please try again.");
            return;
        }

        while (true) {
            try {
                Socket s = serverSocket.accept();
                Connection c = new Connection(s, this, "User" + connections.size());
                connections.add(c);
                c.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isUser(String handle) {
        for (Connection c : connections) {
            if (c.getHandle().equals(handle)) {
                return true;
            }
        }
        return false;
    }

    public void sendTo(String handle, String message) {
        for (Connection c : connections) {
            if (c.getHandle().equals(handle)) {
                c.send(message);
                break;
            }
        }
    }

    public void broadcast(String handle, String message) {
        for (Connection c : connections) {
            if (!c.getHandle().equals(handle)) {
                c.send(message);
            }
        }
    }

    public void removeUser(Connection c) {
        connections.remove(c);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid command. Usage: java Server <port>");
            return;
        }
        int nPort = Integer.parseInt(args[0]);
        new Server(nPort);
    }
}
