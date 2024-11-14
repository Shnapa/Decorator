package ua.edu.ucu.apps;


public abstract class AbstractDecorator implements Document {
    protected Document decoratedDocument;

    public AbstractDecorator(Document document) {
        this.decoratedDocument = document;
    }

    @Override
    public String parse() {
        return decoratedDocument.parse();
    }
}
