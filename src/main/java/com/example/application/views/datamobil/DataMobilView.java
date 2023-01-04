package com.example.application.views.datamobil;

import com.example.application.data.entity.DataMobil;
import com.example.application.data.service.DataMobilService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Data Mobil")
@Route(value = "datamobil/:dataMobilID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class DataMobilView extends Div implements BeforeEnterObserver {

    private final String DATAMOBIL_ID = "dataMobilID";
    private final String DATAMOBIL_EDIT_ROUTE_TEMPLATE = "datamobil/%s/edit";

    private final Grid<DataMobil> grid = new Grid<>(DataMobil.class, false);

    private TextField merek;
    private TextField nama_mobil;
    private TextField banyak_bangku;
    private TextField plat;
    private DatePicker tahun_mobil;
    private TextField warna;
    private TextField harga_perhari;
    private Upload gambar;
    private Image gambarPreview;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<DataMobil> binder;

    private DataMobil dataMobil;

    private final DataMobilService dataMobilService;

    public DataMobilView(DataMobilService dataMobilService) {
        this.dataMobilService = dataMobilService;
        addClassNames("data-mobil-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("merek").setAutoWidth(true);
        grid.addColumn("nama_mobil").setAutoWidth(true);
        grid.addColumn("banyak_bangku").setAutoWidth(true);
        grid.addColumn("plat").setAutoWidth(true);
        grid.addColumn("tahun_mobil").setAutoWidth(true);
        grid.addColumn("warna").setAutoWidth(true);
        grid.addColumn("harga_perhari").setAutoWidth(true);
        LitRenderer<DataMobil> gambarRenderer = LitRenderer.<DataMobil>of(
                "<span style='border-radius: 50%; overflow: hidden; display: flex; align-items: center; justify-content: center; width: 64px; height: 64px'><img style='max-width: 100%' src=${item.gambar} /></span>")
                .withProperty("gambar", item -> {
                    if (item != null && item.getGambar() != null) {
                        return "data:image;base64," + Base64.getEncoder().encodeToString(item.getGambar());
                    } else {
                        return "";
                    }
                });
        grid.addColumn(gambarRenderer).setHeader("Gambar").setWidth("96px").setFlexGrow(0);

        grid.setItems(query -> dataMobilService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DATAMOBIL_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DataMobilView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(DataMobil.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        attachImageUpload(gambar, gambarPreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.dataMobil == null) {
                    this.dataMobil = new DataMobil();
                }
                binder.writeBean(this.dataMobil);
                dataMobilService.update(this.dataMobil);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(DataMobilView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> dataMobilId = event.getRouteParameters().get(DATAMOBIL_ID).map(Long::parseLong);
        if (dataMobilId.isPresent()) {
            Optional<DataMobil> dataMobilFromBackend = dataMobilService.get(dataMobilId.get());
            if (dataMobilFromBackend.isPresent()) {
                populateForm(dataMobilFromBackend.get());
            } else {
                Notification.show(String.format("The requested dataMobil was not found, ID = %s", dataMobilId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DataMobilView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        merek = new TextField("Merek");
        nama_mobil = new TextField("Nama_mobil");
        banyak_bangku = new TextField("Banyak_bangku");
        plat = new TextField("Plat");
        tahun_mobil = new DatePicker("Tahun_mobil");
        warna = new TextField("Warna");
        harga_perhari = new TextField("Harga_perhari");
        Label gambarLabel = new Label("Gambar");
        gambarPreview = new Image();
        gambarPreview.setWidth("100%");
        gambar = new Upload();
        gambar.getStyle().set("box-sizing", "border-box");
        gambar.getElement().appendChild(gambarPreview.getElement());
        formLayout.add(merek, nama_mobil, banyak_bangku, plat, tahun_mobil, warna, harga_perhari, gambarLabel, gambar);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            uploadBuffer.reset();
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            StreamResource resource = new StreamResource(e.getFileName(),
                    () -> new ByteArrayInputStream(uploadBuffer.toByteArray()));
            preview.setSrc(resource);
            preview.setVisible(true);
            if (this.dataMobil == null) {
                this.dataMobil = new DataMobil();
            }
            this.dataMobil.setGambar(uploadBuffer.toByteArray());
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(DataMobil value) {
        this.dataMobil = value;
        binder.readBean(this.dataMobil);
        this.gambarPreview.setVisible(value != null);
        if (value == null || value.getGambar() == null) {
            this.gambar.clearFileList();
            this.gambarPreview.setSrc("");
        } else {
            this.gambarPreview.setSrc("data:image;base64," + Base64.getEncoder().encodeToString(value.getGambar()));
        }

    }
}
