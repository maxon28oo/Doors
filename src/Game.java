import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

class Door {
    Point p1, p2;
    float angle = 0;
    boolean open = false, openning = false;
    boolean prize = false;
    boolean chosen = false;

    Door(boolean prize) {
        this.prize = prize;
    }

}

public class Game extends JPanel implements MouseListener, ActionListener {

    Door[] doors = new Door[3];
    MyThread animator, holding;
    JLabel narrator;
    int state;
    int Timer = 0;
    boolean paused = false;
    JPanel OptionMenu;
    Window window;

    public Game(Window game) {
        this.window = game;
        window.setTitle("Game");
        setLayout(new BorderLayout());
        setBackground(Color.lightGray);
        setPreferredSize(new Dimension(400, 400));
        addMouseListener(this);

        narrator = new JLabel();
        narrator.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(narrator, BorderLayout.CENTER);
        panel.setBackground(new Color(255, 255, 255, 0));
        add(panel, BorderLayout.PAGE_START);
        panel = new JPanel(new BorderLayout());
        JButton options = new JButton();
        panel.setBackground(new Color(255, 255, 255, 0));
        options.setActionCommand("options");
        options.addActionListener(this);
        panel.add(options, BorderLayout.WEST);
        panel.setName("options");
        add(panel, BorderLayout.PAGE_END);

        OptionMenu = new JPanel(new GridLayout(3, 1));
        OptionMenu.setBackground(new Color(255, 255, 255, 100));
        JButton b = new JButton("Restart");
        b.setActionCommand("restart");
        b.addActionListener(this);
        JButton b2 = new JButton("Main menu");
        b2.setActionCommand("main menu");
        b2.addActionListener(this);

        OptionMenu.add(b);
        OptionMenu.add(b2);
        OptionMenu.setVisible(false);
        add(OptionMenu);

        animator = new MyThread("animator") {
            @Override
            public void run() {
                while (alive) {
                    repaint();
                    if (state == 2) {
                        state = 3;
                        int t, t1 = 0;
                        do {
                            t = new Random().nextInt(3);
                        } while (doors[t].prize || doors[t].chosen);
                        for (int i = 0; i < doors.length; i++) {
                            if (doors[i].chosen) {
                                t1 = i;
                                break;
                            }
                        }
                        doors[t].openning = true;
                        doors[t].open = true;
                        narrator.setText("<html>Because the door " + (t + 1)
                                + " has no prize!<br>Do you still want to open " + (t1 + 1) + " door?</html>");

                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        holding = new MyThread("timer") {
            @Override
            public void run() {
                while (Timer > 0) {
                    if (!paused)
                        Timer--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                state++;
            }

        };
        initGame();
    }

    void initGame() {
        for (Component c : getComponents()) {
            if (c.getName() != null && c.getName().equals("options")) {
                ((JButton) ((JPanel) c).getComponent(0)).setText("Options");
            }
        }
        state = 0;
        paused = false;
        narrator.setText("Which door do you choose?");
        int t = new Random().nextInt(3);
        for (int i = 0; i < doors.length; i++) {
            doors[i] = new Door(t == i);
        }
        animator.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        int width = getWidth(), height = getHeight();
        int paddx = width / 18, paddy = height / 9;
        for (int i = 0; i < doors.length; i++) {
            doors[i].p1 = new Point(width / 3 * i + paddx, height / 4);
            doors[i].p2 = new Point(width / 3 - paddx * 2, height / 3 * 2 - paddy);
            g.drawRect(doors[i].p1.x, doors[i].p1.y, doors[i].p2.x, doors[i].p2.y);
            if (doors[i].open) {
                g.drawString(doors[i].prize ? "prize" : "nothing",
                        doors[i].p1.x + doors[i].p2.x / 2 - (int) (getFont().getSize() * 1.3),
                        doors[i].p1.y + doors[i].p2.y / 2);
            }
            if (!doors[i].open) {
                g.fillRect(doors[i].p1.x, doors[i].p1.y, doors[i].p2.x + 1, doors[i].p2.y + 1);
            } else if (doors[i].openning) {
                openDoor(g, i);
            }
        }
    }

    private void openDoor(Graphics g, int i) {
        if (!paused)
            doors[i].angle += 1f;
        Point p = rotate(new Point(doors[i].p2.x, 0), doors[i].angle);
        g.fillPolygon(
                new int[] { doors[i].p1.x + doors[i].p2.x - p.x, doors[i].p1.x + doors[i].p2.x,
                        doors[i].p1.x + doors[i].p2.x, doors[i].p1.x + doors[i].p2.x - p.x },
                new int[] { doors[i].p1.y + p.y, doors[i].p1.y, doors[i].p1.y + doors[i].p2.y,
                        doors[i].p1.y + doors[i].p2.y + p.y },
                4);
        if (doors[i].angle >= 90) {
            doors[i].openning = false;
            if (state == 4) {
                if (doors[i].prize) {
                    narrator.setText("You win!");
                } else {
                    narrator.setText("You lose!");
                }
            }
        }
    }

    Point rotate(Point p, double angle) {
        double rad = Math.toRadians(angle);
        double x = p.x * Math.cos(rad) - p.y * Math.sin(rad);
        double y = p.x * Math.sin(rad) + p.y * Math.cos(rad);
        p.x = (int) x;
        p.y = (int) y;
        return p;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Ignore
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Ignore
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!paused) {
            int x = e.getX(), y = e.getY();
            for (int i = 0; i < doors.length; i++) {
                if (x > doors[i].p1.x && x < (doors[i].p1.x + doors[i].p2.x) && y > doors[i].p1.y
                        && y < (doors[i].p1.y + doors[i].p2.y) && !doors[i].open) {
                    if (state == 0) {
                        state = 1;
                        doors[i].chosen = true;
                        narrator.setText("Do you want to open the door " + (i + 1) + "?");
                        Timer = 2;
                        holding.start();
                    }

                    if (state == 3) {
                        doors[i].openning = true;
                        doors[i].open = true;
                        state = 4;
                    }

                }
            }
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Ignore
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Ignore
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("options")) {
            paused = !paused;
            this.setComponentZOrder(OptionMenu, 0);
            OptionMenu.setVisible(!OptionMenu.isVisible());
            ((JButton) (e.getSource())).setText(paused ? "Back" : "Options");

        } else if (e.getActionCommand().equals("restart")) {
            holding.stop();
            OptionMenu.setVisible(false);
            initGame();
        } else if (e.getActionCommand().equals("main menu")) {
            animator.stop();
            holding.stop();
            window.loadMenu();
        }

    }

}
