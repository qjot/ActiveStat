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
    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private CalendarPicker calendarView;
    @FXML private GridPane statsGrid;
    @FXML private ScrollBar scrollBar;
    @FXML private VBox vBoxMain;
    @FXML private ProgressBar progressBarLoad;
    
    FileParserRunner runningFileLoader;
    private final ObservableList<TrackData> trackDatabase= observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scrollBar.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            vBoxMain.setLayoutY(-new_val.doubleValue());
        });

        calendarView.calendarProperty().addListener((ObservableValue<? extends Calendar> ov, Calendar old_val, Calendar new_val) -> {
            if (calendarView.highlightedCalendars().contains(new_val)) {
                int dateId = calendarView.highlightedCalendars().indexOf(new_val);
                changeWiew(trackDatabase.get(dateId));
            }
        });
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
         
        xAxis.setLabel("Ranges");
        yAxis.setLabel("Frequencies");
        XYChart.Series<String, Number> seriesLine = new XYChart.Series();
        XYChart.Series<String, Number> seriesAreaHD = new XYChart.Series();
        ObservableList<Chunk> chunks = trackData.getChunks();
        System.out.println( chunks.size());
        System.out.println(chunks.get(1).getFirstPoint().elevationProperty());
       double distance=0;
        for (int i=0;i<=chunks.size();i+=chunks.size()/100) {
            distance+=chunks.get(i).getDistance();
            seriesLine.getData().add(new XYChart.Data<>(String.valueOf(round(distance)), round(chunks.get(i).getSpeed())));
        }
       
        lineChart.getData().add(seriesLine);
        //.getData().add(ChartsData.GetStringNumberSerie());

//        text.setText("Start time: " + DateTimeUtils.format(trackData.getStartTime()));
//        text.appendText("\nEnd time: " + DateTimeUtils.format(trackData.getEndTime()));
//        text.appendText(String.format("\nTotal Distance: %.0f m", trackData.getTotalDistance()));
//        text.appendText("\nDuration: " + DateTimeUtils.format(trackData.getTotalDuration()));
//        text.appendText("\nMoving time: " + DateTimeUtils.format(trackData.getMovingTime()));
//        text.appendText(String.format("\nAverage Speed: %.2f m/s", trackData.getAverageSpeed()));
//        text.appendText(String.format("\nAverage Cadence: %d", trackData.getAverageCadence()));
//        text.appendText(String.format("\nAverage Heartrate: %d bpm", trackData.getAverageHeartrate()));
//        text.appendText(String.format("\nTotal ascent: %.2f m", trackData.getTotalAscent()));
//        text.appendText(String.format("\nTotal descend: %.2f m", trackData.getTotalDescend()));
//
//        text.appendText("\nTrack containing " + chunks.size() + " points");
    }

}
