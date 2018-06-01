import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.jCharts.axisChart.axis.XAxis;
import org.w3c.dom.css.Rect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


public class ChartDrawing extends Application {

    private HashMap<String, HashMap<String, HashMap<String, ArrayList<Long>>>> endpoints = new HashMap<>();
    private HashMap<String, HashMap<String, Long>> minMaxNumbers = new HashMap<>();
    private String filePrefix = "1527854676748";
    private String typeSetup = "default-shards-one-node-one-user";

    @Override public void start(Stage stage) {
        System.out.println(filePrefix);
        String type = filePrefix;

        String csvFile = filePrefix + ".csv";
        BufferedReader br = null;
        String line;
        String csvSplitByFirst = "@";
        String cvsSplitBy = ",";


        try {

            br = new BufferedReader(new FileReader("runs/valid/" + typeSetup + "/" + csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(csvSplitByFirst);
                //if(data.length < 2) data = line.split(cvsSplitBy);
                System.out.println(data.length + Arrays.asList(data).toString());
                if(data.length < 5) continue;
                System.out.println(line);
                String user = data[0];
                long epochTime = Long.parseLong(data[1]);
                long responseTime = Long.parseLong(data[2]);
                String endpoint = data[3];

                if(endpoint.contains("/init/addpatient/") || endpoint.contains("/init/reset")) continue;
                if(!endpoints.containsKey(endpoint)) {
                    System.out.println("new endpoint to add - " + endpoint);
                    endpoints.put(endpoint, new HashMap<>());
                }
                if(!endpoints.get(endpoint).containsKey(user)) {
                    System.out.println("new user in endpoint " + endpoint + " - " + user);
                    HashMap userXYMap = new HashMap();
                    userXYMap.put("x", new ArrayList<>());
                    userXYMap.put("y", new ArrayList<>());
                    endpoints.get(endpoint).put(user, userXYMap);
                }

                endpoints.get(endpoint).get(user).get("x").add(epochTime);
                endpoints.get(endpoint).get(user).get("y").add(responseTime);
                if(!minMaxNumbers.containsKey(endpoint)) {
                    minMaxNumbers.put(endpoint, new HashMap());
                    minMaxNumbers.get(endpoint).put("min", 0L);
                    minMaxNumbers.get(endpoint).put("max", 0L);
                }
                if(responseTime > minMaxNumbers.get(endpoint).get("max")) minMaxNumbers.get(endpoint).put("max", responseTime);
                if(responseTime < minMaxNumbers.get(endpoint).get("min")) minMaxNumbers.get(endpoint).put("min", responseTime);

                //System.out.println(endpoints);
                //i++;
                //if(i > 60) System.exit(1);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for(String endpoint : endpoints.keySet()) {

            stage.setTitle(endpoint);
            //defining the axes

            double range = minMaxNumbers.get(endpoint).get("max") - minMaxNumbers.get(endpoint).get("min");
            int tickCount = 50;
            double unroundedTickSize = range/(tickCount-1);
            double x = Math.ceil(Math.log10(unroundedTickSize)-1);
            double pow10x = Math.pow(10, x);
            double roundedTickRange = Math.ceil(unroundedTickSize / pow10x) * pow10x;

            System.out.println("Ticksize " + roundedTickRange);

            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis(minMaxNumbers.get(endpoint).get("min"), minMaxNumbers.get(endpoint).get("max"), roundedTickRange);
            xAxis.setLabel("Request number");
            xAxis.autosize();

            yAxis.setLabel("Response time");
            yAxis.setTickMarkVisible(true);
            yAxis.setMinorTickVisible(false);
            yAxis.setVisible(true);
            yAxis.setTickLabelsVisible(true);
            yAxis.setAutoRanging(false);
            //creating the chart
            LineChart<Number, Number> lineChart =
                    new LineChart<Number, Number>(xAxis, yAxis);

            lineChart.setTitle(endpoint);
            lineChart.getStyleClass().add("thick-chart");
            //defining a series
            //System.out.println(endpoint);
            //System.out.println(endpoints.get(endpoint));
            ArrayList<Long> times = new ArrayList<>();
            for(String user : endpoints.get(endpoint).keySet()) {

                XYChart.Series series = new XYChart.Series();

                series.setName(user);
                //populating the series with data


                for(int i = 0; i < endpoints.get(endpoint).get(user).get("x").size(); i++) {
                    Long responseTime = endpoints.get(endpoint).get(user).get("y").get(i);
                    Long epochTime = endpoints.get(endpoint).get(user).get("x").get(i);

                    if(!times.contains(epochTime)) times.add(epochTime);

                    //System.out.println(responseTime + " " + epochTime);
                    XYChart.Data thisData = new XYChart.Data(i, responseTime);
                    Rectangle rect = new Rectangle(0,0);
                    rect.setVisible(false);
                    thisData.setNode(rect);
                    series.getData().add(thisData);
                    //series.getData().add(new XYChart.Data(endpoints.get(endpoint).get(user).get("x").get(i), endpoints.get(user).get("y").get(i)));
                }
                lineChart.getData().add(series);
            }
            Collections.sort(times, Collections.reverseOrder());
            //yAxis.setTickUnit(100);

            /*xAxis.setAutoRanging(false);
            xAxis.setLowerBound(times.get(0));
            xAxis.setUpperBound(times.get(times.size() - 1));*/

            lineChart.setAnimated(false);
            Scene scene = new Scene(lineChart, 800, 600);
            scene.getStylesheets().add("stylesheet.css");
            endpoint = endpoint.substring(1);

            stage.setScene(scene);
            saveAsPng(scene, "graphs/" + typeSetup + "/" + endpoint.replace("/", "-") + "-" + type + ".png");
            //stage.show();
        }
        System.exit(1);
    }

    public void saveAsPng(Scene scene, String path) {
        WritableImage image = scene.snapshot(null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDrawing(String timestamp) {
        this.filePrefix = timestamp;
        launch();
    }

    public static void main(String[] args) {
        launch(args);
    }
}