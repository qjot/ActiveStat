/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import static java.lang.Math.round;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ObjectProperty;
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
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import jgpx.model.gpx.Track;

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
    //@FXML private NumberAxis xAxis;
    //@FXML private NumberAxis yAxis;
    @FXML private CalendarPicker calendarView;
    @FXML private GridPane statsGrid;
    @FXML private VBox vBoxMain;
    @FXML private ProgressBar progressBarLoad;
    @FXML private Button exitButton;
    @FXML private AnchorPane scrollableContent;
    @FXML private CheckBox speedBox;
    @FXML private CheckBox heartRateBox;
    @FXML private CheckBox pedalingRateBox;
    @FXML ScrollPane scrollPane;
    FileParserRunner runningFileLoader;

    XYChart.Series<String, Number> monthSpeed;
    XYChart.Series<String, Number> monthDistance;
    XYChart.Series<String, Number> monthTime;

    XYChart.Series<Number, Number> speed;
    XYChart.Series<Number, Number> heartRate;
    XYChart.Series<Number, Number> pedalingRate;
    XYChart.Series<Number, Number> height;
    private final ObservableList<TrackData> trackDatabase= observableArrayList();
    @FXML
    private ProgressIndicator chartProgress;
    @FXML
    private BarChart<String, Number> MonthSummary;
    

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
        lineChart.animatedProperty().set(false);
        MonthSummary.animatedProperty().set(false);
    }
    
    private void changeWiew(TrackData trackData) {
       scrollableContent.getScene().setCursor(Cursor.WAIT);

        ChangeText(trackData);
        ChangeCharts(trackData);
        CalculateMonth();
       scrollableContent.getScene().setCursor(Cursor.DEFAULT);
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
        speedBox.selectedProperty().set(true);
        heartRateBox.selectedProperty().set(false);
        pedalingRateBox.selectedProperty().set(false);
        //xAxis.setLabel("Distance");
        //yAxis.setLabel("Frequencies");
        ObservableList<Chunk> chunks = trackData.getChunks();
        double distance = 0;
        speed = new XYChart.Series();
        heartRate = new XYChart.Series();
        pedalingRate = new XYChart.Series();
        height = new XYChart.Series<>();
        speed.setName("Speed");
        heartRate.setName("Heart rate");
        pedalingRate.setName("Pedaling rate");
        height.setName("height");
        double[] mean1 = new double[2];
        double[] mean2 = new double[2];
        double[] mean3 = new double[2];
        double currentHeight = chunks.get(0).getAscent();
        //some creppy logic
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
        //lineChart.getData().add(pedalingRate);
        //lineChart.getData().add(heartRate);
    }

    private void CalculateMonth(){
        int Month = calendarView.calendarProperty().get().getTime().getMonth()+1;
        //System.out.println(Month);
        monthSpeed = new XYChart.Series();
        monthSpeed.setName("Average speed");
        monthDistance = new XYChart.Series();
        monthDistance.setName("Total Distance");
        monthTime = new XYChart.Series();
        monthTime.setName("Total Time");
        
        for (TrackData t : trackDatabase) {
            if (t.getStartTime().getMonth().getValue() == Month) {
                monthSpeed.getData().add(new XYChart.Data<>(String.valueOf(t.getStartTime().getDayOfMonth()), t.getAverageSpeed()));
                monthDistance.getData().add(new XYChart.Data<>(String.valueOf(t.getStartTime().getDayOfMonth()), t.getTotalDistance()));
                monthTime.getData().add(new XYChart.Data<>(String.valueOf(t.getStartTime().getDayOfMonth()), t.getTotalDuration().toMinutes()));
            }
        }
        
        
        MonthSummary.getData().clear();
        MonthSummary.getData().add(monthSpeed);
        MonthSummary.getData().add(monthDistance);
        MonthSummary.getData().add(monthTime);
        
    }
    @FXML
    private void SpeedChartData(ActionEvent event) {
        if (lineChart.getData().contains(speed)) {
            lineChart.getData().remove(speed);
        } else {
            lineChart.getData().add(speed);
        }
    }
    @FXML
    private void HeartChartData(ActionEvent event) {
        if (lineChart.getData().contains(heartRate)) {
            lineChart.getData().remove(heartRate);
        } else {
            lineChart.getData().add(heartRate);
        }
    }
    @FXML
    private void PedalingChartData(ActionEvent event) {

        if (lineChart.getData().contains(pedalingRate)) {
            lineChart.getData().remove(pedalingRate);
        } else {
            lineChart.getData().add(pedalingRate);
            
        }
    }

    @FXML
    private void ClearCalendar(ActionEvent event) {
        trackDatabase.clear();
        calendarView.highlightedCalendars().clear();
    }

}
