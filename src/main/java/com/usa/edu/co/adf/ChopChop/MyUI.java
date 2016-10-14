package com.usa.edu.co.adf.ChopChop;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.usa.edu.co.adf.Data.Objetos.Compra;
import com.usa.edu.co.adf.Data.Objetos.Producto;
import com.usa.edu.co.adf.Service.Service;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

	private Service servicio = Service.getInstance();
	private ComboBox producto= new ComboBox("Ingrese producto");;
	private Button nuevoProducto = new Button("Crear nuevo producto");
	private Button agregar = new Button("Añadir producto");
	private Button verCompras = new Button("Ver Compras");
	private Button eliminarProducto = new Button();
	private Button comprar = new Button("Realizar compra");

	private Grid tabla = new Grid();
	private Grid tablaCompras = new Grid();
	private Grid imagen = new Grid();
	private Image im = new Image();

	private Producto productico = new Producto();
	private Compra comprita;
	private List<Producto> listaProductos;
	private List<Compra> listaAgregados = new LinkedList<>();
	private TextField cantidad = new TextField("Ingrese cantidad");

	private FormularioProducto formulario = new FormularioProducto(this);

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();

		iniciarComboBox();

		tabla.setColumns("nombreProducto", "cantidad", "totalCompra");
		tablaCompras.setColumns("fechaCompra", "nombreProducto", "cantidad", "totalCompra");
		HorizontalLayout main = new HorizontalLayout(tabla, tablaCompras, formulario);

		HorizontalLayout layoutImagen = new HorizontalLayout(producto, im);
		HorizontalLayout layoutAddRemove = new HorizontalLayout(agregar, eliminarProducto);
		HorizontalLayout layoutCompra = new HorizontalLayout(nuevoProducto, comprar, verCompras);
		nuevoProducto.setSizeUndefined();
		comprar.setResponsive(true);
		layoutCompra.setSpacing(true);
		layoutImagen.setSpacing(true);
		layoutAddRemove.setSpacing(true);
		main.setSpacing(true);
		
		main.setSizeFull();
		tabla.setSizeFull();
		tablaCompras.setSizeFull();
		main.setExpandRatio(tabla, 1);
		main.setExpandRatio(tablaCompras, 1);
		tablaCompras.setVisible(false);
		layoutCompra.setVisible(true);

		eliminarProducto.setIcon(FontAwesome.TIMES);
		eliminarProducto.setVisible(false);
		layout.addComponents(layoutImagen, cantidad, layoutAddRemove, main, layoutCompra);
		layout.setMargin(true);
		layout.setSpacing(true);

		setContent(layout);
		formulario.setVisible(false);
		nuevoProducto.addClickListener(e -> {
			formulario.setVisible(true);
		});

		agregar.addClickListener(e -> obtenerProducto());
		comprar.addClickListener(e -> finalizarCompra());
		tabla.addSelectionListener(e -> seleccion(e));
		eliminarProducto.addClickListener(e -> eliminarProducto());
		verCompras.addClickListener(e -> verCompras());
		producto.addValueChangeListener(e -> {
			iniciarProductico();
			if (!productico.getRutaArchivo().equalsIgnoreCase(" ")) {
				im.setSource(new ThemeResource(productico.getRutaArchivo()));
				im.setVisible(true);
			}else{
				im.setVisible(false);
			}
			
		});
		
		
	}

	private void iniciarProductico() {
		String pd = (String) producto.getValue();
		for (int i = 0; i < listaProductos.size(); i++) {
			if (listaProductos.get(i).getNombre().equalsIgnoreCase(pd)) {
				productico = listaProductos.get(i);
				break;
			}
		}

	}

	private void verCompras() {
		tablaCompras.setContainerDataSource(new BeanItemContainer<>(Compra.class, servicio.listadoCompras()));
		tabla.setVisible(false);
		tablaCompras.setVisible(true);
	}

	private void seleccion(SelectionEvent e) {
		// TODO Auto-generated method stub

		if (e.getSelected().isEmpty()) {
			eliminarProducto.setVisible(false);
		} else {
			comprita = (Compra) e.getSelected().iterator().next();
			eliminarProducto.setVisible(true);
		}
	}

	private void eliminarProducto() {
		// TODO Auto-generated method stub
		listaAgregados.remove(comprita);
		actualizarTabla();
		eliminarProducto.setVisible(false);
	}

	private void finalizarCompra() {

		if (listaAgregados.isEmpty()) {
			Notification.show("Selecione productos para comprar");
		} else {
			Date fecha = new Date();
			for (Compra c : listaAgregados) {
				c.setFechaCompra(fecha);
				servicio.agregarCompra(c);
			}
			Notification.show("Compra realizada con Exito");
			listaAgregados.clear();
			actualizarTabla();
		}

	}

	private void obtenerProducto() {
		if (!tabla.isVisible()) {
			tabla.setVisible(true);
			tablaCompras.setVisible(false);
		}

		if (producto.getValue() == null || cantidad.getValue() == null) {
			Notification.show("Por favor rellene todos los campos");
		} else {

			double cd = Double.parseDouble(cantidad.getValue());
			// iniciarProductico();

			String precio = String.valueOf(productico.getPrecio());
			String total = String.valueOf(productico.getPrecio() * cd);
			Compra c = new Compra();
			c.setCantidad(cd);
			c.setNombreProducto(productico.getNombre());
			c.setTotalCompra(Double.parseDouble(total));
			listaAgregados.add(c);
			actualizarTabla();

			producto.clear();
			cantidad.clear();

		}

	}

	private void actualizarTabla() {
		tabla.setContainerDataSource(new BeanItemContainer<>(Compra.class, listaAgregados));
	}

	public void iniciarComboBox() {
	listaProductos = servicio.getProductList();
	LinkedList<String> nombresProductos = new LinkedList<>();
		for (int i = 0; i < listaProductos.size(); i++) {
			nombresProductos.add(listaProductos.get(i).getNombre());
			System.out.println(nombresProductos.get(i));
		}
		producto.setInputPrompt("No product selected");
		if (producto.isEmpty()) {
			producto.setContainerDataSource(new BeanItemContainer<>(String.class,nombresProductos));
		}else{
			producto.removeAllItems();
			producto.setContainerDataSource(new BeanItemContainer<>(String.class,nombresProductos));
			
		}
		
		
		
		
		

	}

	

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}
