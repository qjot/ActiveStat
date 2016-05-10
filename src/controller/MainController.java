/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javax.swing.JFileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jfxtras.scene.control.CalendarPicker;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;
import jgpx.model.jaxb.TrkType;
import jgpx.util.DateTimeUtils;
import model.FileParserRunner;

/**
 *
 * @author qjot
 */
public class MainController implements Initializable {

    @FXML
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

    @FXML
    private LineChart<String, Number> hightDistanceLine;
    @FXML
    private Button loadButton;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    private List<TrackData> trackDataList = new ArrayList();
    @FXML
    private CalendarPicker calendarView;
    @FXML
    private GridPane statsGrid;

    @FXML
    private void load(ActionEvent event) throws InterruptedException {
        FileChooser fileChooser = new FileChooser();
        List<File> trackDataFileList = fileChooser.showOpenMultipleDialog(loadButton.getScene().getWindow());

        FileParserRunner load = new FileParserRunner(trackDataList, trackDataFileList);
        load.start();
    }

    private void initializeCharts(TrackData trackData) {

        xAxis.setLabel("Ranges");
        yAxis.setLabel("Frequencies");
        XYChart.Series<String, Number> seriesLine = new XYChart.Series();
        XYChart.Series<String, Number> seriesAreaHD = new XYChart.Series();
        ObservableList<Chunk> chunks = trackData.getChunks();

        for (Chunk point : chunks) {
            seriesLine.getData().add(new XYChart.Data<>(String.valueOf(point.getDistance()), point.getSpeed()));
        }
        hightDistanceLine.getData().add(seriesLine);
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal2 = new GregorianCalendar(2016, 05, 15);
        Date dateparsed;
        String dateInString = "20160511";
        try {
            dateparsed = sdf.parse(dateInString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateparsed);
            calendarView.highlightedCalendars().add(cal);
        } catch (ParseException ex) {
            System.out.println("nie rozpoznano daty");
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(calendarView.highlightedCalendars().toString());

    }

}
