package paronomasia.prontodroid;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Braden - 9/1/17.
 */

class Remote {


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
    private int hash;

    // Complete constructor, used with a known ID value and hash (e.g. when pulling from the database)
    Remote(long id, ArrayList<Code> codes, int vendor, int type, String name, boolean current, int hash) {
        this.remoteID = id;
        this.codes = codes;
        this.vendor = vendor;
        setType(type);
        this.name = name;
        this.current = current;
        this.hash = hash;
    }

    // Just the vendor, type, name, and current bool.
    // Used in AddRemote.class methods for adding fresh remotes to DB
    // The code list is set to an empty list. This is useful when adding only a remote and no codes yet.
    // The id is left as -1 and the hash is calculated here.
    Remote(int vendor, int type, String name, boolean current){
        this.codes = new ArrayList<>();
        this.vendor = vendor;
        setType(type);
        this.name = name;
        this.current = current;
        this.hashCode();
    }


    // return the remote ID
    public long getID(){
        return this.remoteID;
    }

    //return the vendor ID int
    public int getVendorId() {
        return this.vendor;
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

    // sets the current bit for this remote
    public void setCurrent(boolean status){
        this.current = status;
    }

    // Is this the current remote?
    public boolean getCurrent(){
        return this.current;
    }

    // compare the hash of the current remote to a given hash string
    public boolean compareHash(int h2){
        return this.hash == h2;
    }

    @Override
    public int hashCode(){
        int codesHash = 0;
        for(int i = 0; i < this.codes.size(); i++){
            codesHash += Objects.hash(this.codes.get(i).getHex(),
                    this.codes.get(i).getName(),
                    this.codes.get(i).getType());
        }
        int mainHash = Objects.hash(this.name, this.vendor, this.type, codesHash);
        this.hash = mainHash + codesHash;
        return this.hash;
    }



    // ~ CURRENTLY UNUSED ACCESSORS / MUTATORS ~
    /*
      These are here for completeness, though
      their functionality is usually already
      included in the constructors when used
      by the db helper, activities, etc. or
      by an overloaded version requiring less
      memory resources.
    */

    // set the remote ID
    public void setID(long id){
        // Used by the DB Helper
        this.remoteID = id;
    }

    // set the vendor ID int
    public void setVendor(int vendor){
        this.vendor = vendor;
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

    public String getTypeString() {
        return this.type.toString();
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

    // set hash for a remote (called from the db)
    public void setHash(int hash) {
        this.hash = hash;
    }


}
