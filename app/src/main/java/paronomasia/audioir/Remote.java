package paronomasia.audioir;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Braden - 9/1/17.
 */

class Remote {

    /*
    TODO
	- Complete the enums lists.
     */


    // The following enums are referenced with an integer in the database corresponding
    // to each item's index to increase speed and decrease memory costs.

    // These are the different types of devices one can have. You cannot add a new type.
    public enum deviceType {TV, STEREO, SETTOP, DVR, AC, SURROUND, COMPUTER, PROJECTOR, SATELLITE, VCR, AMPLIFIER, DVDPLAYER, OTHER}




    private long remoteID = -1;    // This *should* always be set by the DB Handler before actual use.
    private ArrayList<Code> codes;   // An array of strings representing the different codes from the DB
    private int vendor;    // The title of the vendor (used in identification)
    private deviceType type;      // The type of device the remote is for. Uses above enums.
    private String name;
    private boolean current;    // Is this the current remote?
    private String hash;

    // Complete constructor, used with a known ID value (e.g. when pulling from the database)
    Remote(int id, ArrayList<Code> codes, int vendor, int type, String name, boolean current, String hash) {
        this.remoteID = id;
        this.codes = codes;
        this.vendor = vendor;
        setType(type);
        this.name = name;
        this.current = current;
        this.hash = hash;
    }

    // Everything but the id (set here to -1). Used in AddRemote.class methods for adding fresh remotes to DB
    // The code list can be passed as null FWIW. This is useful when adding only a remote and no codes yet.
    Remote(ArrayList<Code> codes, int vendor, int type, String name, boolean current, String hash){
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

    // set the type by the ordinal
    public void setType(int i){
        for(deviceType t : deviceType.values()) {
            if (t.ordinal() == i){
                this.type = t;
            }
        }
    }

    // return int of device type ordinal
    public int getType() {
        return this.type.ordinal();
    }

    public String getTypeString() {
        return this.type.toString();
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
    public ArrayList<Code> getCodes(){
        if( this.codes == null || this.codes.size() == 0)
            return null;
        else
            return this.codes;
    }

    // add an ArrayList<> of codes to this remote
    public void setCodes(ArrayList<Code> codes){
        this.codes = codes;
    }

    // add a single code to existing ArrayList<> (should already be in DB - maybe implement a reload() function instead?)
    public void addCode(String hex, int type, String name){
        Code code = new Code(-1, this.remoteID, hex, type, name);
        this.codes.add(code);
    }

    // sets the current bit for this remote
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
