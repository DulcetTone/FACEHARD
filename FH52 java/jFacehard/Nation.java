package jFacehard;

import java.util.Vector;


public class Nation {

    public static Nation US, UK, DE, FR, IT, JP, AH;

    public static Vector<Nation> list = new Vector<Nation>();

    // Each nation has a list of projectile templates, to support
    // the data-centric portion of Nathan's original application
    public Vector<Projectile> projectiles = new Vector<Projectile>();

    // create a list of nation descriptors
    static void classInit() {        
        US = new Nation("America", "American");
        UK = new Nation("Britain", "British");
        DE = new Nation("Germany", "German");
        FR = new Nation("France", "French");
        IT = new Nation("Italy", "Italian");
        JP = new Nation("Japan", "Japanese");
        AH = new Nation("Austria-Hungary", "Austro-Hungarian");
    }

    private String fullName; // e.g.: America
    private String adjName; // e.g.: American


    protected Nation(String n, String adj) {
        fullName = n;
        adjName = adj;
        list.addElement(this);
    }


    public String name() { return fullName; }
    public String adjectiveName() { return adjName; }


}
