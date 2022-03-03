package sample;

public class Light extends Item{
    private LightState lightState = LightState.OFF;
    private int numUses = Integer.MAX_VALUE;

    Light() {
        super();
    }

    Light(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, LightState lightState) {
        super(name, description, isVisible, isCarry, startWith);
        this.lightState = lightState;
    }

    Light(String name, String description, boolean isVisible, boolean isCarry, boolean startWith, LightState lightState, int numUses) {
        super(name, description, isVisible, isCarry, startWith);
        this.lightState = lightState;
        this.numUses = numUses;
    }

    @Override
    public String use(Player p) {
        if (this.numUses <= 0) {
            lightState = LightState.EXHAUSTED;
        }
        switch (lightState) {
            case ON:
                this.numUses--;
                lightState = LightState.OFF;
                return this.getName() + " is now off.";
            case OFF:
                this.numUses--;
                lightState = LightState.ON;
                return this.getName() + " is now on.";
            default:
                return this.getName() + " can no longer be used.";
        }
    }

    public String addUses(int uses) {
        this.numUses = this.numUses + uses;
        if (numUses > 0 && this.lightState == LightState.EXHAUSTED) {
            this.lightState = LightState.ON;
            return this.getName() + " can be used once again.";
        }
        return "";
    }

    public LightState getLightState() {
        return lightState;
    }

    public int getNumUses() {
        return numUses;
    }

    public void setLightState(LightState lightState) {
        this.lightState = lightState;
    }

    public void setNumUses(int numUses) {
        this.numUses = numUses;
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
