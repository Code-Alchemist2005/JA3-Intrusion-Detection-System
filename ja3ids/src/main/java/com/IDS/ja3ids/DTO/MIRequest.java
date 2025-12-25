package com.IDS.ja3ids.DTO;

public class MIRequest {

    private String tls_version;
    private int cipher_count;
    private int extension_count;
    private int elliptic_curve_count;
    private int ec_point_format_count;
    private int source_port;
    private int dest_port;

    public String getTls_version() {
        return tls_version;
    }

    public void setTls_version(String tls_version) {
        this.tls_version = tls_version;
    }

    public int getCipher_count() {
        return cipher_count;
    }

    public void setCipher_count(int cipher_count) {
        this.cipher_count = cipher_count;
    }

    public int getExtension_count() {
        return extension_count;
    }

    public void setExtension_count(int extension_count) {
        this.extension_count = extension_count;
    }

    public int getElliptic_curve_count() {
        return elliptic_curve_count;
    }

    public void setElliptic_curve_count(int elliptic_curve_count) {
        this.elliptic_curve_count = elliptic_curve_count;
    }

    public int getEc_point_format_count() {
        return ec_point_format_count;
    }

    public void setEc_point_format_count(int ec_point_format_count) {
        this.ec_point_format_count = ec_point_format_count;
    }

    public int getSource_port() {
        return source_port;
    }

    public void setSource_port(int source_port) {
        this.source_port = source_port;
    }

    public int getDest_port() {
        return dest_port;
    }

    public void setDest_port(int dest_port) {
        this.dest_port = dest_port;
    }
}
