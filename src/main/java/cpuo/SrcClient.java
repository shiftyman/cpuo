package cpuo;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class SrcClient {
    
//    private CountDownLatch latch = new CountDownLatch(1);
    private Object connectSemphore = new Object();
    
    private String zkHostport = "172.19.31.33:2183,172.19.31.33:2182,172.19.31.33:2181/test";
    private ZooKeeper zk = null;

    public SrcClient() throws IOException, InterruptedException{
        zk = new ZooKeeper(zkHostport, 5000, new Watcher(){

            public void process(WatchedEvent event) {
                System.out.println("received watch event:" + event);
                if(event.getState() == KeeperState.SyncConnected){
                    synchronized (connectSemphore) {
                        connectSemphore.notify();
                    }
//                    Client.latch.countDown();
                }
                
            }
            
        });
        System.out.println("do connecting");
        try {
//            latch.await();
            synchronized (connectSemphore) {
                connectSemphore.wait();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("connecting finished.");
        signOnline();
        getOnlineList();
        Thread.currentThread().sleep(200000000);
    }
    
    public void signOnline(){
        try {
            zk.create("/machine/host-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("sign online.");
        } catch (KeeperException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void getOnlineList(){
        try {
            List<String> children = zk.getChildren("/machine", new Watcher(){
                
                public void process(WatchedEvent event) {
                    if(event.getType() == EventType.NodeChildrenChanged){
                        try {
                            List<String> children = zk.getChildren("/machine", this);
                            System.out.println("Now, friends changed, noline:" + children);
                        } catch (KeeperException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                
            });
            System.out.println("Now, friends noline:" + children);
        } catch (KeeperException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException{
        new SrcClient();
    }
}
