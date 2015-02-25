import org.xml.sax.ContentHandler;

public interface XmlHandler<ResultType> extends ContentHandler{

    public ResultType getResult();
}
