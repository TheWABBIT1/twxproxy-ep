package org.twdata.twxbbs.db;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 02/09/2008
 * Time: 8:45:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException() {
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
