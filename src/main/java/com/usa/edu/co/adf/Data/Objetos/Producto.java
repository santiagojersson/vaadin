package com.usa.edu.co.adf.Data.Objetos;

import com.usa.edu.co.adf.Data.FixedWidthField;

public class Producto {
	@FixedWidthField(position = 1, width = 25) String nombre;
	@FixedWidthField(position = 2, width = 10) double precio;
	@FixedWidthField(position = 3, width = 100) String rutaArchivo;
	
	public Producto(){
		
	}

	public String getRutaArchivo() {
		return rutaArchivo;
	}

	public void setRutaArchivo(String rutaArchivo) {
		this.rutaArchivo = rutaArchivo;
	}

	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	@Override
	public String toString() {
		return "Producto [nombre=" + nombre + ", precio=" + precio + "]";
	}
	
	
	
}
