public class TestAttribute {
    
    private final String key;
    
    public TestAttribute (String key) {
        this.key = key;
    }
    
    public String getKey () {
        return key;
    }
    
    public String toString () {
        return "[TestAttribute " + key + "]";
    }
}