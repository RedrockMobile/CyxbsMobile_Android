package com.mredrock.cyxbs.volunteer.bean;

import java.util.List;

public class PasswordEncode {

    /**
     * status : 1
     * info : ok
     * data : ["CRtrvLG7UWwfmaOLiEL82X4mNkaDEbdcoglCkbeDT/8TAGKfobyGo7NEaiJjIJG/bvbJYZ2xAF4yvFJ0MiqN8Q=="]
     */

    private int status;
    private String info;
    private List<String> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
