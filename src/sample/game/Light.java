package sample.game;

/** Light class for defining items that can be used to light rooms. Extends item.
 * @see Item*/
public class Light extends Item{
    /** The state of the light, ON, OFF or EXHAUSTED.
     * @see LightState*/
    private LightState lightState = LightState.OFF;
    /** The number of times the light can be used before it is EXHAUSTED.
     * @see LightState*/
    private int numUses = Integer.MAX_VALUE;

    /** Default constructor for Light.
     * @see Item*/
    public Light() {
        super();
    }

    /** Constructor for light taking default parameters.
     * @param name The name of the light.
     * @param description The description of the light.
     * @param isVisible Determines if the light can be seen.
     * @param isCarry Determines if the light can be carried.
     * @param startWith Determines if the player starts with the light.
     * @param lightState Determines the state of the light.*/
    public Light(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, LightState lightState) {
        super(name, description, isVisible, isCarry, startWith);
        this.lightState = lightState;
    }

    /** Constructor for key taking default parameters and number of uses.
     * @param numUses The number of uses the light has before it can no longer be used.*/
    public Light(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, LightState lightState, int numUses) {
        super(name, description, isVisible, isCarry, startWith);
        this.lightState = lightState;
        this.numUses = numUses;
    }

    /** Constructor for light taking a unique identifier for overwriting existing items.
     * @param id The unique identifier to overwrite.*/
    public Light(long id, String name, String description, boolean isVisible, boolean isCarry, boolean startWith, LightState lightState, int numUses) {
        super(id, name, description, isVisible, isCarry, startWith);
        this.lightState = lightState;
        this.numUses = numUses;
    }

    /** Overrides the item use function. Turns the light ON if it is OFF, OFF if it is ON.
     * Exhausted if number of uses is 0.
     * @param p The player trying to use the light.
     * @return String Describes the results of trying to use the light.
     * @see Item*/
    @Override
    public String use(Player p) {
        if (this.numUses <= 0) { //light can no longer be used
            lightState = LightState.EXHAUSTED;
        }
        switch (lightState) {
            case ON:
                this.numUses--; //reduce number of uses
                p.incrementTurnCount(); //successful action, increment player turn count
                lightState = LightState.OFF; //update light state
                return this.getName() + " is now off.";
            case OFF:
                this.numUses--;
                p.incrementTurnCount();
                lightState = LightState.ON;
                return this.getName() + " is now on.";
            default:
                return this.getName() + " can no longer be used.";
        }
    }

    /** Method for adding uses to the light. Changes state from EXHAUSTED if value changes
     * from 0 to greater than 0.
     * @param uses Number of uses to add to the light.
     * @return String Alerts if the light can be used again after becoming EXHAUSTED.*/
    public String addUses(int uses) {
        this.numUses = this.numUses + uses;
        if (numUses > 0 && this.lightState == LightState.EXHAUSTED) {
            this.lightState = LightState.ON; //light is usable again, update state and report
            return this.getName() + " can be used once again.";
        }
        return "";
    }

    /** Gets the state of the light, ON, OFF or EXHAUSTED.
     * @return LightState The current state of the light..
     * @see LightState*/
    public LightState getLightState() {
        return lightState;
    }

    /** Gets number of uses remaining for the light.
     * @return int The number of uses remaining.*/
    public int getNumUses() {
        return numUses;
    }

    public void setLightState(LightState lightState) {
        this.lightState = lightState;
    }

    public void setNumUses(int numUses) {
        this.numUses = numUses;
    }

    /** Displays information about the light's fields as a formatted string.
     * @return String The formatted string.*/
    @Override
    public String toString() {
        return super.toString() +
                "lightState=" + lightState +
                ", numUses=" + numUses +
                '}';
    }

    public static void main(String[] args) {
        Player p = new Player("June");
        Light torch = new Light("Torch", "A good light source.", true, true, true, LightState.OFF, 2);
        Light deadTorch = new Light("Dead Torch", "A not so good light source.", true, true, true, LightState.EXHAUSTED, 0);
        p.give(torch);
        p.give(deadTorch);

        System.out.println(p.getInventory().findItemByName("Torch").use(p));
        System.out.println(p.getInventory().findItemByName("Torch").use(p));
        System.out.println(p.getInventory().findItemByName("Torch").use(p));
        System.out.println(p.getInventory().findItemByName("Dead Torch").use(p));

    }
}
