import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;

//Mail puxado do Referenced Libraries
import javax.mail.*;
import javax.mail.internet.*;

public class Fachada {
	private static Repositorio repositorio = new Repositorio();
	private static int Id;
	private static DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	public static ArrayList<Participante> listarParticipantes() {
		return repositorio.getParticipantes();
	}

	public static ArrayList<Reuniao> listarReunioes() {
		return repositorio.getReunioes();
	}

	public static Participante criarParticipante(String nome, String email) throws Exception {
		nome = nome.trim();
		email = email.trim();

		Participante pTeste = repositorio.localizarParticipante(nome);

		if (pTeste == null) {
			Participante p = new Participante(nome, email);
			repositorio.adicionar(p);

			return p;

		} else {
			throw new Exception("Participante já cadastrado");
		}
	}

	public static Reuniao criarReuniao(String datahora, String assunto, ArrayList<String> nomes) throws Exception {
		assunto = assunto.trim();
		String exception = "";
		boolean teste = false;
		Id++;
		int notExist = 0;

		for (String nome : nomes) {
			Participante p = repositorio.localizarParticipante(nome);

			if (p == null) {
				notExist++;
			}
		}

		if (nomes.size() - notExist < 2) {
			throw new Exception("A reunião não pôde ser criada, número de participantes insuficiente");
		} else {
			Reuniao r = new Reuniao(Id, datahora, assunto);

			for (String nome : nomes) {
				Participante p = repositorio.localizarParticipante(nome);

				if (p == null) {
					;
				} else {
					if (p.getReunioes().size() == 0) {
						p.inserirReuniao(r);
						r.inserirParticipante(p);
					} else {
						for (Reuniao reuniao : p.getReunioes()) {
							Duration duracao;
							duracao = Duration.between(reuniao.getDatahora(), r.getDatahora());
							long horas;
							horas = duracao.toHours();

							if (Math.abs(horas) < 2) {
								teste = false;
								break;
							} else {
								teste = true;
							}
						}

						if (teste) {
							p.inserirReuniao(r);
							r.inserirParticipante(p);
						} else {
							exception += (String.format(
							"Houve choque de horário, o participante %s não poderá participar da reunião de id %d\n"
							, p.getNome(), r.getId()));
						}
					}
				}
			}

			if (r.getParticipantes().size() < 2) {
				for (Participante participante : r.getParticipantes()) {
					participante.removerReuniao(r);
				} 
				throw new Exception(exception + "A reunião não pode ser criada, número de participantes insuficiente");
			} else {
				repositorio.adicionar(r);
			}

			for (Participante participante : r.getParticipantes()) {
				enviarEmail(participante.getEmail()/*fausto.ayres@gmail.com*/, "Adicionado a reunião",
				String.format("Olá %s, você foi adicionado a reunião de ID: %d, que acontecerá em %sh com o assunto %s"
				, participante.getNome(), r.getId(), r.getDatahora().format(formato),
				r.getAssunto()));
			}

			return r;
		}
	}

	public static void adicionarParticipanteReuniao(String nome, int id) throws Exception {
		nome = nome.trim();
		boolean testeP = true;
		Participante p = repositorio.localizarParticipante(nome);

		if (p == null) {
			throw new Exception("Participante não encontrado");
		}

		Reuniao r = repositorio.localizarReuniao(id);

		if (r == null) {
			throw new Exception("Reunião não encontrada");
		}

		for (Reuniao reuniao : p.getReunioes()) {
			if (r.getId() == reuniao.getId()) {
				testeP = false;
				break;
			}
		}

		if (testeP) {
			p.inserirReuniao(r);
			r.inserirParticipante(p);
		} else {
			throw new Exception(String.format("Participante %s já está cadastrado na reunião", p.getNome()));
		}

		enviarEmail(p.getEmail()/*fausto.ayres@gmail.com*/, "Adicionado a reunião"
		, String.format("Olá %s, você foi adicionado a reunião de ID: %d, que acontecerá em %sh com o assunto %s"
		, p.getNome(), r.getId(), r.getDatahora().format(formato), r.getAssunto()));
	}

	public static void removerParticipanteReuniao(String nome, int id) throws Exception {
		nome = nome.trim();
		Participante p = repositorio.localizarParticipante(nome);
		boolean testeP = false;
		boolean testeR = false;

		if (p == null) {
			throw new Exception("Participante não encontrado");
		}

		Reuniao r = repositorio.localizarReuniao(id);

		if (r == null) {
			throw new Exception("Reunião não encontrada");
		}

		for (Participante participante : r.getParticipantes()) {
			if (participante.getNome().equalsIgnoreCase(p.getNome())) {
				r.removerParticipante(participante);
				testeP = true;
				break;
			}
		}

		for (Reuniao reuniao : p.getReunioes()) {
			if (reuniao.getId() == r.getId()) {
				p.removerReuniao(reuniao);
				testeR = true;
				break;
			}
		}

		if (!testeP) {
			throw new Exception("Não foi possível remover o participante da reunião, pois ele não se encontrava na reunião");
		}

		if (!testeR) {
			throw new Exception("Não foi possível remover reunião do participante, pois ele não se encontrava na reunião");
		}

		enviarEmail(p.getEmail(), "Remoção do participante na reunião"
		, String.format("O proprietário da reunião lhe removeu da reunião de ID: %d que occorreria em %sh, para entender o motivo entre em contato com o proprietário"
		, r.getId(), r.getDatahora().format(formato)));

		if (r.getParticipantes().size() < 2) {
			cancelarReuniao(r.getId());
		}
	}

