package nl.next2know.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import nl.next2know.parsing.*;
import org.w3c.dom.Document;

/**
 * Created by ruben on 3/4/16.
 */
public class TikaDocumentParserTest
{
    @Test
    public void testParseWordDoc() {

        String testFilePath1 = "src/test/_files/parsing/doc-1.doc";
        String testFilePath2 = "src/test/_files/parsing/doc-2.doc";
        try {
            DocumentParser docParser = new TikaDocumentParser();

            // Test .doc 1
            InputStream inputFile1 = new FileInputStream(testFilePath1);
            ParserResult result1 = docParser.parse(inputFile1);

            String expectedMetaPages1 = "2";
            String actualMetaPages1 = result1.getMetaValue("Page-Count");
            assertEquals(expectedMetaPages1, actualMetaPages1);

            CharSequence expectedContains1 = "Alle nieuwe leden van onze groep";
            assertTrue(result1.getBodyContent().contains(expectedContains1));

            // Test .doc 2
            InputStream inputFile2 = new FileInputStream(testFilePath2);

            ParserResult result2 = docParser.parse(inputFile2);

            String expectedMetaPages2 = "25";
            String actualMetaPages2 = result2.getMetaValue("Page-Count");
            assertEquals(expectedMetaPages2, actualMetaPages2);

            CharSequence expectedContains2 = "Vrede en democratie zijn de hoogste waarden in onze westerse beschaving en geven uiting";
            assertTrue(result2.getBodyContent().contains(expectedContains2));
        }
        catch (FileNotFoundException e) {
            fail("Unable to find test file: " + e.getMessage());
        }
        catch (ParserException e) {
            fail("TikaDocumentParser.parse threw exception: " + e.getMessage());
        }
    }

    @Test
    public void testParseWordDocx() {
        String testFilePath1 = "src/test/_files/parsing/docx-1.docx";
        String testFilePath2 = "src/test/_files/parsing/docx-2.docx";
        try {
            DocumentParser docParser = new TikaDocumentParser();

            // Test .docx 1
            InputStream inputFile1 = new FileInputStream(testFilePath1);
            ParserResult result1 = docParser.parse(inputFile1);

            String expectedMetaPages1 = "1";
            String actualMetaPages1 = result1.getMetaValue("Page-Count");
            assertEquals(expectedMetaPages1, actualMetaPages1);

            CharSequence expectedContains1 = "Kort gezegd houdt dit in dat de bestuursrechter op het moment van de zitting";
            assertTrue(result1.getBodyContent().contains(expectedContains1));

            // Test .docx 2
            InputStream inputFile2 = new FileInputStream(testFilePath2);

            ParserResult result2 = docParser.parse(inputFile2);

            String expectedMetaPages2 = "8";
            String actualMetaPages2 = result2.getMetaValue("Page-Count");
            assertEquals(expectedMetaPages2, actualMetaPages2);

            CharSequence expectedContains2 = "randvoorwaarden om gezond en veilig te leven. Echter, het huidige milieubeleid richt";
            assertTrue(result2.getBodyContent().contains(expectedContains2));
        }
        catch (FileNotFoundException e) {
            fail("Unable to find test file: " + e.getMessage());
        }
        catch (ParserException e) {
            fail("TikaDocumentParser.parse threw exception: " + e.getMessage());
        }
    }

    @Test
    public void testParsePpt() {
        String testFilePath1 = "src/test/_files/parsing/ppt-1.ppt";
        String testFilePath2 = "src/test/_files/parsing/ppt-2.ppt";
        try {
            DocumentParser docParser = new TikaDocumentParser();

            // Test .ppt 1
            InputStream inputFile1 = new FileInputStream(testFilePath1);
            ParserResult result1 = docParser.parse(inputFile1);

            String expectedMetaPages1 = "47";
            String actualMetaPages1 = result1.getMetaValue("Slide-Count");
            assertEquals(expectedMetaPages1, actualMetaPages1);

            CharSequence expectedContains1 = "Snel inzicht krijgen in de gewenste";
            assertTrue(result1.getBodyContent().contains(expectedContains1));

            // Test .ppt 2
            InputStream inputFile2 = new FileInputStream(testFilePath2);

            ParserResult result2 = docParser.parse(inputFile2);

            String expectedMetaPages2 = "8";
            String actualMetaPages2 = result2.getMetaValue("Slide-Count");
            assertEquals(expectedMetaPages2, actualMetaPages2);

            CharSequence expectedContains2 = "Over het fout in de computer staan";
            assertTrue(result2.getBodyContent().contains(expectedContains2));
        }
        catch (FileNotFoundException e) {
            fail("Unable to find test file: " + e.getMessage());
        }
        catch (ParserException e) {
            fail("TikaDocumentParser.parse threw exception: " + e.getMessage());
        }

    }

