/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model.Engines;

import COVID_AgentBasedSimulation.Model.AgentBasedModel.AgentTemplate;
import COVID_AgentBasedSimulation.Model.AgentBasedModel.PythonScript;
import COVID_AgentBasedSimulation.Model.MainModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import py4j.GatewayServer;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class PythonEvaluationEngine {

    public GatewayServer gatewayServer;
    public JTextArea myConsole;

    public PythonEvaluationEngine(MainModel mainModel) {
        gatewayServer = new GatewayServer(mainModel);
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }

    public void connectToConsole(JTextArea console) {
        myConsole = console;
    }

    public void saveAllPythonScripts(ArrayList<AgentTemplate> input) {
        File file = new File(".");
        for (int i = 0; i < file.listFiles().length; i++) {
            if (file.listFiles()[i].getName().contains("tempPython_")) {
                file.listFiles()[i].delete();
            }
        }

        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).constructor.isJavaScriptActive == false) {
                saveScript(input.get(i).constructor.pythonScript);
            }
            if (input.get(i).behavior.isJavaScriptActive == false) {
                saveScript(input.get(i).behavior.pythonScript);
            }
            if (input.get(i).destructor.isJavaScriptActive == false) {
                saveScript(input.get(i).destructor.pythonScript);
            }
        }
    }

    public void saveScript(PythonScript pythonScript) {
        String header = "from py4j.java_gateway import JavaGateway\n"
                + "gateway = JavaGateway()\n"
                + "modelRoot = gateway.entry_point\n"
                + "agentRoot = modelRoot.getABM().getCurrentEvaluatingAgent()\n";
        String script = header + pythonScript.script;
        BufferedWriter writer;
        try {
            int index = -1;
            for (int i = 0; i < 1000; i++) {//MAX NUMBER OF SCRIPTS IS 1000
                File file = new File("./tempPython_" + i + ".py");
                if (file.exists() == false) {
                    index = i;
                    pythonScript.generatedScriptLocation = "./tempPython_" + index + ".py";
                    break;
                }
            }
            if (index == -1) {
                System.out.println("MAX NUMBER OF SCRIPTS REACHED!");
            }
            FileWriter out;
            out = new FileWriter("./tempPython_" + index + ".py");
            writer = new BufferedWriter(out);
            writer.write(script);

            writer.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(PythonScript.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void print(String input) {

    }

    public void runScript(PythonScript pythonScript) {
        File file = new File(pythonScript.generatedScriptLocation);
        if (file.exists()) {
            String runCommand = "\"C:/ProgramData/Anaconda3/python.exe\" " + pythonScript.generatedScriptLocation;
            try {
                Process process = Runtime.getRuntime().exec(runCommand);
                /*
                GETTING PROCESS OUTPUT IS TOO EXPENSIVE!
                 */
                if (myConsole != null) {
                    String[] lines = myConsole.getText().split(System.getProperty("line.separator"));
                    if (lines.length > 1000) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 10; i < lines.length; i++) {
                            sb.append(lines[i]);
                        }
                        String str = sb.toString();
                        myConsole.setText(str);
                    }
                    if (myConsole.getText().length() > 1000) {
                        lines = myConsole.getText().split(System.getProperty("line.separator"));
                        StringBuffer sb = new StringBuffer();
                        int maxNumLines = Math.min(10, lines.length);
                        if (maxNumLines <= 10) {
                            myConsole.setText("");
                        } else {
                            for (int i = maxNumLines; i < lines.length; i++) {
                                sb.append(lines[i]);
                            }
                            String str = sb.toString();
                            myConsole.setText(str);
                        }
                    }

                    InputStream stdout = process.getErrorStream();

                    final byte[] buf = new byte[1000];
                    while (stdout.read(buf) != -1) {
                        String s = new String(buf, StandardCharsets.US_ASCII);
                        myConsole.append(s);
//                        cnt++;
                    }
                    stdout.close();

//                    BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
//                    String thisLine;
//                    while ((thisLine = br.readLine()) != null) {
//                        myConsole.append(thisLine + "\n");
//                    }

//                    Scanner scanner = new Scanner(stdout);
//                    while (scanner.hasNextLine()) {
//                        String str = scanner.nextLine();
//                        myConsole.append(str + "\n");
//                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PythonEvaluationEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
