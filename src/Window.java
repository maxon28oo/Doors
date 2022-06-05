import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Window extends JFrame {
    JButton startGame,startSimulation;
    Game game;
    Simulation simulation;
    public Window() {
        startGame = new JButton("Start Game");
        startGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame.setVisible(false);
                remove(startGame);
                startSimulation.setVisible(false);
                remove(startSimulation);
                setLayout(new GridLayout(1,1));
                game = new Game(Window.this);
                add(game);
                pack();
            }
        });
        startSimulation = new JButton("Simulation");
        startSimulation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startSimulation.setVisible(false);
                remove(startSimulation);
                startGame.setVisible(false);
                remove(startGame);
                setLayout(new GridLayout(1,1));
                simulation = new Simulation(Window.this);
                add(simulation);
                pack();
            }
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        loadMenu();
    }

    public void loadMenu() {
        setTitle("Monty Hall problem");
        if (game != null) {
            remove(game);
        }
        if (simulation != null) {
            remove(simulation);
        }
        setSize(500,500);
        setLayout(new FlowLayout());
        startGame.setVisible(true);
        add(startGame);
        startSimulation.setVisible(true);
        add(startSimulation);
        repaint();
    }
}
    
