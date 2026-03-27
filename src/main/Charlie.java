/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;


/**
 *
 * @author matthewhvizdos
 */
public class Charlie extends Client {
    private static final String C_PBK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmsOVcQqZMuegKTVUgfFFrJbWJJ1k8+tawUIIjmBqJMcz65xgW8vIrA83GZzAHrX8U/uG72eJ0mjh+W1CfnAtOZwAB77zMTelIOdvj1zrtg97RlgKm0IXyn7juDQ35Dokdu1necma8eikZopwkIlOhCNndT4WJP6tIZnF3RBHi+mvRSzcxgISjxjJfsuJviAKyaHr2lnJdliPyS0qoT6n2shY8Iq70P3spqUIiY7RsUrLBL4/W/Vde10OJCLu7Alh+J4Ur3iMPgq+oZDOI8XXGokEKbu1G9+XgVl/VmUDqvxV5+MqactOFh1LFhU2ylE668UYg9ZjRJZRGOtq6QRZtQIDAQAB";
    private static final String C_PRK = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCaw5VxCpky56ApNVSB8UWsltYknWTz61rBQgiOYGokxzPrnGBby8isDzcZnMAetfxT+4bvZ4nSaOH5bUJ+cC05nAAHvvMxN6Ug52+PXOu2D3tGWAqbQhfKfuO4NDfkOiR27Wd5yZrx6KRminCQiU6EI2d1PhYk/q0hmcXdEEeL6a9FLNzGAhKPGMl+y4m+IArJoevaWcl2WI/JLSqhPqfayFjwirvQ/eympQiJjtGxSssEvj9b9V17XQ4kIu7sCWH4nhSveIw+Cr6hkM4jxdcaiQQpu7Ub35eBWX9WZQOq/FXn4yppy04WHUsWFTbKUTrrxRiD1mNEllEY62rpBFm1AgMBAAECggEAIUB+nmAZlG9h4Ri9RIITZ0atUgajRyk9QaKvde/PPoGybSAXMAHz5swKGa2tNawCQAUd89g6K4QvcUkBbWsqvGMBOqjTVbLP/uba+GGNn3tqEC4gcUVdGTv0L4LAs3+ukALoJamkae+DSyjZkKR/wzJcKzugxGgXyM1iTuINjUjdtumuJA3OynQMaISmGcXcaW/6iDFfSC5707t3J25USUq6IqcdrQvCf4UFlkngnbWQXeBHGYH8caVhj3l/zkkD9mym7ehARvP8mAVFiucEvxNLxE+p0Fzl/kNW07iqLqiHZQmecxYM8zck9MCwi642TvyN1Gx6pUbVLipAvhhurQKBgQDM3G7eeCjqtVHJrewqZQWSz19HdjVAaFMwr9PoPBeiNjdgp7mz54DjygeDp/uWA8P+PtDRFqgDQiX75SwqqGCrjs4XJdECb+a1T1lgQ1pPRFtuQRmrkLs+S/aWmpIApLS2i548+Gl9HIVAr+rJ1PAT7qm+61a7pR9u8dnfAxxezwKBgQDBZbeOdlr8Ul8nlahUKMxl6zxxcjv/b7Dnk4jgUCUnkxg1ACBPacMlou/t8NacpGDkYbRmHI7blaqRgyPLcxO8n4GuMA66RVPXXGZqZqekTj3S22PERnYQP0VBSjkwKTEPO6vbVygcVBOg9DLNKcMSpB2bkr98P6TxzludmX2AOwKBgF420lZF9KtckCysa/xjUVjfPfZjisIAOKIfetlHKadUP8uX13PBeClKdII78xxexvhaczZ1sPcuqMFb+B/2J98+YdGLdsAkLnob8AKxdwsVaOcf0WpxuWtBDDrfA16AYQvC0hdAN2NuhmnlfZks8Ox5yl8fZFgP7JDdcVF64MVJAoGAcenB5o3cJcc2+Q/zqC6nxlh6r6/Dmz9kieKius06NUc0+PLsFRqMsXx5qkMzhexkjeDm60Zi9v//CdqemNVdiLrDHaB87EatjaqFQKLal7LmQ26qIxAFtqgt2LkpWi7JENnq7jU2EQmMzj9INQQmPLKiJeKYG005kJwl5nxkKcUCgYB9wBeKE9cxaqq98bWRbYe25eBx5kUO3k3K0U/06P0SJdWdEtoeYipMVR2+RzuNuhjK43e4aqUrzok+1q8iAlOJrkMeDFIQ7RBGEn8lUORfRTO1dSwpe4Eo5NGB0AZvUpOGmnCTfEWncouRL25UOR7p4jQ6w89ca5kYBb0GZoaLZg==";
    private static final int PORT = 8173; 
    
    public Charlie(String pbk, String prk, String id) {
        super(pbk, prk, id, PORT);
    }
    
    
    public static void main(String[] args) {
        Charlie c = new Charlie(C_PBK, C_PRK, C_ID);
        c.start();
    }
}
