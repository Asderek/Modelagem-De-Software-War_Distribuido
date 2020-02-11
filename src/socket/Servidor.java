package socket;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import model.Continente;
import model.Soldado;
import model.Territorio;
import view.Exercito;

public class Servidor {

		private static Servidor server;
		private static ServerSocket socket;
		Socket cliente;
		
		/*Informações gerais sobre o jogo*/
			private ArrayList<Exercito> lstJogadores = new ArrayList<Exercito>();
			private ArrayList<Territorio> lstTerritorios = new ArrayList<Territorio>();;
			private ArrayList<Soldado> lstSoldados = new ArrayList<Soldado>();;
			private static ArrayList<Continente> lstContinentes = new ArrayList<Continente>();;
		/**/
		
		public ArrayList<ClientManager> clients = new ArrayList<ClientManager>();
		private boolean gameStarted = false;
		public static ArrayList<String> currentPlayers = new ArrayList<String>();
		public static ArrayList<String> playersReady = new ArrayList<String>();
		
		private String JogadorDaVez="No One";
		private int OK=0;
	
		public static Servidor getInstance() throws IOException {
			
			Color corAmericaNorte = new Color(238, 64, 54);
			Color corAmericaSul = new Color(0, 104, 56);
			Color corAfrica = new Color(101, 45, 144);
			Color corAsia = new Color(246, 146, 30);
			Color corEuropa = new Color(43, 56, 143);
			Color corOceania = new Color(38, 169, 224);
			
			lstContinentes.add(new Continente("América do norte", corAmericaNorte, 5));
			lstContinentes.add(new Continente("América do sul", corAmericaSul, 2));
			lstContinentes.add(new Continente("África", corAfrica, 3));
			lstContinentes.add(new Continente("Ásia", corAsia, 7));
			lstContinentes.add(new Continente("Europa", corEuropa, 5));
			lstContinentes.add(new Continente("Oceania", corOceania, 2));
			
			if (server == null)
				server =  new Servidor();
			
			return server;
		}
		
		public void OpenConnection() throws IOException
		{
			int porta = 7777;
			socket = new ServerSocket(porta);
			System.out.println("Porta = "+porta);
			
			try {
				//System.out.println(Thread.currentThread().getId());

				while(!gameStarted){
					cliente = socket.accept();
					System.out.println("Conectado ao cliente: "+cliente.getInetAddress().getHostAddress());
					ClientManager manager = new ClientManager(cliente);
					clients.add(manager);
	
					manager.criaThread();
				}
			}
			catch (IOException e)
			{
				clients.get(0).sendMessage("GameStarting");

				
				try {
					clients.get(0).getThread().join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			
			
		}
		
		public void enterGame(String nome){			

			if (currentPlayers.size() == 0)
				try {
					clients.get(0).sendMessage("First");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			currentPlayers.add(nome);
			
			for (ClientManager man : clients){
				try {
					man.sendMessage("Ready-"+nome);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		public void GameReady(String nome){
			if (!playersReady.contains(nome))
				playersReady.add(nome);
			if(currentPlayers.size()>2)
			{
				if (playersReady.size() == currentPlayers.size())
				{
					gameStarted = true;
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
		public void updateClients()
		{
			for (ClientManager a: clients)
			{
				try {
					a.sendUpdateMessage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void setJogadorDaVez(String nome)
		{
			JogadorDaVez = nome;
			
			for (ClientManager a: clients)
			{
				try {
					a.sendMessage("JogadorDaVez -"+ JogadorDaVez);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	
		public void OKUp()
		{
			OK++;
			if (OK>=currentPlayers.size()-1)
			{
				try {
					clients.get(0).sendMessage("OK");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

}
