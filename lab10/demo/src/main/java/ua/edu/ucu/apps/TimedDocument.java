package ua.edu.ucu.apps;

import java.time.Duration;
import java.time.Instant;

public class TimedDocument extends AbstractDecorator {

    public TimedDocument(Document document) {
        super(document);
    }

    @Override
    public String parse() {
        Instant start = Instant.now();
        String result = super.parse();
        Instant end = Instant.now();
        System.out.println("Parse time: " + Duration.between(start, end).toMillis() + "ms");
        return result;
    }
}

