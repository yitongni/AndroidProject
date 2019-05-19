package com.example.mybudget;


public class ImageInformation {
    private String name;
    private String uri;
    private String id;

    ImageInformation(String file, String imageID, String imageName){
        uri= file;
        id=imageID;
        name=imageName;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }
}
