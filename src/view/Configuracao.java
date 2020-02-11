package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import controller.ControllerTabuleiro;
import main.War;
import socket.Client;
import socket.ClientManager;
import socket.Servidor;

@SuppressWarnings("serial")
public class Configuracao extends JFrame {
	private static Configuracao configuracao;
	private static ArrayList<view.Exercito> lstExercitos = new ArrayList<view.Exercito>();
	private ArrayList<view.Exercito> lstJogadores = new ArrayList<Exercito>();
	private static int set = 0;
	public static boolean available = false;
	private static boolean receiveStart = false;
	public CountDownLatch latch;
	private static int loop = 0;

	// Bloco de inicialização dos exercitos
	{
		lstExercitos.add(new view.Exercito("Laranja", new Color(209, 84, 000)));
		lstExercitos.add(new view.Exercito("Vermelho", new Color(255, 003, 003)));
		lstExercitos.add(new view.Exercito("Azul", new Color(030, 067, 186)));
		lstExercitos.add(new view.Exercito("Verde", new Color(006, 124, 000)));
		lstExercitos.add(new view.Exercito("Amarelo", new Color(255, 188, 000)));
		lstExercitos.add(new view.Exercito("Preto", new Color(000, 000, 000)));
	}

	private Configuracao() throws IOException {
		System.out.println(Thread.currentThread().getId());
		exibeExercitos();
		carregabotaoInicio();
		carregaLabel();
		carregaBgJanela();
		configFrame();
		
	}

