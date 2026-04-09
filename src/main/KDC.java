/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;


/**
 *
 * @author matthewhvizdos
 */
public class KDC {
    protected static final int PORT = 8170;
    protected static final String PRK = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKcHaPM83FK5murNheosWlrsdhF7Lw8FGszwj4g7ihB8NycQf1TSrS+YAFjs58sWgDn4aBPWZGOrh/SxiN7HwCaG8ZiDlWO9GviOGHW3Kbp8pzHxp9kU3Q2aDQ8Yg9P0x0HDzQsqGdIo6TOkRqNVweSBaPnXOYRsNZB4GM+BvDo/AgMBAAECgYEAnUxeKwv61bFT0qLA5brlUNF1k5r1w2hkR/KYyYyAxmukqRZhQ+mwDa1RTm21EU5cbbuhkGUMX80PmYKESoHFUzlcYvm4AVRY7SPVQPbe4iYB4nacUZU5uOT42WqLkO8USjrafCx3HxSeWS+wHnbjQqjNz5G6qoKpBt4ysCLg6yECQQDwA1W5zq4KtbqwFZfGofQbvcqkaMUTeBp8D+hUeaxGgFwfuEDEaHNP4hC3FoCPif//jirsAE34xnNn04+cG9bPAkEAsieP0S8vZIxEP2JRU9vvMITTMF2vJx8qg0A9sX+6OaF1od8hSDV21wOvrcqTV54XyE/d6ChCb9GtHvbGgANBkQJAarQQ2JpaJcjFRqNS5qv5qpumC5HIi+9JVv57e5LcVDucuT1hDfCh01HdvIf0f9wKQ8Mgseswvvj7NZ2Iqw51YQJAR42gM7Ix5L55gsOoSnghP2l5sQlPmfTojEK3BJ16XD8/Eb6ejXP7wSwX2UNtnlk+0BNT2zSgSmz6jV8sJqooIQJAIgwbBswiFZDr3wdV7wq3bBK/wYO+D3db5wm8XQUh68FirleDiWRTzyxGHih/BK4Phgiok92K5PvV3PAiSZfGBw==";
    protected static final String ID = "The Bank";
    protected static final String DELIM_REGEX = "\\|";
    protected static final char DELIM = '|';
    protected static final String U_DELIM_REGEX = "~";
    protected static final char U_DELIM = '~';
    protected static final char RESERVED_CHAR = '\uFFFF';
    protected static final String MSG_JOIN = "JOIN";
    protected static final String MSG_UPDATE = "UPDATE";
    
    // Id, public key
    private Map<String, ClientData> key_map = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<KDCThread> threads = new CopyOnWriteArrayList<>();
    private BlockingQueue<String> notifications = new LinkedBlockingQueue<>();
    
    // Where usernames/pwrds are stored
    private static final String USER_FILENAME = "user.txt";
    protected Map<String, String> users = new ConcurrentHashMap<>(); 
    
    private Socket csock = null;        // Client Socket
    private ServerSocket ssock = null;  // Server Socket
    
    private void loadUsers() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILENAME))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(DELIM_REGEX);
                users.put(parts[0], parts[1]); // <username, H(password)>
            }
        } 
    }
    
    private KDC() {
        try {
            loadUsers(); // Load all users into the system
            ssock = new ServerSocket(PORT);
            run();
        } catch (IOException IOException) {
            IOException.printStackTrace();
        }
    }
    
    
 
    
    private void run() throws IOException {
        System.out.println("\033[33mServer started\033[0m");
        while (true) {
            System.out.println("\033[90mWaiting for a client ...\033[0m");
            csock = ssock.accept();
            System.out.println("Client accepted");
            
            KDCThread client_thread = new KDCThread(csock, key_map, notifications, users);
            threads.add(client_thread);
            
            Thread t = new Thread(client_thread);
            t.start();
        }
    }
    
    public static void main(String[] args) {
        KDC server = new KDC();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server shutting down");
            
            for (KDCThread t : server.threads) {
                t.shutdown();
            }
            
            try { server.ssock.close(); } catch (Exception e) {}
        }));
        
    }
    
}








