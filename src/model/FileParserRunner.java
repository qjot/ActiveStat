/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.util.Callback;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author kaqq.pl
 */
public class FileParserRunner extends Thread {

    //List<FileParser> threads;
    // List<TrackData> trackDataList;
    ObservableList<Calendar> highlightedCalendars;
    private final List<File> trackDataFileList;
    private final ReadOnlyObjectWrapper<ObservableList<TrackData>> partialResults
            = new ReadOnlyObjectWrapper<>(this, "partialResults",
                    FXCollections.observableArrayList(new ArrayList()));

    public FileParserRunner(List<File> trackDataFileList, ObservableList<Calendar> highlightedCalendars) {
        this.trackDataFileList = trackDataFileList;
        this.highlightedCalendars = highlightedCalendars;
    }

    public final ObservableList<TrackData> getPartialResults() {
        return (ObservableList<TrackData>) partialResults;
    }

    public final ReadOnlyObjectProperty<ObservableList<TrackData>> partialResultsProperty() {
        return partialResults.getReadOnlyProperty();
    }

    @Override
    public void run() {
        int iterations = 0;
        int max = trackDataFileList.size();
         highlightedCalendars.clear();
        for (File track : trackDataFileList) {
            try {
                iterations++;

                //System.out.println("parsing: " + track.getName());
                JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(track);
                GpxType gpx = (GpxType) root.getValue();

//                    cal.
                if (gpx != null) {
                    TrackData dat = new TrackData(new Track(gpx.getTrk().get(0)));
                    LocalDateTime localdate = dat.getStartTime();
                    Date d = Date.from(localdate.toInstant(ZoneOffset.UTC));
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(d);
                    Platform.runLater(() -> {
                        partialResults.get().add(dat);
                        highlightedCalendars.add(cal);
                    });
//                    // initializeCharts(trackData);
                    //  label.setText("GPX successfully loaded");
                } else {
                    // label.setText("Error loading GPX from " + file.getName());
                }
            } catch (JAXBException ex) {
                System.out.println("Unable to load file: " + track.getName());
                //Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // updateProgress(iterations, 100);

    }
}
