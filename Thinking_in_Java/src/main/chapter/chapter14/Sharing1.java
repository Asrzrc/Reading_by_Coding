package main.chapter.chapter14;


import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class TwoCounter extends Thread {
    private boolean started = false;
    private TextField t1 = new TextField(5),
                      t2 = new TextField(5);
    private Label l = new Label("count1 == count2");
    private int count1 = 0, count2 = 0;

    TwoCounter(Container c) {
        Panel p = new Panel();
        p.add(t1);
        p.add(t2);
        p.add(l);
        c.add(p);
    }

    public void start() {
        if(!started) {
            started = true;
            super.start();
        }
    }

    public void run() {
        while (true) {
            t1.setText(Integer.toString(count1++));
            t2.setText(Integer.toString(count2++));
            try {
                sleep(500);
            } catch (InterruptedException ignored) {}
        }
    }

    void synchTest() {
        Sharing1.incrementAccess();
        if (count1 != count2)
            l.setText("Unsynched!");
    }
}


class Watcher extends Thread {
    private Sharing1 p;
    Watcher(Sharing1 p) {
        this.p = p;
        start();
    }

    public void run() {
        while (true) {
            for (int i = 0; i < p.s.length; i++)
                p.s[i].synchTest();
            try {
                sleep(500);
            } catch (InterruptedException ignored) {}
        }
    }
}

public class Sharing1 extends Applet {
    TwoCounter[] s;
    private static int accessCount = 0;
    private static TextField aCount = new TextField("0", 10);

    static void incrementAccess() {
        accessCount++;
        aCount.setText(Integer.toString(accessCount));
    }

    private Button start = new Button("Start"),
                   observer = new Button("Observer");
    private boolean isApplet = true;
    private int numCounters = 0;
    private int numObservers = 0;

    public void init() {
        if (isApplet) {
            numCounters = Integer.parseInt(getParameter("size"));
            numObservers = Integer.parseInt(getParameter("observers"));
        }
        s = new TwoCounter[numCounters];
        for (int i = 0; i < s.length; i++)
            s[i] = new TwoCounter(this);
        Panel p = new Panel();
        start.addActionListener(new StartL());
        p.add(start);
        observer.addActionListener(new ObserverL());
        p.add(observer);
        p.add(new Label("Access Count"));
        p.add(aCount);
        add(p);
    }

    class StartL implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < numObservers; i++)
                s[i].start();
        }
    }

    class ObserverL implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < numObservers; i++)
                new Watcher(Sharing1.this);
        }
    }

    public static void main(String[] args) {
        Sharing1 applet = new Sharing1();
        applet.isApplet = false;
        applet.numCounters = (args.length == 0 ? 5 : Integer.parseInt(args[0]));
        applet.numObservers = (args.length < 2 ? 5 : Integer.parseInt(args[1]));
        Frame aFrame = new Frame("Sharing1");
        aFrame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                }
        );
        aFrame.add(applet, BorderLayout.CENTER);
        aFrame.setSize(500, applet.numCounters * 100);
        applet.init();
        applet.start();
        aFrame.setVisible(true);
    }
}