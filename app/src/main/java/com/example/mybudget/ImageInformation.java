package com.example.mybudget;


public class ImageInformation {
    private String uri;
    private String id;

    ImageInformation(String file, String imageID){
        uri= file;
        id=imageID;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }
}
