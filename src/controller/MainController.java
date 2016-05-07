/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.stage.FileChooser;
import javax.swing.JFileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;
import jgpx.model.jaxb.TrkType;
import jgpx.util.DateTimeUtils;
import model.CurrentTrackData;
import docs.com.calendarfx.model.Calendar;

/**
 *
 * @author qjot
 */
public class MainController implements Initializable {

    @FXML
    private AnchorPane summaryPane;
    @FXML
    private LineChart<String, Number> hightDistanceLine;
    @FXML
    private AreaChart<String, Number> areaChartHD;
    @FXML
    private Button loadButton;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    private CurrentTrackData tracksData;
    private TrackData trackData;

    @FXML
    private void load(ActionEvent event) throws JAXBException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
        if (file == null) {
            return;
        }
        //label.setText("Loading " + file.getName());
        JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
        GpxType gpx = (GpxType) root.getValue();

        if (gpx != null) {
            trackData = new TrackData(new Track(gpx.getTrk().get(0)));
            initializeCharts(trackData);
            //  label.setText("GPX successfully loaded");
        } else {
            // label.setText("Error loading GPX from " + file.getName());
        }
    }

    private void initializeCharts(TrackData trackData) {

        CalendarView calendar = new CalendarView();
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

    }

}