	private void carregaLabel() {
		JTextArea lblTxtInicio = new JTextArea();
		lblTxtInicio.setBounds(20, 200, 350, 90);
		lblTxtInicio.setFont(new Font("Stencil", Font.PLAIN, 18));
		lblTxtInicio.setForeground(Color.white);
		lblTxtInicio.setText("Selecione pelo menos 3 exércitos \ne clique em iniciar jogo");
		lblTxtInicio.setOpaque(false);

		add(lblTxtInicio);

		if (War.servidor != null) {
			for (final view.Exercito ex : getLstexercitos()) {
				try {
					for (String pl : Servidor.getInstance().currentPlayers) {
						if (ex.getNome().equals(pl)) {
							ex.setSelecionado(true);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				if (validaJogadores()) {
					setVisible(false);
				}
				for (ClientManager man : Servidor.getInstance().clients) {
					try {
						man.sendUpdateMessage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static ArrayList<view.Exercito> getLstexercitos() {
		return lstExercitos;
	}

	public boolean validaJogadores() throws IOException {

		System.out.println("vez = " + loop);
		loop++;
		if (Client.getInstance().first) {

			System.out.println("First Valida Jogadores");
			lstJogadores.clear();

			for (Exercito e : getLstexercitos()) {
				if (e.isSelecionado()) {
					lstJogadores.add(e);
				}
			}

			// Embaralhando a lista de jogadores
			ControllerTabuleiro.embaralhaLista(lstJogadores);

			for (Exercito e : lstJogadores) {
				ControllerTabuleiro.setJogador(e.getNome(), e.getCor());
			}

			// Adiciona os jogadores no pnlJogadores no tabuleiro
			PnlJogadores.setJogadores(lstJogadores);
		} else {
			System.out.println("First Valida Jogadores");
			lstJogadores.clear();

			for (Exercito e : Client.getInstance().lstJogadores) {
					lstJogadores.add(e);
			}

			// Embaralhando a lista de jogadores
			// ControllerTabuleiro.embaralhaLista(lstJogadores);

			for (Exercito e : lstJogadores) {
				ControllerTabuleiro.setJogador(e.getNome(), e.getCor());
			}

			// Adiciona os jogadores no pnlJogadores no tabuleiro
			PnlJogadores.setJogadores(lstJogadores);

		}
		// Abre o tabuleiro
		Tabuleiro.getInstance();

		ControllerTabuleiro.getInstance().preparaTabuleiro();
		return true;

	}

	private void carregabotaoInicio() {
		final JButton btnInicializar = new JButton();
		btnInicializar.setText("Escolher Exercito");
		btnInicializar.setFont(new Font("Stencil", Font.PLAIN, 18));
		btnInicializar.setBounds(95, 470, 200, 60);
		btnInicializar.setBackground(new Color(48, 32, 19));
		btnInicializar.setForeground(Color.white);
		btnInicializar.setFocusPainted(false);
		btnInicializar.setBorderPainted(false);
		add(btnInicializar);

		btnInicializar.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			/* asd */
			@Override
			public void mouseClicked(MouseEvent e) {

				try {

					if (Client.getInstance().getColor() == null) {

						for (final view.Exercito ex : getLstexercitos()) {
							if (ex.isSelecionado()) {
								if (!Client.getInstance().players.contains(ex.nome)) {
									Client.getInstance().sendMessage("Select-" + ex.nome);
									Client.getInstance().setColor(ex.nome);
									System.out.println("-------- MY COLOR = "+ex.nome+" --------");
									btnInicializar.setText("Ready");
									return;
								}
							}
						}

					} else {
						Client.getInstance().sendMessage("Ready-" + Client.getInstance().getColor());

						System.out.println("Waiting...");
						// synchronized(Client.getInstance().mainThread);

						latch = new CountDownLatch(1);
						try {
							latch.await();

							System.out.println("Antes do Valida");
							if (validaJogadores()) {
								// Esconde a janela de configuração do jogo.
								setVisible(false);
							}
							System.out.println("Depois do Valida");

							if (Client.getInstance().first) {
								System.out.println("----- EU SOU O FIRST ------");
								String msg = "AtualizaSequencia";
								for (model.Exercito ex : ControllerTabuleiro.getInstance().getLstJogadores()) {
									msg += "-" + ex.getNome();
								}
								Client.getInstance().sendMessage(msg);
							}
							else
							{
								Client.getInstance().sendMessage("OK");
							}

						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			/* asd */
		});
	}

	private void carregaBgJanela() {
		// TODO Auto-generated method stub
		String Imagem = "src/resources/Images/bgConfiguracao.png";
		JLabel bgJanela = new JLabel();
		ImageIcon imgJanela = new ImageIcon(Imagem);

		imgJanela = new ImageIcon(imgJanela.getImage().getScaledInstance(imgJanela.getIconWidth(),
				imgJanela.getIconHeight(), BufferedImage.SCALE_SMOOTH));
		bgJanela.setIcon(imgJanela); // Imagem de fundo da janela

		bgJanela.setSize(imgJanela.getIconWidth(), imgJanela.getIconHeight());

		getContentPane().add(bgJanela);

		repaint();
	}

	public static Configuracao getInstance() throws IOException {

		if ((configuracao == null) && (set == 0)) {
			set = 1;
			configuracao = new Configuracao();
		}
		return configuracao;
	}

	private void configFrame() {

		int LARG_DEFAULT = 400;
		int ALT_DEFAULT = 680;

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();

		double sl = screenSize.getWidth(); // Largura da tela
		double sa = screenSize.getHeight(); // Altura da tela

		int x = (int) sl / 2 - LARG_DEFAULT / 2; // posicionamento centralizado
													// em x da tela
		int y = (int) sa / 2 - ALT_DEFAULT / 2; // posicionamentro centralizado
												// em y da tela

		setLayout(null);
		setBounds(x, y, LARG_DEFAULT, ALT_DEFAULT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setResizable(false);
		setVisible(true);
	}

	public void exibeExercitos() {

		JPanel pnlExercitos = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnlExercitos.setOpaque(false);
		pnlExercitos.setSize(360, 90);
		pnlExercitos.setLocation(20, 113);

		for (final view.Exercito ex : getLstexercitos()) {

			ex.setLocation(0, 0);
			ex.setPreferredSize(new Dimension(60, 90));
			ex.setOpaque(false);
			ex.setToolTipText("Exercito " + ex.getNome());
			ex.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseClicked(MouseEvent e) {

					try {
						if (Client.getInstance().getColor() == null) {
							for (String player : Client.getInstance().players) {
								if (player.equals(ex.getNome())) {
									return;
								}
							}

							ex.setSelecionado();
							repaint();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			});

			pnlExercitos.add(ex);
		}

		add(pnlExercitos);

	}

}
