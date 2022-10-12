package fr.minemobs.jsonreader;

public class MalformedJsonException extends Exception {

    public MalformedJsonException() {
        super();
    }

    public MalformedJsonException(String text) {
        super(text);
    }
}
