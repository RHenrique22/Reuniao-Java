import java.util.*;

public class Participante {
    String nome = "";
    String email = "";
    ArrayList<Reuniao> reunioes = new ArrayList<Reuniao>();

    public Participante (String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Reuniao> getReunioes() {
        return this.reunioes;
    }

    public void inserirReuniao(Reuniao r) {
        this.reunioes.add(r);
    }

    public void removerReuniao(Reuniao r) {
        this.reunioes.remove(r);
    }

    @Override
    public String toString() {
        String output = "";
        output += String.format("%nNome: %s%nE-mail: %s%nReuniões: ", this.getNome(), this.getEmail());

        for (Reuniao r : reunioes) {
            output += String.format("%s ", r.getId());
        }

        return output;
    }

}
