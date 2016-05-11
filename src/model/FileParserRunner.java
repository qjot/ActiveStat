/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author kaqq.pl
 */
public class FileParserRunner extends Thread {

    //List<FileParser> threads;
    // List<TrackData> trackDataList;
    private final ObservableList<Calendar> highlightedCalendars;
    private final List<File> trackDataFileList;
    private final DoubleProperty progress;
    private final ObjectProperty<Calendar> CalendarSelectedDate;
    private final ReadOnlyObjectWrapper<ObservableList<TrackData>> partialResults
            = new ReadOnlyObjectWrapper<>(this, "partialResults",
                    FXCollections.observableArrayList(new ArrayList()));

    public FileParserRunner(List<File> trackDataFileList,
            ObservableList<Calendar> highlightedCalendars,
            ObjectProperty<Calendar> CalendarSelectedDate
    ) {
        this.trackDataFileList = trackDataFileList;
        this.highlightedCalendars = highlightedCalendars;
        this.CalendarSelectedDate = CalendarSelectedDate;
        progress = new SimpleDoubleProperty(0.01F);

    }

    public final ObservableList<TrackData> getPartialResults() {
        return (ObservableList<TrackData>) partialResults;
    }

    public final ReadOnlyObjectProperty<ObservableList<TrackData>> partialResultsProperty() {
        return partialResults.getReadOnlyProperty();
    }

    public final ReadOnlyDoubleProperty ProgressProperty() {
        return progress;
    }

    @Override
    public void run() {
        int iterations = 0;
        int max = trackDataFileList.size();
        highlightedCalendars.clear();
        System.out.println(CalendarSelectedDate);
        for (File track : trackDataFileList) {
            try {
                iterations++;
                JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(track);
                GpxType gpx = (GpxType) root.getValue();

                if (gpx != null) {
                    progress.set((float) iterations / max);
                    TrackData dat = new TrackData(new Track(gpx.getTrk().get(0)));
                    LocalDateTime localdate = dat.getStartTime();
                    Date d = Date.from(localdate.toInstant(ZoneOffset.UTC));
                    Calendar cal = new GregorianCalendar();
                    LocalDateTime tempDate = dat.getStartTime();
                    Calendar cal2 = new GregorianCalendar(tempDate.getYear(), tempDate.getMonthValue(), tempDate.getDayOfMonth());
                    cal.setTime(d);

                    Platform.runLater(() -> {
                        partialResults.get().add(dat);
                        highlightedCalendars.add(cal2);
                    });
                    if (iterations == 1) {
                        Platform.runLater(() -> {
                            CalendarSelectedDate.set(cal2);
                        });
                    }

                } else {
                    System.out.println("Error loading GPX from " + track.getName());
                }
            } catch (JAXBException ex) {
                System.out.println("Unable to load file: " + track.getName());
                //Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
