package com.mo2a.example.toptenjava;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.util.ArrayList;

 class ParseEntries {
     private static final String TAG = "ParseEntries";
    private ArrayList<FeedEntry> entries;

    ParseEntries() {
        this.entries= new ArrayList<>();
    }

    public ArrayList<FeedEntry> getEntries() {
        return entries;
    }

    public boolean parse(String xmlData){
        boolean status= true;
        FeedEntry currentRecord= null;
        boolean inEntry= false;
        String textValue= "";

        try{
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType= xpp.getEventType();
            currentRecord= new FeedEntry();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName= xpp.getName();
                switch(eventType){
                    case XmlPullParser.START_TAG:

                        if("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue= xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(!inEntry){
                            if(tagName.equalsIgnoreCase("title")){
                                currentRecord.setTitle(textValue);
                                entries.add(currentRecord);
                                Log.d(TAG, "parse: title= "+ textValue);
                            }
                        }
                        if(inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                entries.add(currentRecord);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageUrl(textValue);
                            }
                        }
                        break;

                        default:

                }
                eventType= xpp.next();
            }
        }catch (Exception e){
            status= false;
            e.printStackTrace();
        }
        return status;
    }
}
