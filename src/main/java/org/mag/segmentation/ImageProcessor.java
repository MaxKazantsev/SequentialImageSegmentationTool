package org.mag.segmentation;

import com.pixelmed.dicom.*;
import com.pixelmed.display.SourceImage;
import javafx.scene.control.TextArea;
import org.mag.model.Complexity;
import org.mag.model.Graph;

import java.io.IOException;

public class ImageProcessor {

    private SourceImage sourceImage;
    private String pathToDicom;
    private AttributeList dicomAttributeList;
    private AttributeList imageAttributeList;

    public ImageProcessor(String curPath, SourceImage curSrc) throws IOException, DicomException {
        this.pathToDicom = curPath;
        this.sourceImage = curSrc;

        this.dicomAttributeList = new AttributeList();
        this.imageAttributeList = new AttributeList();
        imageAttributeList.read(this.pathToDicom);
        dicomAttributeList.read(this.pathToDicom);
    }

    public void displayDicomTagsToConsole(TextArea logArea) throws Exception{

        logArea.appendText("\nDICOM TAGS:\n");
        logArea.appendText("\nFile MetaInformation Group Length:" + getDicomTagInformation(TagFromName.FileMetaInformationGroupLength));
        logArea.appendText("\nFile MetaInformation Version:" + getDicomTagInformation(TagFromName.FileMetaInformationVersion));
        logArea.appendText("\nMedia Storage SOP Class UID:" + getDicomTagInformation(TagFromName.MediaStorageSOPClassUID));
        logArea.appendText("\nMedia Storage SOP Instance UID:" + getDicomTagInformation(TagFromName.MediaStorageSOPInstanceUID));
        logArea.appendText("\nTransfer Syntax UID:" + getDicomTagInformation(TagFromName.TransferSyntaxUID));
        logArea.appendText("\nImplementation Class UID:" + getDicomTagInformation(TagFromName.ImplementationClassUID));
        logArea.appendText("\nImplementation Version Name:" + getDicomTagInformation(TagFromName.ImplementationVersionName));
        logArea.appendText("\nSource Application Entity Title:" + getDicomTagInformation(TagFromName.SourceApplicationEntityTitle));
        logArea.appendText("\nPrivate Information Creator UID:" + getDicomTagInformation(TagFromName.PrivateInformationCreatorUID));
        logArea.appendText("\nPrivate Information:" + getDicomTagInformation(TagFromName.PrivateInformation));

        //Data Object Elements
        logArea.appendText("\nStudy Instance UID:" + getDicomTagInformation(TagFromName.StudyInstanceUID));
        logArea.appendText("\nStudy Comments:" + getDicomTagInformation(TagFromName.StudyComments));
        logArea.appendText("\nStudy Status ID:" + getDicomTagInformation(TagFromName.StudyStatusID));
        logArea.appendText("\nStudy Description:" + getDicomTagInformation(TagFromName.StudyDescription));
        logArea.appendText("\nSeries Instance UID:" + getDicomTagInformation(TagFromName.SeriesInstanceUID));
        logArea.appendText("\nSeries Date:" + getDicomTagInformation(TagFromName.SeriesDate));
        logArea.appendText("\nSeries Description:" + getDicomTagInformation(TagFromName.SeriesDescription));
        logArea.appendText("\nImage Comments:" + getDicomTagInformation(TagFromName.ImageComments));
        logArea.appendText("\nSOP Class UID:" + getDicomTagInformation(TagFromName.SOPClassUID));
        logArea.appendText("\nSOP Instance UID:" + getDicomTagInformation(TagFromName.SOPInstanceUID));
    }

    public void displayImageTagsToConsole(TextArea logArea) throws Exception{
        logArea.appendText("\n\nIMAGE TAGS:\n");

        logArea.appendText("\nTransfer Syntax:" + getImageTagInformation(TagFromName.TransferSyntaxUID));
        logArea.appendText("\nSOP Class:" + getImageTagInformation(TagFromName.SOPClassUID));
        logArea.appendText("\nModality:" + getImageTagInformation(TagFromName.Modality));
        logArea.appendText("\nSamples Per Pixel:" + getImageTagInformation(TagFromName.SamplesPerPixel));
        logArea.appendText("\nPhotometric Interpretation:" + getImageTagInformation(TagFromName.PhotometricInterpretation));
        logArea.appendText("\nPixel Spacing:" + getImageTagInformation(TagFromName.PixelSpacing));
        logArea.appendText("\nBits Allocated:" + getImageTagInformation(TagFromName.BitsAllocated));
        logArea.appendText("\nBits Stored:" + getImageTagInformation(TagFromName.BitsStored));
        logArea.appendText("\nHigh Bit:" + getImageTagInformation(TagFromName.HighBit));

        SourceImage img = new SourceImage(imageAttributeList);

        logArea.appendText("\nNumber of frames " + img.getNumberOfFrames());
        logArea.appendText("\nWidth " + img.getWidth());//all frames will have same width
        logArea.appendText("\nHeight " + img.getHeight());//all frames will have same height
        logArea.appendText("\nIs Grayscale? " + img.isGrayscale());
        logArea.appendText("\nPixel Data present:" + (imageAttributeList.get(TagFromName.PixelData) != null));

        //get the 16 bit pixel data values

        /*OtherWordAttribute pixelAttribute = (OtherWordAttribute)(imageAttributeList.get(TagFromName.PixelData));
        short[] data = pixelAttribute.getShortValues();
        System.out.println("Pixels from attribute:");
        for(int i = 0; i < data.length; i += 1000) {
            System.out.print(data[i] + "   ");
        }*/
    }

    public SourceImage imageGraphSegmentation(int k) throws DicomException {

        Graph imageGraph = new Graph(this.sourceImage, Complexity.EIGHT_CONNECTED, k);
        imageGraph.segmentGraph();
        handleDicomAttributes(imageGraph.getNewPixelValues());
        return new SourceImage(imageAttributeList);
    }

    private String getImageTagInformation(AttributeTag attrTag) {
        return Attribute.getDelimitedStringValuesOrEmptyString(imageAttributeList, attrTag);
    }

    private String getDicomTagInformation(AttributeTag attrTag) {
        return Attribute.getDelimitedStringValuesOrEmptyString(dicomAttributeList, attrTag);
    }

    private void handleDicomAttributes(short[] newImagePixels) throws DicomException {
        Attribute pixelData = new OtherWordAttribute(TagFromName.PixelData);
        pixelData.setValues(newImagePixels);
        imageAttributeList.remove(TagFromName.PixelData);
        imageAttributeList.put(pixelData);
    }
}
