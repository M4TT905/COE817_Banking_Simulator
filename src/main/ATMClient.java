/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author jonathan
 */
public class ATMClient extends Client {

    // Replace these with the real key strings for this client
    private static final String CLIENT_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1tMWe3uX6SyMKPEC6+ZaZo8MBSXk5fOZNRTEGxvb9+sNm1y0MeznKe2a/BmT0c09EoQb87VQU9BCdumKIy/Q6TZw2dAJ2oPITXlccijW83K3MLU/nKO1bi5sdd2Pg2oKXMt2W+wWTyqux3rZZs/oKabVs5273sK0Um1Zn/sTU3H9juli365XTHSvLJByb2iXUYDf34Nl0xsRqU+9eSKgfS4TIWjdNB9JA63iW8We/Wsz/WBDi430N8i2mUGWovQBgSwdDc4BxeHg3Jql7TW+IPvS2L+UWAhv5Qg/7FBvhej9a9cLYMZkT/pkZ+sf04c8DFG59o/i0vQjCG8ZGd2bawIDAQAB";
    private static final String CLIENT_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDW0xZ7e5fpLIwo8QLr5lpmjwwFJeTl85k1FMQbG9v36w2bXLQx7Ocp7Zr8GZPRzT0ShBvztVBT0EJ26YojL9DpNnDZ0Anag8hNeVxyKNbzcrcwtT+co7VuLmx13Y+Dagpcy3Zb7BZPKq7Hetlmz+gpptWznbvewrRSbVmf+xNTcf2O6WLfrldMdK8skHJvaJdRgN/fg2XTGxGpT715IqB9LhMhaN00H0kDreJbxZ79azP9YEOLjfQ3yLaZQZai9AGBLB0NzgHF4eDcmqXtNb4g+9LYv5RYCG/lCD/sUG+F6P1r1wtgxmRP+mRn6x/ThzwMUbn2j+LS9CMIbxkZ3ZtrAgMBAAECggEBALzfkmC25XTo6cIFRGBLgyewlQzhqEJUzlrRCyoacGekja4O748Fjqhac206v7AyFoP1Cu5Wd215KTaLVhxhqfehGc8nDcLFIpKbJSBpr7MTJOT6NhduB9H7lvh59/e+f6XebQ2coHCyQkC4TfQKzz/vouvsvoS4AvqFkcsKYI93iJAtJz4SVI0lBT/lyRXDGTeTLUUjoZMDrfPksTG6vORjxk3Y2sV+iTzkamu0fJcU6y7JB7oP2FWq7yrf8pPrQ6J/h2QAgIrLcXzuLQm/kt0nY8iuEyepjRjj0Yu+c/rzhLTpXA0dRAdAHfYzXy7wRyKuRTxZNRp2k7m3IYMCx+ECgYEA9Ife5Jlzm3yPVdpU/ZXzuiYEMmQm2rNSRzkn7EpXeNjceUTCa7dquL3C1ev+hpHe2t0CbnCoH5VR4EFhFqfjSKl3DfMJkxHmcuWHZ+CaqeaXZavA7JzKJ3AHSmjsMBwh8t0C4cPWRLOADEvDdFYcvz9/mkMTyIfhD9Z/zlfXP98CgYEA4OaHcrDRCMP201ZFTCwX+e4jP0dblumqCqYZL65yhCWfO7NjY2bODb5CmDW0eCzqFVQ9rCVCscKQH2Bku2RM87ZM5CcVYTMjV+Qh0vnD4UGCOaxflRBCcC76w4GB/6R2KgePVC7NLe7QKGsgRU+INc6gk7PY3TIo/tPW0u6l5fUCgYBCsehea2g3yYKCxRkXTVX7uLC+WcirL+vKF1HHRgOTp3qC7PkmThGir8c7d7GlXJXvFga6vqlsa9q4Erz7Y/E++VHKkTZUUbaMMgR7law5mPZEOdNV9fHBTfH/Hp0FBjdQOW22zCZd+KN9NxDpYy9WDmisV41GzaDaJAQZ8KV5+wKBgQCFy//21oR1j4fvMNrnNzBIa0ki40P04yxbS3eea00LmwpBTTaqs0WQ3Yjm3sRv3mCgsc5C9KPZ9yMg14r2BPTW5LSX4zyP6Al1x23sAtVq1DsyLn9qwhKUhQ6AczKwsNnvpTG65mEAm5vQ//Jh6a/iOvxDAvLYnXfQKp0aPgoKZQKBgEYZg73Y2P14f6zyCJjJrrhnYjv41jCFvo8YD4UxEklYOxgymZEYpOD9jc1vk7Bjv/5kQC3PC0pCORpnDqL0yKD0RcizUhYPHVQxGuH3WEeqVigye0llXz/104+Ex69zr346vbteIDpNtaWzBwvH3JZS3UvFJGBdfgA61QHYxMzp";

    
    public ATMClient(String id, int port) {
        super(CLIENT_PUBLIC_KEY, CLIENT_PRIVATE_KEY, id, port);
    }

    public static void main(String[] args) {
        String clientId = A_ID;

        if (args.length > 0) {
            clientId = args[0];
        }

        ATMClient client = new ATMClient(clientId, 0);
        client.start();
    }
}
