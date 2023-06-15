import java.io.Serializable;
import java.sql.Timestamp;

public class Park implements Serializable {
    private Timestamp out_time;
    private Timestamp in_time;
    private String admin_region;
    private String berthage;
    private String section;

    public Timestamp getOut_time() {
        return out_time;
    }

    public void setOut_time(Timestamp out_time) {
        this.out_time = out_time;
    }

    public Timestamp getIn_time() {
        return in_time;
    }

    public void setIn_time(Timestamp in_time) {
        this.in_time = in_time;
    }

    public String getAdmin_region() {
        return admin_region;
    }

    public void setAdmin_region(String admin_region) {
        this.admin_region = admin_region;
    }

    public String getBerthage() {
        return berthage;
    }

    public void setBerthage(String berthage) {
        this.berthage = berthage;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
