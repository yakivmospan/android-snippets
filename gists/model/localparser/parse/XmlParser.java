import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Yakiv M. on 09.02.2015.
 */
public class XmlParser<R> implements ParseMethod<InputStream, R> {

    private final XmlHandler<R> mHandler;

    public XmlParser(@NonNull XmlHandler<R> handler) {
        mHandler = handler;
    }

    @Nullable
    @Override
    public R parse(@Nullable InputStream source) {
        if(source == null){
            return null;
        }

        final SAXParserFactory spf = SAXParserFactory.newInstance();
        final SAXParser saxParser;
        try {
            saxParser = spf.newSAXParser();

            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(mHandler);

            xmlReader.parse(new InputSource(source));
            return mHandler.getResult();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            L.e(e.getMessage());
            return null;
        }
    }
}
