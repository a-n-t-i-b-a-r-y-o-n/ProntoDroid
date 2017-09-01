package paronomasia.audioir;

/**
 * Braden - 9/1/17.
 */

class Remote {

    public enum deviceType {TV, STEREO, SETTOP, DVR, OTHER}

    /*
    This is set to -1 until queried from the db
    */

    protected long remoteID = -1;

    protected String[] codes;   // An array of strings representing the different codes
    protected int vendor;    // The title of the vendor (used in identification)
    protected deviceType type;      // The type of device the remote is for
    protected String name;

    Remote(String[] codes, int vendor, deviceType type, String name) {
        this.codes = codes;
        this.vendor = vendor;
        this.type = type;
        this.name = name;
    }

    Remote() {
        this(null, -1, deviceType.OTHER, "Remote");
    }

    Remote(String[] codes, String name) {
        this(codes, -1, deviceType.OTHER, name);
    }

    Remote(String[] codes, deviceType type) {
        this(codes, -1, type, "Remote");
    }

    Remote(String[] codes){
        this(codes, -1, deviceType.OTHER, "Remote");
    }

    public void setID(long id){
        this.remoteID = id;
    }

    public long getID(){
        return this.remoteID;
    }

    public void setVendor(int vendor){
        this.vendor = vendor;
    }

    public void setType(String type){
        // This seems to pose a problem...
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }


}
