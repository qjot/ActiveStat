import java.io.File;
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
public class FileParser extends Thread {

  private final File file;
  private TrackData data;

    public FileParser(File track) {
       this.file=track;
    }

  @Override
  public void run()  {
      try {
          JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
          Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
          JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
          GpxType gpx = (GpxType) root.getValue();
          if (gpx != null) {
              // initializeCharts(trackData);
              //  label.setText("GPX successfully loaded");
              Platform.runLater(new Runnable() {
            @Override public void run() {
                data=new TrackData(new Track(gpx.getTrk().get(0)));
            }
        });
          } else {
              // label.setText("Error loading GPX from " + file.getName());
          }
      } catch (JAXBException ex) {
          System.out.println("Unable to load file: "+file.getName());
          //Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
  public TrackData GetTrackData(){
      return data;
  }
 
}