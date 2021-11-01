import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import javax.swing.*;

public class MainGraf {
    private JFrame frame;
    private JMenu mnParticipante;
    private JMenu mnReuniao;
    private JLabel label;
    private Timer timer;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainGraf window = new MainGraf();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */

    public MainGraf() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent arg0) {
                try {
                    Fachada.inicializar();
                } 
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Fachada.finalizar();
                    JOptionPane.showMessageDialog(frame, "até breve !");
                    timer.stop();
                } 
                catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, exc.getMessage());
                }
            }
        });
        frame.setTitle("Agenda de reuni\u00E3o");
        frame.setBounds(100, 100, 450, 363);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        label = new JLabel("");
        label.setFont(new Font("Tahoma", Font.PLAIN, 26));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText("Inicializando...");
        label.setBounds(0, 0, 450, 313);
        ImageIcon imagem = new ImageIcon(getClass().getResource("/arquivos/imagem.png"));
        imagem = new ImageIcon(imagem.getImage().getScaledInstance(label.getWidth(),label.getHeight(), Image.SCALE_DEFAULT));//		label.setIcon(fotos);
        label.setIcon(imagem);
        frame.getContentPane().add(label);
        frame.setResizable(false);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        mnParticipante = new JMenu("Participante");
        mnParticipante.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GrafParticipante tela = new GrafParticipante();
            }
        });
        menuBar.add(mnParticipante);

        mnReuniao = new JMenu("Reuniao");
        mnReuniao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GrafReuniao tela = new GrafReuniao();
            }
        });
        menuBar.add(mnReuniao);

        frame.setVisible(true);

        //----------timer----------------
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));
                frame.setTitle("Agenda de reuniao - "+ dt);
            }
        });
        timer.setRepeats(true);
        timer.setDelay(1000);	//1segundos
        timer.start();			//inicia o temporizador

    }
}
