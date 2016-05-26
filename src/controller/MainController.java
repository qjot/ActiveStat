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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
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
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import jgpx.model.gpx.Bounds;

/**
 *
 * @author qjot
 */
public class MainController implements Initializable {

    private Label dateLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label exerciseTimeLabel;
    @FXML
    private Label distanceLabel;
    @FXML
    private Label slopeLabel;
    @FXML
    private Label avgSpeedLabel;
    @FXML
    private Label maxSpeedLabel;
    @FXML
    private Label maxHeartRate;
    @FXML
    private Label minHeartRate;
    @FXML
    private Label avgHeartRate;
    @FXML
    private Label maxPedalingRateLabel;
    @FXML
    private Label avgPedalingRateLabel;
    @FXML private LineChart<Number, Number> lineChart;
    //@FXML private NumberAxis xAxis;
    //@FXML private NumberAxis yAxis;
    @FXML private CalendarPicker calendarView;
    @FXML private VBox vBoxMain;
    @FXML private ProgressBar progressBarLoad;
    @FXML private Button exitButton;
    private AnchorPane scrollableContent;
    @FXML private CheckBox speedBox;
    
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
    private TrackData currentTrack;
    @FXML
    private BarChart<String, Number> MonthSummary;
    @FXML
    private GridPane statsGrid1;
    @FXML
    private CheckBox heartBox;
    @FXML
    private CheckBox pedalingBox;
    @FXML
    private AreaChart<Number, Number> hightAreaChart;
    @FXML
    private Slider maxHR;
    @FXML
    private PieChart pieChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    double[] zone;
    @FXML
    private CheckBox speedBoxMonth;
    @FXML
    private CheckBox heartBoxMonth;
    @FXML
    private CheckBox pedalingBoxMonth;

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
        
        //exitButton.disableProperty().set(true);
        double[] zone = new double[5];;
        maxHR.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                if (currentTrack != null) {
                    int max = (int) maxHR.getValue();
                    for (double d : zone) {
                        d = 0;
                    }
                    for (Chunk t : currentTrack.getChunks()) {
                        if (t.getAvgHeartRate() >= 0.9 * max) {
                            zone[0]++;
                            continue;
                        }
                        if (t.getAvgHeartRate() >= 0.8 * max) {
                            zone[1]++;
                            continue;
                        }
                        if (t.getAvgHeartRate() >= 0.7 * max) {
                            zone[2]++;
                            continue;
                        }
                        if (t.getAvgHeartRate() >= 0.6 * max) {
                            zone[3]++;
                        } else {
                            zone[4]++;
                        }
                    }

                    for (double d : zone) {
                        d = d / currentTrack.getChunks().size();
                    }

                    ObservableList<PieChart.Data> pieChartData
                            = FXCollections.observableArrayList(
                                    new PieChart.Data("Z1 Recovery", zone[4]),
                                    new PieChart.Data("Z2 Endurance", zone[3]),
                                    new PieChart.Data("Z3 Tempo", zone[2]),
                                    new PieChart.Data("Z4 Threshold", zone[1]),
                                    new PieChart.Data("Z5 Anaerobic ", zone[0]));
                    pieChart.getData().clear();
                    pieChart.setData(pieChartData);
                    
                }
                
            }
        });
    }
    @FXML private void closeApplication(MouseEvent event)
    {
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit");
                //alert.setHeaderText("You have not any optional parts!");
                alert.setContentText("You want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {                    
               // SaveConfiguration(configurationName);
                }
              Platform.exit();           

        
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
        currentTrack=trackData;
        ChangeText(trackData);
        ChangeCharts(trackData);
        
        CalculateMonth();
       exitButton.getScene().setCursor(Cursor.DEFAULT);
    }

    private void ChangeText(TrackData trackData) {
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
        heartBox.selectedProperty().set(false);
        pedalingBox.selectedProperty().set(false);
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
        hightAreaChart.getData().clear();
        hightAreaChart.getData().add(height);
    }

    private void CalculateMonth(){
        speedBoxMonth.selectedProperty().set(true);
        heartBoxMonth.selectedProperty().set(false);
        pedalingBoxMonth.selectedProperty().set(false);
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
        //MonthSummary.getData().add(monthDistance);
        //MonthSummary.getData().add(monthTime);
        
    }
    @FXML
    private void SpeedChartData(ActionEvent event) {
        if (lineChart.getData().contains(speed)) {
            lineChart.getData().remove(speed);
        } else {
            lineChart.getData().add(speed);
        }
    }
    @FXML private void HeartChartData(ActionEvent event) {
        if (lineChart.getData().contains(heartRate)) {
            lineChart.getData().remove(heartRate);
        } else {
            lineChart.getData().add(heartRate);
        }
    }
   @FXML private void PedalingChartData(ActionEvent event) {

        if (lineChart.getData().contains(pedalingRate)) {
            lineChart.getData().remove(pedalingRate);
        } else {
            lineChart.getData().add(pedalingRate);
            
        }

    }
  @FXML   private void MonthSpeedData(ActionEvent event) {

        if (MonthSummary.getData().contains(monthSpeed)) {
            MonthSummary.getData().remove(monthSpeed);
        } else {
            MonthSummary.getData().add(monthSpeed);
            
        }

    }
 @FXML private void MonthDistanceData(ActionEvent event) {

        if (MonthSummary.getData().contains(monthDistance)) {
            MonthSummary.getData().remove(monthDistance);
        } else {
            MonthSummary.getData().add(monthDistance);
            
        }

    }
   @FXML private void MonthTimeData(ActionEvent event) {

        if (MonthSummary.getData().contains(monthTime)) {
            MonthSummary.getData().remove(monthTime);
        } else {
            MonthSummary.getData().add(monthTime);
            
        }

    }

    @FXML
    private void ClearCalendar(ActionEvent event) {
        trackDatabase.clear();
        calendarView.highlightedCalendars().clear();
    }
    //int[] z1,z2,z3,z4,z5=0;
     //double[] zone = new double[5];
  
}

