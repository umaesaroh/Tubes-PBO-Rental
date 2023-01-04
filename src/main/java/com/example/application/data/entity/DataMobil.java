package com.example.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class DataMobil extends AbstractEntity {

    private String merek;
    private String nama_mobil;
    private String banyak_bangku;
    private String plat;
    private LocalDate tahun_mobil;
    private String warna;
    private String harga_perhari;
    @Lob
    @Column(length = 1000000)
    private byte[] gambar;

    public String getMerek() {
        return merek;
    }
    public void setMerek(String merek) {
        this.merek = merek;
    }
    public String getNama_mobil() {
        return nama_mobil;
    }
    public void setNama_mobil(String nama_mobil) {
        this.nama_mobil = nama_mobil;
    }
    public String getBanyak_bangku() {
        return banyak_bangku;
    }
    public void setBanyak_bangku(String banyak_bangku) {
        this.banyak_bangku = banyak_bangku;
    }
    public String getPlat() {
        return plat;
    }
    public void setPlat(String plat) {
        this.plat = plat;
    }
    public LocalDate getTahun_mobil() {
        return tahun_mobil;
    }
    public void setTahun_mobil(LocalDate tahun_mobil) {
        this.tahun_mobil = tahun_mobil;
    }
    public String getWarna() {
        return warna;
    }
    public void setWarna(String warna) {
        this.warna = warna;
    }
    public String getHarga_perhari() {
        return harga_perhari;
    }
    public void setHarga_perhari(String harga_perhari) {
        this.harga_perhari = harga_perhari;
    }
    public byte[] getGambar() {
        return gambar;
    }
    public void setGambar(byte[] gambar) {
        this.gambar = gambar;
    }

}
