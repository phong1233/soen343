package org.team4.common.exceptions;

public class InvalidTimeEntryException extends Exception{
    public InvalidTimeEntryException(){
        super("The Time is Not Valid, Please enter the valid information");
    }
    public InvalidTimeEntryException(String exp){super(exp);}


}
