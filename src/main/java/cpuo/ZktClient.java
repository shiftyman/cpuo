package cpuo;

import org.I0Itec.zkclient.ZkClient;

public class ZktClient {
    private String zkHostport = "172.19.31.33:2183,172.19.31.33:2182,172.19.31.33:2181/test";
    
    ZkClient client = null;
    
    public ZktClient(){
        client = new ZkClient(zkHostport, 5000, 5000);
    }
    
    public static void main(String[] args){
        new ZktClient();
    }
}
