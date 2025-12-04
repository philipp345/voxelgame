public class Player {

    private double posX;
    private double posY;
    private double posZ;

    public void setPositionX (double position){
        this.posX=position;
    }
    public void setPositionY (double position){
        this.posY=position;
    }
    public void setPositionZ (double position){
        this.posZ=position;
    }
    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public double getPositionX() {
        return this.posX;
    }
    public double getPositionY() {
        return this.posY;
    }
    public double getPositionZ() {
        return this.posZ;
    }

}
