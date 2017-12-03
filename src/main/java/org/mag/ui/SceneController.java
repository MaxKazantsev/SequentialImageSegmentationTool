package org.mag.ui;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.display.SourceImage;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.io.File;

import org.mag.segmentation.ImageProcessor;

public class SceneController implements Initializable{

    @FXML
    private Button openButton;
    @FXML
    private Button infoButton;
    @FXML
    private Button segmentationButton;
    @FXML
    private TextArea logArea;
    @FXML
    private TextField kTextField;
    @FXML
    private ImageView imageView;
    @FXML
    private ScrollBar scrollBar;

    private FileChooser imageChooser = new FileChooser();

    private String dicomPath = null;
    private SourceImage sourceImage;
    private Image fxImage;
    private ImageProcessor imageProcessor;

    @FXML
    private void openButtonHandler(ActionEvent event) throws Exception {

        imageChooser.setInitialDirectory(new File("11121220"));
        List<File> dicoms = imageChooser.showOpenMultipleDialog(null);

        if(dicoms == null) {
            logArea.appendText("\nNo images were opened!");
            return;
        }

        dicomPath = dicoms.get(0).getAbsolutePath();
        sourceImage = new SourceImage(dicomPath);

        fxImage = SwingFXUtils.toFXImage(sourceImage.getBufferedImage(), null);

        imageView.setImage(fxImage);
        logArea.appendText("\nOpened images number:" + dicoms.size());

        scrollBar.setMin(0);
        scrollBar.setMax(dicoms.size() - 1);

        scrollBar.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {

            try {
                dicomPath = dicoms.get(new_val.intValue()).getAbsolutePath();
                sourceImage = new SourceImage(dicomPath);
                fxImage = SwingFXUtils.toFXImage(sourceImage.getBufferedImage(), null);
            }
            catch (IOException | DicomException e) { e.printStackTrace(); }

            imageView.setImage(fxImage);
        });
    }

    @FXML
    private void infoButtonHandler(ActionEvent event) throws Exception {
        if(dicomPath == null) {
            logArea.appendText("\nNo image to give info about!");
            return;
        }
        imageProcessor = new ImageProcessor(dicomPath, sourceImage);
        imageProcessor.displayDicomTagsToConsole(logArea);
        imageProcessor.displayImageTagsToConsole(logArea);
    }

    @FXML
    private void segmentationButtonHandler(ActionEvent event) throws DicomException, IOException {
        if(dicomPath == null) {
            logArea.appendText("\nNo image to segment!");
            return;
        }
        int k = Integer.valueOf(kTextField.getText());
        imageProcessor = new ImageProcessor(dicomPath, sourceImage);
        SourceImage segmentedImage = imageProcessor.imageGraphSegmentation(k);
        fxImage = SwingFXUtils.toFXImage(segmentedImage.getBufferedImage(), null);
        imageView.setImage(fxImage);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        kTextField.setText("300");
    }
}
