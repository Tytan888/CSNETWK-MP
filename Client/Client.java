/* Modified By: Tyler Justin H. Tan, Lanz Kendall Y. Lim, and Ricardo Luis Q. Vicerra CSNETWK (S12) */

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Client {

    Scanner sc = new Scanner(System.in);
    String command = "";
    Socket clientEndpoint = null;
    DataOutputStream writer = null;
    DataInputStream reader = null;

    public Client() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 820);
        frame.setResizable(false);
        frame.setTitle("チットチャット Client");
        frame.setLocationRelativeTo(null);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));

        Font font = new Font("Comic Sans", Font.BOLD, 14);

        JPanel joinPanel = new JPanel(new FlowLayout());

        JLabel joinLabel = new JLabel("/join");
        joinLabel.setPreferredSize(new Dimension(30, 30));
        joinLabel.setFont(font);
        joinPanel.add(joinLabel);

        JTextField joinFieldIP = new JTextField("", 10);
        joinFieldIP.setPreferredSize(new Dimension(100, 30));
        joinPanel.add(joinFieldIP);

        JTextField joinFieldPort = new JTextField("", 5);
        joinFieldPort.setPreferredSize(new Dimension(50, 30));
        joinPanel.add(joinFieldPort);

        JButton joinButton = new JButton("Connect");
        joinButton.setPreferredSize(new Dimension(100, 30));
        joinPanel.add(joinButton);

        frame.add(joinPanel);

        JPanel leavePanel = new JPanel(new FlowLayout());

        JLabel leaveLabel = new JLabel("/leave");
        leaveLabel.setFont(font);
        leaveLabel.setPreferredSize(new Dimension(50, 30));
        leavePanel.add(leaveLabel);

        JButton leaveButton = new JButton("Disconnect");
        leaveButton.setPreferredSize(new Dimension(230, 30));
        leavePanel.add(leaveButton);

        frame.add(leavePanel);

        JPanel registerPanel = new JPanel(new FlowLayout());

        JLabel registerLabel = new JLabel("/register");
        registerLabel.setFont(font);
        registerLabel.setPreferredSize(new Dimension(70, 30));
        registerPanel.add(registerLabel);

        JTextField registerField = new JTextField("", 14);
        registerField.setPreferredSize(new Dimension(150, 30));
        registerPanel.add(registerField);

        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 30));
        registerPanel.add(registerButton);

        frame.add(registerPanel);

        JPanel storePanel = new JPanel(new FlowLayout());

        JLabel storeLabel = new JLabel("/store");
        storeLabel.setFont(font);
        storeLabel.setPreferredSize(new Dimension(50, 30));
        storePanel.add(storeLabel);

        JTextField storeField = new JTextField("", 16);
        storeField.setPreferredSize(new Dimension(100, 30));
        storePanel.add(storeField);

        JButton storeButton = new JButton("Store");
        storeButton.setPreferredSize(new Dimension(100, 30));
        storePanel.add(storeButton);

        frame.add(storePanel);

        JPanel dirPanel = new JPanel(new FlowLayout());

        JLabel dirLabel = new JLabel("/dir");
        dirLabel.setFont(font);
        dirLabel.setPreferredSize(new Dimension(30, 30));
        dirPanel.add(dirLabel);

        JButton dirButton = new JButton("List Files");
        dirButton.setPreferredSize(new Dimension(280, 30));
        dirPanel.add(dirButton);

        frame.add(dirPanel);

        JPanel getPanel = new JPanel(new FlowLayout());

        JLabel getLabel = new JLabel("/get");
        getLabel.setFont(font);
        getLabel.setPreferredSize(new Dimension(50, 30));
        getPanel.add(getLabel);

        JTextField getField = new JTextField("", 17);
        getField.setPreferredSize(new Dimension(100, 30));
        getPanel.add(getField);

        JButton getButton = new JButton("Retrieve");
        getButton.setPreferredSize(new Dimension(100, 30));
        getPanel.add(getButton);

        frame.add(getPanel);

        JPanel msgPanel = new JPanel(new FlowLayout());

        JLabel msgLabel = new JLabel("/msg");
        msgLabel.setFont(font);
        msgLabel.setPreferredSize(new Dimension(40, 30));
        msgPanel.add(msgLabel);

        JTextField msgField1 = new JTextField("", 5);
        msgField1.setPreferredSize(new Dimension(100, 30));
        msgPanel.add(msgField1);

        JTextField msgField2 = new JTextField("", 14);
        msgField2.setPreferredSize(new Dimension(100, 30));
        msgPanel.add(msgField2);

        JButton msgButton = new JButton("Send");
        msgButton.setPreferredSize(new Dimension(70, 30));
        msgPanel.add(msgButton);

        frame.add(msgPanel);

        JPanel allPanel = new JPanel(new FlowLayout());

        JLabel allLabel = new JLabel("/all");
        allLabel.setFont(font);
        allLabel.setPreferredSize(new Dimension(30, 30));
        allPanel.add(allLabel);

        JTextField allField = new JTextField("", 15);
        allField.setPreferredSize(new Dimension(30, 30));
        allPanel.add(allField);

        JButton allButton = new JButton("Broadcast");
        allButton.setPreferredSize(new Dimension(100, 30));
        allPanel.add(allButton);

        frame.add(allPanel);

        JPanel helpPanel = new JPanel(new FlowLayout());

        JLabel helpLabel = new JLabel("/?");
        helpLabel.setFont(font);
        helpLabel.setPreferredSize(new Dimension(50, 30));
        helpPanel.add(helpLabel);

        JButton helpButton = new JButton("Need help with commands?");
        helpButton.setPreferredSize(new Dimension(230, 30));
        helpPanel.add(helpButton);

        frame.add(helpPanel);

        JPanel displayPanel = new JPanel(new FlowLayout());

        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        JScrollPane scrollArea = new JScrollPane(displayArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollArea.setPreferredSize(new Dimension(340, 300));

        displayPanel.add(scrollArea);

        frame.add(displayPanel);

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientEndpoint != null) {
                    displayArea.append(
                            "Error: Connection to the Server has failed! Please disconnect first before reconnecting again! \n\n");
                    return;
                }
                try {
                    String ip = joinFieldIP.getText().trim();
                    int port = Integer.parseInt(joinFieldPort.getText().trim());
                    if (ip.equals("")) {
                        displayArea
                                .append("Error: Connection to the Server has failed! IP Address cannot be empty! \n\n");
                        return;
                    }

                    clientEndpoint = new Socket();
                    clientEndpoint.connect(new InetSocketAddress(ip, port), 1000);
                    writer = new DataOutputStream(clientEndpoint.getOutputStream());
                    reader = new DataInputStream(clientEndpoint.getInputStream());
                    displayArea.append(reader.readUTF() + "\n\n");
                } catch (Exception ex) {
                    clientEndpoint = null;
                    displayArea.append(
                            "Error: Connection to the Server has failed! Please check IP Address and Port Number.\n\n");
                }
            }
        });

        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientEndpoint == null) {
                    displayArea.append("Error: Disconnection failed. Please connect to the server first.\n\n");
                    return;
                }
                try {
                    writer.writeUTF("/leave");
                    displayArea.append(reader.readUTF() + "\n\n");
                    clientEndpoint.close();
                    writer.close();
                    reader.close();
                    clientEndpoint = null;
                    writer = null;
                    reader = null;
                } catch (Exception ex) {
                    displayArea.append("Error: Disconnection failed.\n\n");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientEndpoint == null) {
                    displayArea.append("Error: Registration failed. Please connect to the server first.\n\n");
                    return;
                }
                try {
                    String handle = registerField.getText().trim();
                    if (handle.equals("")) {
                        displayArea.append("Error: Registration failed. Handle cannot be empty.\n\n");
                        return;
                    }
                    writer.writeUTF("/register " + handle);
                    String response = reader.readUTF();
                    displayArea.append(response + "\n\n");
                } catch (Exception ex) {
                    displayArea.append("Error: Registration failed.\n\n");
                }
            }
        });

        storeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientEndpoint == null) {
                    displayArea.append("Error: Storage failed. Please connect to the server first.\n\n");
                    return;
                }
                try {
                    String filename = storeField.getText().trim();
                    if (filename.equals("Client.java") || filename.equals("Server.java")
                            || filename.equals("Connection.java") || filename.equals("Client.class")
                            || filename.equals("Server.class") || filename.equals("Connection.class")) {
                        displayArea.append("Error: Storage failed. Filename is not allowed.\n\n");
                        return;
                    }
                    if (filename.equals("")) {
                        displayArea.append("Error: Storage failed. Filename cannot be empty.\n\n");
                        return;
                    }
                    DataInputStream fileReader = new DataInputStream(new FileInputStream(filename));

                    command = "/store " + filename + " " + fileReader.available();
                    writer.writeUTF(command);

                    String check = reader.readUTF();
                    if (!check.equals("Ready to receive file.")) {
                        displayArea.append(check + "\n\n");
                        fileReader.close();
                        return;
                    }

                    byte[] fileTxt = new byte[fileReader.available()];
                    fileReader.read(fileTxt);
                    writer.write(fileTxt);
                    fileReader.close();

                    String response = reader.readUTF();
                    displayArea.append(response + "\n\n");
                } catch (FileNotFoundException ex) {
                    displayArea.append("Error: File not found.\n\n");
                } catch (Exception ex) {
                    displayArea.append("Error: File storage failed.\n\n");
                }
            }
        });

        dirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientEndpoint == null) {
                    displayArea.append("Error: Directory listing failed. Please connect to the server first.\n\n");
                    return;
                }
                try {
                    writer.writeUTF("/dir");
                    String response = reader.readUTF();
                    displayArea.append(response + "\n\n");
                } catch (Exception ex) {
                    displayArea.append("Error: Directory listing failed.\n\n");
                }
            }
        });

        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientEndpoint == null) {
                    displayArea.append("Error: File retrieval failed. Please connect to the server first.\n\n");
                    return;
                }
                try {
                    String filename = getField.getText().trim();
                    if (filename.equals("")) {
                        displayArea.append("Error: File retrieval failed. Filename cannot be empty.\n\n");
                        return;
                    }
                    writer.writeUTF("/get " + filename);
                    String response = reader.readUTF();
                    if (response.equals("Error: File not found in the server.")) {
                        displayArea.append(response + "\n\n");
                        return;
                    } else if (response.equals("Error: You must register first before downloading files.")) {
                        displayArea.append(response + "\n\n");
                        return;
                    }
                    int filesize = Integer.parseInt(response);
                    byte[] file = new byte[filesize];
                    reader.readFully(file, 0, filesize);
                    FileOutputStream fileWriter = new FileOutputStream(filename);
                    fileWriter.write(file);
                    fileWriter.close();
                    displayArea.append("File received from Server: " + filename + "\n\n");
                } catch (Exception ex) {
                    displayArea.append("Error: File retrieval failed.\n\n");
                }
            }
        });

        msgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientEndpoint == null) {
                    displayArea.append("Error: Message sending failed. Please connect to the server first.\n\n");
                    return;
                }
                try {
                    String handle = msgField1.getText().trim();
                    if (handle.equals("")) {
                        displayArea.append("Error: Message sending failed. Handle cannot be empty.\n\n");
                        return;
                    }
                    String message = msgField2.getText().trim();
                    if (message.equals("")) {
                        displayArea.append("Error: Message sending failed. Message cannot be empty.\n\n");
                        return;
                    }
                    writer.writeUTF("/msg " + handle + " " + message);
                    String response = reader.readUTF();
                    displayArea.append(response + "\n\n");
                } catch (Exception ex) {
                    displayArea.append("Error: Message sending failed.\n\n");
                }
            }
        });

        allButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientEndpoint == null) {
                    displayArea
                            .append("Error: Broadcast message sending failed. Please connect to the server first.\n\n");
                    return;
                }
                try {
                    String message = allField.getText().trim();
                    if (message.equals("")) {
                        displayArea.append("Error: Broadcast message sending failed. Message cannot be empty.\n\n");
                        return;
                    }
                    writer.writeUTF("/all " + message);
                    String response = reader.readUTF();
                    displayArea.append(response + "\n\n");
                } catch (Exception ex) {
                    displayArea.append("Error: Broadcast message sending failed.\n\n");
                }
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayArea.append("Available Commands...\n");
                displayArea.append("/join <server_ip_add> <port> - Connects to the server.\n");
                displayArea.append("/leave - Disconnects from the server.\n");
                displayArea.append("/register <handle> - Registers the client to the server.\n");
                displayArea.append("/store <filename> - Stores the file to the server.\n");
                displayArea.append("/dir - Lists the files in the server.\n");
                displayArea.append("/get <filename> - Retrieves the file from the server.\n");
                displayArea.append("/msg <handle> <message> - Sends a message to a specific client.\n");
                displayArea.append("/all <message> - Sends a message to all clients.\n");
                displayArea.append("/? - Displays the list of available commands.\n\n");
            }
        });

        frame.setVisible(true);

        while (true) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (clientEndpoint != null && clientEndpoint.isConnected()) {
                try {
                    if (reader.available() > 0) {
                        displayArea.append(reader.readUTF() + "\n\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        new Client();

        /*
         * OLD CODE FOR CLI
         * Scanner sc = new Scanner(System.in);
         * String command = "";
         * Socket clientEndpoint = null;
         * DataOutputStream writer = null;
         * DataInputStream reader = null;
         * while (true) {
         * System.out.print("Enter command: ");
         * command = sc.nextLine();
         * StringTokenizer st = new StringTokenizer(command, " ");
         * String cmd = st.nextToken();
         * switch (cmd) {
         * case "/join":
         * if (st.countTokens() != 2) {
         * System.out.println(
         * "Error: Command parameters do not match or is not allowed. Usage: /join <server_ip_add> <port>"
         * );
         * break;
         * }
         * String ip = st.nextToken();
         * int port = Integer.parseInt(st.nextToken());
         * try {
         * clientEndpoint = new Socket(ip, port);
         * writer = new DataOutputStream(clientEndpoint.getOutputStream());
         * reader = new DataInputStream(clientEndpoint.getInputStream());
         * System.out.println(reader.readUTF());
         * } catch (Exception e) {
         * System.out.println(
         * "Error: Connection to the Server has failed! Please check IP Address and Port Number."
         * );
         * }
         * break;
         * case "/leave":
         * if (st.countTokens() != 0) {
         * System.out.
         * println("Error: Command parameters do not match or is not allowed. Usage: /leave"
         * );
         * break;
         * }
         * if (clientEndpoint == null) {
         * System.out.
         * println("Error: Disconnection failed. Please connect to the server first.");
         * break;
         * }
         * try {
         * writer.writeUTF(command);
         * System.out.println(reader.readUTF());
         * clientEndpoint.close();
         * writer.close();
         * reader.close();
         * clientEndpoint = null;
         * writer = null;
         * reader = null;
         * } catch (Exception e) {
         * System.out.println("Error: Disconnection failed.");
         * }
         * break;
         * case "/register":
         * if (st.countTokens() != 1) {
         * System.out.println(
         * "Error: Command parameters do not match or is not allowed. Usage: /register <handle>"
         * );
         * break;
         * }
         * if (clientEndpoint == null) {
         * System.out.
         * println("Error: Registration failed. Please connect to the server first.");
         * break;
         * }
         * try {
         * writer.writeUTF(command);
         * String response = reader.readUTF();
         * System.out.println(response);
         * } catch (Exception e) {
         * System.out.println("Error: Registration failed.");
         * }
         * break;
         * case "/store":
         * if (st.countTokens() != 1) {
         * System.out.println(
         * "Error: Command parameters do not match or is not allowed. Usage: /store <filename>"
         * );
         * break;
         * }
         * if (clientEndpoint == null) {
         * System.out.
         * println("Error: Storage failed. Please connect to the server first.");
         * break;
         * }
         * try {
         * String filename = st.nextToken();
         * if (filename == "Client.java" || filename == "Server.java" || filename ==
         * "Connection.java"
         * || filename == "Client.class" || filename == "Server.class"
         * || filename == "Connection.class") {
         * System.out.println("Error: Storage failed. File is not allowed.");
         * break;
         * }
         * DataInputStream fileReader = new DataInputStream(new
         * FileInputStream(filename));
         * 
         * command += " " + fileReader.available();
         * writer.writeUTF(command);
         * 
         * byte[] fileTxt = new byte[fileReader.available()];
         * int nBytes = fileReader.available();
         * fileReader.read(fileTxt);
         * writer.write(fileTxt);
         * fileReader.close();
         * 
         * String response = reader.readUTF();
         * System.out.println(response);
         * } catch (FileNotFoundException e) {
         * System.out.println("Error: File not found.");
         * } catch (Exception e) {
         * System.out.println("Error: File storage failed.");
         * }
         * break;
         * case "/dir":
         * if (st.countTokens() != 0) {
         * System.out.
         * println("Error: Command parameters do not match or is not allowed. Usage: /dir"
         * );
         * break;
         * }
         * if (clientEndpoint == null) {
         * System.out.
         * println("Error: Directory listing failed. Please connect to the server first."
         * );
         * break;
         * }
         * try {
         * writer.writeUTF(command);
         * String response = reader.readUTF();
         * System.out.println(response);
         * } catch (Exception e) {
         * System.out.println("Error: Directory listing failed.");
         * }
         * break;
         * case "/get":
         * if (st.countTokens() != 1) {
         * System.out.println(
         * "Error: Command parameters do not match or is not allowed. Usage: /get <filename>"
         * );
         * break;
         * }
         * if (clientEndpoint == null) {
         * System.out.
         * println("Error: File retrieval failed. Please connect to the server first.");
         * break;
         * }
         * try {
         * String filename = st.nextToken();
         * writer.writeUTF(command);
         * String response = reader.readUTF();
         * if (response.equals("Error: File not found in the server.")) {
         * System.out.println(response);
         * break;
         * }
         * int filesize = Integer.parseInt(response);
         * byte[] file = new byte[filesize];
         * reader.readFully(file, 0, filesize);
         * FileOutputStream fileWriter = new FileOutputStream(filename);
         * fileWriter.write(file);
         * fileWriter.close();
         * System.out.println("File received from Server: " + filename);
         * } catch (Exception e) {
         * System.out.println("Error: File retrieval failed.");
         * }
         * break;
         * case "/msg":
         * if (st.countTokens() < 2) {
         * System.out.println(
         * "Error: Command parameters do not match or is not allowed. Usage: /msg <handle> <message>"
         * );
         * break;
         * }
         * if (clientEndpoint == null) {
         * System.out.
         * println("Error: Message sending failed. Please connect to the server first."
         * );
         * break;
         * }
         * try {
         * writer.writeUTF(command);
         * String response = reader.readUTF();
         * System.out.println(response);
         * } catch (Exception e) {
         * System.out.println("Error: Message sending failed.");
         * }
         * break;
         * case "/?":
         * System.out.println("Available Commands...");
         * System.out.println("/join <server_ip_add> <port> - Connects to the server.");
         * System.out.println("/leave - Disconnects from the server.");
         * System.out.println("/register <handle> - Registers the client to the server."
         * );
         * System.out.println("/store <filename> - Stores the file to the server.");
         * System.out.println("/dir - Lists the files in the server.");
         * System.out.println("/get <filename> - Retrieves the file from the server.");
         * System.out.println("/? - Displays the list of available commands.");
         * break;
         * default:
         * System.out.println("Error: Command not found.");
         * break;
         * }
         * }
         */
    }
}