package com.jw.porn.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class RunThread extends Thread
{
    InputStream is;
    String type;
    
    RunThread(InputStream is, String printType)
    {
        this.is = is;
        this.type = printType;
    }
    
    @Override
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ( (line = br.readLine()) != null) {
                System.out.println(type + ">" + line);
            }
            } catch (IOException ioe)
              {
                ioe.printStackTrace();
              }
    }
}