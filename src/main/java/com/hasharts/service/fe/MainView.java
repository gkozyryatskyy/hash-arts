package com.hasharts.service.fe;

import com.hasharts.db.model.nft.Image;
import com.hasharts.db.model.nft.Token;
import com.hasharts.service.image.GoogleImageGenerator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.ConfigProvider;

@JBossLog
@Route("")
public class MainView extends VerticalLayout {

    private final GoogleImageGenerator imageGenerator;
    private final String gatewayPrefix;
    private final String gatewayLocalPrefix;

    private final Grid<Token> tokenGrid;
    private Token selectedToken;
    private final Grid<Image> imageGrid;
    private final TextField aiTest;
    private Image selectedImage;

    @Inject
    public MainView(GoogleImageGenerator imageGenerator) {
        // config
        this.imageGenerator = imageGenerator;
        this.gatewayPrefix = ConfigProvider.getConfig().getValue("nft.token.mint.gateway.prefix", String.class);
        this.gatewayLocalPrefix = ConfigProvider.getConfig().getValue("nft.token.mint.gateway.local.prefix", String.class);
        log.infof("MainView:%s", imageGenerator);
        // Token
        tokenGrid = tokenGrid();
        // Image
        imageGrid = imageGrid();
        updateImageGrid();
        aiTest = new TextField();
        aiTest.setPlaceholder("Enter text for AI image generation");
        aiTest.setWidth("800px");
        // generate Image
        Button save = new Button("Save", e -> {
            try {
                this.imageGenerator.generateAndUpload("nft-image", aiTest.getValue());
                updateImageGrid();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        // mint nft
        HorizontalLayout hImages = new HorizontalLayout(aiTest, save);
        VerticalLayout vImages = new VerticalLayout(hImages, imageGrid);
        // Tabs
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeUndefined();
        tabSheet.setWidth("100%");
        tabSheet.add("Tokens", new LazyComponent(() -> tokenGrid));
        tabSheet.add("Images", new LazyComponent(() -> vImages));
        tabSheet.add("NFTs", new Div(new Text("This is the NFTs tab content")));
        add(tabSheet);
    }

    public Grid<Token> tokenGrid() {
        Grid<Token> grid = new Grid<>(Token.class, false);
        grid.addColumn(Token::getId).setHeader("Id")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Token::getHederaTokenId).setHeader("Hedera Token Id")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Token::getName).setHeader("Name")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Token::getSymbol).setHeader("Symbol")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addSelectionListener(selection -> {
            Optional<Token> selected = selection.getFirstSelectedItem();
            selected.ifPresent(token -> this.selectedToken = token);
            System.out.println("Selected token: " + selectedToken.getName());
        });

        List<Token> tokens = Token.<Token>findAll().list();
        grid.setItems(tokens);
        return grid;
    }

    public Grid<Image> imageGrid() {
        Grid<Image> grid = new Grid<>(Image.class, false);
        grid.addColumn(Image::getId).setHeader("Id")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Image::getName).setHeader("Name")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(createEmployeeRenderer(gatewayLocalPrefix)).setHeader("Local Url")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(createEmployeeRenderer(gatewayPrefix)).setHeader("Public Url")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Image::getIpfs).setHeader("Ipfs Hash (CID)")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(item -> {
            Button btn = new Button(new Icon(VaadinIcon.TRASH), click -> Image.deleteById(item.getId()));
            btn.addThemeVariants(ButtonVariant.LUMO_ICON);
            return btn;
        });
        grid.addSelectionListener(selection -> {
            Optional<Image> selected = selection.getFirstSelectedItem();
            selected.ifPresent(image -> this.selectedImage = image);
            System.out.println("Selected image: " + selectedImage.getName());
        });
        return grid;
    }

    public void updateImageGrid() {
        List<Image> images = Image.<Image>findAll().list();
        imageGrid.setItems(images);
    }

    private static Renderer<Image> createEmployeeRenderer(String prefix) {
        return LitRenderer.<Image>of(
                        "<vaadin-avatar img=\"${item.url}\" name=\"${item.name}\" alt=\"Image\"></vaadin-avatar>")
                .withProperty("url", e -> prefix + e.getIpfs())
                .withProperty("name", Image::getName);
    }

    public static class LazyComponent extends Div {
        public LazyComponent(SerializableSupplier<? extends Component> supplier) {
            addAttachListener(e -> {
                if (getElement().getChildCount() == 0) {
                    add(supplier.get());
                }
            });
        }
    }
}
