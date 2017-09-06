package paronomasia.audioir;

import java.util.ArrayList;

/**
 * Braden - 9/1/17.
 */

class Remote {

    /*
    TODO
        - Consider changing device type to string?
        - Clean up unnecessary constructors and their usages.
     */

    public enum deviceType {TV, STEREO, SETTOP, DVR, AC, SURROUND, COMPUTER, PROJECTOR, SATELLITE, VCR, AMPLIFIER, OTHER}

    /*
    This is automatically set by the db to any other value > 0
    */

    protected long remoteID = 0;
    protected ArrayList<String> codes = new ArrayList<>();   // An array of strings representing the different codes
    protected int vendor;    // The title of the vendor (used in identification)
    protected deviceType type;      // The type of device the remote is for
    protected String name;

    Remote(ArrayList<String> codes, int vendor, deviceType type, String name) {
        this.codes = codes;
        this.vendor = vendor;
        this.type = type;
        this.name = name;
    }

    Remote() {
        this(null, -1, deviceType.OTHER, "Remote");
    }

    Remote(ArrayList<String> codes, String name) {
        this(codes, -1, deviceType.OTHER, name);
    }

    Remote(ArrayList<String> codes, deviceType type) {
        this(codes, -1, type, "Remote");
    }

    Remote(ArrayList<String> codes){
        this(codes, -1, deviceType.OTHER, "Remote");
    }

    public void setID(long id){
        // Used by the DB Helper
        this.remoteID = id;
    }

    public long getID(){
        return this.remoteID;
    }

    public void setVendor(int vendor){
        this.vendor = vendor;
    }

    public int getVendorId() {
        return this.vendor;
    }

    public void setType(String type){
        // This is stored in the database as a string.

    }

    public void setType(deviceType type) {
        this.type = type;
    }

    public deviceType getType() {
        return this.type;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<String> getCodes(){
        return this.codes;
    }

    public void setCodes(ArrayList<String> codes){
        this.codes = codes;
    }

    public void addCode(String code){
        this.codes.add(code);
    }


}
