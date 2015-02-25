import com.squareup.okhttp.ResponseBody;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Yakiv M. on 09.02.2015.
 */
public class XmlConverter<S> extends ConvertMethod<S> {

    private final XmlHandler<S> mHandler;

    public XmlConverter(@NonNull XmlHandler<S> handler) {
        mHandler = handler;
    }

    @Override
    @Nullable
    protected S convertResult(@NonNull ResponseBody body) {

        final SAXParserFactory spf = SAXParserFactory.newInstance();
        final SAXParser saxParser;
        try {
            saxParser = spf.newSAXParser();

            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(mHandler);

            xmlReader.parse(new InputSource(body.byteStream()));
            return mHandler.getResult();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            L.e(e.getMessage());
            return null;
        }
    }
}
