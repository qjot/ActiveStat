/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
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

    private final ObservableList<Calendar> runningDates;
    private final List<File> trackDataFileList;
    private final DoubleProperty loadingProgress;
    private final ObjectProperty<Calendar> selectedDate;
    private final ObservableList<TrackData> trackDatabase;
//    private final ReadOnlyObjectWrapper<ObservableList<TrackData>> partialResults
//            = new ReadOnlyObjectWrapper<>(this, "partialResults",
//                    FXCollections.observableArrayList(new ArrayList()));

    public FileParserRunner(ObservableList<TrackData> trackDataFiles,
            List<File> trackDataFileList,
            ObservableList<Calendar> runningDates,
            ObjectProperty<Calendar> CalendarSelectedDate
    ) {
        this.trackDataFileList = trackDataFileList;
        this.runningDates = runningDates;
        this.selectedDate = CalendarSelectedDate;
        loadingProgress = new SimpleDoubleProperty(0.01F);
        this.trackDatabase = trackDataFiles;
    }

    public final ReadOnlyObjectProperty<ObservableList<TrackData>> partialResultsProperty() {
        return (ReadOnlyObjectProperty<ObservableList<TrackData>>) trackDatabase;
    }

    public final ReadOnlyDoubleProperty ProgressProperty() {
        return loadingProgress;
    }

    @Override
    public void run() {
        int workDone = 0;
        int totalWork = trackDataFileList.size();
        boolean firstDateSetted = false;
        for (File track : trackDataFileList) {
            try {
                workDone++;
                JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(track);
                GpxType gpx = (GpxType) root.getValue();

                if (gpx != null) {
                    loadingProgress.set((float) workDone / totalWork);
                    TrackData trackToLoad = new TrackData(new Track(gpx.getTrk().get(0)));
                    LocalDateTime tempDate = trackToLoad.getStartTime();
                    Calendar currentRunningDate = new GregorianCalendar(tempDate.getYear(),
                            tempDate.getMonthValue()-1,
                            tempDate.getDayOfMonth());
                    Platform.runLater(() -> {
                        if (!trackDatabase.contains(trackToLoad)) {
                            trackDatabase.add(trackToLoad);
                            runningDates.add(currentRunningDate);
                        }
                    });

                    if (!firstDateSetted) {
                        Platform.runLater(() -> {
                            selectedDate.set(currentRunningDate);
                        });
                        firstDateSetted = true;
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
