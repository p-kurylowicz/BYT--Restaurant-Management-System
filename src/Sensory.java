import java.io.Serializable;

public class Sensory implements Serializable {
    private double temperature;
    private String taste;
    private String smell;

    public Sensory() {}

    public Sensory(double temperature, String taste, String smell) {
        setTemperature(temperature);
        setTaste(taste);
        setSmell(smell);
    }

    public double getTemperature() { return temperature; }
    public String getTaste() { return taste; }
    public String getSmell() { return smell; }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setTaste(String taste) {
        if (taste == null || taste.trim().isEmpty())
            throw new IllegalArgumentException("Taste cannot be null or empty");
        this.taste = taste.trim();
    }

    public void setSmell(String smell) {
        if (smell == null || smell.trim().isEmpty())
            throw new IllegalArgumentException("Smell cannot be null or empty");
        this.smell = smell.trim();
    }

    @Override
    public String toString() {
        return "Sensory[temperature=" + temperature +
               ", taste=" + taste +
               ", smell=" + smell + "]";
    }
}
