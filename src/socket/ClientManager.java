package socket;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import model.Continente;
import model.Soldado;
import model.Territorio;
import view.Exercito;

public class ClientManager implements Runnable {

	private static Servidor server;
	private static ServerSocket socket;
	Socket cliente;
	private Thread clientThread;
	
	/*Informações gerais sobre o jogo*/
		private ArrayList<Exercito> lstJogadores = new ArrayList<Exercito>();
		private ArrayList<Territorio> lstTerritorios = new ArrayList<Territorio>();;
		private ArrayList<Soldado> lstSoldados = new ArrayList<Soldado>();;
		private static ArrayList<Continente> lstContinentes = new ArrayList<Continente>();;
	/**/
			

	ClientManager (Socket socket) {
		cliente = socket;
		Color corAmericaNorte = new Color(238, 64, 54);
		Color corAmericaSul = new Color(0, 104, 56);
		Color corAfrica = new Color(101, 45, 144);
		Color corAsia = new Color(246, 146, 30);
		Color corEuropa = new Color(43, 56, 143);
		Color corOceania = new Color(38, 169, 224);
		
		if (lstContinentes.size() == 0){
			lstContinentes.add(new Continente("América do norte", corAmericaNorte, 5));
			lstContinentes.add(new Continente("América do sul", corAmericaSul, 2));
			lstContinentes.add(new Continente("África", corAfrica, 3));
			lstContinentes.add(new Continente("Ásia", corAsia, 7));
			lstContinentes.add(new Continente("Europa", corEuropa, 5));
			lstContinentes.add(new Continente("Oceania", corOceania, 2));
		}
		
		
	}
	
	public void sendMessage(String msg) throws IOException
	{	
			//System.out.println("SENDING: "+msg);
			PrintStream input = new PrintStream(cliente.getOutputStream());
			input.print(msg+"\n");
			//input.print("ping"+"\n");
			input.flush();
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String entrada = "open";
		Scanner in=null;
		PrintStream out=null;
				try {
					in = new Scanner(cliente.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					out = new PrintStream (cliente.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		for(String name : Servidor.currentPlayers){
			try {
				sendMessage("Ready-"+name);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while (true)
		{
			System.out.println(entrada);
			if (entrada.contains("Select"))
			{
				try {
					//sendMessage("Ready-OK");
					String partes[] = entrada.split("-");
					Servidor.getInstance().enterGame(partes[1]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(entrada.contains("JogadorDaVez"))
			{
				System.out.println("-----RECEBI JOGADOR DA VEZ------\n"+entrada);
				String partes[] = entrada.split("-");
				
				try {
					for (ClientManager e: Servidor.getInstance().clients)
					{
						if (e!=this)
							e.sendMessage(entrada);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			if(entrada.contains("AtualizaSequencia"))
			{
				try {
					for (ClientManager e: Servidor.getInstance().clients)
					{
						if (e!=this)
							e.sendMessage(entrada);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(entrada.equals("OK"))
			{
				try {
					Servidor.getInstance().OKUp();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (entrada.contains("Ready"))
			{
				
				try {
					//sendMessage("Ready-OK");
					String partes[] = entrada.split("-");
					Servidor.getInstance().GameReady(partes[1]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			if (entrada.equals("FimDaJogada"))
			{

				for (Continente c: lstContinentes)
				{
					entrada = in.nextLine();
					int numTerritorios = Integer.parseInt(entrada);
					
					for (int i=0;i<numTerritorios;i++) //pra cada territorio
					{
						entrada = in.nextLine();
						String nome = entrada;
						
						entrada = in.nextLine();
						int NumSoldados = Integer.parseInt(entrada);
						
						entrada = in.nextLine();
						String dono = entrada;
						
						c.addTerritorio(new Territorio(nome, NumSoldados, dono));
						
					}

					
				}

				
				try {
				
					Servidor.getInstance().updateClients();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				
			}
			else
			{
				//System.out.println(entrada);
			}
				
			entrada = in.nextLine();
		}
		
	}

	public void criaThread() {
		// TODO Auto-generated method stub
		clientThread = new Thread(this);
		clientThread.start();
	}

	public void sendUpdateMessage() throws IOException
	{
		sendMessage("NewUpdate");
		for (Continente c: lstContinentes)
		{
			ArrayList<Territorio> array = c.getLstTerritorios();
			sendMessage(Integer.toString(array.size()));
			//sendMessage(Integer.toString(4));
			for (Territorio t: array)
			{


				sendMessage(t.getNome());
				sendMessage(Integer.toString(t.getNumSoldados()));
				sendMessage(t.getDono());
			}
			
			
		}
		
		
	}
	
	public Thread getThread()
	{
		return clientThread;
	}
	

}
