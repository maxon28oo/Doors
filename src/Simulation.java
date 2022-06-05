import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.*;

class DrawInfo extends JPanel {
    MyThread alwaysSwitching, alwaysStaying, animator;
    int stayingCount = 0, switchingCount = 0;
    int stayingWins = 0, switchingWins = 0;
    boolean playing = false;

    public DrawInfo() {

        alwaysStaying = new MyThread("staying") {
            @Override
            public void run() {
                while (alive) {

                    stayingCount++;
                    int t = new Random().nextInt(3);
                    int chosen = new Random().nextInt(3);
                    if (chosen == t) {
                        stayingWins++;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        alwaysSwitching = new MyThread("switching") {
            @Override
            public void run() {
                while (alive) {

                    switchingCount++;
                    LinkedList<Boolean> doors = new LinkedList<>();
                    int t = new Random().nextInt(3);
                    for (int i = 0; i < 3; i++) {
                        doors.add(i == t);
                    }

                    int chosen = new Random().nextInt(3);
                    doors.remove(chosen);
                    do {
                        t = new Random().nextInt(2);
                    } while (doors.get(t));
                    doors.remove(t);
                    if (doors.get(0)) {
                        switchingWins++;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        animator = new MyThread("animator") {
            @Override
            public void run() {
                while (alive) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    repaint();
                }
            }
        };

        animator.start();
    }

    public void SetCalculating(boolean b) {
        if (b) {
            alwaysStaying.start();
            alwaysSwitching.start();
        } else {
            alwaysStaying.stop();
            alwaysSwitching.stop();
        }
    }

    public void restart() {
        alwaysStaying.stop();
        alwaysSwitching.stop();
        stayingCount = 0;
        switchingCount = 0;
        stayingWins = 0;
        switchingWins = 0;
    }

    public void stop() {
        alwaysStaying.stop();
        alwaysSwitching.stop();
        animator.stop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        double stayingWinRate = stayingCount == 0 ? 0 : (double) stayingWins / stayingCount;
        double switchingWinRate = switchingCount == 0 ? 0 : (double) switchingWins / switchingCount;
        int width = getWidth();
        int height = getHeight();
        int paddx = width / 40;
        int paddy = height / 18;
        int fontSize = Math.max(width, height) / 40;
        g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g2d.drawString("Staying", paddx * 0.1f, paddy * 0.75f);

        g2d.drawRect(paddx, paddy, width - paddx * 2, height / 30);
        g2d.setColor(Color.green);
        g2d.fillRect(paddx + 1, paddy + 1, (int) ((width - paddx * 2) * stayingWinRate), height / 30 - 1);
        int x = paddx + 1 + (int) ((width - paddx * 2) * stayingWinRate);
        int y = paddy + height / 30 + 10;

        g2d.fillPolygon(new int[] { x, x + (int) (paddx / Math.tan(Math.toRadians(60))),
                x - (int) (paddx / Math.tan(Math.toRadians(60))) }, new int[] { y, y + paddx, y + paddx }, 3);
        g2d.drawString(String.format("%.2f", stayingWinRate * 100), x - fontSize, y + paddx + fontSize);

        // switching
        g2d.setColor(Color.black);
        g2d.drawString("Switching", paddx * 0.1f, y + paddx + fontSize * 1.2f + paddy * 0.75f);

        g2d.drawRect(paddx, y + paddx + (int) (fontSize * 1.2f + paddy), width - paddx * 2, height / 30);
        g2d.setColor(Color.green);
        g2d.fillRect(paddx + 1, y + paddx + (int) (fontSize * 1.2f + paddy) + 1,
                (int) ((width - paddx * 2) * switchingWinRate), height / 30 - 1);

        x = paddx + 1 + (int) ((width - paddx * 2) * switchingWinRate);
        y = y + paddx + (int) (fontSize * 1.2f + paddy) + height / 30 + 10;

        g2d.fillPolygon(new int[] { x, x + (int) (paddx / Math.tan(Math.toRadians(60))),
                x - (int) (paddx / Math.tan(Math.toRadians(60))) }, new int[] { y, y + paddx, y + paddx }, 3);

        g2d.drawString(String.format("%.2f", switchingWinRate * 100), x - fontSize, y + paddx + fontSize);
    }

}

public class Simulation extends JPanel implements ActionListener {
    JButton play, restart;
    DrawInfo drawInfo;
    Window window;

    public Simulation(Window window) {
        this.window = window;
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255, 0));
        play = new JButton("Start");
        play.setActionCommand("play");
        play.addActionListener(this);
        panel.add(play, BorderLayout.PAGE_START);
        restart = new JButton("Restart");
        restart.setActionCommand("restart");
        restart.addActionListener(this);
        restart.setVisible(false);
        panel.add(restart, BorderLayout.PAGE_END);
        add(panel, BorderLayout.PAGE_START);
        drawInfo = new DrawInfo();
        add(drawInfo, BorderLayout.CENTER);

        panel = new JPanel(new BorderLayout());
        JButton button = new JButton("Exit");
        button.setActionCommand("exit");
        button.addActionListener(this);
        panel.add(button, BorderLayout.WEST);
        add(panel, BorderLayout.PAGE_END);
        setPreferredSize(new Dimension(400, 400));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("play")) {
            play.setText("Pause");
            play.setActionCommand("pause");
            restart.setVisible(true);
            drawInfo.SetCalculating(true);
        }
        if (e.getActionCommand().equals("pause")) {
            play.setText("Play");
            play.setActionCommand("play");
            drawInfo.SetCalculating(false);
        }
        if (e.getActionCommand().equals("restart")) {
            play.setText("Start");
            play.setActionCommand("play");
            restart.setVisible(false);
            drawInfo.restart();
        }
        if (e.getActionCommand().equals("exit")) {
            drawInfo.stop();
            window.loadMenu();
        }
    }
}
