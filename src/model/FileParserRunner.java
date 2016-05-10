/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;

/**
 *
 * @author kaqq.pl
 */
public class FileParserRunner extends Thread {

    List<FileParser> threads;
    List<TrackData> trackDataList;
    List<File> trackDataFileList;

    public FileParserRunner(List<TrackData> trackDataList, List<File> trackDataFileList) {

        this.trackDataList = trackDataList;
        this.trackDataFileList = trackDataFileList;
        threads = new ArrayList<>();
//        for (File track : trackDataFileList) {
//
//            FileParser t=new FileParser(track);
//            threads.add(t);
//            System.out.println("Added: "+track.getName());
//        }
    }

    @Override
    public void run() {
        for (File track : trackDataFileList) {
            try {

                System.out.println("parsing: " + track.getName());

                JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(track);
                GpxType gpx = (GpxType) root.getValue();
                
                if (gpx != null) {
                  TrackData dat=  new TrackData(new Track(gpx.getTrk().get(0)));
                    Platform.runLater(() -> {
                        trackDataList.add(dat);
                        
                        // initializeCharts(trackData);
                        //  label.setText("GPX successfully loaded");
                        //                trackDataList.add(new TrackData(new Track(gpx.getTrk().get(0))));
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
                        //  }
                    });
                    // initializeCharts(trackData);
                    //  label.setText("GPX successfully loaded");

                } else {
                    // label.setText("Error loading GPX from " + file.getName());
                }
            } catch (JAXBException ex) {
                System.out.println("Unable to load file: " + track.getName());
                //Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
            } 

        }
    }
}
