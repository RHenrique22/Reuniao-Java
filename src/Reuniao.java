import java.time.*;
import java.time.format.*;
import java.util.*;

public class Reuniao {
    int id;
    LocalDateTime dataHora;
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String assunto = "";
    ArrayList<Participante> participantes = new ArrayList<Participante>();

    public Reuniao (int Id, String dataHora, String assunto) {
        this.id = Id;
        this.dataHora = LocalDateTime.parse(dataHora, formato);
        this.assunto = assunto;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDatahora() {
        return this.dataHora;
    }

    public void setDatahora(LocalDateTime datahora) {
        this.dataHora = datahora;
    }

    public String getAssunto() {
        return this.assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public ArrayList<Participante> getParticipantes() {
        return this.participantes;
    }

    public void inserirParticipante(Participante p) {
        this.participantes.add(p);
    }

    public void removerParticipante(Participante p) {
        this.participantes.remove(p);
    }

    @Override
    public String toString() {
        String output = "";
        output += String.format("%nId: %d%nData e hora: %s%nAssunto: %s%nParticipantes: ",
            this.getId(), this.getDatahora().format(formato), this.getAssunto());

        for (Participante p : participantes) {
            output += String.format("%s ", p.getNome());
        }

        return output;
    }

}