	public static void cancelarReuniao(int id) throws Exception {
		Reuniao r = repositorio.localizarReuniao(id);

		if (r == null) {
			throw new Exception("Reunião não encontrada");
		}

		for (Participante participante : r.getParticipantes()) {
			enviarEmail(participante.getEmail()/*fausto.ayres@gmail.com*/, "Cancelamento de reunião",
			String.format("A reunião de ID: %d que occorreria em %sh não irá ocorrer por motivos desconhecidos, por favor entre em contato com o proprietário da reunião"
			, r.getId(), r.getDatahora().format(formato)));
			participante.removerReuniao(r);
		}

		repositorio.remover(r);
	}

	public static void inicializar() throws Exception {
		Scanner arquivo1 = null;
		Scanner arquivo2 = null;
		
		try {
			arquivo1 = new Scanner(new File("participantes.txt"));

			String linha;
			String[] partes;
			String nome, email;
			while (arquivo1.hasNextLine()) {
				linha = arquivo1.nextLine().trim();
				partes = linha.split(";");
				nome = partes[0];
				email = partes[1];
				Fachada.criarParticipante(nome, email);
			}

			arquivo1.close();

		} catch (FileNotFoundException e) {
			throw new Exception("Arquivo de participantes inexistente");
		}
		try {
			arquivo2 = new Scanner(new File("reunioes.txt"));

			String linha;
			String[] partes;
			String[] nomes;
			String datahora, assunto;
			while (arquivo2.hasNextLine()) {
				linha = arquivo2.nextLine().trim();
				partes = linha.split(";");
				datahora = partes[1];
				assunto = partes[2];
				nomes = partes[3].split(",");
				ArrayList<String> listaNomes = new ArrayList<String>(Arrays.asList(nomes));
				Fachada.criarReuniao(datahora, assunto, listaNomes);
			}

			arquivo2.close();

		} catch (FileNotFoundException e) {
			throw new Exception("Arquivo de reuniões inexistente");
		}
	}

	public static void finalizar() throws Exception {
		FileWriter arquivo1 = null;
		FileWriter arquivo2 = null;

		try {
			arquivo1 = new FileWriter(new File("participantes.txt"));

			for (Participante p : repositorio.getParticipantes()) {
				arquivo1.write(p.getNome() + ";" + p.getEmail() + "\n");
			}

			arquivo1.close();
		} catch (IOException e) {
			throw new Exception("Problema na criação do arquivo de participantes");
		}

		try {
			arquivo2 = new FileWriter(new File("reunioes.txt"));

			ArrayList<String> lista;
			String nomes;
			for (Reuniao r : repositorio.getReunioes()) {
				lista = new ArrayList<>();
				for (Participante p : r.getParticipantes()) {
					lista.add(p.getNome());
				}
				nomes = String.join(",", lista);
				arquivo2.write(
						r.getId() + ";" + r.getDatahora().format(formato) + ";" + r.getAssunto() + ";" + nomes + "\n");
			}

			arquivo2.close();
		} catch (IOException e) {
			throw new Exception("Problema na criação do arquivo de reunioes");
		}
	}

	/**************************************************************
	 * 
	 * MÉTODO PARA ENVIAR EMAIL, USANDO UMA CONTA (SMTP) DO GMAIL ELE ABRE UMA
	 * JANELA PARA PEDIR A SENHA DO EMAIL DO EMITENTE ELE USA A BIBLIOTECA JAVAMAIL
	 * 1.6.2 Lembrar de: 1. desligar antivirus e de 2. ativar opcao "Acesso a App
	 * menos seguro" na conta do gmail
	 * 
	 **************************************************************/

	public static void enviarEmail(String emaildestino, String assunto, String mensagem) {
		try {
			//configurar emails
			String emailorigem = "enviadorafakao@gmail.com";
			String senhaorigem = "ifpb1234"; //pegarSenha();

			//Gmail
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");

			Session session;
			session = Session.getInstance(props,
			new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(emailorigem, senhaorigem);
			}
			});

			MimeMessage message = new MimeMessage(session);
			message.setSubject(assunto);
			message.setFrom(new InternetAddress(emailorigem));
			message.setRecipients(Message.RecipientType.TO,
			InternetAddress.parse(emaildestino));
			message.setText(mensagem); // usar "\n" para quebrar linhas
			Transport.send(message);

			//System.out.println("enviado com sucesso");

		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * JANELA PARA DIGITAR A SENHA DO EMAIL
	 */

	public static String pegarSenha() {
		JPasswordField field = new JPasswordField(10);
		field.setEchoChar('*');
		JPanel painel = new JPanel();
		painel.add(new JLabel("Entre com a senha do seu email:"));
		painel.add(field);
		JOptionPane.showMessageDialog(null, painel, "Senha", JOptionPane.PLAIN_MESSAGE);
		String texto = new String(field.getPassword());
		return texto.trim();
	}

}