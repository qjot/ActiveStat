/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import static java.lang.Math.round;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ObjectProperty;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jfxtras.scene.control.CalendarPicker;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.util.DateTimeUtils;
import model.FileParserRunner;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author qjot
 */
public class MainController implements Initializable {

    @FXML private Label dateLabel;
    @FXML private Label durationLabel;
    @FXML private Label exerciseTimeLabel;
    @FXML private Label distanceLabel;
    @FXML private Label slopeLabel;
    @FXML private Label avgSpeedLabel;
    @FXML private Label maxSpeedLabel;
    @FXML private Label maxHeartRate;
    @FXML private Label minHeartRate;
    @FXML private Label avgHeartRate;
    @FXML private Label maxPedalingRateLabel;
    @FXML private Label avgPedalingRateLabel;
    @FXML private LineChart<Number, Number> lineChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private CalendarPicker calendarView;
    @FXML private GridPane statsGrid;
    @FXML private VBox vBoxMain;
    @FXML private ProgressBar progressBarLoad;
    @FXML private Button exitButton;
    
        XYChart.Series<Number, Number> speed;
        XYChart.Series<Number, Number> heartRate;
        XYChart.Series<Number, Number> pedalingRate;
        XYChart.Series<Number, Number> height;
    
    @FXML private AnchorPane scrollableContent;
    @FXML ScrollPane scrollPane;
    FileParserRunner runningFileLoader;
    private final ObservableList<TrackData> trackDatabase= observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

 
        calendarView.calendarProperty().addListener((ObservableValue<? extends Calendar> ov, Calendar old_val, Calendar new_val) -> {
            if (calendarView.highlightedCalendars().contains(new_val)) {
                int dateId = calendarView.highlightedCalendars().indexOf(new_val);
                changeWiew(trackDatabase.get(dateId));
            }
        });
        exitButton.disableProperty().set(true);
    }
      @FXML
    private void load(MouseEvent event) {

        FileChooser fileChooser = new FileChooser();
        List<File> trackDataFileList = fileChooser.showOpenMultipleDialog(vBoxMain.getScene().getWindow());
        if (trackDataFileList != null) {
            ObservableList<Calendar> CalendarRunningDates = calendarView.highlightedCalendars();
            ObjectProperty<Calendar> CalendarSelectedDate = calendarView.calendarProperty();

            runningFileLoader = new FileParserRunner(trackDatabase, trackDataFileList, CalendarRunningDates, CalendarSelectedDate);
            runningFileLoader.setDaemon(false);
            progressBarLoad.progressProperty().bind(runningFileLoader.ProgressProperty());
            runningFileLoader.start();
        }
    }

    private void changeWiew(TrackData trackData) {
        
        ChangeText(trackData);
        ChangeCharts(trackData);
    }

    private void ChangeText(TrackData trackData) {
        dateLabel.setText("Date: " + DateTimeUtils.format(trackData.getStartTime()));
        durationLabel.setText(DateTimeUtils.format(trackData.getTotalDuration()));
        exerciseTimeLabel.setText(DateTimeUtils.format(trackData.getMovingTime()));;
        distanceLabel.setText(String.format("%.0f m", trackData.getTotalDistance()));
        slopeLabel.setText(String.format("%.0f m", trackData.getTotalAscent() + trackData.getTotalDescend()));
        avgSpeedLabel.setText(String.format("%.2f m/s", trackData.getAverageSpeed()));
        maxSpeedLabel.setText(String.format("%.2f m/s", trackData.getMaxSpeed()));
        maxHeartRate.setText(String.valueOf(trackData.getMaxHeartrate()));
        minHeartRate.setText(String.valueOf(trackData.getMinHeartRate()));
        avgHeartRate.setText(String.valueOf(trackData.averageHeartrateProperty().getValue()));
        maxPedalingRateLabel.setText(String.valueOf(trackData.getMaxCadence()));
        avgPedalingRateLabel.setText(String.valueOf(trackData.getAverageCadence()));
    }

    private void ChangeCharts(TrackData trackData) {

        System.out.println(trackData.getNumPoints());

        xAxis.setLabel("Distance");

        yAxis.setLabel("Frequencies");
        ObservableList<Chunk> chunks = trackData.getChunks();
        //System.out.println( chunks.size());
        //System.out.println(chunks.get(1).getFirstPoint().elevationProperty());

        double distance = 0;
        speed = new XYChart.Series();
        heartRate = new XYChart.Series();
        pedalingRate = new XYChart.Series();
        height = new XYChart.Series<>();
        speed.setName("Speed");
        heartRate.setName("Heart rate");
        pedalingRate.setName("Pedaling rate");
        height.setName("height");
        double[] mean1 = new double[3];
        double[] mean2 = new double[3];
        double[] mean3 = new double[3];
        double currentHeight = chunks.get(0).getAscent();
        double currentDesc = chunks.get(0).getDescend();
        for (int i = 0; i < chunks.size(); i++) {
            currentHeight += chunks.get(i).getAscent() - chunks.get(i).getDescend();
            distance += chunks.get(i).getDistance();
            mean3[0] += chunks.get(i).getSpeed();
            mean3[1] += chunks.get(i).getAvgCadence();
            int size = round(chunks.size()/500);
            if (i % size == 0) {
                height.getData().add(new XYChart.Data<>(distance / 1000, currentHeight));
                mean3[0] /= size;
                mean2[0] = (mean1[0] + mean2[0] + mean3[0]) / 3;
                mean3[1] /= size;
                mean2[1] = (mean1[1] + mean2[1] + mean3[1]) / 3;
                speed.getData().add(new XYChart.Data<>((distance - chunks.get(i).getDistance()) / 1000, mean2[0]));
                pedalingRate.getData().add(new XYChart.Data<>((distance - chunks.get(i).getDistance()) / 1000, mean2[1]));
                heartRate.getData().add(new XYChart.Data<>(distance / 1000, chunks.get(i).getAvgHeartRate()));
                mean1 = mean2;
                mean2 = mean3;
                mean3[0] = 0;
                mean3[1] = 0;
            }
        }
        lineChart.getData().clear();
        lineChart.getData().add(speed);
        lineChart.getData().add(pedalingRate);
        lineChart.getData().add(heartRate);
    }

}
