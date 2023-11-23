/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model;

/**
 *
 * @author admin
 */
public class DBoperationException extends Exception{
    
    public static final int ALREADY_EXISTS=1;
    
    private final String errorMessage;
    private final boolean hasCause; 
    private final long code; 
    
    public DBoperationException(int operation)
    {  
        super(getOperationMessage(operation));  
        this.errorMessage=null;
        this.hasCause=false;
        this.code=0;
    }
    public DBoperationException(String msg,Exception ex)
    {  
        super(msg,ex);  
        this.errorMessage=null;
        this.hasCause=true;
        this.code=0;
    }    
    public DBoperationException(String msg)
    {  
        super(msg);  
        this.errorMessage=null;
        this.hasCause=false;
        this.code=0;
    }
    public DBoperationException(int operation,Exception ex)
    {  
        super(getOperationMessage(operation),ex);  
        this.errorMessage=null;
        this.hasCause=true;
        this.code=0;
    }    
    public DBoperationException(int operation,String msg)
    {  
        super(getOperationMessage(operation));  
        this.errorMessage=msg;
        this.hasCause=false;
        this.code=0;
    }   
    public DBoperationException(int operation,int code)
    {  
        super(getOperationMessage(operation));
        this.code=code;
        switch(code)
        {
            case ALREADY_EXISTS:  
                this.errorMessage="Already Exists";
                break;
            default:
                this.errorMessage="Unknown Error";
                break;
        }
        this.hasCause=false;
    }
    private static String getOperationMessage(int operation)
    {
        return DBentity.getOperationName(operation)+" Operation Failed";
    }
    @Override
    public String getMessage()
    {
        String msg=super.getMessage();
        if(this.hasCause)
        {
            msg+=" --> "+this.getCause().getMessage();
        }
        else if(this.errorMessage!=null)
        {
            msg+=" --> "+this.errorMessage;
        }
        return msg;
    }
    public long getCode()
    {
        return this.code;
    }
    @Override
    public String getLocalizedMessage()
    {
        if(this.errorMessage!=null)
            return this.errorMessage;
        else
            return "";
    }
}