    @Test
    public void testParsePdf() {
        String testFilePath1 = "src/test/_files/parsing/pdf-1.pdf";
        String testFilePath2 = "src/test/_files/parsing/pdf-2.pdf";
        try {
            DocumentParser docParser = new TikaDocumentParser();

            // Test .pdf 1
            InputStream inputFile1 = new FileInputStream(testFilePath1);
            ParserResult result1 = docParser.parse(inputFile1);

            String expectedMetaTitle1 = "http://www.communicatieplein.nl/Actueel/Nieuwsbrieven/Communica";
            String actualMetaTitle1 = result1.getMetaValue("title");
            assertEquals(expectedMetaTitle1, actualMetaTitle1);

            CharSequence expectedContains1 = "Erg succesvol was de opzet van de Communicatiepool, een pool van veertig externe";
            assertTrue(result1.getBodyContent().contains(expectedContains1));

            // Test .pdf 2
            InputStream inputFile2 = new FileInputStream(testFilePath2);

            ParserResult result2 = docParser.parse(inputFile2);

            String expectedMetaTitle2 = "VOORBLAD";
            String actualMetaTitle2 = result2.getMetaValue("title");
            assertEquals(expectedMetaTitle2, actualMetaTitle2);

            CharSequence expectedContains2 = "Daarom wil ik bij dezen iedereen bedanken die heeft bijdragen aan de totstandkoming van dit";
            assertTrue(result2.getBodyContent().contains(expectedContains2));
        }
        catch (FileNotFoundException e) {
            fail("Unable to find test file: " + e.getMessage());
        }
        catch (ParserException e) {
            fail("TikaDocumentParser.parse threw exception: " + e.getMessage());
        }
    }

    @Test
    public void testParseGif() {
        String testFilePath1 = "src/test/_files/parsing/gif-1.gif";
        String testFilePath2 = "src/test/_files/parsing/gif-2.gif";
        try {
            DocumentParser docParser = new TikaDocumentParser();

            // Test .gif 1
            InputStream inputFile1 = new FileInputStream(testFilePath1);
            ParserResult result1 = docParser.parse(inputFile1);

            String expectedMetaWidth1 = "659";
            String actualMetaWidth1 = result1.getMetaValue("width");
            assertEquals(expectedMetaWidth1, actualMetaWidth1);

            String expectedMetaHeight1 = "406";
            String actualMetaHeight1 = result1.getMetaValue("height");
            assertEquals(expectedMetaHeight1, actualMetaHeight1);

            // Test .gif 2
            InputStream inputFile2 = new FileInputStream(testFilePath2);
            ParserResult result2 = docParser.parse(inputFile2);

            String expectedMetaWidth2 = "261";
            String actualMetaWidth2 = result2.getMetaValue("width");
            assertEquals(expectedMetaWidth2, actualMetaWidth2);

            String expectedMetaHeight2 = "203";
            String actualMetaHeight2 = result2.getMetaValue("height");
            assertEquals(expectedMetaHeight2, actualMetaHeight2);        }
        catch (FileNotFoundException e) {
            fail("Unable to find test file: " + e.getMessage());
        }
        catch (ParserException e) {
            fail("TikaDocumentParser.parse threw exception: " + e.getMessage());
        }
    }

    @Test
    public void testParseJpg() {
        String testFilePath1 = "src/test/_files/parsing/jpg-1.jpg";
        String testFilePath2 = "src/test/_files/parsing/jpg-2.jpg";
        try {
            DocumentParser docParser = new TikaDocumentParser();

            // Test .jpg 1
            InputStream inputFile1 = new FileInputStream(testFilePath1);
            ParserResult result1 = docParser.parse(inputFile1);

            String expectedMetaWidth1 = "450 pixels";
            String actualMetaWidth1 = result1.getMetaValue("Image Width");
            assertEquals(expectedMetaWidth1, actualMetaWidth1);

            String expectedMetaHeight1 = "338 pixels";
            String actualMetaHeight1 = result1.getMetaValue("Image Height");
            assertEquals(expectedMetaHeight1, actualMetaHeight1);

            // Test .jpg 2
            InputStream inputFile2 = new FileInputStream(testFilePath2);
            ParserResult result2 = docParser.parse(inputFile2);

            String expectedMetaWidth2 = "500 pixels";
            String actualMetaWidth2 = result2.getMetaValue("Image Width");
            assertEquals(expectedMetaWidth2, actualMetaWidth2);

            String expectedMetaHeight2 = "332 pixels";
            String actualMetaHeight2 = result2.getMetaValue("Image Height");
            assertEquals(expectedMetaHeight2, actualMetaHeight2);        }
        catch (FileNotFoundException e) {
            fail("Unable to find test file: " + e.getMessage());
        }
        catch (ParserException e) {
            fail("TikaDocumentParser.parse threw exception: " + e.getMessage());
        }
    }

    @Test
    public void testParsePng() {
        String testFilePath1 = "src/test/_files/parsing/png-1.png";
        String testFilePath2 = "src/test/_files/parsing/png-2.png";
        try {
            DocumentParser docParser = new TikaDocumentParser();

            // Test .jpg 1
            InputStream inputFile1 = new FileInputStream(testFilePath1);
            ParserResult result1 = docParser.parse(inputFile1);

            String expectedMetaWidth1 = "661";
            String actualMetaWidth1 = result1.getMetaValue("width");
            assertEquals(expectedMetaWidth1, actualMetaWidth1);

            String expectedMetaHeight1 = "465";
            String actualMetaHeight1 = result1.getMetaValue("height");
            assertEquals(expectedMetaHeight1, actualMetaHeight1);

            // Test .jpg 2
            InputStream inputFile2 = new FileInputStream(testFilePath2);
            ParserResult result2 = docParser.parse(inputFile2);

            String expectedMetaWidth2 = "372";
            String actualMetaWidth2 = result2.getMetaValue("width");
            assertEquals(expectedMetaWidth2, actualMetaWidth2);

            String expectedMetaHeight2 = "123";
            String actualMetaHeight2 = result2.getMetaValue("height");
            assertEquals(expectedMetaHeight2, actualMetaHeight2);        }
        catch (FileNotFoundException e) {
            fail("Unable to find test file: " + e.getMessage());
        }
        catch (ParserException e) {
            fail("TikaDocumentParser.parse threw exception: " + e.getMessage());
        }
    }
}
