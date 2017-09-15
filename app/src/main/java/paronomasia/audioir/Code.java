package paronomasia.audioir;

/**
 * Braden 9/15/17.
 */

public class Code {

    public String hex;
    public buttonType type;

    // Setting the above to other means you *need* to set this value so we can display something on the button.
    // If it's anything but OTHER then we'll have an icon to display on an imagebutton, else this string on a button.
    public String name;

    // These are the different button options available.
    public enum buttonType {POWER, INPUT, VOLUP, VOLDN, CHANUP, CHANDN, UP, DOWN, LEFT, RIGHT, SELECT, MENU, BACK, CAPTIONS, OTHER}


    public Code(String hex, int type, String name){
        this.hex = hex;
        setType(type);
        this.name = name;
    }


    // Accessors / Mutators

    public String getHex(){
        return this.hex;
    }

    public void setHex(String hex){
        this.hex = hex;
    }

    public int getType(){
        return this.type.ordinal();
    }

    // set the button type from an int
    public void setType(int i){
        for(buttonType t : buttonType.values()) {
            if (t.ordinal() == i){
                this.type = t;
            }
        }
    }

    public String getName(){
        if(this.name == null || this.name.equals("")){
            return "";
        }
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

}
