package ru.syntez.integration.router.exceptions;

/**
 * Wrapper over RuntimeException. Includes additional options for formatting message text.
 *
 * @author Skyhunter
 * @date 14.01.2021
 */
public class RouterException extends RuntimeException {

    public RouterException(String message) {
	    super(message);
    }
   
}
