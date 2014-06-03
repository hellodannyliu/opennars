/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars.main;

import nars.io.ExperienceReader;
import nars.io.ExperienceWriter;
import nars.io.ExperienceWriter.LineOutput;
import nars.main_nogui.NAR;

/**
 * An instance of a web socket session to a NAR
 * @author me
 */
abstract public class NARConnection implements LineOutput, Runnable {
    public final NAR nar;
    private final ExperienceReader reader;
    private final ExperienceWriter writer;
    private Thread thread;

    public NARConnection(NAR nar) {
        this.nar = nar;
        
        this.reader = new ExperienceReader(nar);
        
        
        this.writer = new ExperienceWriter(nar, this);
        nar.addOutputChannel(writer);
        
    }

    public void read(final String message) {
        String[] lines = message.split("\n");
        for (String s : lines)
            this.reader.parse(s);
        if (!running)
            resume();
    }
    
    abstract public void println(String output);
    
    
    boolean running = false;
    
    public void resume() {
        if (!running) {        
            running = true;
            thread = new Thread(this);
            thread.start();        
        }
    }
    public void stop() {
        running = false;
        if (thread!=null) {
            thread.interrupt();
        }
        nar.stop();
        thread = null;
    }
    
    public void run() {
        while (running) {
            /*println("NARSBatch.run():"
                    + " step " + nar.getTime()
                    + " " + nar.isFinishedInputs());*/
            
            nar.tick();
            
            /*println("NARSBatch.run(): after tick"
                    + " step " + nar.getTime()
                    + " " + nar.isFinishedInputs());*/
            
            if (nar.isFinishedInputs() || nar.getTime() == 1000) {
                break;
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {            }
        }
        running = false;
        thread = null;
    }

    
}