/*
    KDC
    
    Public Key: 
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlvAhHyb3qv6q5BZPx34shjjtOh/b7WThchaWs92gs+BoWszrsPEEc5XSLHzrJmNqZMs0rF6uoR6LTFV/eOVqNbhpdUW5zid82Rm0CQaKzIo643+OakU93feQti0YGmGPuPgqO5Gatk3wzYwLLDyc5s4mwyJ/nWIUvhBEJdPo7FtUn6DTelJ3MHkt3gbfPCCyNXnKGzY98HXE58xkLCa+hR0o6TrVkQ8SItymDHOsr8OK+FdkAUBPFkoB3Qy4QXThIrdt/vJymIzU1H9FLTogcZhWBstYCpr7q6FDmDZlyAV7XMnpX9rc4ir0MNZ4d3/uQzLAehOEs5lyAOSBbHAWpwIDAQAB
Private Key: 
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCW8CEfJveq/qrkFk/HfiyGOO06H9vtZOFyFpaz3aCz4GhazOuw8QRzldIsfOsmY2pkyzSsXq6hHotMVX945Wo1uGl1RbnOJ3zZGbQJBorMijrjf45qRT3d95C2LRgaYY+4+Co7kZq2TfDNjAssPJzmzibDIn+dYhS+EEQl0+jsW1SfoNN6UncweS3eBt88ILI1ecobNj3wdcTnzGQsJr6FHSjpOtWRDxIi3KYMc6yvw4r4V2QBQE8WSgHdDLhBdOEit23+8nKYjNTUf0UtOiBxmFYGy1gKmvuroUOYNmXIBXtcyelf2tziKvQw1nh3f+5DMsB6E4SzmXIA5IFscBanAgMBAAECggEAOtpy78Sv+ZrHY5xXiz/leIil524YwR3+0g+Av62BReIQjqcsl5fC1pbqlrI2toc5KwgZB8T7bpOMWT1VMPyzRzMa28FOS9YV1kkxa/uDD3IuuBo6m7FYCD7JzF8U8ORtt//757JEkyUc6ejbSmpv+4cSki/XdrSRxfJtwBo6zj3JoI8e6r5nKon4XbMTm7id8cbrf7oVOI6z+qJhKIEF/oHRp/CQp3ilOvBNTG/2GMDA30kQ1a7czZ5aZeXeX3KIqBGogJH8jOlrKFnjOlp2CmqS60bPj3wZrpoHskgm+5lG4SBJbWonC/TF3VVRJ+q2PR7QRMawzJTf4tZ17eLHMQKBgQDzKp6fu9Gfvs05+rPgfZmOj0XrUvWtAj5k7iLCURVDOL+SscV1tr0XS/OgQ+SEUp5No6wKB6+XYH60ZssVflsaAHco1dIZkbE0DgHnCFIm8eQo1YWn1jTRyn81vAkLQ0p+LdAey0APyZ+KmaHJe3VGgvrCIg6xO7aMGymcp0h0owKBgQCe5209XN/pzr9g6D0VKBNGP1liiFJO/FKJ0SqQ5Xw1W3l+zAXywaSUzOB8IPIHrY+8E/5mRPvdg2RbgTGBDGIgd+G8DU+94Qe/IdfqJ0Dj84u7AkAcIrsQgb6XqiD1a1ebCwodePBuLyrCtbBSIBZ0ddrFqoR6y9cm+inOJWFyLQKBgQCPDpfbqYedz4a1gYSpobrhX/DmjRvz+Mn0XxUb9M/piE1MZEGjCysbtZdSxh5+qoxvaMch73uiNybTw1XfA0NdfMCdyY2ddVUnJsZ/wSlzuANe8p97Kk5Qzi0g08sOUTxWF2XFeBXsM9YkmFXST0ujfBNCev7hzhuLOb+1ZcT+uQKBgApotIIxhEVnkqfBbsblAiY23h8TOPkNlEaM2hH1xmYkYNgKe1VElAhfR1E6Q0B3gRPb521BlZIUGk3YL6r5abAgyjusT43roLtHd0JSYfDMslmEszhkmgZhyV9b+J668lkopvf5RR6inlV022D10yK72aHbIqR8l9JozrY/QpvdAoGBAMFnGzHQrpo8tcIxiMSAXhQlCI+yr/Z/awNP7/qzP9MsWkz/7KlECIWlAjb5D3dqK81J7Rv48Y3otEbJiI3RV637+Mb0jGwN7jQkdS1JVSE6baYi7EXAUtQLzc0liNBSdpvpzrD6wsQj5VTAG69rL+yGmsGaHc72ggK/exKef5I6

    
    */
    
    /*
    A
    Public Key: 
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1tMWe3uX6SyMKPEC6+ZaZo8MBSXk5fOZNRTEGxvb9+sNm1y0MeznKe2a/BmT0c09EoQb87VQU9BCdumKIy/Q6TZw2dAJ2oPITXlccijW83K3MLU/nKO1bi5sdd2Pg2oKXMt2W+wWTyqux3rZZs/oKabVs5273sK0Um1Zn/sTU3H9juli365XTHSvLJByb2iXUYDf34Nl0xsRqU+9eSKgfS4TIWjdNB9JA63iW8We/Wsz/WBDi430N8i2mUGWovQBgSwdDc4BxeHg3Jql7TW+IPvS2L+UWAhv5Qg/7FBvhej9a9cLYMZkT/pkZ+sf04c8DFG59o/i0vQjCG8ZGd2bawIDAQAB
Private Key: 
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDW0xZ7e5fpLIwo8QLr5lpmjwwFJeTl85k1FMQbG9v36w2bXLQx7Ocp7Zr8GZPRzT0ShBvztVBT0EJ26YojL9DpNnDZ0Anag8hNeVxyKNbzcrcwtT+co7VuLmx13Y+Dagpcy3Zb7BZPKq7Hetlmz+gpptWznbvewrRSbVmf+xNTcf2O6WLfrldMdK8skHJvaJdRgN/fg2XTGxGpT715IqB9LhMhaN00H0kDreJbxZ79azP9YEOLjfQ3yLaZQZai9AGBLB0NzgHF4eDcmqXtNb4g+9LYv5RYCG/lCD/sUG+F6P1r1wtgxmRP+mRn6x/ThzwMUbn2j+LS9CMIbxkZ3ZtrAgMBAAECggEBALzfkmC25XTo6cIFRGBLgyewlQzhqEJUzlrRCyoacGekja4O748Fjqhac206v7AyFoP1Cu5Wd215KTaLVhxhqfehGc8nDcLFIpKbJSBpr7MTJOT6NhduB9H7lvh59/e+f6XebQ2coHCyQkC4TfQKzz/vouvsvoS4AvqFkcsKYI93iJAtJz4SVI0lBT/lyRXDGTeTLUUjoZMDrfPksTG6vORjxk3Y2sV+iTzkamu0fJcU6y7JB7oP2FWq7yrf8pPrQ6J/h2QAgIrLcXzuLQm/kt0nY8iuEyepjRjj0Yu+c/rzhLTpXA0dRAdAHfYzXy7wRyKuRTxZNRp2k7m3IYMCx+ECgYEA9Ife5Jlzm3yPVdpU/ZXzuiYEMmQm2rNSRzkn7EpXeNjceUTCa7dquL3C1ev+hpHe2t0CbnCoH5VR4EFhFqfjSKl3DfMJkxHmcuWHZ+CaqeaXZavA7JzKJ3AHSmjsMBwh8t0C4cPWRLOADEvDdFYcvz9/mkMTyIfhD9Z/zlfXP98CgYEA4OaHcrDRCMP201ZFTCwX+e4jP0dblumqCqYZL65yhCWfO7NjY2bODb5CmDW0eCzqFVQ9rCVCscKQH2Bku2RM87ZM5CcVYTMjV+Qh0vnD4UGCOaxflRBCcC76w4GB/6R2KgePVC7NLe7QKGsgRU+INc6gk7PY3TIo/tPW0u6l5fUCgYBCsehea2g3yYKCxRkXTVX7uLC+WcirL+vKF1HHRgOTp3qC7PkmThGir8c7d7GlXJXvFga6vqlsa9q4Erz7Y/E++VHKkTZUUbaMMgR7law5mPZEOdNV9fHBTfH/Hp0FBjdQOW22zCZd+KN9NxDpYy9WDmisV41GzaDaJAQZ8KV5+wKBgQCFy//21oR1j4fvMNrnNzBIa0ki40P04yxbS3eea00LmwpBTTaqs0WQ3Yjm3sRv3mCgsc5C9KPZ9yMg14r2BPTW5LSX4zyP6Al1x23sAtVq1DsyLn9qwhKUhQ6AczKwsNnvpTG65mEAm5vQ//Jh6a/iOvxDAvLYnXfQKp0aPgoKZQKBgEYZg73Y2P14f6zyCJjJrrhnYjv41jCFvo8YD4UxEklYOxgymZEYpOD9jc1vk7Bjv/5kQC3PC0pCORpnDqL0yKD0RcizUhYPHVQxGuH3WEeqVigye0llXz/104+Ex69zr346vbteIDpNtaWzBwvH3JZS3UvFJGBdfgA61QHYxMzp

    
    */
    
    /*
    B
    Public Key: 
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg1dNDAn1ZyXebowUvB5z5l7dVQFQrNbTqtJpkg1eJiDpZdX/a/4GL8liUCkkmlA8EtupcZ8bchF8nOAahZAhP+33LzFL0FdZpsVgu2BkDAfy8vqbJOAmYXK0HjEPhWE+JadjzIafhc/gyJHfrGZtdzFJvE/T9CeZkDIR5Rs7ZA/L2Ho1NPBkLsMHD+XYB8L1Jwt1bmC9AhE4iQz7DHP4ykt2EXaobbzrY6deAgl5vEu/Jati0oC9EUxmj+Y3X9AJpLrd3GgR4qXoe4fhZPtlDzGbaGxFBKfQYloUU33EbqLNISFidBrPLP6EFnHmqri5Zacz56u5UZ2nJ+oz6IMZ/QIDAQAB
Private Key: 
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCDV00MCfVnJd5ujBS8HnPmXt1VAVCs1tOq0mmSDV4mIOll1f9r/gYvyWJQKSSaUDwS26lxnxtyEXyc4BqFkCE/7fcvMUvQV1mmxWC7YGQMB/Ly+psk4CZhcrQeMQ+FYT4lp2PMhp+Fz+DIkd+sZm13MUm8T9P0J5mQMhHlGztkD8vYejU08GQuwwcP5dgHwvUnC3VuYL0CETiJDPsMc/jKS3YRdqhtvOtjp14CCXm8S78lq2LSgL0RTGaP5jdf0Amkut3caBHipeh7h+Fk+2UPMZtobEUEp9BiWhRTfcRuos0hIWJ0Gs8s/oQWceaquLllpzPnq7lRnacn6jPogxn9AgMBAAECggEANSZqBnotNf2sIr5LUcldC5fOJ60tkZQp+hHGKRWByRo+eTh9cZ1NsZ7kSx4rMstPVC5SRqmleKa879r8wJoP6ogJJK7lSD1hCU0MlScvtXKuDUf+6pBO19aMLz+ZUVeFx8USbNz5xzPVejUV9/VHTRnEBhGj60TvLtwkQ7uecCIfg75q5Bx+5tuGcCw4Fhly5sc6V3WtXQkwHaamtSXApgP4DnDyHOvp7jMOQHKqFq2ljKSrs/sOqXjqxqL50nSSbpJpUE73t2S1yXfgyuMFDotgZ56FzlakjXm/NxryM+A+V92Dhi/axH22tyQbOtakftLtr2/QX7GV3uM2xO2cIQKBgQDpyGw8YBjDXMbHIF8lO4NiLJvOPLd9Rn/BqJt0THZxgaPAqCQ7I6Uv/+ZhUJ+EnwWH5pIAPrR/6Gejw3FZmYsZ21LWQpXMQ/cnaowSURsdHClNTWoTe2VP+CV2yzKsk8PQuwl84TyM48etrAHZXnN9zSluKYPcuOL1dNpB3VCd6QKBgQCP0qARzrzwM2zuzrDeYvEKHqRsZIB6QBNXIWYffyYGC7EyUsrgwbMLum0Dfks6SFF/ZdfUfhdUt4k4wmGnJ7qko4zE4kgcuje+cuALjeNtqrDSqpznQAej7RV/eaiCYlo4XsI3JGOaJsTE76Mtyh+6jbIJe2vsx4G80xHmDb3q9QKBgQDJrLRygRPbtLVmIm7x151Hm+PszkQcNi0QCzvP4AZtdMCm+mYG/oEKH1/Go5548hX7XtSkkXN0xDBj/b5S5ToyXBnh7B79z8PupOOhCYayXhDjNt1DYTfM/OyUxkY4ymAqOK++oy9w+y7Oi4ws0GYhKFONTRML3hMsXFnMZ/ebwQKBgAa3qrxKMK7zZ/Ibe01Xx7RWTA23//w1w/F84N01vhvoLeWQ0mL/CRLSKUdRfcgCGrRdmGAXZ5WQYPtisNby1xfbf7aubvnQhMwDZ42Cmg3EPDAKwftXFURFzYw/rWwxlc8uNdoHfBN2HhiorLF10FeRSLCCsD7F7rxYC2z4VVeFAoGBAJLKNnH8Y2e+Bkt0g7Q+14jU8XbBXKtBzfGPceX0xosqX1FIHCc8ozQSJUoGJVbNsMc+KNnjOQS6HCFEkJ5PodfiRWUCxkUC0YGF+cMc15hHBBbMjkxStb2mGeHd6RWJ29knHgebOs+NNQJzLHYDutxIkIqVWsMK6HxT1L4VX4aX

    
    */