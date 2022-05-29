package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Program {

	public static void main(String[] args) {
		List<Integer> listaChei = new ArrayList<>();
		int port = Integer.parseInt(ResourceBundle.getBundle("resources").getString("port"));
		String host = ResourceBundle.getBundle("resources").getString("host");
		try (Socket clientSocket = new Socket(host, port)) {
			System.out.println("Conectat la server!");
			InetSocketAddress address = (InetSocketAddress) clientSocket.getLocalSocketAddress();
			System.out.println("ID-ul dumneavoastra unic este " + address.getPort());
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

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
                System.out.print("Dictionarul contine urmatoarele chei:");
                for(Integer cheie : listaChei) {
                    System.out.print(" " + cheie);
                }
				System.out.println();
            }else {
                System.out.println("Dictionarul este gol!");
            }

			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					String command = scanner.nextLine();
					if ("exit".equals(command.strip())) {
						break;
					}
					else  {
						writer.println(command.strip());
						writer.flush();
						String response = reader.readLine();
						System.out.println(response);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}

	}

}