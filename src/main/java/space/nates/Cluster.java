package space.nates;

import joinery.DataFrame;
import org.encog.ml.MLCluster;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.kmeans.KMeansClustering;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Demonstrates Spark clustering capabilities
 */
public class Cluster {

    public static void main(String[] args) {
        File workingDirectory = new File(System.getProperty("user.dir"));
        File csvFile = null;

        // Create a Java Spark Context

        // Prompt user for CSV File
    try {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(workingDirectory);

        if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)) {
            csvFile = fc.getSelectedFile();
        } else {
            System.out.println("No file provided.");
            return;
        }

        // Import CSV File into joinery DataFrame object
        try {
            DataFrame df = DataFrame.readCsv(csvFile.getCanonicalPath(), ",", "NULL", true);

            // Get Header and Type information
            DataFrame dfInfo = new DataFrame()
                    .add("Column",  Arrays.<Object>asList(df.columns().toArray()))
                    .add("Type", Arrays.<Object>asList(df.types().toArray()));

            // For Demo of k means

            // compute distance between Product and Price
            ArrayList<ArrayList<Double>> lDistances = new ArrayList();

            Iterator<List<String>> it = df.iterrows();

            while(it.hasNext()) {
                List<String> row = it.next();

                ArrayList<ArrayList<Double>> doubleArray = new ArrayList<ArrayList<Double>>();
                ArrayList<Double> array = new ArrayList<Double>();

                for (int i = 0; i < row.size(); i++) {

                    Double distance = LevensteinDistance.computeDistance(String.valueOf(row.get(0)), String.valueOf(row.get(i)));
                    distance += (double) Math.min(String.valueOf(row.get(0)).length(), String.valueOf(row.get(i)).length()) /
                            Math.max(String.valueOf(row.get(0)).length(), String.valueOf(row.get(i)).length()) + 1;

                    array.add(distance);

                }
                lDistances.add(array);
            }


            DataFrame display = new DataFrame();
            int k = 0;
            for (ArrayList<Double> array : lDistances) {
                System.out.println(array);
                display.add("Row " + k , array);
                k++;
            }

            // Plot Data
            display.plot(DataFrame.PlotType.SCATTER);
            display.plot();


            double[][] data = new double[lDistances.get(0).size()][lDistances.size()];
            for (int i = 0; i < lDistances.size(); i++) {
                for (int j = 0; j < lDistances.get(i).size(); j++) {
                    data[j][i] = lDistances.get(i).get(j);
                }
            }

            final BasicMLDataSet set = new BasicMLDataSet();
            for (double[] elem : data) {
                set.add(new BasicMLData(elem));
            }

            final KMeansClustering kmeans = new KMeansClustering(3, set);
            kmeans.iteration(100);

            int i = 1;
            for (final MLCluster cluster : kmeans.getClusters()) {
                System.out.println("*** Cluster " + (i++) + " ***");
                final MLDataSet ds = cluster.createDataSet();
                final MLDataPair pair = BasicMLDataPair.createPair(
                        ds.getInputSize(), ds.getIdealSize());
                for (int j = 0; j < ds.getRecordCount(); j++) {
                    ds.getRecord(j, pair);
                    System.out.println(Arrays.toString(pair.getInputArray()));

                }
            }

            System.out.println("\n\nPlotting table: ");
            System.out.println(dfInfo);

            // Filter and display dates
            DataFrame dateDataFrame = new DataFrame();

            int inc = 0;
            ArrayList<Integer> includeSet = new ArrayList<Integer>();
            for (Object header : df.types()) {
                if (header.getClass().equals(java.util.Date.class)) {
                    includeSet.add(inc);
                }
                inc++;
            }

            dateDataFrame = df.retain(includeSet);

            System.out.println(dateDataFrame);

            // Get Header and Type information of Date Data Frame
            DataFrame dateDataFrameInfo = new DataFrame()
                    .add("Column",  Arrays.<Object>asList(dateDataFrame.columns().toArray()))
                    .add("Type", Arrays.<Object>asList(dateDataFrame.types().toArray()));

            System.out.println("\n\nPlotting Date Table: ");
            System.out.println(dateDataFrameInfo);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }
}
