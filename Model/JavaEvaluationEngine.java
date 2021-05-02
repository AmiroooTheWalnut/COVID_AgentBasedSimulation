/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentTemplate;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class JavaEvaluationEngine {

    public Binding mainBinding;
    public GroovyShell mainShell;
    public JTextAreaOutputStream myConsole;   

    public JavaEvaluationEngine(MainModel mainModel) {
        mainBinding = new Binding();
        mainBinding.setVariable("modelRoot", mainModel);
        //mainBinding.setVariable("agentRoot", mainModel.ABM.currentEvaluatingAgent);
        mainShell = new GroovyShell(mainBinding);
        //mainShell.setProperty(property, mainModel);
    }
    
    public void parseAllScripts(ArrayList<AgentTemplate> agentTemplates){
        for (int i = 0; i < agentTemplates.size(); i++) {
            if(agentTemplates.get(i).constructor.isJavaScriptActive==true){
                agentTemplates.get(i).constructor.javaScript.parsedScript=mainShell.parse(agentTemplates.get(i).constructor.javaScript.script);
            }
            if(agentTemplates.get(i).behavior.isJavaScriptActive==true){
                agentTemplates.get(i).behavior.javaScript.parsedScript=mainShell.parse(agentTemplates.get(i).behavior.javaScript.script);
            }
            if(agentTemplates.get(i).destructor.isJavaScriptActive==true){
                agentTemplates.get(i).destructor.javaScript.parsedScript=mainShell.parse(agentTemplates.get(i).destructor.javaScript.script);
            }
        }
    }

    public void connectToConsole(JTextArea console) {
//        myConsole = new JTextAreaOutputStream(console);
//        mainShell.setProperty("out", new PrintStream(myConsole));
    }

    /*
    RUN RAW TEXT SCRIPT
    */
    public void runScript(String script) {
        mainShell.evaluate(script);
    }
    
    public void runParsedScript(Script script){
        script.run();
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
                    String[] lines = destination.getText().split(System.getProperty("line.separator"));
                    if (lines.length > 1000) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 10; i < lines.length; i++) {
                            sb.append(lines[i]);
                        }
                        String str = sb.toString();
                        destination.setText(str);
                    }
                    if (destination.getText().length() > 1000) {
                        lines = destination.getText().split(System.getProperty("line.separator"));
                        StringBuffer sb = new StringBuffer();
                        int maxNumLines = Math.min(10, lines.length);
                        if (maxNumLines <= 10) {
                            destination.setText("");
                        } else {
                            for (int i = maxNumLines; i < lines.length; i++) {
                                sb.append(lines[i]);
                            }
                            String str = sb.toString();
                            destination.setText(str);
                        }
                    }
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
