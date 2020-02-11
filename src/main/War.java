package main;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import socket.*;
import view.Configuracao;


public class War {
	public static Client client;
	public static Servidor servidor;
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		
		Object[] possibilities = {"Client","Servidor"};
		Object ret = JOptionPane.showInputDialog(null,"Tipo de Instancia","Jogadores",JOptionPane.PLAIN_MESSAGE,null,possibilities,possibilities[0]);
		String i="asd";
		if (ret != null)
			 i = (String)ret;
	
		
		if (i=="Client")
		{
			client = Client.getInstance();
			client.Connect();
			client.criaThread();
			Configuracao.getInstance();
			
		}
		
		if (i=="Servidor")
		{
			servidor = Servidor.getInstance();
			servidor.OpenConnection();
			//servidor.criaThread();
		}
			
		
		
		
	}

}
