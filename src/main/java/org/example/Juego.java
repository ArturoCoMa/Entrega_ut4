package org.example;

public class Juego {
    private int _id;
    private String titulo;
    private String compania;
    private String director;
    private String genero;
    private int ano;
    private double precio;

    public Juego() {
    }

    public Juego(int _id, String titulo, String compania, String director, String genero, int ano, double precio) {
        this._id = _id;
        this.titulo = titulo;
        this.compania = compania;
        this.director = director;
        this.genero = genero;
        this.ano = ano;
        this.precio = precio;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Juego{" +
                "_id=" + _id +
                ", titulo='" + titulo + '\'' +
                ", compania='" + compania + '\'' +
                ", director='" + director + '\'' +
                ", genero='" + genero + '\'' +
                ", ano='" + ano + '\'' +
                ", precio=" + precio +
                '}';
    }
}
