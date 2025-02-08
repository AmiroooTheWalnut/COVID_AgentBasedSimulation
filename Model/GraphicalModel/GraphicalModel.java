/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package COVID_AgentBasedSimulation.Model.GraphicalModel;

import eu.amidst.core.constraints.Constraint;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.exponentialfamily.ParameterVariables;
import eu.amidst.core.io.BayesianNetworkLoader;
import eu.amidst.core.io.BayesianNetworkWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class GraphicalModel {
    public static void makeModelTest(){
        //We can open the data stream using the static class DataStreamLoader
        DataStream<DataInstance> data = DataStreamLoader.open("./datasets/simulated/syntheticData.arff");
        Variables variables = new Variables(data.getAttributes());

        Variable a = variables.getVariableByName("A");
        Variable b = variables.getVariableByName("B");
        Variable c = variables.getVariableByName("C");
        Variable d = variables.getVariableByName("D");
        Variable e = variables.getVariableByName("E");
        Variable g = variables.getVariableByName("G");
        Variable h = variables.getVariableByName("H");
        Variable i = variables.getVariableByName("I");

        Variable hidden = variables.newMultinomialVariable("HiddenVar", Arrays.asList("TRUE", "FALSE"));
        
//        hidden.isParameterVariable()
        DAG dag = new DAG(variables);

        dag.getParentSet(c).addParent(hidden);
        dag.getParentSet(d).addParent(hidden);
        dag.getParentSet(g).addParent(hidden);
        dag.getParentSet(h).addParent(hidden);
        dag.getParentSet(i).addParent(hidden);

        dag.getParentSet(hidden).addParent(a);
        dag.getParentSet(hidden).addParent(b);
        dag.getParentSet(hidden).addParent(e);
        
        ParameterVariables q=new ParameterVariables(1);
        Variable par1 = q.newNormalGamma("param1");
        
//        VariableBuilder builder = new VariableBuilder();
//        builder.setName(name);
//        builder.setDistributionType(distributionTypeEnum);
//        builder.setStateSpaceType(stateSpaceType);
//        builder.setObservable(false);
        
//        svb.addParameterConstraint(new Constraint("alpha",par1,1));
//        variables.newVariable(par1.getVariableBuilder());
//        variables.new

        System.out.println(dag.toString());

        BayesianNetwork bn = new BayesianNetwork(dag);
//        bn.getDAG().
        //bn.s
        System.out.println(bn.toString());

        try {
            BayesianNetworkWriter.save(bn, "BNHiddenExample.bn");
            //BayesianNetworkWriter.save(bn, "."+File.separator+"networks"+File.separator+"simulated"+File.separator+"BNHiddenExample.bn");
        } catch (IOException ex) {
            Logger.getLogger(GraphicalModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void makeSimpleModelTest(){
        //We can open the data stream using the static class DataStreamLoader
        DataStream<DataInstance> data = DataStreamLoader.open("./datasets/simulated/testTwoCluster.arff");
        Variables variables = new Variables(data.getAttributes());

        Variable a = variables.getVariableByName("X");
        Variable b = variables.getVariableByName("Y");

        Variable hiddenC1 = variables.newMultinomialVariable("Z1", Arrays.asList("T", "F"));
//        Variable hiddenC1 = variables.newMultinomialVariable("Z1", Arrays.asList("T", "F"));
//        Variable hiddenC2 = variables.newMultinomialVariable("Z2", Arrays.asList("T", "F"));
        
//        hidden.isParameterVariable()
        DAG dag = new DAG(variables);

//        dag.getParentSet(hiddenC1).addParent(a);
//        dag.getParentSet(hiddenC1).addParent(b);
        dag.getParentSet(a).addParent(hiddenC1);
        dag.getParentSet(b).addParent(hiddenC1);
//        dag.getParentSet(a).addParent(hiddenC2);
//        dag.getParentSet(b).addParent(hiddenC2);
        
//        ParameterVariables q=new ParameterVariables(1);
//        Variable par1 = q.newNormalGamma("param1");
        
//        VariableBuilder builder = new VariableBuilder();
//        builder.setName(name);
//        builder.setDistributionType(distributionTypeEnum);
//        builder.setStateSpaceType(stateSpaceType);
//        builder.setObservable(false);
        
//        svb.addParameterConstraint(new Constraint("alpha",par1,1));
//        variables.newVariable(par1.getVariableBuilder());
//        variables.new

        System.out.println(dag.toString());

        BayesianNetwork bn = new BayesianNetwork(dag);
//        bn.getDAG().
        //bn.s
        System.out.println(bn.toString());
        try {
            BayesianNetworkWriter.save(bn, "SimpleNet.bn");
            //BayesianNetworkWriter.save(bn, "."+File.separator+"networks"+File.separator+"simulated"+File.separator+"BNHiddenExample.bn");
        } catch (IOException ex) {
            Logger.getLogger(GraphicalModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void inferenceTestModel(){
        try {
            SVB svb = new SVB();
            BayesianNetwork bn = BayesianNetworkLoader.loadFromFile("BNHiddenExample.bn");
            svb.setDAG(bn.getDAG());
            svb.setOutput(true);
            svb.setWindowsSize(10);
            DataStream<DataInstance> data = DataStreamLoader.open("datasets/simulated/syntheticData.arff");
//            svb.addParameterConstraint(new Constraint("DirichletParameter_{A = 0,B = 2,E = 0}",bn.getVariables().getVariableByName("HiddenVar"),1));
//            svb.getPlateuStructure().setNRepetitions(1);
            svb.randomInitialize();
            svb.setRandomRestart(true);
            svb.setParallelMode(true);
            Random rnd = new Random(System.currentTimeMillis());
            svb.setSeed(rnd.nextInt());
            svb.initLearning();
            svb.updateModel(data);
//            svb.getPlateuStructure().
            BayesianNetwork model = svb.getLearntBayesianNetwork();
//            svb.setTransitionMethod(TransitionMethod);
            System.out.println(model);
        } catch (IOException ex) {
            Logger.getLogger(GraphicalModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GraphicalModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void inferenceSimpleModel(){
        try {
            SVB svb = new SVB();
            BayesianNetwork bn = BayesianNetworkLoader.loadFromFile("SimpleNet.bn");
            svb.setDAG(bn.getDAG());
            svb.setOutput(true);
            svb.setWindowsSize(40);
            DataStream<DataInstance> data = DataStreamLoader.open("datasets/simulated/testTwoCluster.arff");
//            svb.addParameterConstraint(new Constraint("X | {Z1 = 0}",bn.getVariables().getVariableByName("X"),1));
//            svb.getPlateuStructure().setNRepetitions(1);
            svb.randomInitialize();
            svb.setRandomRestart(true);
            svb.setParallelMode(true);
            Random rnd = new Random(System.currentTimeMillis());
            svb.setSeed(rnd.nextInt());
            svb.initLearning();
            svb.updateModel(data);
//            svb.getPlateuStructure().
            BayesianNetwork model = svb.getLearntBayesianNetwork();
//            svb.setTransitionMethod(TransitionMethod);
            System.out.println(model);
        } catch (IOException ex) {
            Logger.getLogger(GraphicalModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GraphicalModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
