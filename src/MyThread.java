public class MyThread implements Runnable {
    boolean alive = true;
    Thread thread;
    String name = "";
    public void run() {
        //to be overridden
    }

    public void stop() {
        alive = false;
    }

    public void start() {
        if (thread == null || !thread.isAlive() ) {
            thread = new Thread(this, name);
            alive = true;
            thread.start();
        }
    }

    public MyThread(String name) {
        this.name = name;
    }
}
    
