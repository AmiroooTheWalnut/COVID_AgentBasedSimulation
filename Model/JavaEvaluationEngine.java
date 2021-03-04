/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author user
 */
public class JavaEvaluationEngine {

    public Binding mainBinding;
    public GroovyShell mainShell;
    public JTextAreaOutputStream myConsole;

    public JavaEvaluationEngine(MainModel mainModel) {
        mainBinding = new Binding();
        mainBinding.setVariable("root", mainModel);
        mainShell = new GroovyShell(mainBinding);
    }

    public void connectToConsole(JTextArea console) {
        myConsole = new JTextAreaOutputStream(console);
        mainShell.setProperty("out", new PrintStream(myConsole));
    }

    public void runScript(String script) {
        mainShell.evaluate(script);
    }

    public class JTextAreaOutputStream extends OutputStream {

        private JTextArea destination;

        public JTextAreaOutputStream(JTextArea destination) {
            if (destination == null) {
                throw new IllegalArgumentException("Destination is null");
            }

            this.destination = destination;
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            final String text = new String(buffer, offset, length);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    destination.append(text);
                }
            });
        }

        @Override
        public void write(int b) throws IOException {
            write(new byte[]{(byte) b}, 0, 1);
        }
    }
}
