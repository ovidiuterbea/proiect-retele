package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.writer = new PrintWriter(this.clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        while (!clientSocket.isClosed()) {
            try {
                InetSocketAddress address = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
                String command = reader.readLine().toLowerCase();
                String[] valoriCommand = command.split(" ");
                if ("exit".equals(command)) {
                    clientSocket.close();
                } else if ("help".equals(command)) {
                	StringBuilder mesajDeReturnat = new StringBuilder();
                	mesajDeReturnat.append("Comenzi disponibile:");                	
                	mesajDeReturnat.append("add <cheie_unica> <obiect> -- Adauga string-ul cu cheia specificata ");               	
                	mesajDeReturnat.append("search <cheie_unica> -- Afiseaza string-ul cu cheia specificata ");                	
                	mesajDeReturnat.append("delete <cheie_unica> -- Sterge string-ul cu cheia specificata ");                	
                	mesajDeReturnat.append("keys -- Afiseaza lista cheilor din dictionar ");                	
                	mesajDeReturnat.append("help -- Afiseaza comenzile diponibile ");                	
                	mesajDeReturnat.append("exit -- Iesire");
                    writer.println(mesajDeReturnat.toString());
                    writer.flush();
                } else if ("add".equals(valoriCommand[0])) {
                    boolean isValid = true;
                    String[] valori = command.split(" ");
                    BufferedReader bufferedReader = null;
                    try {
                        FileInputStream file = new FileInputStream("dictionar.txt");
                        InputStreamReader inputStreamReader = new InputStreamReader(file);
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] valoriLinie = line.split(",");
                            if (Integer.parseInt(valoriLinie[0]) == Integer.parseInt(valori[1])) {
                                isValid = false;
                                writer.println("Cheia exista deja!");
                                writer.flush();
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bufferedReader != null)
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }

                    if (isValid) {
                        FileOutputStream file = new FileOutputStream("dictionar.txt", true);
                        OutputStreamWriter localWriter = new OutputStreamWriter(file);
                        BufferedWriter buffer = new BufferedWriter(localWriter);
                        buffer.write(valori[1]);
                        buffer.write(",");
                        buffer.write(String.valueOf(address.getPort()));
                        buffer.write(",");
                        for (int i = 2; i < valori.length; i++) {
                            buffer.write(valori[i] + " ");
                        }
                        buffer.newLine();
                        buffer.close();
                        writer.println("String-ul a fost adaugat cu succes!");
                        writer.flush();
                    }

                } else if ("search".equals(valoriCommand[0])) {
                    String[] valoriComanda = command.split(" ");
                    BufferedReader bufferedReader = null;
                    boolean wasFound = false;
                    try {
                        FileInputStream file = new FileInputStream("dictionar.txt");
                        InputStreamReader inputStreamReader = new InputStreamReader(file);
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] valoriRand = line.split(",");
                            if (Integer.parseInt(valoriRand[0]) == Integer.parseInt(valoriComanda[1])) {
                                wasFound = true;
                                writer.println("String-ul cu cheia " + valoriRand[0] + " exista. Acesta are valoarea \""
                                        + valoriRand[2] + "\" si este detinut de clientul cu ID-ul " + valoriRand[1]);
                                writer.flush();
                            }
                        }

                        if (!wasFound) {
                            writer.println("String-ul cu cheia " + valoriComanda[1] + " nu exista.");
                            writer.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bufferedReader != null)
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                } else if ("delete".equals(valoriCommand[0])) {
                    String[] valoriComanda = command.split(" ");
                    BufferedReader bufferedReader = null;
                    boolean wasFound = false;
                    try {
                        FileInputStream file = new FileInputStream("dictionar.txt");
                        InputStreamReader inputStreamReader = new InputStreamReader(file);
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String line;
                        int idClient = 0;
                        StringBuilder sb = new StringBuilder("");
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] valoriLinie = line.split(",");
                            if (!valoriLinie[0].equals(valoriComanda[1])) {
                                sb.append(line);
                                sb.append('\n');
                            } else {
                                wasFound = true;
                                idClient = Integer.parseInt(valoriLinie[1]);
                            }
                        }
                        if (wasFound && (address.getPort() == idClient)) {
                            FileOutputStream fileOutputStream = new FileOutputStream("dictionar.txt", false);
                            OutputStreamWriter localWriter = new OutputStreamWriter(fileOutputStream);
                            BufferedWriter buffer = new BufferedWriter(localWriter);
                            buffer.write(sb.toString());
                            buffer.close();
                            writer.println("String-ul cu cheia " + valoriComanda[1] + " a fost sters!");
                            writer.flush();
                        } else if (!wasFound) {
                            writer.println("String-ul cu cheia " + valoriComanda[1] + " nu exista!");
                            writer.flush();
                        } else {
                            writer.println("Nu aveti permisiunea sa stergeti acest string.");
                            writer.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bufferedReader != null)
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                } else if ("keys".equals(valoriCommand[0])) {
                    List<Integer> listaChei = new ArrayList<>();
                    BufferedReader bufferedReader = null;
                    try {
                        FileInputStream file = new FileInputStream("dictionar.txt");
                        InputStreamReader inputStreamReader = new InputStreamReader(file);
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String line;
                        while((line = bufferedReader.readLine()) != null){
                            String[] valoriRand = line.split(",");
                            listaChei.add(Integer.parseInt(valoriRand[0]));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally{
                        if(bufferedReader != null)
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }

                    if(listaChei.size()>0){
                        writer.print("Dictionarul contine urmatoarele chei:");
                        writer.flush();
                        for(Integer cheie : listaChei) {
                            writer.print(" " + cheie);
                            writer.flush();
                        }
                        writer.println();
                        writer.flush();
                    }else {
                        writer.println("Dictionarul este gol!");
                        writer.flush();
                    }
                } else {
                    writer.println("Comanda invalida.");
                    writer.flush();
                }
            } catch (Exception e) {
                writer.println(e.getMessage());
                writer.flush();
            }
        }
    }

}