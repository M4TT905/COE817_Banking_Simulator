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
public class Bob extends Client {
    private static final String B_PBK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg1dNDAn1ZyXebowUvB5z5l7dVQFQrNbTqtJpkg1eJiDpZdX/a/4GL8liUCkkmlA8EtupcZ8bchF8nOAahZAhP+33LzFL0FdZpsVgu2BkDAfy8vqbJOAmYXK0HjEPhWE+JadjzIafhc/gyJHfrGZtdzFJvE/T9CeZkDIR5Rs7ZA/L2Ho1NPBkLsMHD+XYB8L1Jwt1bmC9AhE4iQz7DHP4ykt2EXaobbzrY6deAgl5vEu/Jati0oC9EUxmj+Y3X9AJpLrd3GgR4qXoe4fhZPtlDzGbaGxFBKfQYloUU33EbqLNISFidBrPLP6EFnHmqri5Zacz56u5UZ2nJ+oz6IMZ/QIDAQAB";
    private static final String B_PRK = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCDV00MCfVnJd5ujBS8HnPmXt1VAVCs1tOq0mmSDV4mIOll1f9r/gYvyWJQKSSaUDwS26lxnxtyEXyc4BqFkCE/7fcvMUvQV1mmxWC7YGQMB/Ly+psk4CZhcrQeMQ+FYT4lp2PMhp+Fz+DIkd+sZm13MUm8T9P0J5mQMhHlGztkD8vYejU08GQuwwcP5dgHwvUnC3VuYL0CETiJDPsMc/jKS3YRdqhtvOtjp14CCXm8S78lq2LSgL0RTGaP5jdf0Amkut3caBHipeh7h+Fk+2UPMZtobEUEp9BiWhRTfcRuos0hIWJ0Gs8s/oQWceaquLllpzPnq7lRnacn6jPogxn9AgMBAAECggEANSZqBnotNf2sIr5LUcldC5fOJ60tkZQp+hHGKRWByRo+eTh9cZ1NsZ7kSx4rMstPVC5SRqmleKa879r8wJoP6ogJJK7lSD1hCU0MlScvtXKuDUf+6pBO19aMLz+ZUVeFx8USbNz5xzPVejUV9/VHTRnEBhGj60TvLtwkQ7uecCIfg75q5Bx+5tuGcCw4Fhly5sc6V3WtXQkwHaamtSXApgP4DnDyHOvp7jMOQHKqFq2ljKSrs/sOqXjqxqL50nSSbpJpUE73t2S1yXfgyuMFDotgZ56FzlakjXm/NxryM+A+V92Dhi/axH22tyQbOtakftLtr2/QX7GV3uM2xO2cIQKBgQDpyGw8YBjDXMbHIF8lO4NiLJvOPLd9Rn/BqJt0THZxgaPAqCQ7I6Uv/+ZhUJ+EnwWH5pIAPrR/6Gejw3FZmYsZ21LWQpXMQ/cnaowSURsdHClNTWoTe2VP+CV2yzKsk8PQuwl84TyM48etrAHZXnN9zSluKYPcuOL1dNpB3VCd6QKBgQCP0qARzrzwM2zuzrDeYvEKHqRsZIB6QBNXIWYffyYGC7EyUsrgwbMLum0Dfks6SFF/ZdfUfhdUt4k4wmGnJ7qko4zE4kgcuje+cuALjeNtqrDSqpznQAej7RV/eaiCYlo4XsI3JGOaJsTE76Mtyh+6jbIJe2vsx4G80xHmDb3q9QKBgQDJrLRygRPbtLVmIm7x151Hm+PszkQcNi0QCzvP4AZtdMCm+mYG/oEKH1/Go5548hX7XtSkkXN0xDBj/b5S5ToyXBnh7B79z8PupOOhCYayXhDjNt1DYTfM/OyUxkY4ymAqOK++oy9w+y7Oi4ws0GYhKFONTRML3hMsXFnMZ/ebwQKBgAa3qrxKMK7zZ/Ibe01Xx7RWTA23//w1w/F84N01vhvoLeWQ0mL/CRLSKUdRfcgCGrRdmGAXZ5WQYPtisNby1xfbf7aubvnQhMwDZ42Cmg3EPDAKwftXFURFzYw/rWwxlc8uNdoHfBN2HhiorLF10FeRSLCCsD7F7rxYC2z4VVeFAoGBAJLKNnH8Y2e+Bkt0g7Q+14jU8XbBXKtBzfGPceX0xosqX1FIHCc8ozQSJUoGJVbNsMc+KNnjOQS6HCFEkJ5PodfiRWUCxkUC0YGF+cMc15hHBBbMjkxStb2mGeHd6RWJ29knHgebOs+NNQJzLHYDutxIkIqVWsMK6HxT1L4VX4aX";
    private static final int PORT = 8172;
    
    public Bob(String pbk, String prk, String id) {
        super(pbk, prk, id, PORT);
    }
 
    public static void main(String[] args) {
        Bob b = new Bob(B_PBK, B_PRK, B_ID);
        b.start();
    }
}
