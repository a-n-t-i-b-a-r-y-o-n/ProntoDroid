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


    private long remoteID = 0;    // This *should* always be set by the DB Handler before actual use.
    private ArrayList<String> codes = new ArrayList<>();   // An array of strings representing the different codes from the DB
    private int vendor;    // The title of the vendor (used in identification)
    private deviceType type;      // The type of device the remote is for. Uses above enums.
    private String name;
    private boolean current;    // Is this the current remote?
    private String hash;

    // Complete constructor, used with a known ID value (e.g. when pulling from the database)
    Remote(int id, ArrayList<String> codes, int vendor, deviceType type, String name, boolean current, String hash) {
        this.remoteID = id;
        this.codes = codes;
        this.vendor = vendor;
        this.type = type;
        this.name = name;
        this.current = current;
        this.hash = hash;
    }

    // Everything but the id (set here to -1). Used in AddRemote.class methods for adding fresh remotes to DB
    Remote(ArrayList<String> codes, int vendor, deviceType type, String name, boolean current, String hash){
        this(-1, codes, vendor, type, name, current, hash);
    }

    // set the remote ID
    public void setID(long id){
        // Used by the DB Helper
        this.remoteID = id;
    }

    // return the remote ID
    public long getID(){
        return this.remoteID;
    }

    // set the vendor ID int
    public void setVendor(int vendor){
        this.vendor = vendor;
    }

    //return the vendor ID int
    public int getVendorId() {
        return this.vendor;
    }

    // set the type using a string
    public void setType(String type){
        for(Remote.deviceType t : Remote.deviceType.values()) {
            if(t.toString().equals(type))
                this.type = t;
        }

        // WARNING this will fail silently.
    }

    // set the type using a Remote.deviceType
    public void setType(deviceType type) {
        this.type = type;
    }

    // return Remote.deviceType of the type
    public deviceType getType() {
        return this.type;
    }

    // set the remote name
    public void setName(String name){
        this.name = name;
    }

    // return the remote name
    public String getName(){
        return this.name;
    }

    // return all codes for a given remote
    public ArrayList<String> getCodes(){
        return this.codes;
    }

    // add an ArrayList<> of codes
    public void setCodes(ArrayList<String> codes){
        this.codes = codes;
    }

    // add a single code to existing ArrayList<> (should already be in DB - maybe implement a reload() function instead?)
    public void addCode(String code){
        this.codes.add(code);
    }

    // ~ UNUSED YET ~ this should be called *from the db handler* to clear the current bit on all remotes, then set it for one.
    public void setCurrent(boolean status){
        this.current = status;
    }

    // Is this the current remote?
    public boolean getCurrent(){
        return this.current;
    }

    // ~ UNUSED YET ~ this should probably also be called *from the db handler* to set a has for a given remote + its codes
    public void setHash(String hash) {
        this.hash = hash;
    }

    // ~ UNUSED YET ~ return hash of remote values + all codes
    public String getHash(){
        return this.hash;
    }

}
