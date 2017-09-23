package paronomasia.prontodroid;


/**
 * Braden 9/15/17.
 */

public class Code {

    private int id = -1;
    private long remoteID = -1;
    private String hex;
    private buttonType type;

    // Setting the type to other means you should set this value so we can display something on the button.
    // If it's OTHER then we'll set the button to display this value; anything else and it'll display the associated drawable.
    private String name;

    // These are the different button options available.
    // NOTE THE CHECK ON hasDrawable() !!  That's set as it is now as I've only just started adding drawables.
    // That's still unused yet, as it defaults to a blank drawable, but still...
    public enum buttonType
    {
        POWER(0, R.drawable.power),
        INPUT(1, R.drawable.input),
        VOLUP(2, R.drawable.volup),
        VOLDN(3, R.drawable.voldn),
        MUTE(4, R.drawable.mute),
        CHANUP(5),
        CHANDN(6),
        UPARROW(7),
        DOWNARROW(8),
        LEFTARROW(9),
        RIGHTARROW(10),
        SELECT(11),
        MENU(12),
        BACK(13),
        CAPTIONS(14),
        OTHER(999);


        private final int n;
        private final int i;
        buttonType(final int num) {
            this.n = num;
            this.i = R.drawable.blank;
        }
        buttonType(final int num, final int image) {
            this.n = num;
            this.i = image;
        }
        public int getNum() { return this.n; }
        public boolean hasDrawable() { return this.n < 5; }
        public int getDrawable() { return this.i; }
    }


    public Code(int id, long remoteID, String hex, int type, String name){
        this.id = id;
        this.remoteID = remoteID;
        this.hex = hex;
        setType(type);
        this.name = name;
    }


    // Accessors / Mutators

    // return the id of the given code
    public int getID(){
        return this.id;
    }

    // set the code id (DANGEROUS - this should be used only by the db handler)
    // it's usually unneeded as this is usually set by the constructor upon db read
    public void setID(int id){
        this.id = id;
    }

    // return the id of the remote this code goes with
    public long getRemoteID(){
        return this.remoteID;
    }

    // set the id of the remote this code is associated with
    public void setRemoteID(long id){
        this.remoteID = id;
    }

    // return a string containing the hex associated with this code
    public String getHex(){
        return this.hex;
    }

    // arbitrarily set the value associated with this code
    public void setHex(String hex){
        this.hex = hex;
    }

    // return the corresponding button int value for a code
    public int getType(){
        return this.type.getNum();
    }

    // set the button type from an int
    public void setType(int i){
        for(buttonType t : buttonType.values()) {
            if (t.getNum() == i){
                this.type = t;
            }
        }
    }

    // get the name of the code
    public String getName(){
        if(this.name == null || this.name.equals("")){
            return "";
        }
        return this.name;
    }

    // set the name of the code (useful for editing/displaying images)
    public void setName(String name){
        this.name = name;
    }

    // return the id of the drawable (needs to be used w/ ContextCompat)
    public int getDrawableID(){
        return this.type.getDrawable();

    }
}
