import java.util.*;

public class Repositorio {
    private TreeMap<String,Participante> participantes = new TreeMap<>();
	private ArrayList<Reuniao> reunioes = new ArrayList<>(); 

	public void adicionar(Participante p) {
        this.participantes.put(p.getNome().toLowerCase(), p);
	}

	public void remover(Participante p) {
        this.participantes.remove(p.getNome().toLowerCase());
	}

	public Participante localizarParticipante(String nome) {
        return this.participantes.get(nome.toLowerCase());
	}

	public void adicionar(Reuniao r) {
        this.reunioes.add(r);
	}

	public void remover(Reuniao r) {
        this.reunioes.remove(r);
	}

	public Reuniao localizarReuniao(int id) {
        for (Reuniao reuniao : reunioes) {
            if (reuniao.getId() == id) {
                return reuniao;
            }
        }
        
        return null;
	}
	
	public ArrayList<Participante> getParticipantes() {
        return new ArrayList<>(this.participantes.values());
	}

	public ArrayList<Reuniao> getReunioes() {
        return this.reunioes;
	}

	public int getTotalParticipante() {
        return this.participantes.size();
	}

	public int getTotalReunioes() {
        return this.reunioes.size();
	}
}
