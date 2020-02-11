package socket;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import controller.ControllerTabuleiro;
import model.Continente;
import model.Territorio;
import view.Configuracao;
import view.Exercito;
import view.Tabuleiro;

public class Client extends Socket implements Runnable{
	
	static String msg = "asd";
	private static Client client;
	private Socket server = null;
	PrintStream input;
	private String color = null;
	private Thread readThread;
	public Thread mainThread;
	public boolean first = false;
	private static ArrayList<Continente> lstContinentes = new ArrayList<Continente>();
	public ArrayList<view.Exercito> lstJogadores = new ArrayList<Exercito>();
	boolean Updated = false;
	
	public static ArrayList<String> players = new ArrayList<String>(); 
	
	public static Client getInstance() throws UnknownHostException, IOException {
		Color corAmericaNorte = new Color(238, 64, 54);
		Color corAmericaSul = new Color(0, 104, 56);
		Color corAfrica = new Color(101, 45, 144);
		Color corAsia = new Color(246, 146, 30);
		Color corEuropa = new Color(43, 56, 143);
		Color corOceania = new Color(38, 169, 224);
		
		if (lstContinentes.size() ==0){
			lstContinentes.add(new Continente("América do norte", corAmericaNorte, 5));
			lstContinentes.add(new Continente("América do sul", corAmericaSul, 2));
			lstContinentes.add(new Continente("África", corAfrica, 3));
			lstContinentes.add(new Continente("Ásia", corAsia, 7));
			lstContinentes.add(new Continente("Europa", corEuropa, 5));
			lstContinentes.add(new Continente("Oceania", corOceania, 2));
		}
		
		if (client == null)
		{
			client = new Client();
		
		}
		
			return client;		
	}
	
	public void Connect()
	{
		try
		{
			server = new Socket("127.0.0.1",7777);
		}
		catch (Exception e)
		{
			System.out.print("Nao ");
		}
		System.out.println("Conectado");
	}
	
	public void sendMessage(String msg) throws IOException
	{	
		
		//System.out.println("SENDING: "+msg);
		input = new PrintStream(server.getOutputStream());
		input.print(msg+"\n");
		input.flush();
	
		
	}

	public void getLstJogadores() throws IOException {
		sendMessage("GetLstJogadores");
	}
	

	public void inicializaJogo() throws IOException {
		// TODO Auto-generated method stub
		sendMessage("InicializaJogo");
	}

	@Override
	public void run() {
		String entrada = "open";
		Scanner in=null;
		try {
			in = new Scanner(server.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		while (true)
		{
			
			System.out.println(entrada);	
			if(entrada.contains("AtualizaSequencia"))
			{
				System.out.println("Sequencia = "+entrada);
					String[] bla = entrada.split("-");
					//ControllerTabuleiro.getInstance().setLstJogadores(bla);
					criaLstJogadores(bla);
					try {
						Configuracao.getInstance().latch.countDown();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
			
			if (entrada.equals("OK"))
			{
				try {
					//sincroniza tabuleiro com o first
					sendMessage("JogadorDaVez-"+ControllerTabuleiro.getInstance().jogadorDaVez.getNome());
					ControllerTabuleiro.getInstance().FimDaJogada();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (entrada.contains("Ready")){
				String bla = entrada.split("-")[1];
				players.add(bla);
				
				try {
					for (final view.Exercito ex : Configuracao.getInstance().getLstexercitos()){
						
						if ( ex.getNome().equals(bla)){
							ex.setSelecionado(true);
						}
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if (entrada.contains("GameStarting")){
				//mainThread.notify();
				try {
					Configuracao.getInstance().latch.countDown();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (entrada.equals("First"))
			{
				first = true;
			}
			
			if (entrada.contains("JogadorDaVez"))
			{
				String bla = entrada.split("-")[1];
				
				//ControllerTabuleiro.getInstance().setJogadorDaVez(bla);
				if (Updated){
					try {
						System.out.println("Chamando Butao dos Outros Jogadores");
						ControllerTabuleiro.getInstance().btnProxJogada_click();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
			if (entrada.equals("NewUpdate"))
			{
				Updated = true;
				ControllerTabuleiro controller = ControllerTabuleiro.getInstance();				
				for (Continente c: lstContinentes)
				{

					int numTerritorios=-1;
					entrada = in.nextLine();
					numTerritorios = Integer.parseInt(entrada);

				
					if (numTerritorios>0)
					{
						for (int i=0;i<numTerritorios;i++) //pra cada territorio
						{
							entrada = in.nextLine();
							String nome = entrada;

							
							
							entrada = in.nextLine();
							int numSoldados=-1;
							numSoldados = Integer.parseInt(entrada);

							
							entrada = in.nextLine();
							String dono = entrada;

							
							if(numSoldados>0)
							{

								c.addTerritorio(new Territorio(nome, numSoldados, dono));
							}
							else
								System.out.println("Soldados Negativos");
							
						}
					}
					else
						System.out.println("Territorios Negativos");
					
				}
				
				controller.UpdateTabuleiro(lstContinentes);
			}
			entrada = in.nextLine();
			
		}
		
		
	}
	
	public void criaThread() {
		// TODO Auto-generated method stub
		mainThread = Thread.currentThread();
		readThread = (new Thread(this));
		readThread.start();
	}

	public void setColor(String nome) {
		this.color  = nome;
		
	}

	public String getColor() {
		// TODO Auto-generated method stub
		return color;
	}
	
	public void criaLstJogadores(String[] jogadores)
	{
		System.out.println("Antes - lstJogadores.size = "+lstJogadores.size());
		lstJogadores.clear();
		System.out.println("Clear - lstJogadores.size = "+lstJogadores.size());
		for(int i=1;i<jogadores.length;i++)
		{
			//lstJogadores.add(new Exercito("Laranja", 	new Color(209, 84, 000)));
			
			switch (jogadores[i].toLowerCase())
			{
			case "laranja":
				lstJogadores.add(new Exercito("Laranja", 	new Color(209, 84, 000)));	
				break;
			case "vermelho":
				lstJogadores.add(new Exercito("Vermelho",	new Color(255, 003, 003)));
				break;
			case "azul":
				lstJogadores.add(new Exercito("Azul", 		new Color(030, 067, 186)));
				break;
			case "verde":
				lstJogadores.add(new Exercito("Verde", 	new Color(006, 124, 000)));
				break;
			case "amarelo":
				lstJogadores.add(new Exercito("Amarelo", 	new Color(255, 188, 000)));
				break;
			case "preto":
				lstJogadores.add(new Exercito("Preto", 	new Color(000, 000, 000)));
				break;
			default:
				break;
			}
			
		}
	}

}
