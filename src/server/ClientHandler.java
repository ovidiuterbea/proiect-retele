package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

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
				} else if ("add".equals(valoriCommand[0])) {
					Boolean isValid = true;
					String[] valori = command.split(" ");
					BufferedReader bufferedReader = null;
					try {
						FileInputStream file = new FileInputStream("dictionar.txt");
						InputStreamReader inputStreamReader = new InputStreamReader(file);
						bufferedReader = new BufferedReader(inputStreamReader);

						String line;
						while ((line = bufferedReader.readLine()) != null) {
							String[] valoriLinie = line.split(" ");
							if (Integer.parseInt(valoriLinie[1]) == Integer.parseInt(valori[2])) {
								isValid = false;
								writer.println("Nu puteti adauga acest string deoarece exista aceasta cheie");
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

					if (isValid == true) {
						FileOutputStream file = new FileOutputStream("dictionar.txt", true);
						OutputStreamWriter localWriter = new OutputStreamWriter(file);
						BufferedWriter buffer = new BufferedWriter(localWriter);
						buffer.write(valori[1]);
						buffer.write(" ");
						buffer.write(valori[2]);
						buffer.write(" ");
						buffer.write(String.valueOf(address.getPort()));
						buffer.newLine();
						buffer.close();
						writer.println("Ati adaugat cu success string-ul " + valori[1] + " cu cheia " + valori[2]);
						writer.flush();
					}

				} else if ("search".equals(valoriCommand[0])) {
					String[] valoriComanda = command.split(" ");
					BufferedReader bufferedReader = null;
					Boolean wasFound = false;
					try {
						FileInputStream file = new FileInputStream("dictionar.txt");
						InputStreamReader inputStreamReader = new InputStreamReader(file);
						bufferedReader = new BufferedReader(inputStreamReader);

						String line;
						while ((line = bufferedReader.readLine()) != null) {
							String[] valoriRand = line.split(" ");
							if (Integer.parseInt(valoriRand[1]) == Integer.parseInt(valoriComanda[1])) {
								wasFound = true;
								writer.println("String-ul cu cheia " + valoriRand[1] + " exista. Acesta are valoarea "
										+ valoriRand[0] + " si este detinut de clientul cu ID-ul " + valoriRand[2]);
								writer.flush();
							}
						}

						if (wasFound == false) {
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
					Boolean wasFound = false;
					try {
						FileInputStream file = new FileInputStream("dictionar.txt");
						InputStreamReader inputStreamReader = new InputStreamReader(file);
						bufferedReader = new BufferedReader(inputStreamReader);

						String line;
						int idClient = 0;
						StringBuilder sb = new StringBuilder("");
						while ((line = bufferedReader.readLine()) != null) {
							String[] valoriLinie = line.split(" ");
							if (!line.contains(valoriComanda[1])) {
								sb.append(line);
								sb.append('\n');
							} else {
								wasFound = true;
								idClient = Integer.parseInt(valoriLinie[2]);
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
