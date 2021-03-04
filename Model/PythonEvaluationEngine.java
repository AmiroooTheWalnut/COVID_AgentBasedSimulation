/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import py4j.GatewayServer;

/**
 *
 * @author user
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

    public void runScript(String script) {
        String header = "from py4j.java_gateway import JavaGateway\n"
                + "gateway = JavaGateway()\n"
                + "root = gateway.entry_point\n";
        script=header+script;
        BufferedWriter writer;
        try {
            FileWriter out = new FileWriter("./tempPython.py");
            writer = new BufferedWriter(out);
            writer.write(script);

            writer.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(PythonEvaluationEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        File file = new File("./tempPython.py");
        if (file.exists()) {
            String runCommand = "\"C:/ProgramData/Anaconda3/envs/BucketRenormalization_pycharm/python.exe\" " + "tempPython.py";
            try {
                Process process = Runtime.getRuntime().exec(runCommand);
                if (myConsole != null) {
                    InputStream stdout = process.getInputStream();
                    Scanner scanner = new Scanner(stdout);
                    while (scanner.hasNextLine()) {
                        String str = scanner.nextLine();
                        myConsole.append(str + "\n");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PythonEvaluationEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
