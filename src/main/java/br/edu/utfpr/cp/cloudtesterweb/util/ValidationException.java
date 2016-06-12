package br.edu.utfpr.cp.cloudtesterweb.util;

/**
 *
 * @author Douglas
 */
public class ValidationException extends Exception {

    public ValidationException(String validationMessage){
        super(validationMessage);        
    }
}
